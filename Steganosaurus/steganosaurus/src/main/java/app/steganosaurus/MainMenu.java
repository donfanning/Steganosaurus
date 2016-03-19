package app.steganosaurus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import steganosaurus.R;

public class MainMenu extends AppCompatActivity {

    private final int ENCRYPT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Button Hide_secrets_button =(Button) findViewById();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //user taps one menu button, start corresponding activity
    }

    public void encrypt(View v) {
        String button_title = (String)((Button)v).getText();
        Toast.makeText(this, "You clicked on " + button_title, Toast.LENGTH_SHORT);
        Intent i = new Intent(this, EncryptActivity.class);
        startActivityForResult(i, ENCRYPT_CODE);
    }

}
