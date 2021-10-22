package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.dsmodels.DSEnvelope;
import com.docusign.androidsdk.exceptions.DSEnvelopeException;

public class GetCachedEnvelopeModel {

    @NonNull
    private final Status status;

    @Nullable
    private final DSEnvelope envelope;

    @Nullable
    private final DSEnvelopeException exception;

    public GetCachedEnvelopeModel(@NonNull Status status, @Nullable DSEnvelope envelope, @Nullable DSEnvelopeException exception) {
        this.status = status;
        this.envelope = envelope;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public DSEnvelope getEnvelope() {
        return envelope;
    }

    @Nullable
    public DSEnvelopeException getException() {
        return exception;
    }
}
