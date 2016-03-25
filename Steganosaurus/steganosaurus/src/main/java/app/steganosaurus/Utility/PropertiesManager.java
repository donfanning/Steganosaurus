package app.steganosaurus.Utility;

import android.app.Activity;
import android.content.SharedPreferences;

public class PropertiesManager {

    Activity context;
    SharedPreferences sharedPref;

    public PropertiesManager(Activity _context) {
        context = _context;
        sharedPref = context.getPreferences(context.MODE_PRIVATE);
    }

    public String getProperty(String property) {
        String defaultValue = "medium";
        return sharedPref.getString(property, defaultValue);
    }

    public void setProperty(String property, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(property, value);
        editor.apply();
    }

}
