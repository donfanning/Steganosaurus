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

    private Pixel[] encodePixels(Pixel[] destPixels, Pixel[] hidePixels){
        Random rand = new Random();
        int min = 0;
        int max = 255;

        for (Pixel P:destPixels) {
            P.R = rand.nextInt((max - min) + 1) + min;
            P.G = rand.nextInt((max - min) + 1) + min;
            P.B = rand.nextInt((max - min) + 1) + min;

        }

        return destPixels;
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