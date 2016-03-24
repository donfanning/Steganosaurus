package app.steganosaurus;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import app.steganosaurus.Utility.Const;
import app.steganosaurus.Utility.GalleryManager;
import app.steganosaurus.Utility.Steganograph;
import steganosaurus.R;

public class DecryptActivity extends AppCompatActivity {

    GalleryManager galleryManager;
    Steganograph steganograph;
    Uri selectedPictureUri = null;
    Bitmap selectedPicture = null;
    Bitmap decryptedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        galleryManager = new GalleryManager(this);
        steganograph = new Steganograph();
    }

    public void getStoredPicturesFromDevice(View v) {
        galleryManager.getStoredPicturesFromDevice(Const.PICK_DECRYPT_IMAGE_REQUEST);
    }

    public void decryptPicture(View v) {
        if (decryptedImage == null)
            decryptedImage = steganograph.decodePicture(selectedPicture);

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.popup_image, null);
        ImageView imgv = (ImageView)view.findViewById(R.id.decrypted_popup_image);
        if (imgv != null)
            imgv.setImageBitmap(decryptedImage);
        Button b_ok = (Button)view.findViewById(R.id.decrypt_popup_go_back);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

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
            else
                Toast.makeText(this, "An error occurred at picture selection.", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e) {e.printStackTrace();}
    }

}
