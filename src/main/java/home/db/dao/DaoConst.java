/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
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

import java.util.Map;

final class DaoConst {

    // Table name
    private static final String DATA_TABLE = "vehicle";

    // The names of the columns in the table
    static final String ID = "id";
    static final String TYPE = "type";
    static final String COLOR = "color";
    static final String NUMBER = "number";
    static final String DATE_TIME = "date_time";
    static final String IS_TRANSPORTS_CARGO = "is_transports_cargo";
    static final String IS_TRANSPORTS_PASSENGERS = "is_transports_passengers";
    static final String HAS_TRAILER = "has_trailer";
    static final String HAS_CRADLE = "has_cradle";

    // Map with values of placeholders for using in NamedFormatter
    // (key - is placeholder, value - is value for placeholder)
    static final Map<String, Object> PLACEHOLDER_VALUES = Map.ofEntries(
            // Table name
            Map.entry("table_name", DATA_TABLE),
            // The names of the columns in the table
            Map.entry("col_id", ID),
            Map.entry("col_type", TYPE),
            Map.entry("col_color", COLOR),
            Map.entry("col_number", NUMBER),
            Map.entry("col_date_time", DATE_TIME),
            Map.entry("col_is_transports_cargo", IS_TRANSPORTS_CARGO),
            Map.entry("col_is_transports_passengers", IS_TRANSPORTS_PASSENGERS),
            Map.entry("col_has_trailer", HAS_TRAILER),
            Map.entry("col_has_cradle", HAS_CRADLE));

    private DaoConst() {
    }
}
