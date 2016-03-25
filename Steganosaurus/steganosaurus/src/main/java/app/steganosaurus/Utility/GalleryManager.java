package app.steganosaurus.Utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageButton;

import steganosaurus.R;


public class GalleryManager{

    private Activity context;
    private List<Drawable> encodedPictures;

    public GalleryManager(Activity _context) {
        encodedPictures = new ArrayList<Drawable>();
        context = _context;
    }

    public List<Drawable> getEncodedPicturesList() {
        return encodedPictures;
    }

    public void makeEncodedPicturesList() {
        //fill encodedPictures list
    }

    public void getStoredPicturesFromDevice(int requestCode) {

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

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


}
