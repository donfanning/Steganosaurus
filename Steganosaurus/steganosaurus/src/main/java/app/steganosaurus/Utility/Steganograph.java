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
        int bitPerByte = 4;
        int amtOfBytesToEncodeInto = data.length * (8-bitPerByte);
        byte[] header = getBytesFromInt(amtOfBytesToEncodeInto);
        byte[] headerWithData = concatByteArray(header, data);
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
        int min = -30;
        int max = 30;

        for (Pixel P:destPixels) {
            P.R += rand.nextInt((max - min) + 1) + min;
            P.G += rand.nextInt((max - min) + 1) + min;
            P.B += rand.nextInt((max - min) + 1) + min;

            P.R = Math.min(255, Math.max(0, P.R));
            P.G = Math.min(255, Math.max(0, P.G));
            P.B = Math.min(255, Math.max(0, P.B));

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



    /**
     * Get a bitmap and look into it to see if there is a hidden message in it. If so, return it.
     * @param picture to analyse
     * @return null if no message, otherwise message as byte array
     */
    private byte[] getDataFromBitmap(Bitmap picture){
        int bitPerColor = 4; //TODO : Get this from the picture data
        int bitmask = (int)Math.pow(2,bitPerColor);
        List<Byte> data = new ArrayList<Byte>();
        int byteIndex = 0;
        int imgW = picture.getWidth();
        int imgH = picture.getHeight();
        int R,G,B;

        /* Values to separate header and body */
        int amtOfBytesHeader = 4 * (8-bitPerColor);
        int amtOfBytesBody  = 0;
        int currentByte = 0;
        boolean inHeader = true;

        /* Get Image Body */
        for (int y = 0; y < imgH; y++){
            for (int x = 0; x < imgW; x++) {
                if(inHeader && currentByte == amtOfBytesHeader) {
                    inHeader = false;
                    // Get value in current data (this is the amount of bytes we need to check to get the body's data)
                    Byte[] dataAsByte = new Byte[data.size()];
                    amtOfBytesBody = getIntFromBytes(toPrimitives(data.toArray(dataAsByte)));
                    //Reset data
                    data = new ArrayList<Byte>();
                    //Here, we see that that the header is not correctly encrypted or decrypted
                    //Hence we know that either the encryption or the decryption is currently problematic
                    //It is most likely the encryption since the visual result seem wrong on encrypted images
                    Log.w("debug : ", "Decoded " + currentByte + " bytes in header");
                    Log.w("debug : ", "Decoding " + amtOfBytesBody + " bytes with " + bitPerColor + " bit per bytes modified");
                    currentByte = 0; //Reset current byte
                } else if(!inHeader && currentByte == amtOfBytesBody) {
                    x = imgW;
                    y = imgH;
                    break;
                }

                //Get R G B bytes
                R = (picture.getPixel(x,y) >> 16) & 0xff;     //bitwise shifting
                G = (picture.getPixel(x,y) >> 8) & 0xff;
                B = picture.getPixel(x,y) & 0xff;
                //extract data from R G B Bytes
                for(int i = (8-bitPerColor); i < 8 ; i++){
                    AddDataToByte(data,byteIndex,bitIsSet((byte)R,i)); //TODO : this cast might not work as intended
                    if(++byteIndex >= 8) byteIndex = 0;
                }
                for(int i = (8-bitPerColor); i < 8 ; i++){
                    AddDataToByte(data,byteIndex,bitIsSet((byte)G,i));
                    if(++byteIndex >= 8) byteIndex = 0;
                }
                for(int i = (8-bitPerColor); i < 8 ; i++){
                    AddDataToByte(data,byteIndex,bitIsSet((byte)B,i));
                    if(++byteIndex >= 8) byteIndex = 0;
                }
                //Increase current Byte
                ++currentByte;
            }
        }


        //Convert list to array
        Byte[] dataAsByte = new Byte[data.size()];
        return(toPrimitives(data.toArray(dataAsByte)));
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
     * DEPRECATED. WE NOW USE A BYTE ARRAY.
     * Returns a string of 0 and 1 represneting the bits of the pixel
     * @param pixel
     * @return
     */
    private String getPixelData(Pixel pixel){
        String pData = "";
        //Red
        if(pixel.R < 10) pData += ("00" + pixel.R);
        else if(pixel.R < 100) pData += ("0" + pixel.R);
        else pData += pixel.R;
        //Green
        if(pixel.G < 10) pData += ("00" + pixel.G);
        else if(pixel.G < 100) pData += ("0" + pixel.G);
        else pData += pixel.G;
        //Blue
        if(pixel.B < 10) pData += ("00" + pixel.B);
        else if(pixel.B < 100) pData += ("0" + pixel.B);
        else pData += pixel.B;

        return pData;
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
                RGBPixels[index].R = (pix[index] >> 16) & 0xff;     //bitwise shifting
                RGBPixels[index].G = (pix[index] >> 8) & 0xff;
                RGBPixels[index].B = pix[index] & 0xff;

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
        int[] bitmapPixelValues = new int[imgW * imgH];


        for (int y = 0; y < imgH; y++){
            for (int x = 0; x < imgW; x++) {
                int index = y * imgW + x;
                int R = (rgbData[index].R);  //bitwise shifting
                int G = (rgbData[index].G);
                int B = (rgbData[index].B);

                bitmapPixelValues[index] = 0xff000000 | (R << 16) | (G << 8) | B;
            }
        }

        bitmapFromRGB = Bitmap.createBitmap(bitmapPixelValues,imgW,imgH, Bitmap.Config.ARGB_8888);
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

    /**
     * Convert a byte array into an int
     * @param bytes byte array to convert to integer
     * @return
     */
    private int getIntFromBytes(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
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