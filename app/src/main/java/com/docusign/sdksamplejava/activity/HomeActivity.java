package com.docusign.sdksamplejava.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSLogoutListener;
import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.fragment.HomeFragment;
import com.docusign.sdksamplejava.utils.ClientUtils;
import com.docusign.sdksamplejava.utils.Constants;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (homeFragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            homeFragment = HomeFragment.newInstance();
            fragmentTransaction.add(R.id.home_container, homeFragment, HomeFragment.TAG);
            fragmentTransaction.commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.ic_logo);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            logout();
        } else if (itemId == R.id.action_reset) {
            reset();
        }
        return true;
    }

    private void logout() {
        // DS: Logout from DocuSign
        try {
            DocuSign.getInstance().getAuthenticationDelegate().logout(this, true, new DSLogoutListener() {
                @Override
                public void onSuccess() {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void onError(@NotNull DSAuthenticationException exception) {
                    Toast.makeText(getApplicationContext(), "Failed to logout: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (DocuSignNotInitializedException exception) {
            Toast.makeText(getApplicationContext(), "Failed to logout: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void reset() {
        ClientUtils.setSignedStatus(this, Constants.CLIENT_A_PREF, false);
        ClientUtils.setSignedStatus(getApplicationContext(), Constants.CLIENT_B_PREF, false);

        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (homeFragment instanceof HomeFragment) {
            ViewPager viewPager = ((HomeFragment) homeFragment).viewPager;

            if (viewPager == null) {
                return;
            }

            PagerAdapter pagerAdapter = viewPager.getAdapter();
            if (pagerAdapter != null) {
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }
}
