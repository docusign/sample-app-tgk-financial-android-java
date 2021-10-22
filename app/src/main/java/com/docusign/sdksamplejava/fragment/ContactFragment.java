package com.docusign.sdksamplejava.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.model.Client;
import com.docusign.sdksamplejava.utils.Constants;
import com.google.gson.Gson;

public class ContactFragment extends Fragment {

    public static final String TAG = ContactFragment.class.getSimpleName();

    @Nullable
    private Client client;

    public static ContactFragment newInstance(@Nullable Client client) {
        ContactFragment fragment = new ContactFragment();
        fragment.client = client;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (client != null) {
            Activity activity = getActivity();

            if (activity != null) {
                EditText nameTextView = activity.findViewById(R.id.client_name_text_view);
                nameTextView.setText(client.getName());
                EditText phoneTextView = activity.findViewById(R.id.phone_text_view);
                phoneTextView.setText(client.getPhone());
                EditText emailTextView = activity.findViewById(R.id.email_text_view);
                emailTextView.setText(client.getEmail());
                EditText addressLine1TextView = activity.findViewById(R.id.address_line1_text_view);
                addressLine1TextView.setText(client.getAddressLine1());
                EditText addressLine2TextView = activity.findViewById(R.id.address_line2_text_view);
                addressLine2TextView.setText(client.getAddressLine2());
                EditText addressLine3TextView = activity.findViewById(R.id.address_line3_text_view);
                addressLine3TextView.setText(client.getAddressLine3());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Activity activity = getActivity();

        if (activity != null) {
            EditText nameTextView = activity.findViewById(R.id.client_name_text_view);
            String clientName =  nameTextView.getText().toString();

            EditText phoneTextView = activity.findViewById(R.id.phone_text_view);
            String clientPhone = phoneTextView.getText().toString();

            EditText emailTextView = activity.findViewById(R.id.email_text_view);
            String clientEmail = emailTextView.getText().toString();

            EditText addressLine1TextView = activity.findViewById(R.id.address_line1_text_view);
            String clientAddressLine1 = addressLine1TextView.getText().toString();

            EditText addressLine2TextView = activity.findViewById(R.id.address_line2_text_view);
            String clientAddressLine2 = addressLine2TextView.getText().toString();

            EditText addressLine3TextView = activity.findViewById(R.id.address_line3_text_view);
            String clientAddressLine3 = addressLine3TextView.getText().toString();

            if(client != null) {
                client.setName(clientName);
                client.setPhone(clientPhone);
                client.setEmail(clientEmail);
                client.setAddressLine1(clientAddressLine1);
                client.setAddressLine2(clientAddressLine2);
                client.setAddressLine3(clientAddressLine3);

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String clientJson = new Gson().toJson(client);

                if(clientJson != null && sharedPreferences != null) {
                    sharedPreferences.edit().putString(client.getStorePref(), clientJson).apply();
                }
            }
        }
    }
}
