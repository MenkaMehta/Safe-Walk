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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mad.safewalk.R;
import com.mad.safewalk.util.Constants;

/**
 * The activity required for adding a single contact data object
 */
public class AddContact extends AppCompatActivity {
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
            intent = new Intent(AddContact.this, MainNavigationActivity.class);
            intent.putExtra(Constants.NAME, mEditTextName.getText().toString());
            intent.putExtra(Constants.NUMBER, mEditTextNumber.getText().toString());
            setResult(Constants.RESULT, intent);
            this.finish();
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
