package com.mad.safewalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The activity required for adding a single contact data object
 */
public class AddContact extends AppCompatActivity{
    private EditText mEditTextName;
    private EditText mEditTextNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mEditTextName = (EditText) findViewById(R.id.addContact_editTextName);
        mEditTextNumber = (EditText) findViewById(R.id.addContact_editTextNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    /**
     * This is the method required to save a contact to the contact list
     */
    public void saveContact(View view) {
        Intent intent;
        if (inputValid()) {
            intent = new Intent(AddContact.this, EmergencyContactsActivity.class);
            intent.putExtra(Constants.NAME, mEditTextName.getText().toString());
            intent.putExtra(Constants.NUMBER, mEditTextNumber.getText().toString());
            setResult(Constants.RESULT, intent);
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.valid_number), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks if the input value for the contact number is in correct format
     */
    public boolean inputValid() {
        try {
            //Get the string from the edit text by:
            String number = mEditTextNumber.getText().toString();
            int value = Integer.valueOf(number);

            if (number != null && number.matches("04[0-9]{8}") || value == 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }

}
