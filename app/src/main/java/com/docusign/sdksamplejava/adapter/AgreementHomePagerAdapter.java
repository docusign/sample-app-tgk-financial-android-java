package com.docusign.sdksamplejava.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.fragment.ContactFragment;
import com.docusign.sdksamplejava.fragment.PortfolioFragment;
import com.docusign.sdksamplejava.model.Client;

public class AgreementHomePagerAdapter extends FragmentPagerAdapter {

    private static final int[] TAB_TITLES = {R.string.tab_portfolio, R.string.tab_contact};

    @Nullable
    private final Client client;

    @NonNull
    private final Context context;


    public AgreementHomePagerAdapter(@Nullable Client client, @NonNull Context context, @NonNull FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.client = client;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PortfolioFragment.newInstance(client);
        }
        return ContactFragment.newInstance(client);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}
