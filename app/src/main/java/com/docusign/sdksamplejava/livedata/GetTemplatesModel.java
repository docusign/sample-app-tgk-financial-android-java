package com.docusign.sdksamplejava.livedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.dsmodels.DSTemplates;
import com.docusign.androidsdk.exceptions.DSTemplateException;

public class GetTemplatesModel {

    @NonNull
    private final Status status;

    @Nullable
    private final DSTemplates dsTemplates;

    @Nullable
    private final DSTemplateException exception;

    public GetTemplatesModel(@NonNull Status status, @Nullable DSTemplates dsTemplate, @Nullable DSTemplateException exception) {
        this.status = status;
        this.dsTemplates = dsTemplate;
        this.exception = exception;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public DSTemplates getDsTemplates() {
        return dsTemplates;
    }

    @Nullable
    public DSTemplateException getException() {
        return exception;
    }
}
