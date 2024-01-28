package home.gui.component;

import javax.swing.JButton;

@SuppressWarnings("serial")
public final class CustomJButton extends JButton {

    private CustomJButton() {
    }

    public static CustomJButton create(String title) {
        var button = new CustomJButton();
        button.setText(title);
        return button;
    }
}
