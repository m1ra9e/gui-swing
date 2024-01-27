package home.gui.component;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class CustomJLabel extends JLabel {

    private static final String FONT_NAME = "Courier";
    private static final int FONT_SIZE = 14;

    public CustomJLabel(String text) {
        super(text);
        setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE));
        setForeground(Color.BLACK);
    }
}
