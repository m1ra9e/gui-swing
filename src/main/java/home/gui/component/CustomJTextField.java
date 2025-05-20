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
package home.gui.component;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public final class CustomJTextField extends JTextField {

    private static final String FONT_NAME = "Courier";
    private static final int FONT_SIZE = 14;

    private CustomJTextField() {
    }

    public static CustomJTextField create(int columns) {
        var textFiled = new CustomJTextField();
        textFiled.setColumns(columns);
        textFiled.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        textFiled.setForeground(Color.BLACK);
        return textFiled;
    }
}
