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
 *
 * Copyright 2022 Baeldung
 *
 * Distributed under MIT License
 *******************************************************************************/
package home.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formatter for work with strings with named placeholders.
 */
public final class NamedFormatter {

    // regex for ${some_word} (${word} or ${wo_rd} or ${word_123} or ${word123}),
    // where group 0 is "${some_word}" and group 1 is "some_word"
    private static final String REGEX = "[$][{](\\w+)}";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * Formats given string: replaces placeholders in
     * {@code strWithNamedPlaceholders} by values from {@code placeholdersValues}
     * map.
     *
     * <pre>
     * {@code
     * String templateWithNamedPlaceholder = "few ${placeholder} words ${other_placeholder}";
     * Map<String, Object> params = Map.ofEntries(
     *         Map.entry("placeholder", "value of this placeholder"),
     *         Map.entry("other_placeholder", 123));
     * }
     * String result = NamedFormatter.format(templateWithNamedPlaceholder, params);
     * // result : few value of this placeholder words 123
     * </pre>
     *
     * @param strWithNamedPlaceholders string containing named placeholders
     * @param placeholdersValues       map with values for placeholders
     * @return formatted string
     */
    public static String format(String strWithNamedPlaceholders,
            Map<String, Object> placeholdersValues) {
        var standartTemplate = new StringBuilder(strWithNamedPlaceholders);
        var standartTemplateValues = new ArrayList<Object>();

        Matcher matcher = PATTERN.matcher(strWithNamedPlaceholders);

        while (matcher.find()) {
            String placeholderName = matcher.group(1);
            String placeholder = "${" + placeholderName + '}';
            int placeholderIdx = standartTemplate.indexOf(placeholder);
            if (placeholderIdx != -1) {
                standartTemplate.replace(placeholderIdx, placeholderIdx + placeholder.length(), "%s");
                standartTemplateValues.add(placeholdersValues.get(placeholderName));
            }
        }

        return standartTemplate.toString().formatted(standartTemplateValues.toArray());
    }

    private NamedFormatter() {
    }
}
