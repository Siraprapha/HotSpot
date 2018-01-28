package com.example.ink.hotspot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

public class MapsActivity extends AppCompatActivity implements Login.LoginListener, View.OnClickListener{

    public DrawerLayout mDrawer;
    public Toolbar toolbar;
    public NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;
    View header_view;
    TextView nav_header_name;
    private static final String TAG = "MapsActivity";
    TextView nav_header_circle;

    UserPref userpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maps);

        setDrawer();

        Fragment mapsFragment = MapsFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map, mapsFragment)
                            .commit();

    }
    //toolbar toggle
    public void setDrawer(){
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        // Find our drawer view
        nvDrawer = findViewById(R.id.nvView);
        // Inflate the header view at runtime
        //View headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        header_view = nvDrawer.getHeaderView(0);
        header_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Class user_acc = Login.class;
                Fragment user_acc = Login.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.map,user_acc)
                        .addToBackStack(null)
                        .commit();
                mDrawer.closeDrawers();
            }
        });
        nav_header_circle = header_view.findViewById(R.id.nav_header_circle);
        nav_header_name = header_view.findViewById(R.id.nav_header_name);

        // Setup drawer view
        setupDrawerContent(nvDrawer);
    }

    //Toggle
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //Select Item
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment;
        switch (menuItem.getItemId()) {
            case R.id.home:
                Fragment current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (!(current_fragment instanceof MapsFragment)) {
                    getSupportFragmentManager().popBackStack();
                }
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ffmc:
                //mapsFragment.showKML(mMap,0);
                Toast.makeText(this,"ffmc",Toast.LENGTH_LONG).show();
                break;
            case R.id.fwi:
                //mapsFragment.showKML(mMap,1);
                Toast.makeText(this,"fwi",Toast.LENGTH_LONG).show();
                break;
            case R.id.st_forest:
                //mapsFragment.caseJson(0);
                Toast.makeText(this,"forest",Toast.LENGTH_LONG).show();
                break;
            case R.id.st_wilds:
                //mapsFragment.caseJson(1);
                Toast.makeText(this,"wild",Toast.LENGTH_LONG).show();
                break;
            case R.id.call:
                fragment = CallFromUser.newInstance();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.map, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.about:
                Toast.makeText(this,"about",Toast.LENGTH_LONG).show();
                break;
            default:
                fragment = null;
        }
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Toast.makeText(MapsActivity.this, "Refresh ", Toast.LENGTH_SHORT).show();
            //handler.postDelayed(runnable,10000);
        }
    };

    //Dialog
    private void ShowDialogExit(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("คุณต้องการที่จะออกจากแอพ?");
        builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    //LoginListener
    @Override
    public void onLoginSuccess(String username) {
        nav_header_name.setText(username);
        nav_header_circle.setText(username.substring(0,1));
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()==0){
            ShowDialogExit();
        }else {
            super.onBackPressed();
        }
    }

}