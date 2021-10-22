package com.docusign.sdksamplejava.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Client {

    @NonNull
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String phone;

    @NonNull
    private String email;

    @NonNull
    private String addressLine1;

    @NonNull
    private String addressLine2;

    @NonNull
    private String addressLine3;

    @NonNull
    private String investmentAmount;

    @NonNull
    private String storePref;

    private boolean cacheEnvelope;

    public Client(@NonNull String id, @NonNull String name, @NonNull String phone, @NonNull String email, @NonNull String addressLine1, @NonNull String addressLine2, @NonNull String addressLine3, @NonNull String investmentAmount, @NonNull String storePref) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.investmentAmount = investmentAmount;
        this.storePref = storePref;
        cacheEnvelope = false;
    }

    public Client(@NonNull String id, @NonNull String name, @NonNull String phone, @NonNull String email, @NonNull String addressLine1, @NonNull String addressLine2, @NonNull String addressLine3, @NonNull String investmentAmount, @NonNull String storePref, boolean cacheEnvelope) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.investmentAmount = investmentAmount;
        this.storePref = storePref;
        this.cacheEnvelope = cacheEnvelope;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(@NonNull String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @NonNull
    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(@NonNull String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @NonNull
    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(@NonNull String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    @NonNull
    public String getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(@NonNull String investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    @NonNull
    public String getStorePref() {
        return storePref;
    }

    public void setStorePref(@NonNull String storePref) {
        this.storePref = storePref;
    }

    public boolean isCacheEnvelope() {
        return cacheEnvelope;
    }
}
