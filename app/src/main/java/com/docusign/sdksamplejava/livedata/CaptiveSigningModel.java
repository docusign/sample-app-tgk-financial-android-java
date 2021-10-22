package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.dsmodels.DSTemplate;
import com.docusign.androidsdk.exceptions.DSSigningException;
import com.docusign.androidsdk.exceptions.DSTemplateException;

public class CaptiveSigningModel {

    @NonNull
    private final Status status;

    @Nullable
    private final DSSigningException exception;

    public CaptiveSigningModel(@NonNull Status status, @Nullable DSSigningException exception) {
        this.status = status;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public DSSigningException getException() {
        return exception;
    }
}