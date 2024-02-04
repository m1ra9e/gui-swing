package home.gui.component;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import home.file.FileHandler;
import home.file.csv.CsvExporter;
import home.file.csv.CsvImporter;
import home.file.json_yaml.JsonExporter;
import home.file.json_yaml.JsonImporter;
import home.file.json_yaml.YamlExporter;
import home.file.json_yaml.YamlImporter;
import home.file.ser.BserExporter;
import home.file.ser.BserImporter;
import home.file.ser.SerExporter;
import home.file.ser.SerImporter;
import home.file.xml.XmlExporter;
import home.file.xml.XmlImporter;
import home.gui.DataActionInGui;
import home.model.AbstractVehicle;

@SuppressWarnings("serial")
public final class CustomJFileChooserImpExp extends JFileChooser {

    public static enum DataFormat {

        XML("xml", "XML (*.xml)"),
        YAML("yaml", "YAML (*.yaml)"),
        JSON("json", "JSON (*.json)"),
        CSV("csv", "CSV (*.csv)"),
        BSER("bser", "BSER (*.bser)"),
        SER("ser", "SER (*.ser)");

        private final String extension;
        private final String extensionDescription;

        private DataFormat(String extension, String extensionDescription) {
            this.extension = extension;
            this.extensionDescription = extensionDescription;
        }

        public String getExtension() {
            return extension;
        }

        public String getExtensionDescription() {
            return extensionDescription;
        }
    }

    private static final File APPLICATION_DIR = new File(".");

    private CustomJFileChooserImpExp() {
        super(APPLICATION_DIR);
    }

    public static void createAndShowChooser(Component parent, DataFormat oper,
            boolean isImport) throws IOException {
        var fileChooser = new CustomJFileChooserImpExp();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                oper.extensionDescription, new String[] { oper.extension }));
        fileChooser.showChooser(parent, oper, isImport);
    }

    private void showChooser(Component parent, DataFormat oper,
            boolean isImport) throws IOException {
        String direction = (isImport ? "Import from " : "Export to ") + oper.name();

        int chooserState = showDialog(parent, direction);
        if (JFileChooser.APPROVE_OPTION != chooserState) {
            return;
        }

        File file = getSelectedFile();
        file = addExtensionToFileIfNotExists(file, oper.getExtension());

        if (isImport) {
            List<AbstractVehicle> dataObjs = switch (oper) {
                case XML -> XmlImporter.importDataObjsFromFile(file);
                case YAML -> YamlImporter.importDataObjsFromFile(file);
                case JSON -> JsonImporter.importDataObjsFromFile(file);
                case CSV -> CsvImporter.importDataObjsFromFile(file);
                case BSER -> BserImporter.importDataObjsFromFile(file);
                case SER -> SerImporter.importDataObjsFromFile(file);
            };
            DataActionInGui.add(dataObjs);
        } else {
            String text = switch (oper) {
                case XML -> XmlExporter.exportAllDataObjsToString();
                case YAML -> YamlExporter.exportAllDataObjsToString();
                case JSON -> JsonExporter.exportAllDataObjsToString();
                case CSV -> CsvExporter.exportAllDataObjsToString();
                case BSER -> BserExporter.exportAllDataObjsToString();
                case SER -> {
                    SerExporter.exportAllDataObjsToFile(file);
                    yield null;
                }
            };

            if (text != null) {
                FileHandler.writeStringToFile(file, text);
            }
        }

        JOptionPane.showMessageDialog(parent, direction + " successfully",
                direction, JOptionPane.INFORMATION_MESSAGE);
    }

    private File addExtensionToFileIfNotExists(File file, String extension) {
        String extensionWithDot = '.' + extension;
        return file.getName().endsWith(extensionWithDot)
                ? file
                : new File(file.getAbsolutePath() + extensionWithDot);
    }
}
