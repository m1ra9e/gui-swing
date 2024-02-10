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
package home.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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

    /**
     * Creating and displaying a frame. When launched via
     * "SwingUtilities.invokeLater(new Runnable(){...}" the frame will be created
     * and displayed after all expected events have been processed, i.e. the frame
     * will be created and displayed when all resources are ready. This is
     * necessary, so that all elements are guaranteed to be displayed in the window
     * (if you do "frame.setVisible(true)" from the main thread, then there is a
     * chance that some element will not be displayed in the window).
     */
    public void makeFrameVisible() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}