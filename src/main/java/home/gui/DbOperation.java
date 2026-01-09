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
package home.gui;

public enum DbOperation {

    CREATE_OR_OPEN_FILE_DATABASE(GuiConst.CREATE_OR_OPEN_FILE_DATABASE),
    CONNECT_TO_SERVER_DATABASE(GuiConst.CONNECT_TO_SERVER_DATABASE),
    SAVE(GuiConst.SAVE),
    SAVE_AS(GuiConst.SAVE_AS);

    private final String operationText;

    private DbOperation(String operationText) {
        this.operationText = operationText;
    }

    public String getOperatioText() {
        return operationText;
    }
}
