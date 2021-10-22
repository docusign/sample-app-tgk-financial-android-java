package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.dsmodels.DSTemplate;
import com.docusign.androidsdk.exceptions.DSTemplateException;

public class CacheTemplateModel {

    @NonNull
    private final Status status;

    @Nullable
    private final DSTemplate template;

    private final int position;

    @Nullable
    private final DSTemplateException exception;

    public CacheTemplateModel(@NonNull Status status, @Nullable DSTemplate template, int position, @Nullable DSTemplateException exception) {
        this.status = status;
        this.template = template;
        this.position = position;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public DSTemplate getTemplate() {
        return template;
    }

    public int getPosition() {
        return position;
    }

    @Nullable
    public DSTemplateException getException() {
        return exception;
    }
}
