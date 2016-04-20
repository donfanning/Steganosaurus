package app.steganosaurus.Utility;

import android.graphics.Bitmap;

/**
 * Created by Alexis on 2016-04-19.
 */
public class DecryptObject {
    int dataType;
    Bitmap decryptedBitmap;
    String decryptedString;

    DecryptObject(Bitmap b){
        decryptedBitmap = b;
        dataType = Const.DataType.PHOTO_VALUE;
    }

    DecryptObject(String s){
        decryptedString = s;
        dataType = Const.DataType.TEXT_VALUE;
    }

    public int GetType() { return dataType; }
    public Bitmap GetBitmap() { return decryptedBitmap; }
    public String GetString() { return decryptedString; }
}
