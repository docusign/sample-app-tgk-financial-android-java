package com.docusign.sdksamplejava;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.docusign.androidsdk.DSEnvironment;
import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSAppearanceDelegate;
import com.docusign.androidsdk.core.dsmodels.DSAppearance;
import com.docusign.androidsdk.exceptions.DSException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.util.DSMode;
import com.docusign.sdksamplejava.utils.Constants;
import com.docusign.sdksamplejava.utils.Utils;

import java.io.File;
import java.util.Objects;

public class SDKSampleApplication extends Application {

    private static final String TAG = SDKSampleApplication.class.getSimpleName();

    @Nullable
    private File portfolioADoc;

    @Nullable
    private File portfolioBDoc;

    @Nullable
    private File accreditedInvestorDoc;

    @Override public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        initializeDocuSign();
        portfolioADoc = Utils.convertAssetToFile(
                this,
                Constants.PORTFOLIO_A_DOCUMENT_FILE_NAME,
                getFilesDir().getAbsolutePath() + "/" + Constants.PORTFOLIO_A_DOCUMENT_FILE_NAME
        );
        portfolioBDoc = Utils.convertAssetToFile(
                this,
                Constants.PORTFOLIO_B_DOCUMENT_FILE_NAME,
                getFilesDir().getAbsolutePath() + "/" + Constants.PORTFOLIO_B_DOCUMENT_FILE_NAME
        );
        accreditedInvestorDoc = Utils.convertAssetToFile(
                this,
                Constants.ACCREDITED_INVESTOR_VERIFICATION_FILE_NAME,
                getFilesDir().getAbsolutePath() + "/" + Constants.ACCREDITED_INVESTOR_VERIFICATION_FILE_NAME
        );
    }

    public void initializeDocuSign() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String integratorKey = sharedPreferences.getString(
                Constants.DOCUSIGN_INTEGRATOR_KEY_PREF,
                Constants.DOCUSIGN_INTEGRATOR_KEY
        );
        String clientSecret = sharedPreferences.getString(
                Constants.CLIENT_SECRET_KEY_PREF,
                Constants.CLIENT_SECRET_KEY
        );
        String redirectUri =
                sharedPreferences.getString(Constants.REDIRECT_URI_PREF, Constants.REDIRECT_URI);

        if (TextUtils.isEmpty(integratorKey)) {
            Toast.makeText(this, "Please provide Integrator Key", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(clientSecret)) {
            Toast.makeText(this, "Please provide Client Secret", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(redirectUri)) {
            Toast.makeText(this, "Please provide Redirect Uri", Toast.LENGTH_LONG).show();
            return;
        }

        // DS: Initialize DocuSign instance
        try {
            DocuSign.init(
                    this,
                    Objects.requireNonNull(integratorKey),
                    clientSecret,
                    redirectUri,
                    DSMode.DEBUG
            ).setEnvironment(DSEnvironment.DEMO_ENVIRONMENT);
        } catch (DSException exception) {
            Log.d(TAG, exception.getMessage());
            Toast.makeText(
                    this,
                    "Failed to Initialize DocuSign. " + exception.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        // DS: Set branding for your app
        DSAppearanceDelegate appearanceDelegate = null;
        try {
            appearanceDelegate = DocuSign.getInstance().getAppearanceDelegate();
        } catch (DocuSignNotInitializedException exception) {
            Log.d(TAG, exception.getMessage());
        }
        DSAppearance appearance = new DSAppearance.Builder()
                .setActionBarColor(new ColorDrawable(getResources().getColor(R.color.colorPrimary)))
                .setStatusBarColor(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)))
                .setActionBarLogo(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_logo, null))
                .setActionBarTitleTextColor(new ColorDrawable(getResources().getColor(android.R.color.white)))
                .setBottomToolbarButtonColor(new ColorDrawable(getResources().getColor(R.color.colorPrimary)))
                .setBottomToolbarButtonTextColor(new ColorDrawable(getResources().getColor(android.R.color.white)))
                .setBottomToolbarDocuSignImageVisibility(true)
                .build();
        if (appearanceDelegate != null) {
            appearanceDelegate.setAppearance(appearance);
        }
    }

    @Nullable
    public File getPortfolioADoc() {
        return portfolioADoc;
    }

    public void setPortfolioADoc(@Nullable File portfolioADoc) {
        this.portfolioADoc = portfolioADoc;
    }

    @Nullable
    public File getPortfolioBDoc() {
        return portfolioBDoc;
    }

    public void setPortfolioBDoc(@Nullable File portfolioBDoc) {
        this.portfolioBDoc = portfolioBDoc;
    }

    @Nullable
    public File getAccreditedInvestorDoc() {
        return accreditedInvestorDoc;
    }

    public void setAccreditedInvestorDoc(@Nullable File accreditedInvestorDoc) {
        this.accreditedInvestorDoc = accreditedInvestorDoc;
    }
}
