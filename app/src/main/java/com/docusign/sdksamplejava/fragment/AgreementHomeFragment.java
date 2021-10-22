package com.docusign.sdksamplejava.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.adapter.AgreementHomePagerAdapter;
import com.docusign.sdksamplejava.model.Client;
import com.google.android.material.tabs.TabLayout;

public class AgreementHomeFragment extends Fragment {

    public static final String TAG = AgreementHomeFragment.class.getSimpleName();

    @Nullable
    private Client client;

    @Nullable
    private ViewPager viewPager;

    public static AgreementHomeFragment newInstance(@Nullable Client client) {
        AgreementHomeFragment fragment = new AgreementHomeFragment();
        fragment.client = client;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agreement_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();

        if (activity != null) {
            try {
                String version = DocuSign.getInstance().getSDKVersion();
                Log.d(TAG, "DocuSign SDK version: " + version);
            } catch (DocuSignNotInitializedException e) {
                Log.d(TAG, "DocuSign SDK is not initialized.");
            }

            AgreementHomePagerAdapter agreementHomePagerAdapter =
                    new AgreementHomePagerAdapter(client,
                            activity,
                            activity.getSupportFragmentManager());

            viewPager = activity.findViewById(R.id.view_pager);
            viewPager.setAdapter(agreementHomePagerAdapter);

            TabLayout tabLayout = activity.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
    }
}
