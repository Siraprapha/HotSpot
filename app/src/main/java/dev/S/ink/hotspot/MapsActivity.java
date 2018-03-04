package dev.S.ink.hotspot;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener{

    public DrawerLayout mDrawer;
    public Toolbar toolbar;
    public NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;
    View header_view;
    private static final String TAG = "MapsActivity";
    ImageView kml_color_level;

    PopupMenu popup_ffmc;
    MenuInflater inflater_ffmc;
    PopupMenu popup_fwi;
    MenuInflater inflater_fwi;
    MenuItem last_item;

    Fragment current_fragment;

    Handler handler;

    Fragment mapsFragment;
    Fragment about_us_fragment;

    public final Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            Toast.makeText(MapsActivity.this, "Refresh ", Toast.LENGTH_SHORT).show();
            if (mapsFragment instanceof MapsFragment) {
                String url = "http://tatam.esy.es/test/api.php?key=maprealtime";
                //((MapsFragment)mapsFragment).CallJsonHotSpot(((MapsFragment)mapsFragment).getmMap(), url);
            }
            //handler.postDelayed(runnable,60000);
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

        //handler = new Handler();
        //handler.postDelayed(runnable, 60000);

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
        nvDrawer.setItemIconTintList(null);
        // Inflate the header view at runtime
        //View headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        header_view = nvDrawer.getHeaderView(0);

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
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (!(current_fragment instanceof MapsFragment)) {
                    unCheckItems();
                    int count = getSupportFragmentManager().getBackStackEntryCount();
                    for(int i = 0; i < count; ++i) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.terra:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).showSat(0);
                }
                break;
            case R.id.aqua:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).showSat(1);
                }
                break;
            case R.id.sumi:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).showSat(2);
                }
                break;
            case R.id.ffmc:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    showPopUpMenu(0);
                }
                break;
            case R.id.fwi:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    //((MapsFragment) current_fragment).showKML(((MapsFragment) current_fragment).getmMap(),1,3);
                    showPopUpMenu(1);
                }
                break;
            case R.id.pm10:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).onCallJson(2);
                }
                break;
            case R.id.st_forest:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).onCallJson(0);
                }
                break;
            case R.id.st_wilds:
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (current_fragment instanceof MapsFragment) {
                    ((MapsFragment) current_fragment).onCallJson(1);
                }
                break;
            case R.id.call:
                kml_color_level.setVisibility(View.GONE);//remove color index
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (!(current_fragment instanceof UserCall)) {
                    fragment = UserCall.newInstance();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.map, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.about:{
                //Toast.makeText(this,"about",Toast.LENGTH_LONG).show();
                current_fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                if (!(current_fragment instanceof AboutFragment)) {
                    fragment = AboutFragment.newInstance();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.map, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            }
             default:break;
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
        if(!MapsFragment.isNetworkConn()){
            MapsFragment.showInternetAlertDialog();
            return;
        }
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
        }else if(last_item==item){//check same item
            item.setChecked(!last_item.isChecked());
        }else {// check new item
            item.setChecked(true);
            last_item.setChecked(false);
            last_item = item;
        }
    }
    public PopupMenu setPopUpMenu(PopupMenu popup){
        popup.getMenu().getItem(0).setTitle(getDate(0)+" (วันนี้)");
        popup.getMenu().getItem(1).setTitle(getDate(1));
        popup.getMenu().getItem(2).setTitle(getDate(2));
        popup.getMenu().getItem(3).setTitle(getDate(3));
        popup.getMenu().getItem(4).setTitle(getDate(4));
        popup.getMenu().getItem(5).setTitle(getDate(5));
        popup.getMenu().getItem(6).setTitle("ดูข้อมูลย้อนหลัง...");
        return popup;
    }
    public void onClickDays_ffmc(MenuItem item,boolean checked){
        MapsFragment m = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(checked){

            switch (item.getItemId()){
                case R.id.ffmc_0:
                    m.showKML(0,1);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.ffmc_1:
                    m.showKML(0,2);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.ffmc_2:
                    m.showKML(0,3);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.ffmc_3:
                    m.showKML(0,4);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.ffmc_4:
                    m.showKML(0,5);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.ffmc_5:
                    m.showKML(0,6);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.ffmc_past:
                    //m.showKML(m.getmMap(),0,6);
                    kml_color_level.setVisibility(View.GONE);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www2.dnp.go.th/gis/FDRS/Blog%20Posts/Archive_SEA.php"));
                    startActivity(browserIntent);
                    break;
            }
        }else {
            kml_color_level.setVisibility(View.GONE);
            m.removeLayer();
        }
    }
    public void onClickDays_fwi(MenuItem item,boolean checked){
        MapsFragment m = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(checked){
            switch (item.getItemId()){
                case R.id.fwi_0:
                    m.showKML(1,1);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.fwi_1:
                    m.showKML(1,2);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.fwi_2:
                    m.showKML(1,3);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.fwi_3:
                    m.showKML(1,4);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.fwi_4:
                    m.showKML(1,5);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.fwi_5:
                    m.showKML(0,6);
                    kml_color_level.setVisibility(View.VISIBLE);
                    break;
                case R.id.fwi_past:
                    //m.showKML(m.getmMap(),0,6);
                    kml_color_level.setVisibility(View.GONE);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www2.dnp.go.th/gis/FDRS/Blog%20Posts/Archive_SEA.php"));
                    startActivity(browserIntent);
                    break;
            }
        }else {
            kml_color_level.setVisibility(View.GONE);
            m.removeLayer();
        }
    }
    public void unCheckItems(){
        last_item = null;
        for(int i=0;i<popup_ffmc.getMenu().size();i++){
            popup_ffmc.getMenu().getItem(i).setChecked(false);
        }
        for(int i=0;i<popup_fwi.getMenu().size();i++){
            popup_fwi.getMenu().getItem(i).setChecked(false);
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
            unCheckItems();
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
        return dateformat.format(cal.getTime());
    }

}