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

package com.mad.safewalk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.safewalk.model.Contact;
import com.mad.safewalk.model.ContactList;

import java.util.ArrayList;

/**
 * This class runs algorithm for detecting falls using accelerometer values x, y and z
 */
public class Accelerometer extends Service implements SensorEventListener {

    private ContactList mContactList;
    private ArrayList<Contact> mList;
    private static final String TAG = "";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private int TEST_THRESHOLD = 5000;

    private TextView x_indicator;
    private TextView y_indicator;
    private TextView z_indicator;
    private TextView resultView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mContactList = ContactList.getInstance(getApplicationContext());
        mList = mContactList.getList();

        double testValue = Math.abs(10 + 5 + 2 - 3 - 1 - 1)/ 2 * 10000;
        double height = Math.sqrt(Math.pow((5-2),2) + Math.pow((6-3),3) + Math.pow((5-1),2));

        //Accelerometer setup
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;
        double value = 0;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            //Get the current time in milliseconds
            long curTime = System.currentTimeMillis();

            //Confirm and record values after every 100 milliseconds
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                //Check the time speed at which a certain movement on the device was experienced
                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                double height = Math.sqrt(Math.pow((x - last_x), 2) + Math.pow((y - last_y), 3) + Math.pow((z - last_z), 2));

                double gravity = (2 * height) / (diffTime * 10000);


                Log.v(TAG, "Speed is " + speed);
                Log.v(TAG, "gravity is " + gravity);
                Log.v(TAG, "height is " + height);

                if (speed > TEST_THRESHOLD) {
                    //x_indicator.setText("X value is : " + speed);
                    Toast.makeText(this, "Fall index value is : " + speed, Toast.LENGTH_LONG).show();

                        //permissions asked at the beginning
                        SmsManager smsManager = SmsManager.getDefault();

                    //smsManager.sendTextMessage("0413401650", null, "I HAVE FALLEN DOWN.NEED HELP", null, null);

                       for (Contact contact : mList) {
                           smsManager.sendTextMessage(contact.getNumber().toString(), null, "Please help I may have fallen down.Call me as soon as possible.", null, null);
                       }

                   /*     Toast.makeText(getApplicationContext(),
                                "SMS failed, please try again later!", Toast.LENGTH_LONG).show();*/
                        }


                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
