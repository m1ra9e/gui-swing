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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;

final class SqliteDdlTest extends AbstractDdlTest {

    @Override
    protected DbType getDbType() {
        return DbType.SQLite;
    }

    @Test
    @SkipIfDbUnavailable(dbTypes = "SQLite")
    void checkGeneratedDbFileTest() {
        try (var sampleDbFileStream = getClass().getResourceAsStream(getDbFileName())) {
            byte[] sampleDbFileByte = sampleDbFileStream.readAllBytes();
            assertTrue(sampleDbFileByte.length != 0, "Sample Db file is empty.");

            byte[] generatedDbFileByte = Files.readAllBytes(getGeneratedDbFile().toPath());
            assertTrue(generatedDbFileByte.length != 0, "Generated Db file is empty.");

            assertArrayEquals(sampleDbFileByte, generatedDbFileByte,
                    "Expected and generated SQLite DB file does not match.");
        } catch (Exception e) {
            fail("Errors while read file.", e);
        }
    }
}
