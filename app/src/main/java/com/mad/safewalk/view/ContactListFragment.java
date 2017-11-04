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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.mad.safewalk.R;
import com.mad.safewalk.adapter.ContactsAdapter;
import com.mad.safewalk.model.ContactList;
import com.mad.safewalk.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mContactAdapter;
    private ListView mListView;
    private ContactsAdapter mContactsAdapter;
    private ContactList mContactList;
    private Button mAddButton;

    private OnFragmentInteractionListener mListener;

    public ContactListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactListFragment newInstance(String param1, String param2) {
        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Emergency Contacts");
        }

        mAddButton = (Button) view.findViewById(R.id.addContact);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddButton();
            }
        });

        mListView = (ListView) view.findViewById(R.id.activity_emergency_contacts_listView);

        mContactList = ContactList.getInstance(getActivity().getApplicationContext());
        //mContactList.addContact(getResources().getString(R.string.my_name), getResources().getString(R.string.my_number));

        mContactsAdapter = new ContactsAdapter(getActivity(), mContactList.getList());
        mListView.setAdapter(mContactsAdapter);
        return view;
    }

    /**
     * This method handle add button actions
     */
    public void handleAddButton() {
        Intent request = new Intent(getActivity(), AddContact.class);
        startActivityForResult(request, Constants.REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:

                if (resultCode == Constants.RESULT) {
                    String name = data.getExtras().getString(Constants.NAME);
                    String number = data.getExtras().getString(Constants.NUMBER);
                    mContactList.addContact(name, number);
                    mContactsAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * this method adds title to the fragment
     */
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
}
