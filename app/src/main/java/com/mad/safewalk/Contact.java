package com.mad.safewalk;

/**
 * This class represents a single contact object
 */
public class Contact {
    private String mName;
    private String mNumber;

    /**
     * Constructor for the Contact object
     *
     * @param name
     * @param number
     */
    public Contact(String name, String number) {
        this.mName = name;
        this.mNumber = number;
    }

    /**
     * Setter for Name
     *
     * @param name
     */
    private void setName(String name) {
        this.mName = name;
    }

    /**
     * Setter for Number
     *
     * @param number
     */
    private void setNumber(String number) {
        this.mNumber = number;
    }

    /**
     * Getter for Name
     *
     * @return mName
     */
    public String getName() {
        return this.mName;
    }

    /**
     * Getter for Number
     *
     * @return mNumber
     */
    public String getNumber() {
        return this.mNumber;
    }

}


