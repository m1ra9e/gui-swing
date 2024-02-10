/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
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
package home.gui.component.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
abstract sealed class AbstractCustomJDialog
        extends JDialog permits AbstractDialogVehicle, DialogDbConnection {

    private static final int GAP_BETWEEN_COMPONENTS = 2;

    private static final String CLOSE_DIALOG_ACTION = "closeDialogAction";

    private final String title;
    private final int width;
    private final int height;

    protected AbstractCustomJDialog(String title, int width, int height) {
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
                        actionOnHotKeyForClose();
                    }
                });
    }

    protected void actionOnHotKeyForClose() {
        dispose();
    }

    /**
     * Creating and displaying a dialog. When launched via
     * "SwingUtilities.invokeLater(new Runnable(){...}" the dialog will be created
     * and displayed after all expected events have been processed, i.e. the dialog
     * will be created and displayed when all resources are ready. This is
     * necessary, so that all elements are guaranteed to be displayed in the window
     * (if you do "setVisible(true)" from the main thread, then there is a chance
     * that some element will not be displayed in the window).
     */
    protected void makeDialogVisible() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
