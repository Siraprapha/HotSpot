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
    public LinearLayout nvheader;
    View header_view;
    TextView nav_header_name;
    TextView nav_header_circle;
    private SwitchCompat switcher;

    public MapsFragment mapsFragment;

    private GoogleMap mMap;

    private String[] user_data;
    private boolean is_login = false;

    UserPref userpref;

    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maps);

        setDrawer();

        if(savedInstanceState == null){
            //CreateMap();
            try {
                NewFragment(MapsFragment.class);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        else {

        }
        String value;
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            value = bundle.getString("casenoti");
            if(value != null){
                switch (value){
                    case "fireupdate":{
                        Log.e("push noti test", "fire update" );
                        break;
                    }
                    case "userrequest":{
                        break;
                    }
                    default:break;
                }
            }
        }

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
                Class user_acc = Login.class;
                try {
                    NewFragment(user_acc);
                    Toast.makeText(getApplication() ,"Login Fragment",Toast.LENGTH_SHORT);
                    mDrawer.closeDrawers();
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
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
    public static final String EXTRA_MESSAGE = "Inkie1234";
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.home:
                //CreateMap();
                fragmentClass = MapsFragment.class;
                mDrawer.openDrawer(GravityCompat.START);

                break;
            case R.id.ffmc:
                mapsFragment.showKML(mMap,0);
                break;
            case R.id.fwi:
                mapsFragment.showKML(mMap,1);
                break;
            case R.id.st_forest:
                //
                mapsFragment.caseJson(0);
                break;
            case R.id.st_wilds:
                //
                mapsFragment.caseJson(1);
                break;
            case R.id.call:
                fragmentClass = CallFromUser.class;
                break;
            case R.id.about:
                //fragmentClass = wildfire_notify.class;
                break;
            default:
                fragmentClass = null;
        }

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);
        if(fragmentClass!= null){
            try {
                NewFragment(fragmentClass);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        // Set action bar title
        TextView toolbar = findViewById(R.id.toolbar_text);
        toolbar.setText(menuItem.getTitle());

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    public void NewFragment(Class fragmentClass) throws IllegalAccessException, InstantiationException {
        Fragment fragment = (Fragment) fragmentClass.newInstance();
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.map, fragment).commit();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Start Map
   /* public void CreateMap() {
        mapsFragment = new MapsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map, mapsFragment);
        fragmentTransaction.commit();
    }*/

    public final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Toast.makeText(MapsActivity.this, "Refresh ", Toast.LENGTH_SHORT).show();
            //handler.postDelayed(runnable,10000);
        }
    };

    //Dialog
    private void ShowDialog(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Please check your internet connection.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(),
                        "...", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onLoginSuccess(boolean x){
        String name;
        if(x){
            userpref = new UserPref(this);
            name = userpref.getFacebookUserInfo("fb_first_name");
            nav_header_name.setText(name);
            nav_header_circle.setText(name.substring(0,1));
        }else{
            name = "ลงชื่อเข้าใช้";
            nav_header_name.setText(name);
            nav_header_circle.setText("A");
        }
    }
    @Override
    public void updateUserPhoto(){

    }


    @Override
    public void onClick(View view) {

    }
}