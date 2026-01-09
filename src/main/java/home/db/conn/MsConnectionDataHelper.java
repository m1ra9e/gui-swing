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
package home.db.conn;

import java.util.Properties;

import home.db.DbType;

// TODO add support for MS SQL Server
final class MsConnectionDataHelper { // extends AbstractConnectionDataHelper

    private static String QUERY_TIMEOUT = "15"; // sec
    private static String LOCK_TIMEOUT = "10000"; // ms

    private final String host;
    private final int port; // default port usually is 1433
    private final String dbName;
    private final String user;
    private final String pass;

    MsConnectionDataHelper(String host, int port, String dbName,
            String user, String pass) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.pass = pass;
    }

    // @Override
    protected DbType getDbType() {
        // DbType.MS_SQL_Server
        return null;
    }

    // @Override
    protected String getUrl() {
        return getDbType().getUrl(host, port, dbName);
    }

    // @Override
    protected Properties getConnectionProps() {
        var props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", pass);
        props.setProperty("trustServerCertificate", "true");
        props.setProperty("queryTimeout", QUERY_TIMEOUT);
        props.setProperty("lockTimeout", LOCK_TIMEOUT);
        return props;
    }
}
