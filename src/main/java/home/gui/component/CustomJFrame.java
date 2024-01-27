package home.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class CustomJFrame extends JFrame {

    private static final String TITLE = "VEHICLE ACCOUNTING";

    private static final int GAP_BETWEEN_COMPONENTS = 2;

    public static final int FRAME_PREF_WIDTH = 650;
    public static final int FRAME_PREF_HEIGHT = 500;

    public static final int FRAME_MIN_WIDTH = 400;
    public static final int FRAME_MIN_HEIGHT = 400;

    public CustomJFrame() {
        super(TITLE);
        setSize(FRAME_PREF_WIDTH, FRAME_PREF_HEIGHT);
        setPreferredSize(new Dimension(FRAME_PREF_WIDTH, FRAME_PREF_HEIGHT));
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(GAP_BETWEEN_COMPONENTS, GAP_BETWEEN_COMPONENTS));
    }
}