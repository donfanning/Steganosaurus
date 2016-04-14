package app.steganosaurus.Utility;

/**
 * a simple class acting as an Enum for request codes
 */
public class Const {

    public static final int ENCRYPT_CODE = 1;
    public static final int DECRYPT_CODE = 2;

    public static final int PICK_SOURCE_IMAGE_REQUEST = 3;
    public static final int PICK_HIDDEN_IMAGE_REQUEST = 4;

    public static final int PICK_DECRYPT_IMAGE_REQUEST = 5;

    public static final int REQUEST_SOURCE_IMAGE_CAPTURE = 6;
    public static final int REQUEST_HIDDEN_IMAGE_CAPTURE = 7;

    public enum DataType {
        PHOTO(0),
        TEXT(1),
        SOUND(2);

        private final int id;
        DataType(int id) {this.id = id;}
        public int getValue() {return id;}
        public static final int PHOTO_VALUE = 0;
        public static final int TEXT_VALUE = 1;
        public static final int SOUND_VALUE = 2;
    }
}
