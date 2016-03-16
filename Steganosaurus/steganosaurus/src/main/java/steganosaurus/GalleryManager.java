package steganosaurus;

import android.graphics.drawable.Drawable;
import android.widget.Gallery;

import java.util.ArrayList;
import java.util.List;


public class GalleryManager {

    private List<Drawable> encodedPictures;

    public GalleryManager() {
        encodedPictures = new ArrayList<Drawable>();
    }

    public List<Drawable> getEncodedPicturesList() {
        return encodedPictures;
    }

    public void getStoredPicturesFromDevice() {

    }

    public void makeEncodedPicturesList() {
        //fill encodedPictures list
    }
}
