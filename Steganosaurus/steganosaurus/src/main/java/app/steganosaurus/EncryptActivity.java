package app.steganosaurus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

import app.steganosaurus.Utility.Const;
import app.steganosaurus.Utility.GalleryManager;
import steganosaurus.R;


public class EncryptActivity extends AppCompatActivity {

    private Uri selectedBaseImageUri;
    private Uri selectedPictureToHideUri;
    private Bitmap selectedBasePicture;
    private Bitmap selectedPictureToHide;

    GalleryManager galleryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        galleryManager = new GalleryManager(this);
    }

    public void encrypt(View v) {
        String button_title = (String) ((Button)v).getText();
        Toast.makeText(this, "You clicked on " + button_title, Toast.LENGTH_SHORT).show();
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
        Uri selectedPictureUri = null;
        Bitmap selectedPicture = null;
        int pictureId = 0;

        try {
            if (resultCode == RESULT_OK) {
                if(data.getData() != null)
                    selectedPictureUri = data.getData();
                else
                    Toast.makeText(getApplicationContext(), "failed to get Image!", Toast.LENGTH_SHORT).show();
                selectedPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedPictureUri);

                if (requestCode == Const.PICK_SOURCE_IMAGE_REQUEST) {
                    selectedBasePicture = selectedPicture;
                    selectedBaseImageUri = selectedPictureUri;
                    pictureId = R.id.encrypt_source_image;
                }
                else if (requestCode == Const.PICK_HIDDEN_IMAGE_REQUEST) {
                    selectedPictureToHide = selectedPicture;
                    selectedPictureToHideUri = selectedPictureUri;
                    pictureId = R.id.encrypt_hidden_image;
                }

                ImageButton imgbtn = (ImageButton)findViewById(pictureId);
                if (imgbtn != null)
                    imgbtn.setImageBitmap(selectedPicture);
            }
            else
                Toast.makeText(this, "An error occurred at picture selection.", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e) {e.printStackTrace();}
    }

}
