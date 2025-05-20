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
package home.db.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DaoTest {

    private static final Logger LOG = LoggerFactory.getLogger(DaoTest.class);
    private static final String userMsg = "MY_MESSAGE";

    @ParameterizedTest(name = "[{0}] : {2}")
    @CsvSource(delimiter = ';', value = {
            // name...........|.results.......|.description
            "correct_execution; 1, 0, 3, -2, 4; Check results of correct batch execution.",
            "null_arg         ;               ; Check work with null args result.",
    })
    void checkBatchSuccessTest(String testName, String strBatchResults, String description)
            throws SQLException {
        int[] intBatchResults = convertToIntArray(strBatchResults);
        assertDoesNotThrow(() -> new SQLiteDao().checkBatchExecution(intBatchResults, userMsg, LOG));
    }

    @ParameterizedTest(name = "[{0}]: {2}")
    @CsvSource(delimiter = ';', value = {
            // name........|.results........|.erroMsg
            "execute_failed; 1, 2, 3, 4, -3 ; When executing the batch, result code 'EXECUTE_FAILED' was received.",
            "undocumented  ; 1, 2, -11, 3, 4; When executing the batch, unknown result code '-11' was received.",
    })
    void checkBatchFailTest(String testName, String strBatchResults, String erroMsg) {
        try {
            int[] intBatchResults = convertToIntArray(strBatchResults);
            new SQLiteDao().checkBatchExecution(intBatchResults, userMsg, LOG);
            fail("Expected java.sql.SQLException to be thrown, but nothing was thrown.");
        } catch (SQLException e) {
            String actual = e.getMessage().strip();
            assertTrue(actual.endsWith(erroMsg), """
                    Expected and actual error message does not match:
                    expected: %s
                    actual: %s
                    """.formatted(erroMsg, actual));
        }
    }

    // Think about replacing it by
    // Arrays.stream(strBatchResults.split(",")).mapToInt(Integer::parseInt).toArray();
    private int[] convertToIntArray(String strBatchResults) {
        if (strBatchResults == null) {
            return null;
        }

        String[] array = strBatchResults.split(",");
        int size = array.length;

        int[] intBatchResults = new int[size];
        for (int i = 0; i < size; i++) {
            intBatchResults[i] = Integer.parseInt(array[i].strip());
        }
        return intBatchResults;
    }
}
