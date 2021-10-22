package com.docusign.sdksamplejava.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccreditedInvestorVerifier {
    
    @NonNull
    private final String name;
    
    @NonNull
    private final String company;
    
    @NonNull
    private final String licenseNumber;
    
    @NonNull
    private final String stateRegistered;
    
    @NonNull
    private final String addressLine1;
    
    @Nullable
    private final String addressLine2;
    
    @NonNull
    private final String addressLine3;

    public AccreditedInvestorVerifier(@NonNull String name, @NonNull String company, @NonNull String licenseNumber, @NonNull String stateRegistered, @NonNull String addressLine1, @Nullable String addressLine2, @NonNull String addressLine3) {
        this.name = name;
        this.company = company;
        this.licenseNumber = licenseNumber;
        this.stateRegistered = stateRegistered;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getCompany() {
        return company;
    }

    @NonNull
    public String getLicenseNumber() {
        return licenseNumber;
    }

    @NonNull
    public String getStateRegistered() {
        return stateRegistered;
    }

    @NonNull
    public String getAddressLine1() {
        return addressLine1;
    }

    @Nullable
    public String getAddressLine2() {
        return addressLine2;
    }

    @NonNull
    public String getAddressLine3() {
        return addressLine3;
    }
}
