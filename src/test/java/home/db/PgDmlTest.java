/*******************************************************************************
 * Copyright 2021-2026 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

import home.db.conn.Connector;

final class PgDmlTest extends AbstractDmlTest {

    private static final int DEFAULT_TIMEOUT = 5; // sec

    private static final String PG_CONNECTION_DATA_HELPER_CLASS = "home.db.conn.PgConnectionDataHelper";
    private static final String QUERY_TIMEOUT_FIELD = "QUERY_TIMEOUT";
    private static final String LOCK_TIMEOUT_FIELD = "LOCK_TIMEOUT";

    private static final String TEMPORARY_QUERY_TIMEOUT = "3000"; // ms
    private static final String TEMPORARY_LOCK_TIMEOUT = "2000"; // ms

    // https://www.ibm.com/docs/en/db2woc?topic=messages-sqlstate
    // 57 - Resource Not Available or Operator Intervention
    // 57014 - Processing was cancelled as requested.
    //
    // https://www.postgresql.org/docs/current/errcodes-appendix.html
    // 57 - Class 57 — Operator Intervention
    // 57014 - query_canceled
    private static final String QUERY_TIMEOUT_ERROR_CODE = "57014";
    // https://www.postgresql.org/docs/current/errcodes-appendix.html
    // 57 - Class 55 — Object Not In Prerequisite State
    // 55P03 - lock_not_available
    private static final String LOCK_TIMEOUT_ERROR_CODE = "55P03";

    private static final String EXPECTED_AND_ACTUAL_NOT_MATCH = """
            Expected and actual %s does not match:
            expected: %s
            actual: %s
            """;

    @Override
    protected DbType getDbType() {
        return DbType.PostgreSQL;
    }

    @Test
    @SkipIfDbUnavailable(dbTypes = { "PostgreSQL" })
    void queryTimeoutTest() throws Exception {
        String currentQueryTimeout = getCurrentQueryTimeout();

        resetConnection();
        setQueryTimeout(TEMPORARY_QUERY_TIMEOUT);

        try (var conn = Connector.getConnection();
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery(
                        "SELECT clock_timestamp(), pg_sleep(10), clock_timestamp()")) {
            fail("Expected org.postgresql.util.PSQLException to be thrown, but nothing was thrown.");
        } catch (PSQLException e) {
            assertEquals(QUERY_TIMEOUT_ERROR_CODE, e.getSQLState());
        }

        setQueryTimeout(currentQueryTimeout);
        resetConnection();
    }

    private String getCurrentQueryTimeout() throws Exception {
        return getFieldValue(PG_CONNECTION_DATA_HELPER_CLASS, QUERY_TIMEOUT_FIELD, String.class);
    }

    private void setQueryTimeout(String queryTimeout) throws Exception {
        setFieldValue(queryTimeout, PG_CONNECTION_DATA_HELPER_CLASS, QUERY_TIMEOUT_FIELD);
    }

    @Test
    @SkipIfDbUnavailable(dbTypes = { "PostgreSQL" })
    void lockTimeoutTest() throws Exception {
        String currentQueryTimeout = getCurrentQueryTimeout();
        String currentLockTimeout = getCurrentLockTimeout();

        resetConnection();
        // query timeout is set so that the blocking request
        // does not run longer than necessary
        setQueryTimeout(TEMPORARY_QUERY_TIMEOUT);
        setLockTimeout(TEMPORARY_LOCK_TIMEOUT);

        // PREPARE
        DbTestUtils.execute(
                "DROP TABLE IF EXISTS public.check_lock_timeout",
                "CREATE TABLE public.check_lock_timeout (id integer, some_data integer)",
                "INSERT INTO public.check_lock_timeout (id, some_data) VALUES (1, 11), (2, 22)");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // LOCKER
        Future<PSQLException> futureExceptionOfLocker = executor.submit(() -> executeAndGetException(
                "SELECT clock_timestamp(), id, pg_sleep(10) FROM public.check_lock_timeout"));

        TimeUnit.MILLISECONDS.sleep(300);

        // LOCKABLE
        Future<PSQLException> futureExceptionOfLockable = executor.submit(() -> executeAndGetException(
                "DROP TABLE public.check_lock_timeout"));

        executor.shutdown();

        try {
            PSQLException exceptionOfLocker = futureExceptionOfLocker.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            checkError(exceptionOfLocker, QUERY_TIMEOUT_ERROR_CODE,
                    List.of("выполнение оператора отменено из-за тайм-аута",
                            "canceling statement due to statement timeout"));

            PSQLException exceptionOfLockable = futureExceptionOfLockable.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            checkError(exceptionOfLockable, LOCK_TIMEOUT_ERROR_CODE,
                    List.of("выполнение оператора отменено из-за тайм-аута блокировки",
                            "canceling statement due to lock timeout"));
        } catch (TimeoutException | InterruptedException e) {
            futureExceptionOfLocker.cancel(true);
            futureExceptionOfLockable.cancel(true);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("time for processing (" + DEFAULT_TIMEOUT + " seconds) is over");
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }

        if (!executor.awaitTermination(DEFAULT_TIMEOUT * 2, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new InterruptedException("time for processing (" + DEFAULT_TIMEOUT + " seconds) is over");
        }

        setQueryTimeout(currentQueryTimeout);
        setLockTimeout(currentLockTimeout);
        resetConnection();
    }

    private PSQLException executeAndGetException(String query) throws SQLException {
        try {
            DbTestUtils.execute(DbTestUtils.NOT_OVERRIDE_QUERY_TIMEOUT, query);
            return null;
        } catch (PSQLException e) {
            return e;
        }
    }

    private void checkError(PSQLException exceptionOfOperation,
            String expectedErrorCode, List<String> expectedErrorMsgs) {
        if (exceptionOfOperation != null) {
            boolean isEndsWithExpectedMsg = false;
            String actualErrorMsg = exceptionOfOperation.getMessage();
            for (String expectedErrorMsg : expectedErrorMsgs) {
                if (actualErrorMsg.endsWith(expectedErrorMsg)) {
                    isEndsWithExpectedMsg = true;
                    break;
                }
            }

            assertTrue(isEndsWithExpectedMsg, EXPECTED_AND_ACTUAL_NOT_MATCH
                    .formatted("error message", String.join(" \n\t|| ", expectedErrorMsgs), actualErrorMsg));

            String actualErrorCode = exceptionOfOperation.getSQLState();
            assertEquals(expectedErrorCode, actualErrorCode, EXPECTED_AND_ACTUAL_NOT_MATCH
                    .formatted("error code", expectedErrorCode, actualErrorCode));
        } else {
            fail("Expected org.postgresql.util.PSQLException to be thrown, but nothing was thrown.");
        }
    }

    private String getCurrentLockTimeout() throws Exception {
        return getFieldValue(PG_CONNECTION_DATA_HELPER_CLASS, LOCK_TIMEOUT_FIELD, String.class);
    }

    private void setLockTimeout(String lockTimeout) throws Exception {
        setFieldValue(lockTimeout, PG_CONNECTION_DATA_HELPER_CLASS, LOCK_TIMEOUT_FIELD);
    }

    private <T> T getFieldValue(String className, String fieldName, Class<T> returnType) throws Exception {
        Field field = getClassField(className, fieldName);
        @SuppressWarnings("unchecked")
        var fieldValue = (T) field.get(null);
        return fieldValue;
    }

    private void setFieldValue(Object fieldValue, String className, String fieldName) throws Exception {
        Field field = getClassField(className, fieldName);
        field.set(null, fieldValue);
    }

    private Field getClassField(String className, String fieldName) throws Exception {
        Class<?> clazz = Class.forName(className);
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
