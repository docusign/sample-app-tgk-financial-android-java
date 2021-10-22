package com.docusign.sdksamplejava.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.activity.AgreementActivity;
import com.docusign.sdksamplejava.model.Client;
import com.docusign.sdksamplejava.utils.Constants;
import com.google.gson.Gson;

public class NewPresentationFragment extends Fragment {

    public static final String TAG = NewPresentationFragment.class.getSimpleName();

    private EditText investorNameEditText;

    private EditText investorEmailEditText;

    private CheckBox cacheEnvelopeCheckBox;

    private Button viewPortfolioButton;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_new_presentation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        investorNameEditText = view.findViewById(R.id.investor_name_edit_text);
        investorEmailEditText = view.findViewById(R.id.investor_email_edit_text);
        cacheEnvelopeCheckBox = view.findViewById(R.id.cache_envelope_checkbox);

        viewPortfolioButton = view.findViewById(R.id.view_portfolio_button);
        viewPortfolioButton.setOnClickListener(v -> {
            String investorName = investorNameEditText.getText().toString();
            String investorEmail = investorEmailEditText.getText().toString();

            boolean error = investorName.isEmpty() || investorEmail.isEmpty();

            if (investorName.isEmpty())
                investorNameEditText.setError(getString(R.string.investor_name_empty));

            if (investorEmail.isEmpty())
                investorEmailEditText.setError(getString(R.string.investor_email_empty));

            if (error) {
                return;
            }

            Client client = new Client(
                    "FA-45231-007",
                    investorName,
                    "415-555-1236",
                    investorEmail,
                    "W Chalmers Pl",
                    "Chicago, IL",
                    "USA - 60614",
                    "$100,000",
                    Constants.CLIENT_C_PREF,
                    cacheEnvelopeCheckBox.isChecked()
            );

            Intent intent = new Intent(requireContext(), AgreementActivity.class);
            String clientJson = new Gson().toJson(client);
            if (clientJson != null) {
                intent.putExtra(AgreementActivity.CLIENT_DETAILS, clientJson);
                intent.putExtra(AgreementActivity.OPEN_CLIENT_INVESTMENT, true);
            }
            startActivity(intent);
        });
    }

    public static NewPresentationFragment newInstance() {
        return new NewPresentationFragment();
    }
}
