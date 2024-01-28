package home.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class CustomJFrame extends JFrame {

    private static final int GAP = 2;

    private static final int PREF_WIDTH = 650;
    private static final int PREF_HEIGHT = 500;

    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 400;

    private CustomJFrame(String title) {
        super(title);
    }

    public static CustomJFrame create(String title) {
        var frame = new CustomJFrame(title);
        frame.setSize(PREF_WIDTH, PREF_HEIGHT);
        frame.setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
        frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(GAP, GAP));
        return frame;
    }
}