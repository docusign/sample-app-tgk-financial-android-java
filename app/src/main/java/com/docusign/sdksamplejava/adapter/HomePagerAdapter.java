package com.docusign.sdksamplejava.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.fragment.OverviewFragment;
import com.docusign.sdksamplejava.fragment.PendingSyncFragment;
import com.docusign.sdksamplejava.fragment.TemplatesFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private static final int[] TAB_TITLES = {R.string.tab_overview,
            R.string.tab_templates,
            R.string.tab_pending_sync};

    @NonNull
    private final Context context;

    public HomePagerAdapter(@NonNull Context context, @NonNull FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.context = context;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_TITLES[position]);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OverviewFragment.newInstance();
            case 1:
                return TemplatesFragment.newInstance();
            default:
                return PendingSyncFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}
