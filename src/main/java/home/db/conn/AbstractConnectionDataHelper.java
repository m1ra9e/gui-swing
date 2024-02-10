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
package home.db.conn;

import java.util.Properties;

import home.db.DbType;

abstract sealed class AbstractConnectionDataHelper permits PgConnectionDataHelper, SQLiteConnectionDataHelper {

    ConnectionData getConnectionData() {
        String url = getUrl();
        Properties connProps = getConnectionProps();
        DbType dbType = getDbType();
        return new ConnectionData(url, connProps, dbType.getJdbcDriver());
    }

    protected abstract DbType getDbType();

    protected abstract String getUrl();

    protected abstract Properties getConnectionProps();
}
