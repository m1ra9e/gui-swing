package home.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public abstract class CustomJDialog extends JDialog {

    private static final int GAP_BETWEEN_COMPONENTS = 2;

    private static final String CLOSE_DIALOG_ACTION = "closeDialogAction";

    private final String title;
    private final int width;
    private final int height;

    protected CustomJDialog(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    protected void init() {
        setTitle(title);
        setSize(width, height);
        setMinimumSize(new Dimension(width, height));
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(GAP_BETWEEN_COMPONENTS,
                GAP_BETWEEN_COMPONENTS));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addHotKeyForClose();
    }

    private void addHotKeyForClose() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE_DIALOG_ACTION);

        getRootPane().getActionMap()
                .put(CLOSE_DIALOG_ACTION, new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
    }
}
