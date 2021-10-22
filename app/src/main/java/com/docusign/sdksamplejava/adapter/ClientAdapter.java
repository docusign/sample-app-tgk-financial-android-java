package com.docusign.sdksamplejava.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.model.Client;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    @NonNull
    public List<Client> clients;

    public ClientAdapter(@NonNull List<Client> clients) {
        this.clients = clients;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClientViewHolder(LayoutInflater.from((parent.getContext())), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        holder.bind(clients.get(position));
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }


    class ClientViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private TextView clientTextView;

        public ClientViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_client, parent, false));

            clientTextView = itemView.findViewById(R.id.client_text_view);
        }

        public void bind(@NonNull Client client) {
            clientTextView.setText(client.getName());
        }
    }
}
