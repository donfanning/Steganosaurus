package app.steganosaurus;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;


public class GalleryManager extends MainMenu{

    private int PICK_IMAGE_REQUEST = 1;
    private List<Drawable> encodedPictures;
    private Uri selectedImageUri;
    private Bitmap selectedPicture;

    public GalleryManager() {
        encodedPictures = new ArrayList<Drawable>();
    }

    public List<Drawable> getEncodedPicturesList() {
        return encodedPictures;
    }

    public void makeEncodedPicturesList() {
        //fill encodedPictures list
    }

    public void getStoredPicturesFromDevice(View v) {

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
                if(data.getData() != null) {
                    selectedImageUri = data.getData();
                }
                else {
                    Toast.makeText(getApplicationContext(), "failed to get Image!", Toast.LENGTH_SHORT).show();
                }
                selectedPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            }
        }
        catch(IOException e) {e.printStackTrace();}
    }
}
