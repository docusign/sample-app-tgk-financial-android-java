package com.docusign.sdksamplejava.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.fragment.NewPresentationFragment;

public class NewPresentationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_presentation);

        Fragment newPresentationFragment =
                getSupportFragmentManager().findFragmentByTag(NewPresentationFragment.TAG);

        if(newPresentationFragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            newPresentationFragment = NewPresentationFragment.newInstance();
            fragmentTransaction.add(
                    R.id.new_presentation_container,
                    newPresentationFragment,
                    NewPresentationFragment.TAG
            );
            fragmentTransaction.addToBackStack(NewPresentationFragment.TAG);
            fragmentTransaction.commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.ic_logo);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
}
