package com.example.ink.hotspot;

import android.os.Handler;
import android.support.v4.widget.ImageViewCompat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements Login.LoginListener, View.OnClickListener{

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    Location mCurrentLocation;
    Location mCameraPosition;

    public DrawerLayout mDrawer;
    public Toolbar toolbar;
    public NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;
    View header_view;
    TextView nav_header_name;
    private static final String TAG = "MapsActivity";
    TextView nav_header_circle;
    ImageView kml_color_level;

    PopupMenu popup_ffmc;
    MenuInflater inflater_ffmc;
    PopupMenu popup_fwi;
    MenuInflater inflater_fwi;
    int popup_item_position;
    MenuItem last_item;

    Fragment current_fragment;

    UserPref userpref;

    Handler handler;

    Fragment mapsFragment;

    public final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MapsActivity.this, "Refresh ", Toast.LENGTH_SHORT).show();
            if (mapsFragment instanceof MapsFragment) {
                String url = "http://tatam.esy.es/test/querytestmodule.php?key=maprealtime";
                ((MapsFragment)mapsFragment).CallJsonHotSpot(((MapsFragment)mapsFragment).getmMap(), url);
            }
            handler.postDelayed(runnable,60000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maps);

        //nav drawer + toolbar
        setDrawer();

        //start map
        mapsFragment = MapsFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map, mapsFragment)
                            .commit();

        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        handler = new Handler();
        handler.postDelayed(runnable, 60000);

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

        popup_ffmc = new PopupMenu(this,toolbar);
        inflater_ffmc = popup_ffmc.getMenuInflater();
        inflater_ffmc.inflate(R.menu.popup_menu_ffmc, popup_ffmc.getMenu());
        popup_fwi = new PopupMenu(this,toolbar);
        inflater_fwi = popup_fwi.getMenuInflater();
        inflater_fwi.inflate(R.menu.popup_menu_fwi, popup_fwi.getMenu());

        kml_color_level = findViewById(R.id.kml_color_level);
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
        boolean IS_POPUP=false;
        Fragment fragment;
        switch (menuItem.getItemId()) {
            case R.id.home:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (!(current_fragment instanceof MapsFragment)) {
                    getSupportFragmentManager().popBackStack();
                }
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ffmc:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    showPopUpMenu(0);
                }
                break;
            case R.id.fwi:
                //mapsFragment.showKML(mMap,1);
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    //((MapsFragment) current_fragment).showKML(((MapsFragment) current_fragment).getmMap(),1,3);
                    showPopUpMenu(1);
                }
                break;
            case R.id.st_forest:
                //mapsFragment.caseJson(0);
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).caseJson(0);
                }
                break;
            case R.id.st_wilds:
                //mapsFragment.caseJson(1);
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).caseJson(1);
                }
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

    //Popup Menu
    public void showPopUpMenu(int KML_KEY){
        if(KML_KEY==0){
            popup_ffmc = setPopUpMenu(popup_ffmc);
            popup_ffmc.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setCheckItem(item);
                    onClickDays_ffmc(item,item.isChecked());
                    return false;
                }
            });
            popup_ffmc.show();
        }else {
            popup_fwi = setPopUpMenu(popup_fwi);
            popup_fwi.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setCheckItem(item);
                    onClickDays_fwi(item,item.isChecked());
                    return false;
                }
            });
            popup_fwi.show();
        }
    }
    public void setCheckItem(MenuItem item){
        if(last_item==null){//check first time
            item.setChecked(true);
            last_item = item;
            Log.e(TAG, "setCheckItem: last_item "+last_item.getTitle());
        }else if(last_item==item){//check same item
            item.setChecked(!last_item.isChecked());
            Log.e(TAG, "setCheckItem: last_item "+last_item.getTitle());
        }else {// check new item
            item.setChecked(true);
            last_item.setChecked(false);
            last_item = item;
            Log.e(TAG, "setCheckItem: last_item "+last_item.getTitle());
        }
    }
    public PopupMenu setPopUpMenu(PopupMenu popup){
        popup.getMenu().getItem(0).setTitle(getDate(-2));
        popup.getMenu().getItem(1).setTitle(getDate(-1));
        popup.getMenu().getItem(2).setTitle(getDate(0)+" (วันนี้)");
        popup.getMenu().getItem(3).setTitle(getDate(1));
        popup.getMenu().getItem(4).setTitle(getDate(2));
        return popup;
    }
    public void setPopUpMenuItem(PopupMenu popup,int item_index){
        popup.getMenu().getItem(item_index).setTitle(getDate(item_index-2));
    }
    public void onClickDays_ffmc(MenuItem item,boolean checked){
        MapsFragment m = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(checked){
        switch (item.getItemId()){
            case R.id.ffmc_2p:
                m.showKML(m.getmMap(),0,1);
                break;
            case R.id.ffmc_1p:
                m.showKML(m.getmMap(),0,2);
                break;
            case R.id.ffmc_0:
                m.showKML(m.getmMap(),0,3);
                break;
            case R.id.ffmc_1f:
                m.showKML(m.getmMap(),0,4);
                break;
            case R.id.ffmc_2f:
                m.showKML(m.getmMap(),0,5);
                break;
        }}else {
            m.removeLayer();
        }
    }
    public void onClickDays_fwi(MenuItem item,boolean checked){
        MapsFragment m = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(checked){
            switch (item.getItemId()){
                case R.id.fwi_2p:
                    m.showKML(m.getmMap(),1,1);
                    break;
                case R.id.fwi_1p:
                    m.showKML(m.getmMap(),1,2);
                    break;
                case R.id.fwi_0:
                    m.showKML(m.getmMap(),1,3);
                    break;
                case R.id.fwi_1f:
                    m.showKML(m.getmMap(),1,4);
                    break;
                case R.id.fwi_2f:
                    m.showKML(m.getmMap(),1,5);
                    break;
            }}else {
            m.removeLayer();
        }
    }
    public void unCheck(MapsFragment m, MenuItem item){
        if(!item.isChecked()){
            item.setChecked(false);
        }
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
    //Date
    public String getDate(int day){
        DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,day);
        Log.e(TAG, "showPopUpMenu: Current Date"+dateformat.format(cal.getTime()) );
        return dateformat.format(cal.getTime());
    }

    //LoginListener
    @Override
    public void onLoginSuccess(String username) {
        Log.e(TAG, "onLoginSuccess: ");
        nav_header_name.setText(username);
        nav_header_circle.setText(username.substring(0,1));
    }



}