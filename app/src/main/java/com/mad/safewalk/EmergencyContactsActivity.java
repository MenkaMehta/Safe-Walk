package com.mad.safewalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * This activity is used to see the list view and manage the contact list by adding or deleting
 */
public class EmergencyContactsActivity extends AppCompatActivity {

    private ListView mListView;
    private ContactsAdapter mContactsAdapter;
    private ContactList mContactList;

    /**
     * Creates the listView, ContactList, BaseAdapter and sets it on the listView
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        mListView = (ListView) findViewById(R.id.activity_emergency_contacts_listView);

        mContactList = ContactList.getInstance();
        mContactList.addContact(getResources().getString(R.string.my_name), getResources().getString(R.string.my_number));

        mContactsAdapter = new ContactsAdapter(this, mContactList.getList());
        mListView.setAdapter(mContactsAdapter);
    }

    /**
     * Starts a new add activity and expects results to come back from the other activity
     * @param view
     */

    public void handleAddButton(View view) {
        Intent request = new Intent(EmergencyContactsActivity.this, AddContact.class);
        startActivityForResult(request, Constants.REQUEST);
    }

    /**
     * Extracts the information from the intent
     * makes a new contact object and adds it to the list
     * Notifies the base adapter of the data change
     * @param requestcode
     * @param resultcode
     * @param data
     */

    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        switch (requestcode) {
            case 1:

                if (resultcode == Constants.RESULT) {
                    String name = data.getExtras().getString(Constants.NAME);
                    String number = data.getExtras().getString(Constants.NUMBER);
                    mContactList.addContact(name, number);
                    mContactsAdapter.notifyDataSetChanged();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergency_contacts, menu);
        return true;
    }

    /**
     * Checks all the contacts one send each to the sendData() Method
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Alert) {
            for (Contact contact : mContactList.getList()) {
                //send a message....to be implemented
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
