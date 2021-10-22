package com.docusign.sdksamplejava.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSEnvelopeDelegate;
import com.docusign.androidsdk.dsmodels.DSEnvelope;
import com.docusign.androidsdk.exceptions.DSEnvelopeException;
import com.docusign.androidsdk.exceptions.DSException;
import com.docusign.androidsdk.exceptions.DSSyncException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSGetCachedEnvelopeListener;
import com.docusign.androidsdk.listeners.DSGetEnvelopeIdsListener;
import com.docusign.androidsdk.listeners.DSSyncAllEnvelopesListener;
import com.docusign.androidsdk.listeners.DSSyncEnvelopeListener;
import com.docusign.sdksamplejava.livedata.GetCachedEnvelopeModel;
import com.docusign.sdksamplejava.livedata.GetSyncPendingEnvelopeIdsModel;
import com.docusign.sdksamplejava.livedata.Status;
import com.docusign.sdksamplejava.livedata.SyncAllEnvelopesModel;
import com.docusign.sdksamplejava.livedata.SyncEnvelopeModel;

import java.util.List;

public class EnvelopeViewModel extends ViewModel {

    public static final String TAG = EnvelopeViewModel.class.getSimpleName();

    @NonNull
    private final MutableLiveData<SyncEnvelopeModel> syncEnvelopeLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<SyncAllEnvelopesModel> syncAllEnvelopesLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<GetSyncPendingEnvelopeIdsModel> getSyncPendingEnvelopeIdsLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<GetCachedEnvelopeModel> getCachedEnvelopeLiveData = new MutableLiveData<>();

    @Nullable
    private DSEnvelopeDelegate envelopeDelegate;

    public EnvelopeViewModel() {
        try {
            envelopeDelegate = DocuSign.getInstance().getEnvelopeDelegate();
        } catch (DocuSignNotInitializedException e) {
            envelopeDelegate = null;
        }
    }

    public void syncEnvelope(@NonNull String envelopeId, int position) {
        // DS: Sync envelope
        if (envelopeDelegate != null) {
            envelopeDelegate.syncEnvelope(envelopeId, new DSSyncEnvelopeListener() {

                @Override
                public void onSuccess(@NonNull String localEnvelopeId, @Nullable String serverEnvelopeId) {
                    Log.d(TAG, "Sync envelope Success- Local EnvelopeId: $localEnvelopeId Server EnvelopeId: $serverEnvelopeId");
                    SyncEnvelopeModel syncEnvelopeModel = new SyncEnvelopeModel(Status.COMPLETE, position, null);
                    syncEnvelopeLiveData.setValue(syncEnvelopeModel);
                }

                @Override
                public void onError(@NonNull DSSyncException exception, @NonNull String s, @Nullable Integer integer) {
                    SyncEnvelopeModel syncEnvelopeModel = new SyncEnvelopeModel(Status.ERROR, position, exception);
                    syncEnvelopeLiveData.setValue(syncEnvelopeModel);
                }
            }, true);
        }
    }

    public void syncAllEnvelopes() {
        // DS: Sync all envelopes
        if (envelopeDelegate != null) {
            envelopeDelegate.syncAllEnvelopes(new DSSyncAllEnvelopesListener() {

                @Override
                public void onStart() {
                    SyncAllEnvelopesModel syncAllEnvelopesModel = new SyncAllEnvelopesModel(Status.START, null);
                    syncAllEnvelopesLiveData.setValue(syncAllEnvelopesModel);
                }

                @Override
                public void onComplete(@Nullable List<String> failedEnvelopeIdList) {
                    SyncAllEnvelopesModel syncAllEnvelopesModel = new SyncAllEnvelopesModel(Status.COMPLETE, null);
                    syncAllEnvelopesLiveData.setValue(syncAllEnvelopesModel);
                }

                @Override
                public void onEnvelopeSyncError(@NonNull DSSyncException exception, @NonNull String localEnvelopeId, @Nullable Integer syncRetryCount) {
                    Log.d(TAG, "Sync Error- Local EnvelopeId: $localEnvelopeId : ${exception.message}");
                }

                @Override
                public void onEnvelopeSyncSuccess(@NonNull String localEnvelopeId, @Nullable String serverEnvelopeId) {
                    /* NO-OP */
                }

                @Override
                public void onError(@NonNull DSException exception) {
                    SyncAllEnvelopesModel syncAllEnvelopesModel = new SyncAllEnvelopesModel(Status.ERROR, exception);
                    syncAllEnvelopesLiveData.setValue(syncAllEnvelopesModel);
                }

            }, true);
        }
    }

    public void getSyncPendingEnvelopeIds() {
        // DS: Get sync pending envelope Ids
        if (envelopeDelegate != null) {
            envelopeDelegate.
                    getSyncPendingEnvelopeIdsList(new DSGetEnvelopeIdsListener() {
                                                      @Override
                                                      public void onComplete(@NonNull List<String> envelopeIdList) {
                                                          GetSyncPendingEnvelopeIdsModel getSyncPendingEnvelopeIdsModel = new
                                                                  GetSyncPendingEnvelopeIdsModel(Status.COMPLETE, envelopeIdList, null);
                                                          getSyncPendingEnvelopeIdsLiveData.setValue(getSyncPendingEnvelopeIdsModel);
                                                      }

                                                      @Override
                                                      public void onError(@NonNull DSEnvelopeException exception) {
                                                          GetSyncPendingEnvelopeIdsModel getSyncPendingEnvelopeIdsModel = new
                                                                  GetSyncPendingEnvelopeIdsModel(Status.ERROR, null, exception);
                                                          getSyncPendingEnvelopeIdsLiveData.setValue(getSyncPendingEnvelopeIdsModel);
                                                      }
                                                  }
                    );
        }
    }

    public void getCachedEnvelope(@NonNull String envelopeId) {
        // DS: Get cached envelope
        if (envelopeDelegate != null) {
            envelopeDelegate.getCachedEnvelope(envelopeId, new DSGetCachedEnvelopeListener() {
                @Override
                public void onComplete(@NonNull DSEnvelope envelope) {
                    GetCachedEnvelopeModel getCachedEnvelopeModel = new GetCachedEnvelopeModel(Status.COMPLETE, envelope, null);
                    getCachedEnvelopeLiveData.setValue(getCachedEnvelopeModel);
                }

                @Override
                public void onError(@NonNull DSEnvelopeException exception) {
                    GetCachedEnvelopeModel getCachedEnvelopeModel = new GetCachedEnvelopeModel(Status.ERROR, null, exception);
                    getCachedEnvelopeLiveData.setValue(getCachedEnvelopeModel);
                }
            });
        }
    }

    @NonNull
    public MutableLiveData<SyncEnvelopeModel> getSyncEnvelopeLiveData() {
        return syncEnvelopeLiveData;
    }

    @NonNull
    public MutableLiveData<SyncAllEnvelopesModel> getSyncAllEnvelopesLiveData() {
        return syncAllEnvelopesLiveData;
    }

    @NonNull
    public MutableLiveData<GetSyncPendingEnvelopeIdsModel> getGetSyncPendingEnvelopeIdsLiveData() {
        return getSyncPendingEnvelopeIdsLiveData;
    }

    @NonNull
    public MutableLiveData<GetCachedEnvelopeModel> getGetCachedEnvelopeLiveData() {
        return getCachedEnvelopeLiveData;
    }
}
