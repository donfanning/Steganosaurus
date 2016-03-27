package app.steganosaurus;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import app.steganosaurus.Utility.Const;
import app.steganosaurus.Utility.MediaManager;
import app.steganosaurus.Utility.Steganograph;
import steganosaurus.R;

/**
 * Activity class to analyse a picture and return if there is
 * a hidden picture inside it
 */
public class DecryptActivity extends AppCompatActivity {

    MediaManager mediaManager;
    Steganograph steganograph;
    Uri selectedPictureUri = null;
    Bitmap selectedPicture = null;
    Bitmap decryptedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        mediaManager = new MediaManager(this);
        steganograph = new Steganograph();
    }

    /**
     * Callback. Allows user to select a picture from their device.
     * galleryManager starts the selection activity for result.
     * @param v the button that was clicked
     */
    public void getStoredPicturesFromDevice(View v) {
        mediaManager.getStoredPicturesFromDevice(Const.PICK_DECRYPT_IMAGE_REQUEST);
    }

    /**
     * Callback. Method to decrypt the selected image and show the result
     * in a dialog box to the user.
     * @param v the button that was clicked
     */
    public void decryptPicture(View v) {
        if (decryptedImage == null)
            decryptedImage = steganograph.decodePicture(selectedPicture);

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.popup_image, null);
        ImageView imgv = (ImageView)view.findViewById(R.id.decrypt_popup_image);
        if (imgv != null)
            imgv.setImageBitmap(decryptedImage);
        Button b_ok = (Button)view.findViewById(R.id.decrypt_popup_go_back_btn);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * Callback. Allows user to return to previous activity.
     * @param v the button that was clicked
     */
    public void backHome(View v) {
        this.finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == Const.PICK_DECRYPT_IMAGE_REQUEST && resultCode == RESULT_OK) {
                if(data.getData() != null)
                    selectedPictureUri = data.getData();
                else
                    Toast.makeText(this, "failed to get Image!", Toast.LENGTH_SHORT).show();

                selectedPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedPictureUri);
                ImageButton imgbtn = (ImageButton)findViewById(R.id.decrypt_image_button);
                if (imgbtn != null)
                    imgbtn.setImageBitmap(selectedPicture);
            }
        }
        catch(IOException e) {e.printStackTrace();}
    }

}
