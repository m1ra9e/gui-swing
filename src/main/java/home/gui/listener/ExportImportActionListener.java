package home.gui.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;

import home.gui.component.CustomJFileChooserImpExp;
import home.gui.component.CustomJFileChooserImpExp.DataFormat;
import home.utils.ThreadUtil;
import home.utils.LogUtils;

public final class ExportImportActionListener implements ActionListener {

    private final DataFormat dataFomat;
    private final boolean isImport;
    private final Component parent;
    private final Logger log;

    public ExportImportActionListener(DataFormat dataFormat,
            boolean isImport, Component parent, Logger log) {
        this.dataFomat = dataFormat;
        this.isImport = isImport;
        this.parent = parent;
        this.log = log;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ThreadUtil.runInThread(() -> {
            Thread.currentThread().setName("-> export/import operation");
            try {
                CustomJFileChooserImpExp.createAndShowChooser(parent, dataFomat, isImport);
            } catch (Exception e) {
                LogUtils.logAndShowError(log, parent, e.getMessage(), "Export/Import error", e);
            }
        });
    }
}