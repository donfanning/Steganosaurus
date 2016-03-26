package app.steganosaurus.Utility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
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
            Bitmap selectedPicture = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedPictureUri);

            if (requestCode == Const.PICK_SOURCE_IMAGE_REQUEST)
                pictureId = R.id.encrypt_source_image;
            else
                pictureId = R.id.encrypt_hidden_image;

            ImageButton imgbtn = (ImageButton) context.findViewById(pictureId);
            if (imgbtn != null)
                imgbtn.setImageBitmap(selectedPicture);
            return selectedPicture;
        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    public void takeVideo() {
        //start camera app, take video, save video to device
    }

    public void selectVideoFromDevice() {
        //browse user's videos and select one to use in app
    }
}
