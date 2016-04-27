package app.steganosaurus;

import android.app.Dialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import app.steganosaurus.Utility.Const;
import app.steganosaurus.Utility.DecryptObject;
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
    String decryptedText = "";

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
        if(selectedPicture != null) {
            DecryptObject dObj = null;
            if (decryptedImage == null) {
                dObj = steganograph.decodePicture(selectedPicture);
                if (dObj.GetType() == Const.DataType.PHOTO)
                    decryptedImage = dObj.GetBitmap();
                else if (dObj.GetType() == Const.DataType.TEXT)
                    decryptedText = dObj.GetString();
            }
            final Context c = this;

            final Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            View view = null;
            if (dObj.GetType() == Const.DataType.PHOTO) {
                view = getLayoutInflater().inflate(R.layout.popup_image_decrypt_bitmap, null);
                ImageView imgv = (ImageView) view.findViewById(R.id.result_popup_image_after);
                if (imgv != null)
                    imgv.setImageBitmap(decryptedImage);
            } else if (dObj.GetType() == Const.DataType.TEXT) {
                view = getLayoutInflater().inflate(R.layout.popup_image_decrypt_text, null);
                TextView textv = (TextView) view.findViewById(R.id.result_popup_text_after);
                if (textv != null) {
                    textv.setText(decryptedText);
                }
            }
            Button b_ok = (Button) view.findViewById(R.id.decrypt_popup_go_back_btn);
            Button b_save = (Button) view.findViewById(R.id.decrypt_popup_save_btn);
            b_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });
            if (b_save != null) {
                b_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaManager.SaveImageOn(decryptedImage, getApplicationContext())) {
                            Toast.makeText(c, "Image saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(c, "Image failed to save properly", Toast.LENGTH_SHORT).show();
                        }
                        dialog.hide();
                    }
                });
            }
            dialog.setContentView(view);
            dialog.show();
        }
        else
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
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
