package com.docusign.sdksamplejava.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.model.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    @NonNull
    public List<Appointment> appointments;

    @NonNull
    public AppointmentListener listener;

    public AppointmentAdapter(@NonNull List<Appointment> appointments, @NonNull AppointmentListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppointmentViewHolder(LayoutInflater.from((parent.getContext())), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        holder.bind(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public interface AppointmentListener {
        void onAppointmentSelected(@NonNull Appointment appointment);
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView dateTextView;

        @NonNull
        private final TextView clientNameTextView;

        @NonNull
        private final TextView clientStatus;


        public AppointmentViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_appointment, parent, false));

            dateTextView = itemView.findViewById(R.id.date_text_view);
            clientNameTextView = itemView.findViewById(R.id.client_name_text_view);
            clientStatus = itemView.findViewById(R.id.client_status_text_view);
        }

        public void bind(@NonNull Appointment appointment) {
            itemView.setOnClickListener(view -> {
                listener.onAppointmentSelected(appointment);
            });

            dateTextView.setText(appointment.getDate());
            if (appointment.isClientSigned()) {
                clientStatus.setText(itemView.getContext().getString(R.string.signed));
                clientStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
            } else {
                clientStatus.setText(itemView.getContext().getString(R.string.unsigned));
                clientStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
            }
            clientNameTextView.setText(appointment.getClient().getName());
        }
    }
}
