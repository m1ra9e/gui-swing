package home.gui;

public interface IGuiConsts {

    String DATE_FORMAT = "yyyy.MM.dd | HH:mm:ss";

    // Table column names
    String TYPE = "Type";
    String COLOR = "Color";
    String NUMBER = "Number";
    String DATE = "Date";
    String DELETION_MARK = "Deletion mark";

    // Button names
    String CAR = "Car";
    String TRUCK = "Truck";
    String MOTORCYCLE = "Motorcycle";
    String DELETE = "Delete";
    String OK = "Ok";
    String CANCEL = "Cancel";
    String HAS_TRAILER = "has trailer";
    String TRANSPORTS_PASSENGERS = "transports passengers";
    String TRANSPORTS_CARGO = "transports cargo";
    String HAS_CRADLE = "has cradle";

    // Menu names
    String FILE = "File";
    String CREATE_OR_OPEN = "Create / Open";
    String SAVE = "Save";
    String SAVE_AS = "Save as ...";
    String IMPORT_FROM = "Import from ...";
    String EXPORT_TO = "Export to ...";
    String STYLE = "Style";
    String HELP = "Help";
    String ABOUT = "About";

    // About dialog text
    String ABOUT_TITLE = "About";
    String ABOUT_TEXT = "Test application.\nVersion: %s";

    // Save dialog text
    String SAVE_TITLE = "Saving";
    String SAVE_TEXT = "Saved successfully";

    // Export/Import dialog text
    String EXPORT_TITLE = "Exporting";
    String EXPORT_TEXT = "Exported successfully";
    String IMPORT_TITLE = "Importing";
    String IMPORT_TEXT = "Imported successfully";

    // DB label
    String CHOOSE_DB_FILE = "Choose SQLite DB file via [File] -> [Open]";
}
