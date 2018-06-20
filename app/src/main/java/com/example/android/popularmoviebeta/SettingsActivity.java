package com.example.android.popularmoviebeta;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /*
         * Set to be able to go back from the setting to MainActivity
         * without going to close the application
         */
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Navigate to the appropriate clicked items
     * @param item          item Clicked
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get the item id and navigate to the appropriate activity
        int itemId = item.getItemId();

        // If home button was clicked to go back
        if(itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
