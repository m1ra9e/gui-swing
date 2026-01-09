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
package home.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class NamedFormatterTest {

    @ParameterizedTest(name = "checking :'${'{0}'}' ")
    @ValueSource(strings = {
            "paramname",
            "param_name",
            "paramname_123",
            "paramname123",
    })
    void formattingSuccessTest(String paramName) {
        String expected = "Start value-of-placeholder text";

        String template = "Start ${%s} text".formatted(paramName);
        Map<String, Object> params = Map.of(paramName, "value-of-placeholder");
        String actual = NamedFormatter.format(template, params);

        assertEquals(expected, actual, """
                Expected and actual text does not match:
                expected: %s
                actual: %s
                """.formatted(expected, actual));
    }

    @Test
    void formattingSeveralEqualParamsTest() {
        String expected = "Start value_text middle [123] end value_text";

        String template = "Start ${param_1} middle [${param_2}] end ${param_1}";
        Map<String, Object> params = Map.ofEntries(
                Map.entry("param_1", "value_text"),
                Map.entry("param_2", 123));
        String actual = NamedFormatter.format(template, params);

        assertEquals(expected, actual, """
                Expected and actual text does not match:
                expected: %s
                actual: %s
                """.formatted(expected, actual));
    }

    @Test
    void formattingFailTest() {
        String incorrectParamName = "param-name";

        String expected = "Start value-of-placeholder text";

        String template = "Start ${%s} text".formatted(incorrectParamName);
        Map<String, Object> params = Map.of(incorrectParamName, "value-of-placeholder");
        String actual = NamedFormatter.format(template, params);

        if (expected.equals(actual)) {
            fail("""
                    Expected and actual text should be different:
                    expected: %s
                    actual: %s
                        """.formatted(expected, actual));
        }
    }
}
