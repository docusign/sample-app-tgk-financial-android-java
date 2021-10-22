package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.exceptions.DSException;

public class SyncAllEnvelopesModel {

    @NonNull
    private final Status status;

    @Nullable
    private final DSException exception;

    public SyncAllEnvelopesModel(@NonNull Status status, @Nullable DSException exception) {
        this.status = status;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public DSException getException() {
        return exception;
    }
}
