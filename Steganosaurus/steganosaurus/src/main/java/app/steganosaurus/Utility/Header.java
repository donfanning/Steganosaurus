package app.steganosaurus.Utility;
import android.util.Log;

import java.util.*;

/**
 * Created by utilisateur on 2016-04-14.
 */
public class Header {

    public Const.DataType dataType;
    public int noBytesToDecrypt = 0;
    public int bitPerBytes = 0;

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

        //Type
        Byte[] rawType = Steganograph.GetSubArrayOfByteArray(data, 0, 4);
        int type = Steganograph.getIntFromBytes(rawType);
        switch (type) {
            case Const.DataType.PHOTO_VALUE:
                dataType = Const.DataType.PHOTO;
                break;
            case Const.DataType.TEXT_VALUE:
                dataType = Const.DataType.TEXT;
                break;
            case Const.DataType.SOUND_VALUE:
                dataType = Const.DataType.SOUND;
                break;
            default:
                Log.v("ERROR : ", "Data type with ID " + type + " Does not exist");
        }
        RemoveAtStart(data, 4);

        //Number of byte to decrypt
        Byte[] rawByteToDecrypt = Steganograph.GetSubArrayOfByteArray(data, 0, 4);
        noBytesToDecrypt = Steganograph.getIntFromBytes(rawByteToDecrypt);
        RemoveAtStart(data, 4);

        //Bit per Bytes
        Byte[] rawBitPerByte = Steganograph.GetSubArrayOfByteArray(data, 0, 4);
        bitPerBytes = Steganograph.getIntFromBytes(rawBitPerByte);
        RemoveAtStart(data, 4);

        return data.toArray(new Byte[data.size()]);
    }


    /**
     * Encode Header inside the encoded data
     * @param type The type of data
     * @param noBytesToDecrypt The number of bytes to decrypt
     * @param bitPerBytes The number of modified bit per bytes
     * @param encodedData The encoded data without the header
     * @return
     */
    public static Byte[] EncodeHeader(Const.DataType type, int noBytesToDecrypt, int bitPerBytes, byte[] encodedData) {
        List<Byte> header = new ArrayList<Byte>();

        //Verification Symbols
        header.add((byte)'@');
        header.add((byte)'%');

        //Type
        switch (type) {
            case PHOTO:
                header.addAll(DataTypeToByte(Const.DataType.PHOTO));
                break;
            case TEXT:
                header.addAll(DataTypeToByte(Const.DataType.TEXT));
                break;
            case SOUND:
                header.addAll(DataTypeToByte(Const.DataType.SOUND));
                break;
        }

        //Number of byte to decrypt
        header.addAll(IntToByte(noBytesToDecrypt));

        //Bit per bytes
        header.addAll(IntToByte(bitPerBytes));

        //Add header to encoded data
        byte[] rawHeader = Steganograph.toPrimitives(header.toArray(new Byte[header.size()]));
        encodedData = Steganograph.concatByteArray(rawHeader, encodedData);

        return Steganograph.CastPrimitiveByteToByteWrapper(encodedData);
    }

    private List<Byte> RemoveAtStart(List<Byte> data, int bytesToRemove){
        for (int i=0; i < bytesToRemove; ++i) {
            data.remove(0);
        }
        return data;
    }

    private static List<Byte> DataTypeToByte(Const.DataType type) {
        return Arrays.asList(
            Steganograph.CastPrimitiveByteToByteWrapper(
                Steganograph.getBytesFromInt(
                    type.getValue()
                )
            )
        );
    }

    private static List<Byte> IntToByte(int data) {
        return Arrays.asList(
            Steganograph.CastPrimitiveByteToByteWrapper(
                Steganograph.getBytesFromInt(
                        data
                )
            )
        );
    }

    /**
     * Check if the data was encoded by
     * @param data
     * @return
     */
    private List<Byte> CheckBytesValidity(List<Byte> data) {
        if (data.get(0) == '@' && data.get(1) == '%') {
            data = RemoveAtStart(data, 2);
            return data;
        }
        return null;
    }
}
