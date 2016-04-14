package app.steganosaurus.Utility;
import android.util.Log;

import java.util.*;

/**
 * Created by utilisateur on 2016-04-14.
 */
public class Header {


    /**
     * Decodes the header data from the received bytes
     * @param rawData The raw data in an array of bytes
     * @return The bytes without the header or null if the data is invalid
     */
    public Byte[] DecodeHeader(Byte[] rawData) {
        List<Byte> data = Arrays.asList(rawData);
        if (CheckBytesValidity(data) == null) {
            Log.v("Debug : ", "The photo that is being decoded was not encoded prior to this");
            return null;
        }



        return data.toArray(new Byte[data.size()]);
    }

    public static Byte[] EncodeHeader(int dataType, int noBytesToDecrypt, int bitPerBytes) {
        
    }

    /**
     * Check if the data was encoded by
     * @param data
     * @return
     */
    private List<Byte> CheckBytesValidity(List<Byte> data) {
        if (data.get(0) == '@' && data.get(1) == '%') {
            for (int i = 0; i < 2; ++i){
                data.remove(0);
            }
            return data;
        }
        return null;
    }
}
