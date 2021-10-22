package com.docusign.sdksamplejava.model;

import androidx.annotation.NonNull;

import java.io.File;

public class AccreditedInvestorVerification {

    @NonNull
    private final String clientName;

    @NonNull
    private final String clientAddress;

    @NonNull
    private final AccreditedInvestorVerifier verifier;

    @NonNull
    private final File file;

    public AccreditedInvestorVerification(@NonNull String clientName, @NonNull String clientAddress, @NonNull AccreditedInvestorVerifier verifier, @NonNull File file) {
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.verifier = verifier;
        this.file = file;
    }

    @NonNull
    public String getClientName() {
        return clientName;
    }

    @NonNull
    public String getClientAddress() {
        return clientAddress;
    }

    @NonNull
    public AccreditedInvestorVerifier getVerifier() {
        return verifier;
    }

    @NonNull
    public File getFile() {
        return file;
    }
}