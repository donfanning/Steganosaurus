package app.steganosaurus.Utility;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.sql.Time;
import java.util.Random;

/**
 * Class to make the actual steganography
 */
public class Steganograph {

    protected class Pixel{
        public int R,G,B;
        Pixel(int r, int g, int b){
            R = r;
            G = g;
            B = b;
        }
        Pixel(){ R=0; G=0; B=0; }
    }

    public Bitmap encodePicture(Bitmap destinationPicture, Bitmap pictureToHide) {
        //Create pixel arrays
        Pixel[] destinationPixel = getRGBFromBitmap(destinationPicture);
        Pixel[] toHidePixels = getRGBFromBitmap(pictureToHide);
        //This is where the magic happens
        Pixel[] resultingPixels = encodePixels(destinationPixel, toHidePixels);
        //Transform pixels into a new bitmap
        Bitmap resultingBitmap = getBitmapFromRGB(resultingPixels, destinationPicture.getWidth(), destinationPicture.getHeight());
        return resultingBitmap;
    }

    public Bitmap decodePicture(Bitmap picture) {

        return picture;
    }

    //Translates the image into a string and encode the string into the given image
    private Pixel[] encodePixels(Pixel[] destPixels, Pixel[] hidePixels){
        Random rand = new Random();
        int min = 0;
        int max = 0;

        for (Pixel P:destPixels) {
            P.R += rand.nextInt((max - min) + 1) + min;
            P.G += rand.nextInt((max - min) + 1) + min;
            P.B += rand.nextInt((max - min) + 1) + min;

            P.R = Math.min(255, Math.max(0, P.R));
            P.G = Math.min(255, Math.max(0, P.G));
            P.B = Math.min(255, Math.max(0, P.B));

        }

        /*
        String testMessage = "";
        for(int i =0; i<10000; i++) {
            testMessage += '0';
        }
        hideMessageInPixels(destPixels,7, testMessage);
        */

        return destPixels;
    }

    //Encode the given string into the destination pixels
    private Pixel[] encodeString(Pixel[] destPixels, String rawData){
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

    private void hideMessageInPixels(Pixel[] destPixels,  int bitPerColor, String message) {
        int curBit = 0;
        int curPixel = 0;
        int curColor = 0; //0=red, 1=green, 2=blue

        for(int i =0; i<message.length(); i++){
            switch(curColor){
                case 0 :
                    if(message.charAt(i) == '1') destPixels[curPixel].R = (byte) (destPixels[curPixel].R | (1 << (7-curBit)));
                    else  destPixels[curPixel].R = (byte) (destPixels[curPixel].R & ~(1 << (7-curBit)));
                    break;
                case 1 :
                    if(message.charAt(i) == '1') destPixels[curPixel].G = (byte) (destPixels[curPixel].G | (1 << (7-curBit)));
                    else  destPixels[curPixel].G = (byte) (destPixels[curPixel].G & ~(1 << (7-curBit)));
                    break;
                case 2 :
                    if(message.charAt(i) == '1') destPixels[curPixel].B = (byte) (destPixels[curPixel].B | (1 << (7-curBit)));
                    else  destPixels[curPixel].B = (byte) (destPixels[curPixel].B & ~(1 << (7-curBit)));
                    break;
            }

            curBit++;
            if(curBit >= bitPerColor) {
                curBit = 0;
                curColor++;
                if(curColor >= 3){
                    curPixel++;
                    curColor = 0;
                    if(curPixel >= destPixels.length){
                        i = message.length(); //Stop
                    }
                }
            }
        }
    }

    // Convert pixel into a string
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

    private Bitmap getBitmapFromRGB(Pixel[] rgbData, int imgW, int imgH){
        Bitmap bitmapFromRGB;
        int[] bitmapPixelValues = new int[imgW * imgH];


        for (int y = 0; y < imgH; y++){
            for (int x = 0; x < imgW; x++) {
                int index = y * imgW + x;
                int R = (rgbData[index].R);  //bitwise shifting
                int G = (rgbData[index].G);
                int B = rgbData[index].B;

                bitmapPixelValues[index] = 0xff000000 | (R << 16) | (G << 8) | B;
            }
        }

        bitmapFromRGB = Bitmap.createBitmap(bitmapPixelValues,imgW,imgH, Bitmap.Config.ARGB_8888);
        return bitmapFromRGB;
    }
}