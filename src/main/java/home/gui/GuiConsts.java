package home.gui;

import java.text.SimpleDateFormat;

public interface GuiConsts {

    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd | HH:mm:ss");

    // table column names
    String TYPE = "Type";
    String COLOR = "Color";
    String NUMBER = "Number";
    String DATE = "Date";
    String DELETION_MARK = "Deletion mark";

    // button names
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
    String CREATE_OR_OPEN = "Create or Open";
    String SAVE = "Save";
    String STYLE = "Style";
    String DEFAULT = "Default";
    String SYSTEM = "System";
    String HELP = "Help";
    String ABOUT = "About";

    // About dialog text
    String ABOUT_TITLE = "About";
    String ABOUT_TEXT = "Test application.";

    // DB label
    String CHOOSE_DB_FILE = "Choose SQLite DB file via [File] -> [Open]";
}
