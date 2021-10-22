package com.docusign.sdksamplejava.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.adapter.PendingSyncAdapter;
import com.docusign.sdksamplejava.viewmodel.EnvelopeViewModel;

public class PendingSyncFragment extends Fragment {

    public static final String TAG = PendingSyncFragment.class.getSimpleName();

    @Nullable
    private EnvelopeViewModel envelopeViewModel;

    @Nullable
    private RecyclerView clientsRecyclerView;

    @Nullable
    private PendingSyncAdapter pendingSyncAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_sync, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        envelopeViewModel = new EnvelopeViewModel();
        initLiveDataObservers();

        Activity activity = getActivity();
        if (activity != null) {
            Button syncAllButton = activity.findViewById(R.id.sync_all_button);
            syncAllButton.setOnClickListener(view -> {
                toggleProgressBar(true);
                envelopeViewModel.syncAllEnvelopes();
            });

            TextView synPendingErrorTextView = activity.findViewById(R.id.pending_sync_error_text_view);
            synPendingErrorTextView.setVisibility(View.GONE);
            syncAllButton.setVisibility(View.VISIBLE);

            clientsRecyclerView = activity.findViewById(R.id.pending_sync_recycler_view);
            clientsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            pendingSyncAdapter = new PendingSyncAdapter((position, envelopeId) -> {
                toggleProgressBar(true);
                envelopeViewModel.syncEnvelope(envelopeId, position);
            });
            clientsRecyclerView.setAdapter(pendingSyncAdapter);

            envelopeViewModel.getSyncPendingEnvelopeIds();
        }
    }

    private void initLiveDataObservers() {
        if (envelopeViewModel != null) {
            envelopeViewModel.getGetSyncPendingEnvelopeIdsLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (model.getEnvelopeIds() == null || model.getEnvelopeIds().size() == 0) {
                            displayError(getResources().getString(R.string.syn_pending_error));
                            return;
                        }
                        for (String envelopeId : model.getEnvelopeIds()) {
                            envelopeViewModel.getCachedEnvelope(envelopeId);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TemplatesFragment.TAG, model.getException().getMessage());
                            displayError(model.getException().getMessage());
                        }
                }
            });

            envelopeViewModel.getGetCachedEnvelopeLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (model.getEnvelope() != null && pendingSyncAdapter != null) {
                            pendingSyncAdapter.addItem(model.getEnvelope());
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TemplatesFragment.TAG, model.getException().getMessage());
                            Toast.makeText(getActivity(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            envelopeViewModel.getSyncEnvelopeLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (pendingSyncAdapter != null) {
                            pendingSyncAdapter.removeItem(model.getPosition());
                            showSuccessfulSyncDialog(requireActivity(), false);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TemplatesFragment.TAG, model.getException().getMessage());
                            Toast.makeText(getActivity(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            envelopeViewModel.getSyncAllEnvelopesLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (pendingSyncAdapter != null) {
                            pendingSyncAdapter.removeAll();
                            showSuccessfulSyncDialog(requireActivity(), true);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        if (model.getException() != null) {
                            Log.d(TemplatesFragment.TAG, model.getException().getMessage());
                            Toast.makeText(getActivity(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });
        }
    }

    private void toggleProgressBar(boolean isBusy) {
        Activity activity = getActivity();

        if (activity != null) {
            ProgressBar progressBar = activity.findViewById(R.id.pending_sync_progress_bar);
            progressBar.setVisibility(isBusy ? View.VISIBLE : View.GONE);
        }
    }

    private void displayError(@NonNull String error) {
        Activity activity = getActivity();

        if (activity != null) {
            TextView synPendingErrorTextView = activity.findViewById(R.id.pending_sync_error_text_view);
            synPendingErrorTextView.setVisibility(View.VISIBLE);
            synPendingErrorTextView.setText(error);
            Button syncAllButton = activity.findViewById(R.id.sync_all_button);
            syncAllButton.setVisibility(View.GONE);
        }
    }

    private void showSuccessfulSyncDialog(@NonNull Context context, boolean syncAll) {

        String message = syncAll ? getString(R.string.envelope_sync_all_success_message) : getString(R.string.envelope_sync_success_message);

        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.envelope_created_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), (DialogInterface dialog, int id) -> {
                    if (pendingSyncAdapter != null && pendingSyncAdapter.getSize() == 0) {
                        displayError(getResources().getString(R.string.syn_pending_error));
                    }
                    dialog.cancel();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    public static PendingSyncFragment newInstance() {
        return new PendingSyncFragment();
    }
}
