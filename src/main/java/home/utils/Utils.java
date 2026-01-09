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

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

import home.Settings;
import home.db.DbType;
import home.gui.GuiConst;

public final class Utils {

    public static String getFormattedDate(long dateTimeInMilliseconds) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dateTimeInMilliseconds),
                TimeZone.getDefault().toZoneId());
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(GuiConst.DATE_FORMAT, Locale.ROOT);
        return dateTime.format(dateFormatter);
    }

    public static long getLongFromFormattedDate(String formattedDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(GuiConst.DATE_FORMAT, Locale.ROOT);
        long millisecondsSinceEpoch = LocalDateTime.parse(formattedDate, dateFormatter)
                .atZone(TimeZone.getDefault().toZoneId())
                .toInstant().toEpochMilli();
        return millisecondsSinceEpoch;
    }

    public static String generateDbDescription() {
        try {
            if (Settings.hasDatabase()) {
                return Settings.getDatabaseType() == DbType.SQLite ? Settings.getDatabase()
                        : GuiConst.DB_LABEL_FORMAT.formatted(
                                Settings.getDatabaseType().name().toLowerCase(Locale.ROOT),
                                Settings.getHost(),
                                Settings.getPort(),
                                Settings.getDatabase());
            } else {
                return GuiConst.DATABASE_NOT_SELECTED;
            }
        } catch (SQLException e) {
            return GuiConst.DATABASE_NOT_SELECTED;
        }
    }

    private Utils() {
    }
}
