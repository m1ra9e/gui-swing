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
package home.db.dao;

import java.sql.SQLException;
import java.util.List;

import home.model.AbstractVehicle;

sealed interface IDao permits AbstractDao {

    List<AbstractVehicle> readAll() throws SQLException;

    @Deprecated(forRemoval = true) // because it uses only in test
    AbstractVehicle readOne(long id) throws SQLException;

    void saveAllChanges() throws SQLException;

    void saveAs() throws SQLException;
}
