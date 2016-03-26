package app.steganosaurus;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import app.steganosaurus.Utility.Const;
import app.steganosaurus.Utility.GalleryManager;
import app.steganosaurus.Utility.MediaManager;
import steganosaurus.R;

/**
 * Activity class for steganography itself. Allows user to
 * select pictures to mix together.
 */
public class EncryptActivity extends AppCompatActivity {

    Bitmap selectedBasePicture;
    Bitmap selectedPictureToHide;
    Bitmap cameraPicture;
    Uri cameraImageUri;

    MediaManager mediaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        mediaManager = new MediaManager(this);
    }

    /**
     * Callback. Takes selectedBasePicture and encrypts selectedPictureToHide in it
     * @param v the button that was clicked
     */
    public void encrypt(View v) {
        String button_title = (String) ((Button)v).getText();
        Toast.makeText(this, "You clicked on " + button_title, Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback. Allows user to take picture with the device's camera
     * request code depends on clicked button's id. MediaManager starts
     * the camera activity for result
     * @param v the button that was ciicked
     */
    public void takePicture(View v) {
        int requestCode = 0;
        int id = v.getId();

        if (id == R.id.encrypt_source_take_picture_btn)
            requestCode = Const.REQUEST_SOURCE_IMAGE_CAPTURE;
        else if (id == R.id.encrypt_hidden_take_picture_btn)
            requestCode = Const.REQUEST_HIDDEN_IMAGE_CAPTURE;

        cameraImageUri = mediaManager.takePicture(requestCode);
    }

    /**
     * Callback. allows user to select picture from their device
     * request code depends on the view that was clicked.
     * galleryManager starts the selection activity for result.
     * @param v the button that was clicked
     */
    public void getStoredPicturesFromDevice(View v) {
        int requestCode = 0;
        int id = v.getId();

        if (id == R.id.encrypt_source_image)
            requestCode = Const.PICK_SOURCE_IMAGE_REQUEST;
        else if (id == R.id.encrypt_hidden_image)
            requestCode = Const.PICK_HIDDEN_IMAGE_REQUEST;

        mediaManager.getStoredPicturesFromDevice(requestCode);
    }

    /**
     * Callback. Allows user to return to previous activity
     * @param v the button that was clicked
     */
    public void backHome(View v) {
        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
            //Results for selecting a picture on the device
            case Const.PICK_SOURCE_IMAGE_REQUEST:
            case Const.PICK_HIDDEN_IMAGE_REQUEST:
                if (data.getData() != null) {
                    Bitmap selectedPicture = mediaManager.getSelectedPictureBitmap(requestCode, data);
                    if (requestCode == Const.PICK_SOURCE_IMAGE_REQUEST)
                        selectedBasePicture = selectedPicture;
                    else
                        selectedPictureToHide = selectedPicture;
                }
                break;

            //Result for taking a picture with hardware camera
            case Const.REQUEST_SOURCE_IMAGE_CAPTURE:
            case Const.REQUEST_HIDDEN_IMAGE_CAPTURE:
                try {
                    int id;
                    this.getContentResolver().notifyChange(cameraImageUri, null);
                    ContentResolver cr = this.getContentResolver();
                    cameraPicture = MediaStore.Images.Media.getBitmap(cr, cameraImageUri);
                    if (requestCode == Const.REQUEST_SOURCE_IMAGE_CAPTURE)
                        id = R.id.encrypt_source_image;
                    else
                        id = R.id.encrypt_hidden_image;
                    ImageButton imgbtn = (ImageButton) findViewById(id);
                    if (imgbtn != null)
                        imgbtn.setImageBitmap(cameraPicture);
                } catch (Exception e) { e.printStackTrace(); }
                break;
        }
    }

}
