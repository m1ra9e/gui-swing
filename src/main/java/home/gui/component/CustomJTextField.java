package home.gui.component;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CustomJTextField extends JTextField {

    private static final String FONT_NAME = "Courier";
    private static final int FONT_SIZE = 14;

    public CustomJTextField(int columns) {
        setColumns(columns);
        setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        setForeground(Color.BLACK);
    }
}
