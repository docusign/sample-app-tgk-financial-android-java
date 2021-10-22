package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.exceptions.DSTemplateException;

public class UseTemplateOnlineModel {

    @NonNull
    private final Status status;

    @Nullable
    private final String envelopeId;

    @Nullable
    private final DSTemplateException exception;

    public UseTemplateOnlineModel(@NonNull Status status, @Nullable String envelopeId, @Nullable DSTemplateException exception) {
        this.status = status;
        this.envelopeId = envelopeId;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public String getEnvelopeId() {
        return envelopeId;
    }

    @Nullable
    public DSTemplateException getException() {
        return exception;
    }
}
