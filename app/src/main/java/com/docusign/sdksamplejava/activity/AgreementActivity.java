package com.docusign.sdksamplejava.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.fragment.AgreementHomeFragment;
import com.docusign.sdksamplejava.fragment.AgreementTemplatesFragment;
import com.docusign.sdksamplejava.fragment.ClientInvestmentFragment;
import com.docusign.sdksamplejava.fragment.PortfolioFragment;
import com.docusign.sdksamplejava.model.Client;
import com.google.gson.Gson;

public class AgreementActivity extends AppCompatActivity
        implements PortfolioFragment.PortFolioFragmentListener, ClientInvestmentFragment.ClientInvestmentFragmentListener {

    public static final String CLIENT_DETAILS = "ClientDetails";

    public static final String OPEN_CLIENT_INVESTMENT = "Open client investment";

    private static final String TAG = AgreementActivity.class.getSimpleName();

    @Nullable
    private Client client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            String clientJson = intent.getStringExtra(CLIENT_DETAILS);

            if (clientJson != null) {
                client = new Gson().fromJson(clientJson, Client.class);
            }
        }

        setContentView(R.layout.activity_agreement);

        Fragment agreementHomeFragment = getSupportFragmentManager().findFragmentByTag(AgreementHomeFragment.TAG);
        if (agreementHomeFragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            agreementHomeFragment = AgreementHomeFragment.newInstance(client);

            fragmentTransaction.add(R.id.agreement_container, agreementHomeFragment, AgreementHomeFragment.TAG);
            fragmentTransaction.addToBackStack(AgreementHomeFragment.TAG);
            fragmentTransaction.commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.ic_logo);
            actionBar.setDisplayUseLogoEnabled(true);
        }

        if(intent != null) {
            if (intent.getBooleanExtra(OPEN_CLIENT_INVESTMENT, false)) {
                displayClientInvestment(client);
            }
        }

    }

    @Override
    public void displayAgreementTemplates(@Nullable Client client) {
        Fragment agreementTemplatesFragment =
                getSupportFragmentManager().findFragmentByTag(AgreementTemplatesFragment.TAG);
        if (agreementTemplatesFragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            agreementTemplatesFragment = AgreementTemplatesFragment.newInstance(client);
            fragmentTransaction.add(
                    R.id.agreement_container,
                    agreementTemplatesFragment,
                    AgreementTemplatesFragment.TAG
            );
            fragmentTransaction.addToBackStack(AgreementTemplatesFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void displayClientInvestment(@Nullable Client client) {
        Fragment clientInvestmentFragment =
                getSupportFragmentManager().findFragmentByTag(ClientInvestmentFragment.TAG);
        if (clientInvestmentFragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            clientInvestmentFragment = ClientInvestmentFragment.newInstance(client);
            fragmentTransaction.add(
                    R.id.agreement_container,
                    clientInvestmentFragment,
                    ClientInvestmentFragment.TAG
            );
            fragmentTransaction.addToBackStack(ClientInvestmentFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
}
