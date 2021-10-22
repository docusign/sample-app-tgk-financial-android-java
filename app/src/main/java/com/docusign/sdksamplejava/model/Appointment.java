package com.docusign.sdksamplejava.model;

import androidx.annotation.NonNull;

public class Appointment {

    @NonNull
    private final String date;

    @NonNull
    private final Client client;

    private boolean clientSigned;

    public Appointment(@NonNull String date, @NonNull Client client) {
        this(date, client, false);
    }

    public Appointment(@NonNull String date, @NonNull Client client, boolean clientSigned) {
        this.date = date;
        this.client = client;
        this.clientSigned = clientSigned;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public Client getClient() {
        return client;
    }

    public boolean isClientSigned() {
        return clientSigned;
    }

    public void setClientSigned(boolean clientSigned) {
        this.clientSigned = clientSigned;
    }
}
