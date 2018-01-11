package com.example.ink.hotspot;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by die_t on 12/20/2017.
 */

public class NaviBar extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;

    public NaviBar(DrawerLayout mDrawer,Toolbar toolbar,NavigationView nvDrawer,ActionBarDrawerToggle drawerToggle){
        this.mDrawer = mDrawer;
        this.toolbar = toolbar;
        this.nvDrawer = nvDrawer;
        this.drawerToggle = drawerToggle;

    }


}
