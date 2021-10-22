package com.docusign.sdksamplejava.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.dsmodels.DSEnvelopeDefaults;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.model.Client;
import com.docusign.sdksamplejava.utils.ClientUtils;
import com.docusign.sdksamplejava.utils.Constants;
import com.docusign.sdksamplejava.utils.EnvelopeUtils;
import com.docusign.sdksamplejava.utils.SigningType;
import com.docusign.sdksamplejava.utils.Utils;
import com.docusign.sdksamplejava.viewmodel.TemplatesViewModel;

import org.jetbrains.annotations.NotNull;

public class AgreementTemplatesFragment extends TemplatesFragment {

    public static final String TAG = AgreementTemplatesFragment.class.getSimpleName();

    @Nullable
    private Client client;

    @Nullable
    private SharedPreferences sharedPreferences;

    @Nullable
    private TemplatesViewModel templatesViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        templatesViewModel = new TemplatesViewModel();
        sharedPreferences = requireContext().getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        initLiveDataObservers();
    }

    private void initLiveDataObservers() {
        if (templatesViewModel != null) {
            templatesViewModel.getUseTemplateOnlineLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        Log.d(TAG, "Envelope with " + model.getEnvelopeId() + " is signed online successfully");
                        Activity activity = getActivity();
                        if (activity != null) {
                            showSuccessfulSigningDialog(activity, SigningType.ONLINE_SIGNING);

                            if (client != null) {
                                ClientUtils.setSignedStatus(requireContext(), client.getStorePref(), true);
                            }
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

            templatesViewModel.getUseTemplateOfflineLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        Log.d(TAG, "Envelope with " + model.getEnvelopeId() + " is signed offline successfully");
                        Activity activity = getActivity();
                        if (activity != null) {
                            showSuccessfulSigningDialog(activity, SigningType.OFFLINE_SIGNING);

                            if (client != null) {
                                ClientUtils.setSignedStatus(requireContext(), client.getStorePref(), true);
                            }
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
            ProgressBar progressBar = activity.findViewById(R.id.templates_progress_bar);
            progressBar.setVisibility(isBusy ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void templateSelected(@NonNull @NotNull String templateId, @Nullable @org.jetbrains.annotations.Nullable String templateName) {
        Activity activity = getActivity();
        if (activity != null) {
            launchSigning(activity, templateId, templateName);
        }
    }

    private void launchSigning(@NonNull Context context, @NonNull String templateId, @NonNull String templateName) {
        try {
            String version = DocuSign.getInstance().getSDKVersion();
            Log.d(TAG, "DocuSign SDK version: " + version);
        } catch (DocuSignNotInitializedException e) {
            Log.d(TAG, "DocuSign SDK is not initialized.");
        }

        // If you want to prefill template with recipient details, tab details etc, you can set EnvelopeDefaults
        DSEnvelopeDefaults envelopeDefaults = null;
        try {
            envelopeDefaults = EnvelopeUtils.buildEnvelopeDefaults(context, templateId, templateName, client != null ? client.getStorePref() : null);
            if (templatesViewModel != null) {
                if (Utils.isNetworkAvailable(context)) {
                    toggleProgressBar(true);
                    templatesViewModel.useTemplateOnline(context, templateId, envelopeDefaults);
                    // templatesViewModel.useTemplateOnline(context, templateId, null);
                } else {
                    templatesViewModel.useTemplateOffline(context, templateId, envelopeDefaults);
                    // templatesViewModel.useTemplateOffline(context, templateId, null);
                }
            }
        } catch (DocuSignNotInitializedException | DSAuthenticationException e) {
            e.printStackTrace();
        }
    }

    private void showSuccessfulSigningDialog(@NonNull Context context, SigningType signingType) {
        String message = getString(R.string.envelope_signed_offline_message);
        if (signingType == SigningType.ONLINE_SIGNING) {
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

    public static AgreementTemplatesFragment newInstance(@Nullable Client client) {
        AgreementTemplatesFragment fragment = new AgreementTemplatesFragment();
        fragment.client = client;
        return fragment;
    }
}
