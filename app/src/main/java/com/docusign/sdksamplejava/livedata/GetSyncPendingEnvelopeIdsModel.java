package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.exceptions.DSEnvelopeException;

import java.util.List;

public class GetSyncPendingEnvelopeIdsModel {

    @NonNull
    private final Status status;

    @Nullable
    private final List<String> envelopeIds;

    @Nullable
    private final DSEnvelopeException exception;

    public GetSyncPendingEnvelopeIdsModel(@NonNull Status status, @Nullable List<String> envelopeIds, @Nullable DSEnvelopeException exception) {
        this.status = status;
        this.envelopeIds = envelopeIds;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public List<String> getEnvelopeIds() {
        return envelopeIds;
    }

    @Nullable
    public DSEnvelopeException getException() {
        return exception;
    }
}
