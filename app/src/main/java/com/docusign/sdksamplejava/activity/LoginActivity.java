package com.docusign.sdksamplejava.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSAuthenticationDelegate;
import com.docusign.androidsdk.dsmodels.DSUser;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSAuthenticationListener;
import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.utils.Constants;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    static String TAG = LoginActivity.class.toString();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToDocuSign();
            }
        });

        String title = getString(R.string.login);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            launchSettings();
        }
        return true;
    }


    private void loginToDocuSign() {
        try {
            String clientSecretKey = DocuSign.getInstance().getClientSecret();
            String integratorKey = DocuSign.getInstance().getIntegratorKey();
            String redirectURI = DocuSign.getInstance().getRedirectUri();

            if (TextUtils.isEmpty(clientSecretKey)) {
                Toast.makeText(this, "Please provide Client Secret Key", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(integratorKey)) {
                Toast.makeText(this, "Please provide DocuSIgn Integrator Key", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(redirectURI)) {
                Toast.makeText(this, "Please provide Redirect URI", Toast.LENGTH_LONG).show();
                return;
            }

            DSAuthenticationDelegate authDelegate = DocuSign.getInstance().getAuthenticationDelegate();
            authDelegate.login(Constants.LOGIN_REQUEST_CODE, this, new DSAuthenticationListener() {
                @Override
                public void onSuccess(@NotNull DSUser dsUser) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(@NotNull DSAuthenticationException exception) {
                    Log.d(TAG, exception.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to Login to DocuSign", Toast.LENGTH_LONG).show();
                }
            });


        } catch (DocuSignNotInitializedException exception) {
            Log.d(TAG, exception.getMessage());
            Toast.makeText(this, "Failed to Login to DocuSign", Toast.LENGTH_LONG).show();
        }
    }

    private void launchSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
