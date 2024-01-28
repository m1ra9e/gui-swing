package home.gui;

import java.time.format.DateTimeFormatter;

public interface IGuiConsts {

    String DATE_FORMAT = "yyyy.MM.dd | HH:mm:ss";
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

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
    String STYLE = "Style";
    String HELP = "Help";
    String ABOUT = "About";

    // About dialog text
    String ABOUT_TITLE = "About";
    String ABOUT_TEXT = "Test application.\nVersion: %s";

    // Save dialog text
    String SAVE_TITLE = "Saving";
    String SAVE_TEXT = "Saved successfully";

    // DB label
    String CHOOSE_DB_FILE = "Choose SQLite DB file via [File] -> [Open]";
}
