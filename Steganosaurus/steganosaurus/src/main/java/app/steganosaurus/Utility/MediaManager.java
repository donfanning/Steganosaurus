package app.steganosaurus.Utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import steganosaurus.R;

/**
 * Class to manage pictures and videos from and to the device
 */
public class MediaManager {

    Activity context;
    private static final int MAX_SIZE = 4096;
    private static final int MAX_WIDTH = 1280;
    private static final int MAX_HEIGHT = 720;

    public MediaManager(Activity _context) {
        context = _context;
    }

    /**
     * Method to create a temporary file of the picture taken with the camera
     * This allows getting a hold of the full resolution picture instead of
     * only the thumbnail returned by the intent
     * @return the temporary file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return  File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Method to start the camera app and take a picture only
     * @param requestCode the requestCode depending of which button was clicked
     * @return the Uri of the taken picture
     */
    public Uri takePicture(int requestCode) {
        Uri cameraImageUri = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                File cameraPicture = createImageFile();
                cameraImageUri = Uri.fromFile(cameraPicture);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                context.startActivityForResult(takePictureIntent, requestCode);
            }
        } catch(Exception e) { e.printStackTrace(); }
        return cameraImageUri;
    }

    /**
     * Method to select a picture from the user's device
     * @param requestCode the requestCode depending of which button was clicked
     */
    public void getStoredPicturesFromDevice(int requestCode) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    /**
     * gets Bitmap image from selected image Uri
     * @param requestCode the requestCode depending of which button was clicked
     * @param data the intent's data
     * @return the bitmap image
     */
    public Bitmap getSelectedPictureBitmap(int requestCode, Intent data) {

        try {

            int pictureId;
            Uri selectedPictureUri = data.getData();

            //Create bitmapFactory options
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //Decode img properties
            InputStream inputStream = context.getApplicationContext().getContentResolver().openInputStream(selectedPictureUri);
            BitmapFactory.decodeStream(inputStream,new Rect(),options);

            //Get sample size
            options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);
            options.inJustDecodeBounds = false;

            //Decode image with correct properties
            inputStream = context.getApplicationContext().getContentResolver().openInputStream(selectedPictureUri);
            Bitmap selectedPicture = BitmapFactory.decodeStream(inputStream,new Rect(),options);

            //Set correct picutre ID so image is set in the correct canvas
            if (requestCode == Const.PICK_SOURCE_IMAGE_REQUEST)
                pictureId = R.id.encrypt_source_image;
            else
                pictureId = R.id.encrypt_hidden_image;

            ImageButton imgbtn = (ImageButton) context.findViewById(pictureId);
            if (imgbtn != null)
                imgbtn.setImageBitmap(selectedPicture);
            return selectedPicture;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap resizeBitmap(Bitmap b) {
        Bitmap bitmap;
        float ratio = b.getWidth() / b.getHeight();
        if (ratio > 0)
            bitmap = Bitmap.createScaledBitmap(b, MAX_SIZE, Math.round(MAX_SIZE/ratio), false);
        else if (ratio < 0)
            bitmap = Bitmap.createScaledBitmap(b, Math.round(MAX_SIZE/ratio), MAX_SIZE, false);
        else
            bitmap = null;
        return bitmap;
    }

    public void takeVideo() {
        //start camera app, take video, save video to device
    }

    public void selectVideoFromDevice() {
        //browse user's videos and select one to use in app
    }

    public Boolean SaveImageOn(Bitmap imageToSave, Context context) {
        if(!isExternalStorageWritable()) {
            Log.w("ExternalStorage", "External Storage is not writable ");
            return false;
        }

        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String name = "img";
        int nameNumber = 0;
        File file = new File(path, name + ".jpg");

        try {
            // Make sure the Pictures directory exists.
            path.mkdirs();
            // Check if file with name already exists, if so, append number at the end
            while(file.exists())
                file = new File(path, name + (++nameNumber) + ".jpg");

            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            OutputStream os = new FileOutputStream(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageToSave.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();
            os.write(data);
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
            return false;
        }

        return true;
    }

    /**
     * Check if external storage is available for write
     * @return true if available, false if not available
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
