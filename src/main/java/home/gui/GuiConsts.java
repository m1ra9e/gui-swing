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
    String SAVE = "Save";
    String CANCEL = "Cancel";
    String HAS_TRAILER = "has trailer";
    String TRANSPORTS_PASSENGERS = "transports passengers";
    String TRANSPORTS_CARGO = "transports cargo";
    String HAS_CRADLE = "has cradle";
}
