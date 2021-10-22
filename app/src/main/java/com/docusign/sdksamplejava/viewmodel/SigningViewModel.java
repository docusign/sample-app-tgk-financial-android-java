package com.docusign.sdksamplejava.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSSigningDelegate;
import com.docusign.androidsdk.dsmodels.DSEnvelope;
import com.docusign.androidsdk.exceptions.DSSigningException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSCaptiveSigningListener;
import com.docusign.androidsdk.listeners.DSEnvelopeOfflineSigningListener;
import com.docusign.androidsdk.listeners.DSOfflineSigningListener;
import com.docusign.androidsdk.listeners.DSOnlineSigningListener;
import com.docusign.sdksamplejava.livedata.CachedEnvelopeSigningModel;
import com.docusign.sdksamplejava.livedata.CaptiveSigningModel;
import com.docusign.sdksamplejava.livedata.SignOfflineModel;
import com.docusign.sdksamplejava.livedata.SignOnlineModel;
import com.docusign.sdksamplejava.livedata.Status;

import java.util.Objects;

public class SigningViewModel extends ViewModel {

    public static final String TAG = SigningViewModel.class.getSimpleName();

    @NonNull
    private final MutableLiveData<SignOfflineModel> signOfflineLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<SignOnlineModel> signOnlineLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<CaptiveSigningModel> captiveSigningLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<CachedEnvelopeSigningModel> cachedEnvelopeSigningLiveData = new MutableLiveData<>();

    @Nullable
    private DSSigningDelegate signingDelegate;

    public SigningViewModel() {
        try {
            signingDelegate = DocuSign.getInstance().getSigningDelegate();
        } catch (DocuSignNotInitializedException e) {
            signingDelegate = null;
        }
    }

    public void signOffline(@NonNull Context context, @NonNull String envelopeId) {
        // DS: Offline Signing using local envelopeId
        if (signingDelegate != null) {
            signingDelegate.signOffline(context, envelopeId, new DSOfflineSigningListener() {

                @Override
                public void onSuccess(@NonNull String envelopeId) {
                    SignOfflineModel signOfflineModel = new SignOfflineModel(Status.COMPLETE, null);
                    signOfflineLiveData.setValue(signOfflineModel);
                }

                @Override
                public void onCancel(@NonNull String envelopeId) {
                    /* NO- OP */
                }

                @Override
                public void onError(@NonNull DSSigningException exception) {
                    SignOfflineModel signOfflineModel = new SignOfflineModel(Status.ERROR, exception);
                    signOfflineLiveData.setValue(signOfflineModel);
                }
            });
        }
    }

    public void signOnline(@NonNull Context context, @NonNull String envelopeId) {
        // DS: Online Signing using local envelopeId
        if (signingDelegate != null) {
            signingDelegate.createEnvelopeAndLaunchOnlineSigning(context, envelopeId, new DSOnlineSigningListener() {

                @Override
                public void onStart(@NonNull String envelopeId) {
                    SignOnlineModel signOnlineModel = new SignOnlineModel(Status.START, null);
                    signOnlineLiveData.setValue(signOnlineModel);
                }

                @Override
                public void onSuccess(@NonNull String envelopeId) {
                    SignOnlineModel signOnlineModel = new SignOnlineModel(Status.COMPLETE, null);
                    signOnlineLiveData.setValue(signOnlineModel);
                }

                @Override
                public void onCancel(@NonNull String envelopeId, @NonNull String recipientId) {
                    /* NO-OP */
                }

                @Override
                public void onError(@Nullable String envelopeId, @NonNull DSSigningException exception) {
                    SignOnlineModel signOnlineModel = new SignOnlineModel(Status.ERROR, exception);
                    signOnlineLiveData.setValue(signOnlineModel);
                }

                @Override
                public void onRecipientSigningError(@NonNull String envelopeId, @NonNull String recipientId, @NonNull DSSigningException exception) {
                    /* NO-OP */
                }

                @Override
                public void onRecipientSigningSuccess(@NonNull String envelopeId, @NonNull String recipientId) {
                    /* NO-OP */
                }
            });
        }
    }

    public void captiveSigning(@NonNull Context context, @NonNull DSEnvelope envelope) {
        Objects.requireNonNull(signingDelegate).launchCaptiveSigning(context,
                envelope.getEnvelopeId(),
                Objects.requireNonNull(Objects.requireNonNull(envelope.getRecipients()).get(0).getClientUserId()),
                new DSCaptiveSigningListener() {
                    @Override
                    public void onCancel(
                            @NonNull String envelopeId,
                            @NonNull String recipientId
                    ) {
                        /* NO-OP */
                    }

                    @Override
                    public void onError(
                            @Nullable String envelopeId,
                            @NonNull DSSigningException exception
                    ) {
                        CaptiveSigningModel captiveSigningModel = new CaptiveSigningModel(Status.ERROR, exception);
                        captiveSigningLiveData.setValue(captiveSigningModel);
                    }

                    @Override
                    public void onRecipientSigningError(
                            @NonNull String envelopeId,
                            @NonNull String recipientId,
                            @NonNull DSSigningException exception
                    ) {
                        /* NO-OP */
                    }

                    @Override
                    public void onRecipientSigningSuccess(
                            @NonNull String envelopeId,
                            @NonNull String recipientId
                    ) {
                        /* NO-OP */
                    }

                    @Override
                    public void onStart(@NonNull String envelopeId) {
                        CaptiveSigningModel captiveSigningModel = new CaptiveSigningModel(Status.START, null);
                        captiveSigningLiveData.setValue(captiveSigningModel);
                    }

                    @Override
                    public void onSuccess(@NonNull String envelopeId) {
                        CaptiveSigningModel captiveSigningModel = new CaptiveSigningModel(Status.COMPLETE, null);
                        captiveSigningLiveData.setValue(captiveSigningModel);
                    }
                });
    }

    public void signCachedEnvelope(@NonNull Context context, @NonNull String envelopeId) {
        // DS: Offline Signing using local envelopeId
        if (signingDelegate != null) {
            signingDelegate.signOffline(context, envelopeId, new DSEnvelopeOfflineSigningListener() {

                @Override
                public void onSuccess(@NonNull String envelopeId) {
                    CachedEnvelopeSigningModel signOfflineModel = new CachedEnvelopeSigningModel(Status.COMPLETE, null);
                    cachedEnvelopeSigningLiveData.setValue(signOfflineModel);
                }

                @Override
                public void onCancel(@NonNull String envelopeId) {
                    /* NO- OP */
                }

                @Override
                public void onError(@NonNull DSSigningException exception) {
                    CachedEnvelopeSigningModel signOfflineModel = new CachedEnvelopeSigningModel(Status.ERROR, exception);
                    cachedEnvelopeSigningLiveData.setValue(signOfflineModel);
                }
            });
        }
    }

    @NonNull
    public MutableLiveData<SignOfflineModel> getSignOfflineLiveData() {
        return signOfflineLiveData;
    }

    @NonNull
    public MutableLiveData<SignOnlineModel> getSignOnlineLiveData() {
        return signOnlineLiveData;
    }

    @NonNull
    public MutableLiveData<CaptiveSigningModel> getCaptiveSigningLiveData() {
        return captiveSigningLiveData;
    }

    @NonNull
    public MutableLiveData<CachedEnvelopeSigningModel> getCachedEnvelopeSigningLiveData() {
        return cachedEnvelopeSigningLiveData;
    }
}
