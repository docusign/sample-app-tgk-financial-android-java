package com.docusign.sdksamplejava.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.docusign.androidsdk.dsmodels.DSEnvelope;
import com.docusign.sdksamplejava.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PendingSyncAdapter extends RecyclerView.Adapter<PendingSyncAdapter.PendingSyncViewHolder> {

    @NonNull
    private final PendingSyncListener pendingSyncListener;

    @NonNull
    private final List<DSEnvelope> envelopes = new ArrayList<>();

    public PendingSyncAdapter(@NonNull PendingSyncListener pendingSyncListener) {
        this.pendingSyncListener = pendingSyncListener;
    }

    @NonNull
    @Override
    public PendingSyncViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PendingSyncViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingSyncViewHolder holder, int position) {
        holder.bind(envelopes.get(position), position);
    }

    @Override
    public int getItemCount() {
        return envelopes.size();
    }

    public void addItem(@NonNull DSEnvelope envelope) {
        envelopes.add(envelope);
        notifyDataSetChanged();
    }

    public void addItems(@NonNull List<DSEnvelope> envelopesList) {
        envelopes.addAll(envelopesList);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        envelopes.remove(position);
        notifyItemChanged(position);
    }

    public void removeAll() {
        envelopes.clear();
        notifyDataSetChanged();
    }

    public int getSize() {
        return envelopes.size();
    }

    public interface PendingSyncListener {
        void syncEnvelope(int position, @NonNull String envelopeId);
    }

    class PendingSyncViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView envelopeNameTextView;

        @NonNull
        private final Button envelopeSyncButton;

        public PendingSyncViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_pending_sync, parent, false));

            envelopeNameTextView = itemView.findViewById(R.id.envelope_name_text_view);
            envelopeSyncButton = itemView.findViewById(R.id.envelope_sync_button);
        }

        public void bind(@NonNull DSEnvelope envelope, int position) {
            List<String> names = null;
            if (envelope.getEmailSubject() != null) {
                names = Arrays.asList(envelope.getEmailSubject().split("Please DocuSign: "));
            }

            if (names != null && !names.isEmpty()) {
                envelopeNameTextView.setText(names.get(names.size() - 1));
            }

            envelopeSyncButton.setOnClickListener(view -> {
                pendingSyncListener.syncEnvelope(position, envelope.getEnvelopeId());
            });
        }
    }
}
