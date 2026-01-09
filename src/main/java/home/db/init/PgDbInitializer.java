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
package home.db.init;

final class PgDbInitializer extends AbstractDbInitializer {

    private static final String CREATE_TABLE_QUERY = """
            CREATE SEQUENCE IF NOT EXISTS public.vehicle_id_seq
                AS bigint
                START WITH 1
                INCREMENT BY 1
                NO MAXVALUE
                NO MINVALUE
                CACHE 1;

            CREATE TABLE IF NOT EXISTS public.vehicle (
                id bigint DEFAULT nextval('public.vehicle_id_seq'::regclass) NOT NULL,
                type character varying(50),
                color text,
                number character varying(50),
                is_transports_cargo integer,
                is_transports_passengers integer,
                has_trailer integer,
                has_cradle integer,
                date_time bigint,
                CONSTRAINT vehicle_pkey PRIMARY KEY (id)
            );
            """;

    @Override
    protected String getTableCreationQuery() {
        return CREATE_TABLE_QUERY;
    }
}
