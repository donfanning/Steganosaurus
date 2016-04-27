package app.steganosaurus.Utility;

import android.graphics.Bitmap;

public class DecryptObject {
    Const.DataType dataType;
    Bitmap decryptedBitmap;
    String decryptedString;

    DecryptObject(Bitmap b){
        decryptedBitmap = b;
        dataType = Const.DataType.PHOTO;
    }

    DecryptObject(String s){
        decryptedString = s;
        dataType = Const.DataType.TEXT;
    }

    public Const.DataType GetType() { return dataType; }
    public Bitmap GetBitmap() { return decryptedBitmap; }
    public String GetString() { return decryptedString; }
}
