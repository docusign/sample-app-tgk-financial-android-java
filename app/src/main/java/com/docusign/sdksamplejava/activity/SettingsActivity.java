package com.docusign.sdksamplejava.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.SDKSampleApplication;
import com.docusign.sdksamplejava.utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Button saveButton = findViewById(R.id.save_button);
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        final EditText integratorKeyEditText = findViewById(R.id.integrator_key_edit_text);
        final EditText clientSecretEditText = findViewById(R.id.client_secret_edit_text);
        final EditText redirectURIEditText = findViewById(R.id.redirect_uri_edit_text);
        final String integratorKey = sharedPreferences.getString(Constants.DOCUSIGN_INTEGRATOR_KEY_PREF, Constants.DOCUSIGN_INTEGRATOR_KEY);
        final String clientSecret = sharedPreferences.getString(Constants.CLIENT_SECRET_KEY_PREF, Constants.CLIENT_SECRET_KEY);
        final String redirectURI = sharedPreferences.getString(Constants.REDIRECT_URI_PREF, Constants.REDIRECT_URI);
        integratorKeyEditText.setText(integratorKey);
        clientSecretEditText.setText(clientSecret);
        redirectURIEditText.setText(redirectURI);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(clientSecretEditText.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter Client Secret", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(integratorKeyEditText.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter Integrator Key", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(redirectURIEditText.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter Redirect Uri", Toast.LENGTH_LONG).show();
                    return;
                }

                sharedPreferences.edit().putString(Constants.DOCUSIGN_INTEGRATOR_KEY_PREF, integratorKeyEditText.getText().toString()).apply();
                sharedPreferences.edit().putString(Constants.CLIENT_SECRET_KEY_PREF, clientSecretEditText.getText().toString()).apply();
                sharedPreferences.edit().putString(Constants.REDIRECT_URI_PREF, redirectURIEditText.getText().toString()).apply();

                ((SDKSampleApplication) getApplication()).initializeDocuSign();
                finish();
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
}
