package com.mad.safewalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Dell on 26-Sep-17.
 */
public class MainActivity extends AppCompatActivity {

    private ContactList mContactList;
    private ArrayList<Contact> mList;
    private Address mAddress;

    // Add something here later
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactList = ContactList.getInstance();
        mList = mContactList.getList();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sw_activity,menu);
        return true;
    }

    /**
     * This is the method that sends messages (Your saved + location message) to all the contacts saved on your phone
     *
     *
     * @param view
     */
    public void sendAlert(View view)
    {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MESSAGE_DATA, Context.MODE_PRIVATE);
        String personalMessage = sharedPreferences.getString(Constants.MESSAGE_VALUE, Constants.DEFAULT);

        String message = "" + personalMessage;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        SmsManager smsManager = SmsManager.getDefault();
        if(message != null)
        {
            for (Contact contact : mList)
            {
                smsManager.sendTextMessage(contact.getNumber().toString(), null, message.toString(), null, null);
            }
        }
    }

    /**
     * This method moves you around the other activities
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.emergency_contacts:
                intent = new Intent(MainActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.messageActivity:
                intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
                break;
            //message and facebook case to be added
        }

        return super.onOptionsItemSelected(item);
    }

}
