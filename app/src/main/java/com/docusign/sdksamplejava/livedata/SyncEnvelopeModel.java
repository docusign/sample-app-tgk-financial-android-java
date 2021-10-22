package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.exceptions.DSSyncException;

public class SyncEnvelopeModel {

    @NonNull
    private final Status status;

    private final int position;

    @Nullable
    private final DSSyncException exception;

    public SyncEnvelopeModel(@NonNull Status status, int position, @Nullable DSSyncException exception) {
        this.status = status;
        this.position = position;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    public int getPosition() {
        return position;
    }

    @Nullable
    public DSSyncException getException() {
        return exception;
    }
}
