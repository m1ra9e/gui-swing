/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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
 *
 * Copyright 2012 Charlie Hubbard
 *
 * Distributed under LGPLv3. License can be found here:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * This is provided as is. If you have questions please direct them to
 * charlie.hubbard at gmail dot you know what.
 *******************************************************************************/
package home.gui.component;

import java.awt.Color;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;

import home.gui.GuiConst;

@SuppressWarnings("serial")
public final class CustomJXDatePicker extends JXDatePicker {

    private JSpinner timeSpinner;
    private JPanel timePanel;
    private DateFormat timeFormat;

    private CustomJXDatePicker() {
    }

    public static CustomJXDatePicker creat(Date date) {
        var datePicker = new CustomJXDatePicker();
        datePicker.getMonthView().setSelectionModel(new SingleDaySelectionModel());
        datePicker.setFormats(GuiConst.DATE_FORMAT);
        datePicker.setTimeFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
        datePicker.setDate(date);
        return datePicker;
    }

    @Override
    public void commitEdit() throws ParseException {
        commitTime();
        super.commitEdit();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setTimeSpinners();
    }

    @Override
    public JPanel getLinkPanel() {
        super.getLinkPanel();
        if (timePanel == null) {
            timePanel = createTimePanel();
        }
        setTimeSpinners();
        return timePanel;
    }

    private JPanel createTimePanel() {
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new FlowLayout());
        // newPanel.add(panelOriginal);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(dateModel);
        if (timeFormat == null) {
            timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        }
        updateTextFieldFormat();
        newPanel.add(new JLabel("Time:"));
        newPanel.add(timeSpinner);
        newPanel.setBackground(Color.WHITE);
        return newPanel;
    }

    private void updateTextFieldFormat() {
        if (timeSpinner == null) {
            return;
        }
        JFormattedTextField tf = ((JSpinner.DefaultEditor) timeSpinner.getEditor()).getTextField();
        DefaultFormatterFactory factory = (DefaultFormatterFactory) tf.getFormatterFactory();
        DateFormatter formatter = (DateFormatter) factory.getDefaultFormatter();
        // Change the date format to only show the hours
        formatter.setFormat(timeFormat);
    }

    private void commitTime() {
        Date date = getDate();
        if (date != null) {
            Date time = (Date) timeSpinner.getValue();
            GregorianCalendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(time);

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date newDate = calendar.getTime();
            setDate(newDate);
        }
    }

    private void setTimeSpinners() {
        Date date = getDate();
        if (date != null) {
            timeSpinner.setValue(date);
        }
    }

    public void setTimeFormat(DateFormat timeFormat) {
        this.timeFormat = timeFormat;
        updateTextFieldFormat();
    }
}
