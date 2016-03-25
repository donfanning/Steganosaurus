package app.steganosaurus.Utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaManager {

    Activity context;

    public MediaManager(Activity _context) {
        context = _context;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return  File.createTempFile(imageFileName, ".jpg", storageDir);
    }

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

    public void selectPictureFromDevice() {
        //browse user's pictures and select one to use in app
    }

    public void takeVideo() {
        //start camera app, take video, save video to device
    }

    public void selectVideoFromDevice() {
        //browse user's videos and select one to use in app
    }
}
