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
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public final class CustomJCheckBox extends JCheckBox {

    private static final String FONT_NAME = "Courier";
    private static final int FONT_SIZE = 14;
    private static final boolean IS_SELECTED = false;

    private CustomJCheckBox() {
    }

    public static CustomJCheckBox create(String text, ItemListener itemListener) {
        var checkBox = new CustomJCheckBox();
        checkBox.setText(text);
        checkBox.setSelected(IS_SELECTED);
        checkBox.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        checkBox.setForeground(Color.BLACK);
        checkBox.addItemListener(itemListener);
        return checkBox;
    }
}
