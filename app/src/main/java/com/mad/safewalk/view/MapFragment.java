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

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mad.safewalk.R;
import com.mad.safewalk.model.Contact;
import com.mad.safewalk.model.ContactList;
import com.mad.safewalk.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private List<Address> mAddressList;
    private Marker mMarker;
    private File mImageFile;
    private ContactList mContactList;
    private ArrayList<Contact> mList;
    private Address mAddress;

    private OnFragmentInteractionListener mListener;

    /**
     * empty MapFragment constructor
     */
    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContactList = ContactList.getInstance(getActivity().getApplicationContext());
        mList = mContactList.getList();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Your Location");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            sendAlert();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*
     * adds title to the fragment
     */
    public void onButtonPressed(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
     * saves address in a shared preference
     * @param address
     */
    private void saveAddress(Address address)
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.LOCATION_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.LOCATION_LOCALITY, address.getLocality().toString());
        editor.putString(Constants.LOCATION_LATITUDE, "" + address.getLatitude());
        editor.putString(Constants.LOCATION_LONGITUDE, "" + address.getLongitude());
        editor.commit();
    }

    /**
     * sends sms alerts to saved emergency contacts
     */
    public void sendAlert() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.MESSAGE_DATA, Context.MODE_PRIVATE);
        String personalMessage = sharedPreferences.getString(Constants.MESSAGE_VALUE, Constants.DEFAULT);

        String message = "" + personalMessage + " " + getResources().getString(R.string.location) + " " + mAddress.getLocality().toString() + " " + getResources().getString(R.string.postcode) + " " + mAddress.getPostalCode().toString() + " " + getResources().getString(R.string.country) + " " + mAddress.getCountryName().toString();


        try {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.SEND_SMS}, 1);
            SmsManager smsManager = SmsManager.getDefault();
            for (Contact contact : mList) {
                smsManager.sendTextMessage(contact.getNumber().toString(), null, message.toString(), null, null);
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity().getApplicationContext(), "ALERT Sent!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //The last location is got by using fused location API
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //Checks if the last location is null
        if (mLastLocation == null)
        {
            Toast.makeText(getActivity(), getResources().getString(R.string.location_not_found), Toast.LENGTH_LONG).show();
        }
        else
        {
            //Makes an object of LatLng using the last location latitude and longitude
            LatLng location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            //Creates the update for camera and sets the location and zoom level
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, Constants.ZOOM_LEVEL);
            //Calls animate  camera to move to the location in an animated way rather than direct through moveCamera()
            mMap.animateCamera(update);
            getLocationAddress();

        }

        //Location request is the class in which you package up all the information you need on how to use the location service
        LocationRequest locationRequest = LocationRequest.create();
        //It is set to high accuracy because this will give the most recent report of location changes
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Google recommends to check for updates every one minute as this will save the battery power
        locationRequest.setInterval(Constants.LOCATION_REQUEST_INTERVAL);
        //sets the rate at which your app can handle location updates
        locationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Constants.SUSPENDED, getResources().getString(R.string.GoogleAPIClient_Suspended));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(Constants.FAILED, getResources().getString(R.string.GoogleAPIClient_Failed));
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            Geocoder geoCoder = new Geocoder(getActivity());
            List<Address> list = geoCoder.getFromLocation(location
                    .getLatitude(), location.getLongitude(), 1);
            Address address = list.get(Constants.DEFAULT_VALUE);
            //Toast.makeText(getActivity(), "" + address.getLocality(), Toast.LENGTH_SHORT).show();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
        setMapInfoWindowAdapter();

    }

    /**
     * set up map to be used
     */
    public void setUpMap() {

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     * set what the window will display
     */
    private void setMapInfoWindowAdapter()
    {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.marker_info, null);
                TextView current_locality = (TextView) view.findViewById(R.id.current_locality);
                TextView current_latitude = (TextView) view.findViewById(R.id.current_latitude);
                TextView current_longitude = (TextView) view.findViewById(R.id.current_longitude);
                TextView current_postcode = (TextView) view.findViewById(R.id.current_postcode);

                LatLng current_position = marker.getPosition();
                current_locality.setText(getResources().getString(R.string.current_location) + marker.getTitle());
                current_latitude.setText(getResources().getString(R.string.latitude)+ current_position.latitude);
                current_longitude.setText(getResources().getString(R.string.longitude)+ current_position.longitude);
                current_postcode.setText(getResources().getString(R.string.postcode) + marker.getSnippet());

                return view;
            }
        });
    }

    /**
     * ask the user for permissions
     * @return
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Google Maps API uses GeoLocation services of adding longitudes and latitudes to get ones location
     *I am using a map fragment and not a support map fragment
     *
     *  The code below uses GoogleApiClient the recommended method by Google. The connect() is called to connect
     *  to the services and disconnect method is called onStop()
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }

    /**
     * The Geocorder class is used to convert latitudes and longitudes to their address (name) and vice versa
     */
    public void getLocationAddress()
    {
        try {
            Geocoder geocoder = new Geocoder(getActivity());
            mAddressList = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);

        }catch (IOException e)
        {
            //This prints out where the errors took place and the type of error.
            e.printStackTrace();
        }
        mAddress = mAddressList.get(0);
        saveAddress(mAddress);
        addMarker(mAddress);

    }

    /**
     * adds Marker on the app
     * @param address
     */
    private void addMarker(Address address)
    {
        //I tried getFeatureName() on the address object but it does not work
        //so you get Locality
        String locality = address.getLocality();
        //Creates the marker options to add to the map
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .position(new LatLng(address.getLatitude(), address.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action))
                .snippet(address.getPostalCode());
        //defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        //BitmapDescriptorFactory Object is the one that helps in defining the icon attributes
        //And the BitMapDescriptorFactory constants are used to describe the color of the icon

        mMarker = mMap.addMarker(options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
}
