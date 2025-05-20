/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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
package home.gui.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;

import home.gui.component.CustomJFileChooserImpExp;
import home.gui.component.CustomJFileChooserImpExp.DataFormat;
import home.utils.LogUtils;

public final class ExportImportActionListener implements ActionListener {

    private final DataFormat dataFomat;
    private final boolean isImport;
    private final Component parent;
    private final Logger log;

    public ExportImportActionListener(DataFormat dataFormat,
            boolean isImport, Component parent, Logger log) {
        this.dataFomat = dataFormat;
        this.isImport = isImport;
        this.parent = parent;
        this.log = log;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            CustomJFileChooserImpExp.createAndShowChooser(parent, dataFomat, isImport);
        } catch (Exception e) {
            LogUtils.logAndShowError(log, parent, e.getMessage(), "Export/Import error", e);
        }
    }
}