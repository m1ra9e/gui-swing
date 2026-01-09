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
package home.file;

public enum Tag {

    COLOR("color"),
    DATE("date"),
    HAS_CRADLE("has_cradle"),
    HAS_TRAILER("has_trailer"),
    IS_TRANSPORTS_CARGO("is_transports_cargo"),
    IS_TRANSPORTS_PASSENGERS("is_transports_passengers"),
    NUMBER("number"),
    TYPE("type"),
    VEHICLE("vehicle"),
    VEHICLES("vehicles");

    private final String tagName;

    private Tag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public static Tag getTag(String tagName, String errorMsg) {
        String tagFormatted = tagName.strip();
        for (Tag tag : Tag.values()) {
            if (tagFormatted.equals(tag.getTagName())) {
                return tag;
            }
        }

        throw new IllegalArgumentException(errorMsg.formatted(tagName));
    }
}
