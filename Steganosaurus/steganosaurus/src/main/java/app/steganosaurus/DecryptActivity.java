package app.steganosaurus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import steganosaurus.R;

public class DecryptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);

    }

    public void backHome(View v) {
        this.finish();
    }


}
