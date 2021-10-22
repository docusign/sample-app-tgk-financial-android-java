package com.docusign.sdksamplejava.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.activity.AgreementActivity;
import com.docusign.sdksamplejava.model.Client;
import com.docusign.sdksamplejava.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PortfolioFragment extends Fragment {

    public static final String TAG = PortfolioFragment.class.getSimpleName();

    @Nullable
    private Client client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            if (client != null) {
                TextView clientNameTextView = activity.findViewById(R.id.name_text_view);
                clientNameTextView.setText(client.getName());
                ImageView clientGraphImageView = activity.findViewById(R.id.client_graph_image_view);
                if (client.getStorePref().equals(Constants.CLIENT_A_PREF)) {
                    clientGraphImageView.setImageResource(R.drawable.portfolio_a);
                } else {
                    clientGraphImageView.setImageResource(R.drawable.portfolio_b);
                }
                TextView datedTextView = activity.findViewById(R.id.dated_text_view);
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                datedTextView.setText("Dated: " + dateFormat.format(date));
            }
            Button viewAgreementButton = activity.findViewById(R.id.view_agreement_button);
            viewAgreementButton.setOnClickListener(view -> ((AgreementActivity) activity).displayClientInvestment(client));
        }
    }

    public static PortfolioFragment newInstance(@Nullable Client client) {
        PortfolioFragment fragment = new PortfolioFragment();
        fragment.client = client;
        return fragment;
    }

    public interface PortFolioFragmentListener {
        void displayClientInvestment(@Nullable Client client);
    }
}
