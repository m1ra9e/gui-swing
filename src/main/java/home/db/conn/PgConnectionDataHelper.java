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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.db.DbType;

final class PgConnectionDataHelper extends AbstractConnectionDataHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PgConnectionDataHelper.class);

    private static final String TIMEOUT = "30"; // sec
    private static String QUERY_TIMEOUT = "15000"; // ms // without final-modifier because of test
    private static String LOCK_TIMEOUT = "10000"; // ms // without final-modifier because of test

    private final String host;
    private final int port; // default port usually is 5432
    private final String dbName;
    private final String user;
    private final String pass;

    PgConnectionDataHelper(String host, int port, String dbName,
            String user, String pass) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.pass = pass;
    }

    @Override
    protected DbType getDbType() {
        return DbType.PostgreSQL;
    }

    @Override
    protected String getUrl() {
        String db;
        try {
            db = URLEncoder.encode(dbName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Encoding error of database name.", ex);
            db = dbName;
        }
        return getDbType().getUrl(host, port, db);
    }

    @Override
    protected Properties getConnectionProps() {
        var props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", pass);
        props.setProperty("reWriteBatchedInserts", "true");
        props.setProperty("loginTimeout", TIMEOUT);
        props.setProperty("connectTimeout", TIMEOUT);
        props.setProperty("cancelSignalTimeout", TIMEOUT);
        props.setProperty("socketTimeout", TIMEOUT);
        props.setProperty("options", "-c statement_timeout=%s -c lock_timeout=%s"
                .formatted(QUERY_TIMEOUT, LOCK_TIMEOUT));
        return props;
    }
}
