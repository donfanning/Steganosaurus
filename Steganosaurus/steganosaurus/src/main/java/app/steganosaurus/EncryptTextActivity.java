package app.steganosaurus;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import app.steganosaurus.Utility.Const;
import app.steganosaurus.Utility.MediaManager;
import app.steganosaurus.Utility.Steganograph;
import steganosaurus.R;

/**
 * Activity class for steganography itself. Allows user to
 * hide text into an image.
 */
public class EncryptTextActivity extends AppCompatActivity {

    Bitmap selectedBasePicture;
    String textToHide;
    Bitmap cameraPicture;
    Uri cameraImageUri;

    MediaManager mediaManager;
    Steganograph steganograph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_text);

        mediaManager = new MediaManager(this);
        steganograph = new Steganograph();
    }

    /**
     * Callback. Takes selectedBasePicture and encrypts textToHide in it
     * @param v the button that was clicked
     */

    public void encrypt(View v) {
        final String button_title = (String) ((Button)v).getText();
        final Context c = this;

        EditText editText = (EditText)findViewById(R.id.textToHide);
        if (editText.getText().toString().trim().length() > 0)
            textToHide = editText.getText().toString().trim();

        if(selectedBasePicture == null || textToHide == null) {
            Toast.makeText(c, "Select a picture and write text to proceed ", Toast.LENGTH_LONG).show();
            return;
        }

        final Bitmap resultingImage = steganograph.encodePicture(selectedBasePicture,textToHide);

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.popup_image_encrypt, null);
        //Imageviews
        ImageView imgvBefore = (ImageView)view.findViewById(R.id.result_popup_image_before);
        if (imgvBefore != null)
            imgvBefore.setImageBitmap(selectedBasePicture);
        ImageView imgvAfter = (ImageView)view.findViewById(R.id.result_popup_image_after);
        if (imgvAfter != null)
            imgvAfter.setImageBitmap(resultingImage);


        //Buttons
        Button b_ok = (Button)view.findViewById(R.id.encrypt_popup_go_back_btn);
        Button b_save = (Button)view.findViewById(R.id.encrypt_popup_save_btn);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaManager.SaveImageOn(resultingImage, getApplicationContext())){
                    Toast.makeText(c, "Image saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(c, "Image failed to save properly", Toast.LENGTH_SHORT).show();
                }
                dialog.hide();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * Callback. Allows user to take picture with the device's camera
     * request code depends on clicked button's id. MediaManager starts
     * the camera activity for result
     * @param v the button that was clicked
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
                if (data.getData() != null) {
                    Bitmap selectedPicture = mediaManager.getSelectedPictureBitmap(requestCode, data);
                    selectedBasePicture = selectedPicture;
                }
                break;

            //Result for taking a picture with hardware camera
            case Const.REQUEST_SOURCE_IMAGE_CAPTURE:
                try {
                    int id;
                    this.getContentResolver().notifyChange(cameraImageUri, null);
                    ContentResolver cr = this.getContentResolver();
                    cameraPicture = MediaStore.Images.Media.getBitmap(cr, cameraImageUri);
                    id = R.id.encrypt_source_image;
                    selectedBasePicture = cameraPicture;
                    ImageButton imgbtn = (ImageButton) findViewById(id);
                    if (imgbtn != null)
                        imgbtn.setImageBitmap(cameraPicture);
                } catch (Exception e) { e.printStackTrace(); }
                break;
        }
    }

}
