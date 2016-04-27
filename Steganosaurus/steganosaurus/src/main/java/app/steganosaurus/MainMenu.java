package app.steganosaurus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.steganosaurus.Utility.Const;
import steganosaurus.R;

/**
 * Launcher app. Contains app title and logo, and main menu
 */
public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Callback. Starts encryption activity
     * @param v the button that was clicked
     */
    public void encrypt(View v) {
        String button_text = ((Button)v).getText().toString();

        if (button_text.equals("Hide image")) {
            Intent i = new Intent(this, EncryptImageActivity.class);
            startActivityForResult(i, Const.ENCRYPT_IMAGE_CODE);
        }
        else if (button_text.equals("Hide text")) {
            Intent i = new Intent(this, EncryptTextActivity.class);
            startActivityForResult(i, Const.ENCRYPT_TEXT_CODE);
        }
    }

    /**
     * Callback. Starts decryption activity
     * @param v the button that was clicked
     */
    public void decrypt(View v) {
        Intent i = new Intent(this, DecryptActivity.class);
        startActivityForResult(i, Const.DECRYPT_CODE);
    }

    /**
     * Callback. Starts sharing activity
     * @param v the button that was clicked
     */
    public void share(View v) {
        String title = (String)((Button)v).getText();
        Toast.makeText(this, "You clicked on " + title, Toast.LENGTH_LONG).show();
    }

    /**
     * Callback. Starts options activity
     * @param v the button that was clicked
     */
    public void showOptions(View v) {
        Intent i = new Intent(this, OptionsActivity.class);
        startActivity(i);
    }

}
