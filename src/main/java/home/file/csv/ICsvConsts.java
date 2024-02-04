package home.file.csv;

import home.file.Tag;

interface ICsvConsts {

    int TYPE_IDX = 0;
    int COLOR_IDX = 1;
    int NUMBER_IDX = 2;
    int DATE_IDX = 3;
    int HAS_TRAILER_IDX = 4;
    int IS_TRANSPORTS_PASSENGERS_IDX = 5;
    int IS_TRANSPORTS_CARGO_IDX = 6;
    int HAS_CRADLE_IDX = 7;

    String[] CSV_HEADER = {
            Tag.TYPE.getTagName(),
            Tag.COLOR.getTagName(),
            Tag.NUMBER.getTagName(),
            Tag.DATE.getTagName(),
            Tag.HAS_TRAILER.getTagName(),
            Tag.IS_TRANSPORTS_PASSENGERS.getTagName(),
            Tag.IS_TRANSPORTS_CARGO.getTagName(),
            Tag.HAS_CRADLE.getTagName()
    };

    int CSV_ROW_SIZE = CSV_HEADER.length;
}
