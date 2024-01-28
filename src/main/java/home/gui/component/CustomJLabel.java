package home.gui.component;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public final class CustomJLabel extends JLabel {

    private static final String FONT_NAME = "Courier";
    private static final int FONT_SIZE = 14;

    private CustomJLabel(String text) {
        super(text);
    }

    public static CustomJLabel create(String text) {
        var label = new CustomJLabel(text);
        label.setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE));
        label.setForeground(Color.BLACK);
        return label;
    }
}
