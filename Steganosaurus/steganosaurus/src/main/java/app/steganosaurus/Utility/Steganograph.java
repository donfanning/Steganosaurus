package app.steganosaurus.Utility;

import android.graphics.Bitmap;
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
     * TODO : Decode given bitmap
     * @param picture
     * @return
     */
    public Bitmap decodePicture(Bitmap picture) {

        byte[] retrievedData = getDataFromBitmap(picture);

        return picture;
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

        //Encrypt data in destination pixels
        hideBytesInPixels(destPixels,4, data);

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
     * Check if bit is set. Return true if bit at position of byte is 1
     * @param b byte to check
     * @param position position (bit) to check
     * @return
     */
    public boolean bitIsSet(byte b, int position) {
       return ((b >> position) & 1) == 1;
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
                            destPixels[curPixel].R = (byte) (destPixels[curPixel].R | (1 << (curBit - 7)));
                        //Add 0
                        else
                            destPixels[curPixel].R = (byte) (destPixels[curPixel].R & ~(1 << (curBit - 7)));
                        break;
                    case 1:
                        if (bitIsSet(message[i],j))
                            destPixels[curPixel].G = (byte) (destPixels[curPixel].G | (1 << (curBit - 7)));
                        else
                            destPixels[curPixel].G = (byte) (destPixels[curPixel].G & ~(1 << (curBit - 7)));
                        break;
                    case 2:
                        if (bitIsSet(message[i],j))
                            destPixels[curPixel].B = (byte) (destPixels[curPixel].B | (1 << (curBit - 7)));
                        else
                            destPixels[curPixel].B = (byte) (destPixels[curPixel].B & ~(1 << (curBit - 7)));
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

    private byte[] getDataFromBitmap(Bitmap picture){
        int bitPerColor = 4; //TODO : Get this from the picture data
        List<Byte> data = new ArrayList<Byte>();


        //Convert list to array
        Byte[] dataAsByte = new Byte[data.size()];
        return(toPrimitives(data.toArray(dataAsByte)));
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
}