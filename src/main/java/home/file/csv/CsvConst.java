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
package home.file.csv;

import home.file.Tag;

final class CsvConst {

    static final int TYPE_IDX = 0;
    static final int COLOR_IDX = 1;
    static final int NUMBER_IDX = 2;
    static final int DATE_IDX = 3;
    static final int HAS_TRAILER_IDX = 4;
    static final int IS_TRANSPORTS_PASSENGERS_IDX = 5;
    static final int IS_TRANSPORTS_CARGO_IDX = 6;
    static final int HAS_CRADLE_IDX = 7;

    static final String[] CSV_HEADER = {
            Tag.TYPE.getTagName(),
            Tag.COLOR.getTagName(),
            Tag.NUMBER.getTagName(),
            Tag.DATE.getTagName(),
            Tag.HAS_TRAILER.getTagName(),
            Tag.IS_TRANSPORTS_PASSENGERS.getTagName(),
            Tag.IS_TRANSPORTS_CARGO.getTagName(),
            Tag.HAS_CRADLE.getTagName()
    };

    static final int CSV_ROW_SIZE = CSV_HEADER.length;

    private CsvConst() {
    }
}
