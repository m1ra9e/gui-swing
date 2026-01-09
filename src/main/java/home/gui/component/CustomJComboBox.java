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
package home.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.util.EnumSet;

import javax.swing.JComboBox;

import home.db.DbType;

@SuppressWarnings("serial")
public final class CustomJComboBox extends JComboBox<String> {

    private static final String FONT_NAME = "Courier";
    private static final int FONT_SIZE = 14;

    private CustomJComboBox(String[] items) {
        super(items);
    }

    public static CustomJComboBox create(EnumSet<DbType> dbTypes) {
        var comboBox = new CustomJComboBox(dbTypes.stream()
                .map(type -> type.name()).toArray(String[]::new));
        comboBox.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        comboBox.setForeground(Color.BLACK);
        return comboBox;
    }
}
