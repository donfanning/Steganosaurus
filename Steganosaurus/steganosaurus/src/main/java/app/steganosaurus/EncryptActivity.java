package app.steganosaurus;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.steganosaurus.Utility.Const;
import app.steganosaurus.Utility.GalleryManager;
import app.steganosaurus.Utility.MediaManager;
import steganosaurus.R;


public class EncryptActivity extends AppCompatActivity {

    Bitmap selectedBasePicture;
    Bitmap selectedPictureToHide;
    Bitmap cameraPicture;
    Uri cameraImageUri;

    GalleryManager galleryManager;
    MediaManager mediaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        galleryManager = new GalleryManager(this);
        mediaManager = new MediaManager(this);
    }

    public void encrypt(View v) {
        String button_title = (String) ((Button)v).getText();
        Toast.makeText(this, "You clicked on " + button_title, Toast.LENGTH_SHORT).show();
    }

    public void takePicture(View v) {
        int requestCode = 0;
        int id = v.getId();

        if (id == R.id.source_take_picture_btn)
            requestCode = Const.REQUEST_SOURCE_IMAGE_CAPTURE;
        else if (id == R.id.hidden_take_picture_btn)
            requestCode = Const.REQUEST_HIDDEN_IMAGE_CAPTURE;

        cameraImageUri = mediaManager.takePicture(requestCode);
    }

    public void getStoredPicturesFromDevice(View v) {
        int requestCode = 0;
        int id = v.getId();

        if (id == R.id.encrypt_source_image)
            requestCode = Const.PICK_SOURCE_IMAGE_REQUEST;
        else if (id == R.id.encrypt_hidden_image)
            requestCode = Const.PICK_HIDDEN_IMAGE_REQUEST;

        galleryManager.getStoredPicturesFromDevice(requestCode);
    }

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
                    Bitmap selectedPicture = galleryManager.getSelectedPictureBitmap(requestCode, data);
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
        else
            Toast.makeText(this, "Something went wrong on activity result!", Toast.LENGTH_LONG).show();
    }

}
