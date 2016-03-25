package app.steganosaurus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.steganosaurus.Utility.Const;
import steganosaurus.R;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //user taps one menu button, start corresponding activity
    }

    public void encrypt(View v) {
        String button_title = (String)((Button)v).getText();
        Toast.makeText(this, "You clicked on " + button_title, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, EncryptActivity.class);
        startActivityForResult(i, Const.ENCRYPT_CODE);
    }

    public void decrypt(View v) {
        String button_title = (String)((Button)v).getText();
        Toast.makeText(this, "You clicked on " + button_title, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, DecryptActivity.class);
        startActivityForResult(i, Const.DECRYPT_CODE);
    }

    public void share(View v) {
        String title = (String)((Button)v).getText();
        Toast.makeText(this, "You clicked on " + title, Toast.LENGTH_LONG).show();
    }

    public void showOptions(View v) {
        String title = (String)((Button)v).getText();
        Toast.makeText(this, "You clicked on " + title, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, OptionsActivity.class);
        startActivity(i);
    }

}
