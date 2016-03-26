package app.steganosaurus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.steganosaurus.Utility.PropertiesManager;
import steganosaurus.R;


/**
 * Activity class to select resolution and compression preferences
 */
public class OptionsActivity extends AppCompatActivity {

    PropertiesManager propertiesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        propertiesManager = new PropertiesManager(this);

        Spinner res_spinner = (Spinner) findViewById(R.id.options_resolution_spinner);
        String [] res_items = {"low", "medium", "high"};
        setSpinnerItems(res_spinner, res_items);

        Spinner comp_spinner = (Spinner) findViewById(R.id.options_compression_spinner);
        String [] comp_items = {"low", "medium", "high"};
        setSpinnerItems(comp_spinner, comp_items);
    }

    /**
     * Set spinner items programmatically on activity creation
     * @param s the spinner to populate
     * @param items an array of Strings representing the items
     */
    private void setSpinnerItems(Spinner s, String [] items) {
        List<String> spinnerArray =  new ArrayList<>();
        spinnerArray.addAll(Arrays.asList(items));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    /**
     * Callback. Allows user to save their selected options
     * @param v the button that was clicked
     */
    public void saveOptions(View v) {
        Spinner resolutionSpinner = (Spinner)findViewById(R.id.options_resolution_spinner);
        Spinner compressionSpinner = (Spinner)findViewById(R.id.options_compression_spinner);

        String selectedResolution = resolutionSpinner.getSelectedItem().toString();
        String selectedCompression = compressionSpinner.getSelectedItem().toString();

        String resProp = propertiesManager.getProperty("resolution");
        String compProp = propertiesManager.getProperty("compression");

        if (!selectedResolution.equals(resProp))
            propertiesManager.setProperty("resolution", selectedResolution);
        if (!selectedCompression.equals(compProp))
            propertiesManager.setProperty("compression", selectedCompression);
    }

    /**
     * Callback. Allows user to return to the previous activity
     * @param v the button that was clicked
     */
    public void goBack(View v) {
        this.finish();
    }

}
