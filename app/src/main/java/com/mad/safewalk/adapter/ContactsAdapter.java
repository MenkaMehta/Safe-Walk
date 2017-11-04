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

package com.mad.safewalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mad.safewalk.R;
import com.mad.safewalk.model.Contact;

import java.util.ArrayList;

/**
 * This class represents a custom adapter for the model contact and list view
 */
public class ContactsAdapter extends BaseAdapter {
    //Declare the BusData list and context
    ArrayList<Contact> mlist;
    Context mContext;

    /**
     * This constructor for the adapter
     * @param c
     * @param contactList
     */
    public ContactsAdapter(Context c, ArrayList contactList) {
        mContext = c;  //Sets the context (base)
        mlist = contactList;
    }

    /**
     * Return the size of the array in consideration
     * @return array size
     */
    @Override
    public int getCount()
    {
        return mlist.size();
    }
    /**
     * Return the contact object at a specified position
     * @param position
     * @return contact object
     */
    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    /**
     * Return the position on the list view
     * @param position
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;  //We do this since we are not dealing with databases at the moment populate etc
    }

    /**
     * This method is called within each row of the system. It is used to recycle views
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, null);
        }

        TextView name = (TextView) view.findViewById(R.id.contact_list_item_nameTextView);
        TextView number = (TextView) view.findViewById(R.id.contact_list_item_numberTextView);

        Contact contact = mlist.get(position);
        name.setText(contact.getName());
        number.setText("" + contact.getNumber());

        imageButtonClick(view, position);

        return view;
    }

    /**
     * Is used to control the user action on the delete button of the list view item.
     * @param view
     * @param position
     */
    public void imageButtonClick(final View view, final int position)
    {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = mlist.get(position);
                mlist.remove(contact);
                notifyDataSetChanged();
            }
        };
        ImageButton button = (ImageButton) view.findViewById(R.id.delete_Button);
        button.setOnClickListener(listener);
    }
}
