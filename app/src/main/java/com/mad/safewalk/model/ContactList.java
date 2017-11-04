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

package com.mad.safewalk.model;


import android.content.Context;

import java.util.ArrayList;

import com.mad.safewalk.R;

/**
 * This class is created to act as the Singleton class to provide data to all other activities when required
 */
public class ContactList {

    private static ContactList contactList = null;
    private ArrayList<Contact> mList;

    /**
     * Empty Constructor that I only called once
     */
    protected ContactList(Context context) {
        mList = new ArrayList();
        mList.add(new Contact(context.getString(R.string.MainContact),"0413401650"));
    }


    /**
     * Method to create or return the one instance of contact list
     * @return instance of contact list
     */
    public static ContactList getInstance(Context context) {
        if (contactList == null) {
            contactList = new ContactList(context);
        }
        return contactList;
    }

    /**
     * adds contact to the contact list created above
     * @param name
     * @param number
     */

    public void addContact(String name, String number) {
        mList.add(new Contact(name, number));
    }

    /**
     * Return the array list of contact objects
     * @return array list
     */
    public ArrayList<Contact> getList() {
        return mList;
    }
}

