package com.mdlive.embedkit.uilayer.login;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.navigation.NavigationDrawerCallbacks;
import com.mdlive.embedkit.uilayer.navigation.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.pharmacy.activities.MDLivePharmacy;

/**
 * Created by unnikrishnan_b on 4/3/2015.
 */
public class MDLiveDashboard extends ActionBarActivity
        implements NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    LinearLayout savLl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_dashboard);
        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        //LocalisationHelper.localiseLayout(this, view);
        savLl=(LinearLayout)findViewById(R.id.SavLl);
        savLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
//        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);
//        setSupportActionBar(mToolbar);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.setUserData(getResources().getString(R.string.app_name), "Accounts & Settings", BitmapFactory.decodeResource(getResources(), R.drawable.arrow_down));
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void onButtonClick(){
        //card io implementation
       // Intent i = new Intent(getApplicationContext(),MDLBTMHMedications.class);
        Intent i = new Intent(getApplicationContext(),MDLivePharmacy.class);
        startActivity(i);
    }
}

