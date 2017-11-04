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
package com.mad.safewalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.mad.safewalk.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link FacebookFragment#} factory method to
 * create an instance of this fragment.
 * code slidenerd: github.com.slidenerd
 */
public class FacebookFragment extends Fragment {
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private TextView mFacebookText;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private static final List<String> PERMISSIONS = Arrays.asList(Constants.PERMISSION);
    private AccessToken mAccessToken;

    private MapFragment.OnFragmentInteractionListener mListener;
    /*
     *
     * Login to Facebook is controlled by the android sdk but after login we need to check if the login was successful or
     * not using the FacebookCallBack which calls the Facebook SDK
     *
     * LoginResult - this class shows the result of login operations
     */
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>()
    {
        @Override
        public void onSuccess(LoginResult loginResult)
        {
            //Note: Your accessToken is not fixed you can loose it at anytime when you loose connection or other errors
            mAccessToken = loginResult.getAccessToken(); //Gets you the access token
            //Note: The user profile is also not fixed so you can also change like name, picture, which can cause changes to the app
            //Gets you the profile of the person logged in
            Profile profile = Profile.getCurrentProfile();
            displayWelcome(profile);
            retriveData();
        }

        @Override
        public void onCancel()
        {
            Log.d(Constants.CANCEL, getResources().getString(R.string.operation_cancel));
        }

        @Override
        public void onError(FacebookException e)
        {
            Log.d(Constants.ERROR, getResources().getString(R.string.facebook_error));
        }
    };

    /**
     * Empty Constructor of fragment
     *
     */
    public FacebookFragment()
    {

    }

    /**
     * Code that extracts the location data and saves it to various variables
     *
     */
    public void retriveData()
    {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Constants.LOCATION_DATA, Context.MODE_PRIVATE);
        String locality = sharedPreferences.getString(Constants.LOCATION_LOCALITY, Constants.DEFAULT);
        String latitude = sharedPreferences.getString(Constants.LOCATION_LATITUDE, Constants.DEFAULT);
        String longitude = sharedPreferences.getString(Constants.LOCATION_LONGITUDE, Constants.DEFAULT);

        String url = getResources().getString(R.string.google_link);

        postOnWall(url, locality, latitude, longitude);
    }

    /**
     * Creates the link to post and share it on the user's wall
     *
     * It also display a Toast Message
     *
     * @param url
     * @param locality
     * @param latitude
     * @param longitude
     */
    private void postOnWall(String url, String locality, String latitude, String longitude)
    {

        String link = url + "?api=1&query=" + latitude + "," + longitude;
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(link))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#HelpMePlease")
                        .build())
                .setPlaceId("Please help me at this address")
                .build();
        ShareApi.share(content, null);

        Profile profile = Profile.getCurrentProfile();
        Toast.makeText(this.getActivity(), getResources().getString(R.string.successful_post), Toast.LENGTH_LONG).show();
    }

    /**
     * Creates and starts the tracking of tokens and profiles
     *
     * References : https://www.youtube.com/slidenerd
     *
     * @param savedInstanceState
     */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_facebook);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        //Initialises the call back manager used for handling onActivity result from the facebook fragment
        mCallbackManager = CallbackManager.Factory.create();
        //In order to prevent errors caused when you loose the token or the profile we use trackers for both
        //For example : below is a AccessToken Tracker followed by a ProfileTracker
        mTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken)
            {


            }
        };

        mProfileTracker = new ProfileTracker()
        {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile)
            {

            }
        };

        //Because it is not a continuos process it needs to be stopped
        //Start the tracker but both of these need to be stopped on Resume or On Destroy or On Stop
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }

    /**
     * adds title to the fragment
     * @param title
     */
    public void onButtonPressed(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    /**
     * Inflates the layout for the fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (mListener != null) {
            mListener.onFragmentInteraction("Facebook");
        }
        //creates the layout for the fragment
        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    /**
     * Initialises the buttons, setsFragment on the button, registers the Call Back Manager
     *
     * References: https://www.youtube.com/slidenerd
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mFacebookText = (TextView) view.findViewById(R.id.facebook_text);
        mLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        //request permissions
        mLoginButton.setPublishPermissions(PERMISSIONS);
        //sets the button on the fragment
        mLoginButton.setFragment(FacebookFragment.this);
        //where the mCallBackManager will handle everything, and mCallBack will handle the results
        mLoginButton.registerCallback(mCallbackManager, mCallBack);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapFragment.OnFragmentInteractionListener) {
            mListener = (MapFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * OnActivityResult is passed to the callBackManager
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Note: You for facebook you should always pass the onActivityResult to the callBackManager
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Checks if the user is still logged in to his/her facebook and display the right information
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        if(isLoggedIn())
        {
            mFacebookText.setText(getResources().getString(R.string.welcome_message));
        }
        else
        {
            mFacebookText.setText(getResources().getString(R.string.wall_post));
        }
    }

    /**
     * Checks if the user is still logged into the system
     *
     * @return true/false
     */
    public boolean isLoggedIn()
    {
        return mAccessToken != null;
    }

    /**
     * Display the welcome message to the user after log in
     *
     * @param profile
     */
    private void displayWelcome(Profile profile)
    {
        if(profile != null)   //Note:  the profile maybe null
        {
            mFacebookText.setText(getResources().getString(R.string.welcome_message) + profile.getName());
        }
        else
        {
            mFacebookText.setText(getResources().getString(R.string.wall_post));
        }
    }

    /**
     * Stops tracking of the system and logs out the user by checking if his logged in
     *
     */
    @Override
    public void onStop()
    {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * handle interactons
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }

}
