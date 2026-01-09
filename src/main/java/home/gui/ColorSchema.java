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

import javax.swing.UIManager;

public enum ColorSchema {

    // !!! "equalsIgnoreCase" between enum constant and nameForGui must be true
    CROSSPLATFORM("CrossPlatform", UIManager.getCrossPlatformLookAndFeelClassName()),
    GTK("GTK", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
    MAC("MAC", "com.sun.java.swing.plaf.mac.MacLookAndFeel"),
    METAL("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"),
    MOTIF("Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
    NIMBUS("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel"),
    SYSTEM("System", UIManager.getSystemLookAndFeelClassName());

    private final String nameForGui;
    private final String lookAndFeelClassName;

    private ColorSchema(String guiName, String lookAndFeelClassName) {
        this.nameForGui = guiName;
        this.lookAndFeelClassName = lookAndFeelClassName;
    }

    public String getNameForGui() {
        return nameForGui;
    }

    public String getLookAndFeelClassName() {
        return lookAndFeelClassName;
    }

    public static ColorSchema getColorSchema(String colorSchemaName) {
        String colorSchemaFormattedName = colorSchemaName.strip();
        for (ColorSchema colorSchema : ColorSchema.values()) {
            if (colorSchemaFormattedName.equalsIgnoreCase(colorSchema.name())) {
                return colorSchema;
            }
        }

        throw new IllegalArgumentException("Incorrect style name : " + colorSchemaName);
    }
}
