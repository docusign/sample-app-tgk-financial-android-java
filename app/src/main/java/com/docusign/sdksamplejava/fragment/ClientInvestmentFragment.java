package com.docusign.sdksamplejava.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSAuthenticationDelegate;
import com.docusign.androidsdk.delegates.DSEnvelopeDelegate;
import com.docusign.androidsdk.dsmodels.DSEnvelope;
import com.docusign.androidsdk.dsmodels.DSUser;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DSEnvelopeException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSCacheEnvelopeListener;
import com.docusign.androidsdk.listeners.DSComposeAndSendEnvelopeListener;
import com.docusign.androidsdk.listeners.DSGetEnvelopeListener;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.SDKSampleApplication;
import com.docusign.sdksamplejava.activity.AgreementActivity;
import com.docusign.sdksamplejava.model.AccreditedInvestorVerification;
import com.docusign.sdksamplejava.model.AccreditedInvestorVerifier;
import com.docusign.sdksamplejava.model.Client;
import com.docusign.sdksamplejava.utils.ClientUtils;
import com.docusign.sdksamplejava.utils.Constants;
import com.docusign.sdksamplejava.utils.EnvelopeUtils;
import com.docusign.sdksamplejava.utils.SigningType;
import com.docusign.sdksamplejava.utils.Utils;
import com.docusign.sdksamplejava.viewmodel.SigningViewModel;
import com.google.gson.Gson;

import java.io.File;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientInvestmentFragment extends Fragment {

    public static final String TAG = ClientInvestmentFragment.class.getSimpleName();

    @Nullable
    private Client client;

    @Nullable
    private SigningViewModel signingViewModel;

    public static ClientInvestmentFragment newInstance(@Nullable Client client) {
        ClientInvestmentFragment fragment = new ClientInvestmentFragment();
        fragment.client = client;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_investment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            signingViewModel = new SigningViewModel();
            initLiveDataObservers(activity);

            ImageView clientGraphImageView = activity.findViewById(R.id.client_graph_growth_image_view);
            Spinner investmentAmountSpinner = activity.findViewById(R.id.invest_amount_spinner);
            String[] investments = {"$300,000", "$400,000", "$500,000"};
            ArrayAdapter<String> investmentSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, investments);
            CheckBox accreditedInvestorCheckbox = activity.findViewById(R.id.accredited_investor_checkbox);
            investmentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            investmentAmountSpinner.setAdapter(investmentSpinnerAdapter);
            if (client != null && client.getStorePref().equals(Constants.CLIENT_B_PREF)) {
                investmentAmountSpinner.setSelection(investments.length - 1);
                TextView accreditedInvestorTextView = activity.findViewById(R.id.accredited_investor_text_view);
                accreditedInvestorTextView.setVisibility(View.VISIBLE);
                accreditedInvestorCheckbox.setVisibility(View.VISIBLE);
                accreditedInvestorCheckbox.setChecked(true);
            }

            investmentAmountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int[] clientGraphs = {R.drawable.growth_portfolio_a_0, R.drawable.growth_portfolio_a_1, R.drawable.growth_portfolio_a_2};
                    if (position < clientGraphs.length) {
                        clientGraphImageView.setImageResource(clientGraphs[position]);
                        if (client != null) {
                            client.setInvestmentAmount(investments[position]);
                            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                            String clientJson = new Gson().toJson(client);
                            sharedPreferences.edit().putString(client.getStorePref(), clientJson).apply();

                            accreditedInvestorCheckbox.setChecked(position == investments.length - 1);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    /* NO-OP */
                }
            });
            Button investButton = activity.findViewById(R.id.invest_button);
            investButton.setOnClickListener(view -> {
                if (client != null) {
                    if (client.getStorePref().equals(Constants.CLIENT_A_PREF)) {
                        // Launch template flow
                        ((AgreementActivity) getActivity()).displayAgreementTemplates(client);
                    } else if (client.getStorePref().equals(Constants.CLIENT_B_PREF)) {
                        // Launch envelope flow
                        createEnvelope(activity, accreditedInvestorCheckbox.isChecked(), client.getStorePref());
                    } else {
                        if (client.isCacheEnvelope()) {
                            cachedEnvelope(requireContext(), Objects.requireNonNull(client).getStorePref());
                        } else {
                            captiveSigning(requireContext(), Objects.requireNonNull(client).getStorePref());
                        }
                    }
                }
            });
        }
    }

    private void cachedEnvelope(@NonNull Context context, @Nullable String clientPref) {

        EnvelopeDefinition envelopeDefinition =
                EnvelopeUtils.buildCachedEnvelopeDefinition(Objects.requireNonNull(clientPref), requireActivity());

        EnvelopesApi envelopesApi;
        try {
            envelopesApi = DocuSign.getInstance().getESignApiDelegate().createApiService(EnvelopesApi.class);
        } catch (DocuSignNotInitializedException exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        DSAuthenticationDelegate authenticationDelegate;
        try {
            authenticationDelegate = DocuSign.getInstance().getAuthenticationDelegate();
        } catch (DocuSignNotInitializedException exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        DSUser user;
        try {
            user = authenticationDelegate.getLoggedInUser(context);
        } catch (DSAuthenticationException exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (envelopesApi != null) {
            Call<EnvelopeSummary> call = envelopesApi.envelopesPostEnvelopes(
                    user.getAccountId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    envelopeDefinition
            );


            DSEnvelopeDelegate envelopeDelegate;
            try {
                envelopeDelegate = DocuSign.getInstance().getEnvelopeDelegate();
            } catch (DocuSignNotInitializedException exception) {
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            call.enqueue(new Callback<EnvelopeSummary>() {

                @Override
                public void onResponse(
                        @NonNull Call<EnvelopeSummary> call,
                        @NonNull Response<EnvelopeSummary> response
                ) {
                    if (response.isSuccessful()) {
                        EnvelopeSummary envelopeSummary = response.body();
                        if (envelopeSummary != null) {
                            envelopeDelegate.cacheEnvelope(envelopeSummary.getEnvelopeId(), new DSCacheEnvelopeListener() {
                                @Override
                                public void onStart() {
                                    toggleProgressBar(true);
                                }

                                @Override
                                public void onComplete(@NonNull DSEnvelope dsEnvelope) {
                                    Objects.requireNonNull(signingViewModel).signCachedEnvelope(context, dsEnvelope.getEnvelopeId());
                                }

                                @Override
                                public void onError(@NonNull DSEnvelopeException e) {
                                    Log.d(OverviewFragment.TAG, e.getMessage());
                                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EnvelopeSummary> call, @NonNull Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void initLiveDataObservers(@NonNull Context context) {
        if (signingViewModel != null) {
            signingViewModel.getSignOfflineLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        showSuccessfulSigningDialog(context, SigningType.OFFLINE_SIGNING);
                        if (client != null) {
                            ClientUtils.setSignedStatus(requireContext(), client.getStorePref(), true);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            signingViewModel.getSignOnlineLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        showSuccessfulSigningDialog(context, SigningType.ONLINE_SIGNING);
                        if (client != null) {
                            ClientUtils.setSignedStatus(requireContext(), client.getStorePref(), true);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            signingViewModel.getCaptiveSigningLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        showSuccessfulSigningDialog(context, SigningType.CAPTIVE_SIGNING);
                        if (client != null) {
                            ClientUtils.setSignedStatus(requireContext(), client.getStorePref(), true);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            signingViewModel.getCachedEnvelopeSigningLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        showSuccessfulSigningDialog(context, SigningType.OFFLINE_SIGNING);
                        if (client != null) {
                            ClientUtils.setSignedStatus(requireContext(), client.getStorePref(), true);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });
        }
    }

    private void toggleProgressBar(boolean isBusy) {
        Activity activity = getActivity();
        if (activity != null) {
            ProgressBar progressBar = activity.findViewById(R.id.envelopes_progress_bar);
            progressBar.setVisibility(isBusy ? View.VISIBLE : View.GONE);
        }
    }

    private void captiveSigning(@NonNull Context context, @Nullable String clientPref) {

        EnvelopeDefinition envelopeDefinition =
                EnvelopeUtils.buildEnvelopeDefinition(Objects.requireNonNull(clientPref), requireActivity());

        EnvelopesApi envelopesApi;
        try {
            envelopesApi = DocuSign.getInstance().getESignApiDelegate().createApiService(EnvelopesApi.class);
        } catch (DocuSignNotInitializedException exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        DSAuthenticationDelegate authenticationDelegate;
        try {
            authenticationDelegate = DocuSign.getInstance().getAuthenticationDelegate();
        } catch (DocuSignNotInitializedException exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        DSUser user;
        try {
            user = authenticationDelegate.getLoggedInUser(context);
        } catch (DSAuthenticationException exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (envelopesApi != null) {
            Call<EnvelopeSummary> call = envelopesApi.envelopesPostEnvelopes(
                    user.getAccountId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    envelopeDefinition
            );


            DSEnvelopeDelegate envelopeDelegate;
            try {
                envelopeDelegate = DocuSign.getInstance().getEnvelopeDelegate();
            } catch (DocuSignNotInitializedException exception) {
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            DSEnvelopeDelegate finalEnvelopeDelegate = envelopeDelegate;
            call.enqueue(new Callback<EnvelopeSummary>() {

                @Override
                public void onResponse(
                        @NonNull Call<EnvelopeSummary> call,
                        @NonNull Response<EnvelopeSummary> response
                ) {
                    if (response.isSuccessful()) {
                        EnvelopeSummary envelopeSummary = response.body();
                        if (envelopeSummary != null) {
                            finalEnvelopeDelegate.getEnvelope(envelopeSummary.getEnvelopeId(), new DSGetEnvelopeListener() {
                                @Override
                                public void onError(@NonNull DSEnvelopeException exception) {
                                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onSuccess(@NonNull DSEnvelope envelope) {
                                    Objects.requireNonNull(signingViewModel).captiveSigning(requireContext(), envelope);
                                }
                            });
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<EnvelopeSummary> call, @NonNull Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void createEnvelope(@NonNull Context context, boolean isAccreditedInvestor, @Nullable String clientPref) {
        File document = Objects.equals(clientPref, Constants.CLIENT_A_PREF) ? ((SDKSampleApplication) requireActivity().getApplication()).getPortfolioADoc() : ((SDKSampleApplication) requireActivity().getApplication()).getPortfolioBDoc();

        if (document == null) {
            Log.e(TAG, "Unable to retrieve document");
            Toast.makeText(context, "Unable to retrieve document", Toast.LENGTH_LONG).show();
            return;
        }

        AccreditedInvestorVerification accreditedInvestorVerification = null;

        if (isAccreditedInvestor) {
            File accreditedInvestorVerificationDocument = ((SDKSampleApplication) requireActivity().getApplication()).getAccreditedInvestorDoc();

            if (accreditedInvestorVerificationDocument == null) {
                Toast.makeText(context, "AccreditedInvestor Verification document is not available", Toast.LENGTH_LONG).show();
                return;
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String clientString = sharedPreferences.getString(clientPref, null);

            if (clientString == null) {
                Toast.makeText(context, "Client details not available", Toast.LENGTH_LONG).show();
                return;
            }

            Client client = new Gson().fromJson(clientString, Client.class);
            String clientAddress = client.getAddressLine1() + "," + client.getAddressLine2() + "," + client.getAddressLine3();
            DSAuthenticationDelegate authenticationDelegate = null;
            try {
                authenticationDelegate = DocuSign.getInstance().getAuthenticationDelegate();
            } catch (DocuSignNotInitializedException e) {
                Log.d(TAG, "DocuSign SDK is not initialized.");
            }
            DSUser user = null;
            try {
                if (authenticationDelegate != null) {
                    user = authenticationDelegate.getLoggedInUser(context);
                }
            } catch (DSAuthenticationException e) {
                Log.d(TAG, "Authentication failed.");
            }
            AccreditedInvestorVerifier accreditedInvestorVerifier = null;
            if (user != null) {
                accreditedInvestorVerifier = new AccreditedInvestorVerifier(user.getName(),
                        getString(R.string.androidfinance), "TN12345", "CA",
                        "202 Main Street", null, "San Francisco, CA, 94105");
            }
            if (accreditedInvestorVerifier != null) {
                accreditedInvestorVerification =
                        new AccreditedInvestorVerification(client.getName(), clientAddress, accreditedInvestorVerifier, accreditedInvestorVerificationDocument);
            }
        }

        DSEnvelope envelope = EnvelopeUtils.buildEnvelope(context, document, accreditedInvestorVerification, clientPref);
        if (envelope == null) {
            Log.e(TAG, "Unable to create envelope");
            Toast.makeText(context, "Unable to create envelope", Toast.LENGTH_LONG).show();
            return;
        }

        DSEnvelopeDelegate envelopeDelegate = null;
        try {
            envelopeDelegate = DocuSign.getInstance().getEnvelopeDelegate();
        } catch (DocuSignNotInitializedException e) {
            Log.d(TAG, "DocuSign SDK is not initialized.");
        }

        if (envelopeDelegate != null)
            envelopeDelegate.composeAndSendEnvelope(envelope, new DSComposeAndSendEnvelopeListener() {

                @Override
                public void onSuccess(@NonNull String envelopeId, boolean isEnvelopeSent) {
                    if (Utils.isNetworkAvailable(context)) {
                        toggleProgressBar(true);
                        if (signingViewModel != null)
                            signingViewModel.signOnline(context, envelopeId);
                    } else {
                        if (signingViewModel != null)
                            signingViewModel.signOffline(context, envelopeId);
                    }
                }

                @Override
                public void onError(@NonNull DSEnvelopeException exception) {
                    Log.e(TAG, exception.getMessage());
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void showSuccessfulSigningDialog(@NonNull Context context, @NonNull SigningType signingType) {
        String message = getString(R.string.envelope_signed_offline_message);
        if (signingType == SigningType.ONLINE_SIGNING || signingType == SigningType.CAPTIVE_SIGNING) {
            message = getString(R.string.envelope_signed_message);
        }
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.envelope_created_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                    requireActivity().finish();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    public interface ClientInvestmentFragmentListener {
        void displayAgreementTemplates(@Nullable Client client);
    }
}
