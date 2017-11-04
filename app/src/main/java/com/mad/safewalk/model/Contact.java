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

/**
 * This class represents a one contact object. The contact includes a name and a number
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


