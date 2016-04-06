package app.steganosaurus.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Class to make the actual steganography
 */
public class Steganograph {

    public final int bitPerByte = 4;

    /**
     * Pixel object used to easily modify and access rgb of pixels
     */
    protected class Pixel{
        public int R,G,B;
        Pixel(int r, int g, int b){
            R = r;
            G = g;
            B = b;
        }
        Pixel(){ R=0; G=0; B=0; }
    }

    /**
     * Encode a picture into another
     * @param destinationPicture bitmap to encrypt into
     * @param pictureToHide bitmap to hide in destination picture
     * @return
     */
    public Bitmap encodePicture(Bitmap destinationPicture, Bitmap pictureToHide) {
        //Create pixel arrays
        Pixel[] destinationPixel = getRGBFromBitmap(destinationPicture);
        byte[] toHideByte = getBytesFromBitmap(pictureToHide);
        //This is where the magic happens
        Pixel[] resultingPixels = encodePixels(destinationPixel, toHideByte);
        //Transform pixels into a new bitmap
        Bitmap resultingBitmap = getBitmapFromRGB(resultingPixels, destinationPicture.getWidth(), destinationPicture.getHeight());

        //Used to test decryption without going through save and load image process
        //resultingBitmap = decodePicture(resultingBitmap);

        return resultingBitmap;
    }

    /**
     * Get byte array from bitmap and create a bitmap with it
     * @param picture
     * @return
     */
    public Bitmap decodePicture(Bitmap picture) {

        byte[] retrievedData = getDataFromBitmap(picture);
        //TODO : This currently only return a bitmap. Later, we will want this to return the correct media

        return getBitmapFromBytes(retrievedData);
    }

    /**
     * Add Header and foodter to data and encode it into destination pixels
     * @param destPixels pixel array to encrypt into
     * @param data data to encrypt in bytes
     * @return
     */
    private Pixel[] encodePixels(Pixel[] destPixels, byte[] data){

        //TODO : Add a header which will be recognized when decrypting
        //Should include type of hidden data (picture, sound, text) and amount of bit on which the encoding is done
        int amtOfBytesToEncodeInto = data.length * (8/bitPerByte);
        byte[] header = getBytesFromInt(amtOfBytesToEncodeInto);
        byte[] headerWithData = concatByteArray(header, data);
        Log.w("debug : ", "HEADER IN ENCRYPT : ");
        LogByteArray(header); // log header for debug purposes
        Log.w("debug : ", "Encoding " + amtOfBytesToEncodeInto + " bytes with " + bitPerByte + " bit per bytes modified");

        //Encrypt data in destination pixels
        hideBytesInPixels(destPixels,bitPerByte, headerWithData);


        return destPixels;
    }

    /**
     * Randomize the pixels of the pixel array
     * @param destPixels pixels to encrypt into
     * @return
     */
    private Pixel[] randomizePixels(Pixel[] destPixels){
        Random rand = new Random();
        int min = -80;
        int max = 80;

        for (Pixel P:destPixels) {
            P.R += rand.nextInt((max - min) + 1) + min;
            //P.G += rand.nextInt((max - min) + 1) + min;
            //P.B += rand.nextInt((max - min) + 1) + min;

            P.R =  Math.min(255, Math.max(0, P.R));
            P.G =  Math.min(255, Math.max(0, P.G));
            P.B =  Math.min(255, Math.max(0, P.B));

        }

        return destPixels;
    }

    /**
     * Hide given bytes into the destination pixel array
     * @param destPixels Pixel array to store the message on
     * @param bitPerColor amount of bit per byte to modify.
     * @param message message in bytes to encore
     */
    private void hideBytesInPixels(Pixel[] destPixels,  int bitPerColor, byte[] message) {
        if(bitPerColor > 8) {
            Log.v("Error","Trying to encode on more than 8 bits");
        }

        int curBit = 0;
        int curPixel = 0;
        int curColor = 0; //0=red, 1=green, 2=blue




        for(int i =0; i<message.length; i++){

            for(int j=0; j<8; j++) {
                switch (curColor) {
                    case 0:
                        //Add 1
                        if (bitIsSet(message[i],j))
                            destPixels[curPixel].R = (byte) (destPixels[curPixel].R | (1 << (curBit)));
                        //Add 0
                        else
                            destPixels[curPixel].R = (byte) (destPixels[curPixel].R & ~(1 << (curBit)));
                        break;
                    case 1:
                        if (bitIsSet(message[i],j))
                            destPixels[curPixel].G = (byte) (destPixels[curPixel].G | (1 << (curBit)));
                        else
                            destPixels[curPixel].G = (byte) (destPixels[curPixel].G & ~(1 << (curBit)));
                        break;
                    case 2:
                        if (bitIsSet(message[i],j))
                            destPixels[curPixel].B = (byte) (destPixels[curPixel].B | (1 << (curBit)));
                        else
                            destPixels[curPixel].B = (byte) (destPixels[curPixel].B & ~(1 << (curBit)));
                        break;
                }

                curBit++;
                if (curBit >= bitPerColor) {
                    curBit = 0;
                    curColor++;
                    if (curColor >= 3) {
                        curPixel++;
                        curColor = 0;
                        if (curPixel >= destPixels.length) {
                            i = message.length; //Stop
                        }
                    }
                }
            }


        }

    }

    /* Object containing Values to separate header and body */
    protected class DecodingStatusObject {
        long amtOfBytesHeader = 0;
        long amtOfBytesBody  = 0;
        long currentByte = 0;
        boolean inHeader = true;
        int x = 0;
        int y = 0;
        int imgW  = 0;
        int imgH = 0;

        protected DecodingStatusObject() {
            x = 0;
            y = 0;
            long amtOfBytesBody  = 0;
            long currentByte = 0;
            boolean inHeader = true;
        }
    }

    /**
     * Get a bitmap and look into it to see if there is a hidden message in it. If so, return it.
     * @param picture to analyse
     * @return null if no message, otherwise message as byte array
     */
    private byte[] getDataFromBitmap(Bitmap picture){
        /* Values to separate header and body */
        DecodingStatusObject decodingStatusObject = new DecodingStatusObject();
        int bitmask = (int)Math.pow(2,bitPerByte);
        List<Byte> data = new ArrayList<Byte>();
        int byteIndex = 0;
        int R,G,B;

        decodingStatusObject.imgW = picture.getWidth();
        decodingStatusObject.imgH = picture.getHeight();
        decodingStatusObject.amtOfBytesHeader =  4 * (8/bitPerByte);


        /* Get Image Body and Header */
        for (decodingStatusObject.y = 0; decodingStatusObject.y < decodingStatusObject.imgH; decodingStatusObject.y++){
            for (decodingStatusObject.x = 0; decodingStatusObject.x < decodingStatusObject.imgW; decodingStatusObject.x++) {


                //Get R G B bytes
                R = (picture.getPixel(decodingStatusObject.x,decodingStatusObject.y) >> 16) & 0xff;     //bitwise shifting
                G = (picture.getPixel(decodingStatusObject.x,decodingStatusObject.y) >> 8) & 0xff;
                B = picture.getPixel(decodingStatusObject.x,decodingStatusObject.y) & 0xff;
                //extract data from R G B Bytes
                for(int i = (8-bitPerByte); i < 8 ; i++){
                    //Log.w("Debug : ", "Checking Red byte of (x,y) " + decodingStatusObject.x + " " + decodingStatusObject.y + " at bit " + i  + "   set : " +  bitIsSet((byte)R ,i));
                    AddDataToByte(data, byteIndex, bitIsSet((byte) R, i)); //TODO : this cast might not work as intended
                    if(++byteIndex >= 8) byteIndex = 0;
                }
                ++decodingStatusObject.currentByte;
                if(CheckStopDecodingConditions(decodingStatusObject, data, bitPerByte)) break;


                for(int i = (8-bitPerByte); i < 8 ; i++){
                    //Log.w("Debug : ", "Checking Green byte of (x,y) " + decodingStatusObject.x + " " + decodingStatusObject.y + " at bit " + i + "   set : " +  bitIsSet((byte)G ,i));
                    AddDataToByte(data,byteIndex,bitIsSet((byte)G ,i));
                    if(++byteIndex >= 8) byteIndex = 0;
                }
                ++decodingStatusObject.currentByte;
                if(CheckStopDecodingConditions(decodingStatusObject, data, bitPerByte)) break;


                for(int i = (8-bitPerByte); i < 8 ; i++){
                    //Log.w("Debug : ", "Checking Blue byte of (x,y) " + decodingStatusObject.x + " " + decodingStatusObject.y + " at bit " + i  + "   set : " +  bitIsSet((byte)B ,i));
                    AddDataToByte(data,byteIndex,bitIsSet((byte)B,i));
                    if(++byteIndex >= 8) byteIndex = 0;
                }
                ++decodingStatusObject.currentByte;
                if(CheckStopDecodingConditions(decodingStatusObject, data, bitPerByte)) break;


            }
        }

        //Convert list to array
        Byte[] dataAsByte = new Byte[data.size()];
        return(toPrimitives(data.toArray(dataAsByte)));
    }

    /**
     * Check for conditions during decryption to change state. This require a lot of variable from
     * previous method and needs to be called at different places. This is why a class storing various
     * values is used.
     *
     * If we are in header, check for end of header. If found, get length of body and start clear data
     * IF we are in body, check for end of body. If found, stop decrypting.
     *
     * @param statusObj Object containing various variables used to check decryption progress
     * @param data data as a list of bytes
     * @param bitPerColor amt of bit per color
     * @return
     */
    private boolean CheckStopDecodingConditions(DecodingStatusObject statusObj,  List<Byte> data, int bitPerColor){


        if(statusObj.inHeader && statusObj.currentByte == statusObj.amtOfBytesHeader) {
            statusObj.inHeader = false;
            // Get value in current data (this is the amount of bytes we need to check to get the body's data)
            Byte[] dataAsByte = new Byte[data.size()];
            statusObj.amtOfBytesBody = getUnsignedLongFromBytes(toPrimitives(data.toArray(dataAsByte)));

            Log.w("debug : ", "HEADER IN DECRYPT : ");
            LogByteArray(toPrimitives(data.toArray(dataAsByte)));
            Log.w("debug : ", "Decoded " + statusObj.currentByte + " bytes in header");
            Log.w("debug : ", "Decoding " + statusObj.amtOfBytesBody + " bytes with " + bitPerColor + " bit per bytes modified");
            //Log.w("debug : ", "test amt bytes " + testAmtBytes);
            //Reset data
            data = new ArrayList<Byte>();
            statusObj.currentByte = 0; //Reset current byte

        } else if(!statusObj.inHeader && statusObj.currentByte == statusObj.amtOfBytesBody) {
            statusObj.x = statusObj.imgW;
            statusObj.y = statusObj.imgH;
            return true;
        }
        return false;
    }

    /**
     * Set a bit at index ByteIndex on the last byte of the given byte list. Used when creating the
     * byte list when decrypting an image.
     * @param data list of bytes (content of decryption)
     * @param ByteIndex Current index in byte we are modifying
     * @param isSet does the bit need to be set or not
     */
    private void AddDataToByte(List<Byte> data, int ByteIndex, Boolean isSet){
        if(ByteIndex == 0)  data.add(new Byte("0"));

        if (isSet)
            data.set(data.size() - 1, (byte) (data.get(data.size() - 1) | (1 << (ByteIndex))));
        else
            data.set(data.size() - 1, (byte) (data.get(data.size() - 1) & ~(1 << (ByteIndex))));

    }


    /* UTILS TO CONVERT VARIOUS TYPES AND MODIFY BYTES */

    public byte[] concatByteArray(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }


    /**
     * Transform given bitmap into a pixel array
     * @param receivedPicture
     * @return
     */
    private Pixel[] getRGBFromBitmap(Bitmap receivedPicture){
        int imgW = receivedPicture.getWidth();
        int imgH = receivedPicture.getHeight();
        Pixel[] RGBPixels = new Pixel[imgW * imgH];

        int[] pix = new int[imgW * imgH];
        receivedPicture.getPixels(pix, 0, imgW, 0, 0, imgW, imgH);

        for (int y = 0; y < imgH; y++){
            for (int x = 0; x < imgW; x++) {
                int index = y * imgW + x;

                RGBPixels[index] = new Pixel();
                RGBPixels[index].R =  ((pix[index] >> 16) & 0xff);     //bitwise shifting
                RGBPixels[index].G =  ((pix[index] >> 8) & 0xff);
                RGBPixels[index].B =  (pix[index] & 0xff);

            }
        }

        return RGBPixels;
    }

    /**
     * Transform given pixel array into a bitmap
     * @param rgbData pixel array
     * @param imgW width of image
     * @param imgH height of image
     * @return
     */
    private Bitmap getBitmapFromRGB(Pixel[] rgbData, int imgW, int imgH){
        Bitmap bitmapFromRGB;

        // create bitmap by modifying each pixel in succession
        int[] bitmapPixelValues = new int[imgW * imgH];
        for (int y = 0; y < imgH; y++){
            for (int x = 0; x < imgW; x++) {
                int index = y * imgW + x;
                int R = (rgbData[index].R);
                int G = (rgbData[index].G);
                int B = (rgbData[index].B);


                bitmapPixelValues[index] = 0xff000000 | ((R & 0xff) << 16) | ((G & 0xff) << 8) | (B & 0xff);
            }
        }
        bitmapFromRGB = Bitmap.createBitmap(bitmapPixelValues,imgW,imgH, Bitmap.Config.ARGB_8888);

        /*
        //TODO : Investigate using image.setRGB(0, 0, width, height, pixels, 0, width);
        int[] pixels = new int[imgW * imgH * 3];
        bitmapFromRGB.setPixels(pixels, 0, 0, 0, 0 imgW, imgH);
        */

        return bitmapFromRGB;
    }


    /**
     * Convert an integer to byte array
     * @param i integer to convert
     * @return
     */
    private byte[] getBytesFromInt(int i)
    {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i);

        return result;
    }

    private void LogByteArray(byte[] bytesToLog){
        String result = "";
        for(int i = 0; i < bytesToLog.length; i++) {
            result += String.format("%8s", Integer.toBinaryString(bytesToLog[i] & 0xFF)).replace(' ', '0');
            result += " ";
        }
        Log.w("Debug : ", "Logged Byte Array : " + result);
    }

    private void LogByteArray(byte byteToLog){
        byte[] bArray = new byte[1];
        bArray[0] = byteToLog;
        LogByteArray(bArray);
    }


    /**
     * Convert a byte array into an int
     * @param bytes byte array to convert to integer
     * @return
     */
    private int getIntFromBytes(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
    private int getIntFromBytes(Byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
    private long getUnsignedLongFromBytes(byte[] bytes) {
        int i = getIntFromBytes(bytes);
        long l = 0x00000000FFFFFFFFl & (long) i;
        return l;
    }
    private long getUnsignedLongFromBytes(Byte[] bytes) {
        int i = getIntFromBytes(bytes);
        long l = 0x00000000FFFFFFFFl & (long) i;
        return l;
    }

    /**
     * convert from bitmap to byte array
     * @param bitmap bitmap to transfer to bytes
     * @return
     */
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream  stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    /**
     * convert from byte array to bitmap
     * @param bitmapData bite array date to convert
     * @return
     */
    public Bitmap getBitmapFromBytes(byte[] bitmapData){
        return(BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length));
    }

    /**
     * Check if bit is set. Return true if bit at position of byte is 1
     * @param b byte to check
     * @param position position (bit) to check
     * @return
     */
    public boolean bitIsSet(byte b, int position) {
        return ((b >> position) & 1) == 1;
    }


    /**
     * Method Byte to byte
     * @param oBytes Bytes to convert
     * @return bytes
     */
    byte[] toPrimitives(Byte[] oBytes)
    {
        byte[] bytes = new byte[oBytes.length];

        for(int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }
}