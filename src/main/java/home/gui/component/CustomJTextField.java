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
