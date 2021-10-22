package com.docusign.sdksamplejava.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.activity.AgreementActivity;
import com.docusign.sdksamplejava.activity.NewPresentationActivity;
import com.docusign.sdksamplejava.adapter.AppointmentAdapter;
import com.docusign.sdksamplejava.model.Appointment;
import com.docusign.sdksamplejava.model.Client;
import com.docusign.sdksamplejava.utils.ClientUtils;
import com.docusign.sdksamplejava.utils.Constants;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OverviewFragment extends Fragment implements AppointmentAdapter.AppointmentListener {

    public static final String TAG = OverviewFragment.class.getSimpleName();

    @Nullable
    private RecyclerView appointmentRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.new_presentation_button).setOnClickListener(v -> {
            Intent intent = new Intent(
                    requireContext(),
                    NewPresentationActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            appointmentRecyclerView = activity.findViewById(R.id.appointments_recycler_view);
            appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            List<Appointment> appointments = new ArrayList<>();

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

            Client client1 = new Client(
                    "FA-45231-005",
                    "Tom Wood",
                    "415-555-1234",
                    "tom.wood@digital.com",
                    "726 Tennessee St",
                    "San Francisco, CA",
                    "USA - 94107",
                    "$300,000",
                    Constants.CLIENT_A_PREF
            );

            Client client2 = new Client(
                    "FA-45231-006",
                    "Andrea G Kuhn",
                    "415-555-1235",
                    "andrea.kuhn@global.com",
                    "231 Dalton Way",
                    "New York, CA",
                    "USA - 10005",
                    "$500,000",
                    Constants.CLIENT_B_PREF
            );

            Gson gson = new Gson();
            String client1Json = gson.toJson(client1);
            String client2Json = gson.toJson(client2);

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(Constants.CLIENT_A_PREF, client1Json).apply();
            sharedPreferences.edit().putString(Constants.CLIENT_B_PREF, client2Json).apply();

            boolean client1SignedStatus = ClientUtils.getSignedStatus(requireContext(), client1.getStorePref());
            boolean client2SignedStatus = ClientUtils.getSignedStatus(requireContext(), client2.getStorePref());
            appointments.add(new Appointment(dateFormat.format(date), client1, client1SignedStatus));
            appointments.add(new Appointment(dateFormat.format(date), client2, client2SignedStatus));

            appointmentRecyclerView.setAdapter(new AppointmentAdapter(appointments, this));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (appointmentRecyclerView != null) {
            AppointmentAdapter adapter = ((AppointmentAdapter) appointmentRecyclerView.getAdapter());
            if (adapter != null) {
                List<Appointment> appointments = adapter.appointments;
                for (Appointment appointment : appointments) {
                    appointment.setClientSigned(ClientUtils.getSignedStatus(requireContext(), appointment.getClient().getStorePref()));
                }
                appointmentRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAppointmentSelected(@NonNull Appointment appointment) {
        boolean clientSignedStatus = ClientUtils.getSignedStatus(requireContext(), appointment.getClient().getStorePref());
        if (!clientSignedStatus) {
            Intent intent = new Intent(getActivity(), AgreementActivity.class);
            String clientJson = new Gson().toJson(appointment.getClient());
            if (clientJson != null) {
                intent.putExtra(AgreementActivity.CLIENT_DETAILS, clientJson);
            }
            requireActivity().startActivity(intent);
        }
    }

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }
}
