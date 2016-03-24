package app.steganosaurus.Utility;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;


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


}
