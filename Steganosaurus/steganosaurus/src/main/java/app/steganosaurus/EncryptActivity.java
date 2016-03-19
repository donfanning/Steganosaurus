package app.steganosaurus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import steganosaurus.R; //TODO may cause problem


public class EncryptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_encrypt);
    }

}
