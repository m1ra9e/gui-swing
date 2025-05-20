/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import home.db.conn.Connector;

final class DbTestUtils {

    private static final int DEFAULT_TIMEOUT = 5; // sec

    static final int NOT_OVERRIDE_QUERY_TIMEOUT = -1;

    static int count(String tableName) throws SQLException, InterruptedException {
        try (var conn = Connector.getConnection();
                var stmt = conn.createStatement()) {
            stmt.setQueryTimeout(DEFAULT_TIMEOUT);
            try (var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM %s"
                    .formatted(tableName))) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    static boolean isExists(String tableName) throws SQLException, InterruptedException {
        try (var conn = Connector.getConnection();
                var stmt = conn.createStatement()) {
            stmt.setQueryTimeout(DEFAULT_TIMEOUT);
            try (var rs = stmt.executeQuery("SELECT 1 FROM %s".formatted(tableName))) {
                return true;
            }
        }
    }

    static void truncate(String tableName, DbType dbType) throws SQLException, InterruptedException {
        switch (dbType) {
            case SQLite ->
                    // SQLite doesn't have TRUNCATE, instead it uses DELETE without WHERE.
                    // In SQLite, to reset autoincrement, its entry is removed from the
                    // sqlite_sequence table.
                    execute("DELETE FROM %s".formatted(tableName),
                            "DELETE FROM sqlite_sequence WHERE name='%s'".formatted(tableName));
            case PostgreSQL ->
                    // The application logic uses the sequence name, which is usually
                    // the default in PostgreSQL: public.{tableName}_id_seq.
                    execute("TRUNCATE %s".formatted(tableName),
                            "ALTER SEQUENCE %s_id_seq RESTART WITH 1".formatted(tableName));
            default -> throw new SQLException("Unsupported database type : " + dbType);
        }
    }

    static void drop(String tableName) throws SQLException, InterruptedException {
        execute("DROP TABLE IF EXISTS %s".formatted(tableName));
    }

    static void execute(String... queries) throws SQLException {
        execute(DEFAULT_TIMEOUT, queries);
    }

    static void execute(int timeout, String... queries) throws SQLException {
        try (var conn = Connector.getConnection();
                var stmt = conn.createStatement()) {

            if (timeout >= 0) {
                stmt.setQueryTimeout(timeout);
            }

            for (String query : queries) {
                stmt.executeUpdate(query);
            }
        }
    }

    static <T> T runAndWait(Callable<T> task) throws InterruptedException {
        return runAndWait(task, DEFAULT_TIMEOUT);
    }

    private static <T> T runAndWait(Callable<T> task, int timeout) throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // uses method-"submit" to put all uncaught exceptions from the "Callable"
        // into an ExecutionException
        Future<T> futureOfTask = executor.submit(task);
        executor.shutdown();

        try {
            return futureOfTask.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException e) {
            futureOfTask.cancel(true);
            Thread.currentThread().interrupt();
            throw new InterruptedException("time for processing (" + timeout + " seconds) is over");
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    @Deprecated(forRemoval = true)
    static void runAndWait(Runnable task) throws InterruptedException {
        runAndWait(task, DEFAULT_TIMEOUT);
    }

    @Deprecated(forRemoval = true)
    private static void runAndWait(Runnable task, int timeout) throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // uses method-"execute" to use UncaughtExceptionHandler for handle uncaught
        // exceptions inside "Runnable"
        executor.execute(task);
        executor.shutdown();

        if (!executor.awaitTermination(timeout, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new InterruptedException("time for processing (" + timeout + " seconds) is over");
        }
    }

    private DbTestUtils() {
    }
}
