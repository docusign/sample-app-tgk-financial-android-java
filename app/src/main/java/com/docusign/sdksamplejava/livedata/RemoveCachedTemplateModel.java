package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.dsmodels.DSTemplateDefinition;
import com.docusign.androidsdk.exceptions.DSTemplateException;

public class RemoveCachedTemplateModel {

    @NonNull
    private final Status status;

    @Nullable
    private final DSTemplateDefinition templateDefinition;

    private final int position;

    @Nullable
    private final DSTemplateException exception;

    public RemoveCachedTemplateModel(@NonNull Status status, @Nullable DSTemplateDefinition templateDefinition, int position, @Nullable DSTemplateException exception) {
        this.status = status;
        this.templateDefinition = templateDefinition;
        this.position = position;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public DSTemplateDefinition getTemplateDefinition() {
        return templateDefinition;
    }

    public int getPosition() {
        return position;
    }

    @Nullable
    public DSTemplateException getException() {
        return exception;
    }
}
