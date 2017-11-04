/*
 * Copyright (C) 2017 Menka J Mehta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mad.safewalk.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mad.safewalk.R;
import com.mad.safewalk.service.Accelerometer;

/**
 * The main activity that linearly supports other activities
 */
public class MainNavigationActivity extends AppCompatActivity implements

        MapFragment.OnFragmentInteractionListener,
        ContactListFragment.OnFragmentInteractionListener,
        SaveMessageFragment.OnFragmentInteractionListener,
        FacebookFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private static final String TAG = "MainNavigationActivity";
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName, txtWebsite;
    private NavigationView navigationView;
    private static final String urlProfileImg = "https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAAdZAAAAJDhhN2ZhNjNlLWM5ZDUtNDkzMS05YWQ2LTljN2FkYzBjMjUxOA.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_navigation_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }};

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_map);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.userName);
        txtWebsite = (TextView) navHeader.findViewById(R.id.userEmail);
        imgProfile = (ImageView) navHeader.findViewById(R.id.userImage);
        loadNavHeader();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NOTE:  Open fragment1 initially.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, new MapFragment());
        ft.commit();
    }

    private void loadNavHeader() {
        // name, website
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser.getDisplayName()!=null) {
            txtName.setText(mUser.getDisplayName());
        }else {
            //txtName.setText("Menka Mehta");
            txtName.setText(R.string.menka_mehta);
        }
        if(mUser.getEmail()!=null) {
            txtWebsite.setText(mUser.getEmail());
        }else {
           // txtWebsite.setText("menka@saibaba.info");
            txtWebsite.setText(R.string.website_main);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        //NOTE: creating fragment object
        Fragment fragment = null;

        if (id == R.id.nav_map) {
            fragment = new MapFragment();
        } else if (id == R.id.nav_emerg_contact) {
            fragment = new ContactListFragment();
        }else if (id == R.id.nav_save_message) {
            fragment = new SaveMessageFragment();
        }else if (id == R.id.nav_facebook) {
            fragment = new FacebookFragment();
        }else if (id == R.id.nav_logout) {
            logout();
        }

        //NOTE: Fragment changing code
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }

        //NOTE:  Closing the drawer after selecting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //Ya you can also globalize this variable :P
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onFragmentInteraction(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void logout() {
        mFirebaseAuth.signOut();
        Intent intent = new Intent(MainNavigationActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, Accelerometer.class);
        //Start Service
        startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainNavigationActivity.this, Accelerometer.class));
    }
}

