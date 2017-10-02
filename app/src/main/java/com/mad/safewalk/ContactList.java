package com.mad.safewalk;

import java.util.ArrayList;

/**
 * This class is created to act as the Singleton class to provide data to all other activities when required
 */
public class ContactList {

    private static ContactList contactList = null;
    private ArrayList<Contact> mList;

    /**
     * Empty Constructor that I only called once
     */
    protected ContactList() {
        mList = new ArrayList();
    }

    /**
     * Method to create or return the one instance of contact list
     * @return instance of contact list
     */
    public static ContactList getInstance() {
        if (contactList == null) {
            contactList = new ContactList();
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
     *
     * @return array list
     */

    public ArrayList<Contact> getList() {
        return mList;
    }
}

