package app.steganosaurus.Utility;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 *  A class to manage the app's options
 *  for image resolution and compression
 *  using Android's SharedPreferences
 */
public class PropertiesManager {

    Activity context;
    SharedPreferences sharedPref;

    /**
     * @param _context the activity from which the class is created
     */
    public PropertiesManager(Activity _context) {
        context = _context;
        sharedPref = context.getPreferences(context.MODE_PRIVATE);
    }

    /**
     * @param property the property to extract the value from
     * @return the property's value
     */
    public String getProperty(String property) {
        String defaultValue = "medium";
        return sharedPref.getString(property, defaultValue);
    }

    /**
     * @param property the property to modify
     * @param value the value to set the property to
     */
    public void setProperty(String property, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(property, value);
        editor.apply();
    }

}
