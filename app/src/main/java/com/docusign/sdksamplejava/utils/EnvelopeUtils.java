package com.docusign.sdksamplejava.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSAuthenticationDelegate;
import com.docusign.androidsdk.dsmodels.DSCustomFields;
import com.docusign.androidsdk.dsmodels.DSDocument;
import com.docusign.androidsdk.dsmodels.DSEnvelope;
import com.docusign.androidsdk.dsmodels.DSEnvelopeDefaults;
import com.docusign.androidsdk.dsmodels.DSEnvelopeDefaultsUniqueRecipientSelectorType;
import com.docusign.androidsdk.dsmodels.DSEnvelopeRecipient;
import com.docusign.androidsdk.dsmodels.DSListCustomField;
import com.docusign.androidsdk.dsmodels.DSRecipientDefault;
import com.docusign.androidsdk.dsmodels.DSRecipientType;
import com.docusign.androidsdk.dsmodels.DSTab;
import com.docusign.androidsdk.dsmodels.DSTabType;
import com.docusign.androidsdk.dsmodels.DSTextCustomField;
import com.docusign.androidsdk.dsmodels.DSUser;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DSEnvelopeException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.InPersonSigner;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Tabs;
import com.docusign.esign.model.Text;
import com.docusign.sdksamplejava.SDKSampleApplication;
import com.docusign.sdksamplejava.model.AccreditedInvestorVerification;
import com.docusign.sdksamplejava.model.Client;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EnvelopeUtils {

    public static final String TAG = EnvelopeUtils.class.getSimpleName();

    @Nullable
    public static EnvelopeDefinition buildEnvelopeDefinition(@NonNull String clientPref, @NonNull Activity activity) {
        File document =
                clientPref.equals(Constants.CLIENT_A_PREF) ? ((SDKSampleApplication)activity.getApplication()).getPortfolioADoc() :
                ((SDKSampleApplication)activity.getApplication()).getPortfolioBDoc();

        int size = (int) document.length();
        byte[] buffer = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(document));
            buf.read(buffer, 0, buffer.length);
            buf.close();
        } catch (IOException exception) {
            Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Subject");
        Document doc1 = new Document();
        String doc1b64 = Base64.encodeToString(buffer, Base64.DEFAULT);
        doc1.setDocumentBase64(doc1b64);
        doc1.setName("Lorem Ipsum");
        doc1.setFileExtension("pdf");
        doc1.setDocumentId("3");
        envelopeDefinition.setDocuments(Collections.singletonList(doc1));

        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String clientString = sharedPreferences.getString(clientPref, null);

        if (clientString == null) {
            Toast.makeText(activity, "Client details not available", Toast.LENGTH_LONG).show();
            return null;
        }

        Client client = new Gson().fromJson(clientString, Client.class);
        InPersonSigner signer1 = new InPersonSigner();
        signer1.setEmail(client.getEmail());
        signer1.setName(client.getName());
        signer1.setSignerName(client.getName());
        signer1.setSignerEmail(client.getEmail());
        signer1.clientUserId(client.getId());
        signer1.recipientId("1");

        SignHere signHere1 = new SignHere();
        signHere1.setDocumentId("3");
        signHere1.setRecipientId("1");
        signHere1.setPageNumber("1");
        signHere1.setXPosition("370");
        signHere1.setYPosition("720");

        Tabs signer1Tabs = new Tabs();
        signer1Tabs.setTextTabs(getEnvelopeDefinitionTextTabs(client));
        signer1Tabs.setSignHereTabs(Arrays.asList(signHere1));
        signer1.setTabs(signer1Tabs);

        Recipients recipients = new Recipients();

        recipients.addInPersonSignersItem(signer1);
        envelopeDefinition.setRecipients(recipients);

        envelopeDefinition.setStatus("sent");

        return envelopeDefinition;
    }

    @Nullable
    public static EnvelopeDefinition buildCachedEnvelopeDefinition(@NonNull String clientPref, @NonNull Activity activity){
        File document =
                clientPref.equals(Constants.CLIENT_A_PREF) ? ((SDKSampleApplication)activity.getApplication()).getPortfolioADoc() :
                        ((SDKSampleApplication)activity.getApplication()).getPortfolioBDoc();

        int size = (int) document.length();
        byte[] buffer = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(document));
            buf.read(buffer, 0, buffer.length);
            buf.close();
        } catch (IOException exception) {
            Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Subject");
        Document doc1 = new Document();
        String doc1b64 = Base64.encodeToString(buffer, Base64.DEFAULT);
        doc1.setDocumentBase64(doc1b64);
        doc1.setName("Lorem Ipsum");
        doc1.setFileExtension("pdf");
        doc1.setDocumentId("3");
        doc1.setOrder("1");
        envelopeDefinition.setDocuments(Collections.singletonList(doc1));

        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String clientString = sharedPreferences.getString(clientPref, null);

        if (clientString == null) {
            Toast.makeText(activity, "Client details not available", Toast.LENGTH_LONG).show();
            return null;
        }

        Client client = new Gson().fromJson(clientString, Client.class);

        DSUser user;
        try {
            user = DocuSign.getInstance().getAuthenticationDelegate().getLoggedInUser(activity);
        } catch (DSAuthenticationException | DocuSignNotInitializedException exception) {
            Toast.makeText(activity, "User details not available", Toast.LENGTH_LONG).show();
            return null;
        }

        InPersonSigner signer1 = new InPersonSigner();
        signer1.recipientId("1");
        signer1.setSigningGroupId(null);
        signer1.setSigningGroupName(null);
        signer1.setCanSignOffline("true");
        signer1.setRoutingOrder("1");
        signer1.setRecipientType(DSRecipientType.IN_PERSON_SIGNER.name());
        signer1.setHostEmail(user.getEmail());
        signer1.setHostName(user.getName());
        signer1.setSignerName(client.getName());

        SignHere signHere1 = new SignHere();
        signHere1.setDocumentId("3");
        signHere1.setRecipientId("1");
        signHere1.setPageNumber("1");
        signHere1.setXPosition("370");
        signHere1.setYPosition("720");

        Tabs signer1Tabs = new Tabs();
        signer1Tabs.setTextTabs(getEnvelopeDefinitionTextTabs(client));
        signer1Tabs.setSignHereTabs(Collections.singletonList(signHere1));
        signer1.setTabs(signer1Tabs);

        Recipients recipients = new Recipients();
        recipients.addInPersonSignersItem(signer1);
        envelopeDefinition.setRecipients(recipients);

        envelopeDefinition.setIs21CFRPart11("false");
        envelopeDefinition.setSignerCanSignOnMobile("true");
        envelopeDefinition.setStatus("sent");
        envelopeDefinition.setEmailSubject("Test Envelope Offline Signing");

        return envelopeDefinition;
    }

    @Nullable
    public static DSEnvelope buildEnvelope(@NonNull Context context, @NonNull File file, @Nullable AccreditedInvestorVerification accreditedInvestorVerification, @Nullable String clientPref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (clientPref == null) {
            Toast.makeText(context, "Client Preference not available", Toast.LENGTH_LONG).show();
            return null;
        }
        String clientString = sharedPreferences.getString(clientPref, null);

        if (clientString == null) {
            Toast.makeText(context, "Client details not available", Toast.LENGTH_LONG).show();
            return null;
        }

        Client client = new Gson().fromJson(clientString, Client.class);
        try {
            DSAuthenticationDelegate authenticationDelegate = DocuSign.getInstance().getAuthenticationDelegate();
            DSUser user = authenticationDelegate.getLoggedInUser(context);
            URI fileURI = file.toURI();
            List<DSDocument> documents = new ArrayList<>();
            DSDocument document = new DSDocument.Builder()
                    .documentId(1)
                    .uri(fileURI.toString())
                    .name("TGK Capital Portfolio B Agreement")
                    .build();
            documents.add(document);
            List<DSTab> tabs = createInvestmentAgreementTabs(client);

            List<DSEnvelopeRecipient> recipients = new ArrayList<>();
            recipients.add(
                    new DSEnvelopeRecipient.Builder()
                            .recipientId(1)
                            .routingOrder(1)
                            .hostName(user.getName())
                            .hostEmail(user.getEmail())
                            .signerName(client.getName())
                            .signerEmail(client.getEmail())
                            .type(DSRecipientType.IN_PERSON_SIGNER)
                            .tabs(tabs)
                            .build()
            );

            int value = 0;

            if (accreditedInvestorVerification != null) {
                value = 1;
                URI accreditedInvestorVerificationFileURI = accreditedInvestorVerification.getFile().toURI();
                DSDocument accreditedInvestorVerificationDocument = new DSDocument.Builder()
                        .documentId(2)
                        .uri(accreditedInvestorVerificationFileURI.toString())
                        .name("TGK Capital Portfolio B Agreement")
                        .build();
                documents.add(accreditedInvestorVerificationDocument);

                List<DSTab> accreditedInvestorVerificationTabs = createAccreditedInvestorVerificationTabs(accreditedInvestorVerification);
                recipients.add(
                        new DSEnvelopeRecipient.Builder()
                                .recipientId(2)
                                .routingOrder(2)
                                .hostName(user.getName())
                                .hostEmail(user.getEmail())
                                .signerName(user.getName())
                                .signerEmail(user.getEmail())
                                .type(DSRecipientType.IN_PERSON_SIGNER)
                                .tabs(accreditedInvestorVerificationTabs)
                                .build()
                );
            }
            recipients.add(
                    new DSEnvelopeRecipient.Builder()
                            .recipientId(2 + value)
                            .routingOrder(2 + value)
                            .signerName("Jack Doe") // if someone needs a signed copy, their name here
                            .signerEmail("j.d@gmail.com") // if someone needs a signed copy, their email here
                            .type(DSRecipientType.CARBON_COPY)
                            .build()
            );
            // DS: Envelope creation
            return new DSEnvelope.Builder()
                    .envelopeName("TGK Capital Portfolio B Agreement")
                    .documents(documents)
                    .recipients(recipients)
                    .textCustomFields( // this is for free-form metadata
                            Objects.requireNonNull(getTextCustomFields(client))
                    )
                    .build();
        } catch (DSAuthenticationException | DocuSignNotInitializedException | DSEnvelopeException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return null;
    }

    @NonNull
    private static List<DSTab> createAccreditedInvestorVerificationTabs(@NonNull AccreditedInvestorVerification accreditedInvestorVerification) {
        List<DSTab> tabs = new ArrayList<>();
        try {
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(470)
                            .yPosition(25)
                            .type(DSTabType.DATE_SIGNED)
                            .optional(true)
                            .build()
            );                // Date
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(130)
                            .yPosition(50)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getClientName())
                            .optional(true)
                            .build()
            );       // Client name
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(130)
                            .yPosition(80)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getClientAddress())
                            .optional(true)
                            .build()
            );                    // Client address
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(220)
                            .yPosition(140)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getVerifier().getName())
                            .optional(true)
                            .build()
            );                      // Verifier name
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(150)
                            .yPosition(250)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getVerifier().getLicenseNumber())
                            .optional(true)
                            .build()
            );                      // Verifier License Number
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(480)
                            .yPosition(250)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getVerifier().getStateRegistered())
                            .optional(true)
                            .build()
            );                      // Verifier State Registered
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(50)
                            .yPosition(560)
                            .type(DSTabType.SIGNATURE)
                            .optional(false)
                            .build()
            );                      // Verifier Signature
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(50)
                            .yPosition(620)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getVerifier().getName())
                            .optional(true)
                            .build()
            );                      // Verifier Name
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(50)
                            .yPosition(660)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getVerifier().getCompany())
                            .optional(true)
                            .build()
            );                      // Verifier Company
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(320)
                            .yPosition(570)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getVerifier().getAddressLine1())
                            .optional(true)
                            .build()
            );                      // Verifier AddressLine1
            if (accreditedInvestorVerification.getVerifier().getAddressLine2() != null) {
                tabs.add(
                        new DSTab.Builder()
                                .documentId(2)
                                .recipientId(2)
                                .pageNumber(1)
                                .xPosition(320)
                                .yPosition(620)
                                .type(DSTabType.TEXT)
                                .value(accreditedInvestorVerification.getVerifier().getAddressLine2())
                                .optional(true)
                                .build()
                );                      // Verifier AddressLine2
            }
            tabs.add(
                    new DSTab.Builder()
                            .documentId(2)
                            .recipientId(2)
                            .pageNumber(1)
                            .xPosition(320)
                            .yPosition(660)
                            .type(DSTabType.TEXT)
                            .value(accreditedInvestorVerification.getVerifier().getAddressLine3())
                            .optional(true)
                            .build()
            );             // Verifier AddressLine3
        } catch (DSEnvelopeException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return tabs;
    }

    private static List<DSTab> createInvestmentAgreementTabs(@NonNull Client client) {
        List<DSTab> tabs = new ArrayList<>();

        try {
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(370)
                            .yPosition(110)
                            .type(DSTabType.TEXT)
                            .value(client.getAddressLine1())
                            .optional(true)
                            .build()
            );                 // Address line 1
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(370)
                            .yPosition(140)
                            .type(DSTabType.TEXT)
                            .value(client.getAddressLine2())
                            .optional(true)
                            .build()
            );                        // Address line 2
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(370)
                            .yPosition(165)
                            .type(DSTabType.TEXT)
                            .value(client.getAddressLine3())
                            .optional(true)
                            .build()
            );                      // Address line 3
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(50)
                            .yPosition(165)
                            .type(DSTabType.TEXT)
                            .value(client.getName())
                            .optional(true)
                            .build()
            );                       // Full name
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(370)
                            .yPosition(550)
                            .type(DSTabType.TEXT)
                            .value(client.getId())
                            .optional(true)
                            .build()
            );                        // Client number
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(370)
                            .yPosition(630)
                            .type(DSTabType.TEXT)
                            .value(client.getInvestmentAmount())
                            .optional(true)
                            .build()
            );                        // Investment amount
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(370)
                            .yPosition(720)
                            .type(DSTabType.SIGNATURE)
                            .build()
            );                        // Signature
            tabs.add(
                    new DSTab.Builder()
                            .documentId(1)
                            .recipientId(1)
                            .pageNumber(1)
                            .xPosition(50)
                            .yPosition(730)
                            .type(DSTabType.TEXT)
                            .value(client.getName())
                            .optional(true)
                            .build()
            );                 // Name of the applicant
        } catch (DSEnvelopeException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return tabs;
    }

    @NonNull
    private static List<DSTextCustomField> getTextCustomFields(@NonNull Client client) {
        DSTextCustomField textCustomField1;
        List<DSTextCustomField> textCustomFields = new ArrayList<>();
        try {
            textCustomField1 = new DSTextCustomField.Builder()
                    .fieldId(123)
                    .name("Phone number")
                    .value(client.getPhone())
                    .build();
            textCustomFields.add(textCustomField1);
        } catch (DSEnvelopeException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return textCustomFields;
    }

    @Nullable
    public static DSEnvelopeDefaults buildEnvelopeDefaults(@NonNull Context context, @NonNull String templateId, @Nullable String templateName, @Nullable String clientPref) throws DocuSignNotInitializedException, DSAuthenticationException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (clientPref == null) {
            Toast.makeText(context, "Client Preference not available", Toast.LENGTH_LONG).show();
            return null;
        }
        String clientString = sharedPreferences.getString(clientPref, null);

        if (clientString == null) {
            Toast.makeText(context, "Client details not available", Toast.LENGTH_LONG).show();
            return null;
        }
        Client client = new Gson().fromJson(clientString, Client.class);

        List<DSRecipientDefault> recipientDefaults = new ArrayList<>();
        DSAuthenticationDelegate authenticationDelegate = DocuSign.getInstance().getAuthenticationDelegate();
        DSUser user = authenticationDelegate.getLoggedInUser(context);


        String emailBlurb = "";
        String envelopeTitle = "Investment Agreement";

        // In person signer
        DSRecipientDefault recipient1 = new DSRecipientDefault(
                user.getEmail(),
                user.getName(),
                client.getName(),
                client.getEmail(),
                null,
                "IPS1",  // This should match the ROLE NAME in the template in DocuSign web portal
                DSEnvelopeDefaultsUniqueRecipientSelectorType.ROLE_NAME
        );

        // Receives carbon copy
        DSRecipientDefault recipient2 = new DSRecipientDefault(
                "j.d@gmail.com",
                "Jack Doe",
                null,
                null,
                null,
                "CC1",  // This should match the ROLE NAME in the template in DocuSign web portal
                DSEnvelopeDefaultsUniqueRecipientSelectorType.ROLE_NAME
        );

        recipientDefaults.add(recipient1);
        recipientDefaults.add(recipient2);

        boolean isPortfolioA = templateId.equals(Constants.TemplateConstants.PortfolioA.TEMPLATE_ID);

        Map<String, String> tabValueDefaults = new HashMap<>();
        // 'Text 6ffeb574-5e0f-4aa0-a2f6-7c402db4045e' is retrieved from 'Data Label' attribute in template document in DocuSign web portal

        String tagLabelAddressLine1 =
                isPortfolioA ? Constants.TemplateConstants.PortfolioA.TAG_DATA_LABEL_ADDRESS_LINE_1
                        : Constants.TemplateConstants.PortfolioB.TAG_DATA_LABEL_ADDRESS_LINE_1;

        String tagLabelAddressLine2 =
                isPortfolioA ? Constants.TemplateConstants.PortfolioA.TAG_DATA_LABEL_ADDRESS_LINE_2
                        : Constants.TemplateConstants.PortfolioB.TAG_DATA_LABEL_ADDRESS_LINE_2;

        String tagLabelAddressLine3 =
                isPortfolioA ? Constants.TemplateConstants.PortfolioA.TAG_DATA_LABEL_ADDRESS_LINE_3
                        : Constants.TemplateConstants.PortfolioB.TAG_DATA_LABEL_ADDRESS_LINE_3;

        String tagLabelFullName =
                isPortfolioA ? Constants.TemplateConstants.PortfolioA.TAG_DATA_LABEL_FULL_NAME
                        : Constants.TemplateConstants.PortfolioB.TAG_DATA_LABEL_FULL_NAME;

        String tagLabelClientNumber =
                isPortfolioA ? Constants.TemplateConstants.PortfolioA.TAG_DATA_LABEL_CLIENT_NUMBER
                        : Constants.TemplateConstants.PortfolioB.TAG_DATA_LABEL_CLIENT_NUMBER;

        String tagLabelInvestmentAmount =
                isPortfolioA ? Constants.TemplateConstants.PortfolioA.TAG_DATA_LABEL_INVESTMENT_AMOUNT
                        : Constants.TemplateConstants.PortfolioB.TAG_DATA_LABEL_INVESTMENT_AMOUNT;

        String tagLabelApplicantName =
                isPortfolioA ? Constants.TemplateConstants.PortfolioA.TAG_DATA_LABEL_APPLICANT_NAME
                        : Constants.TemplateConstants.PortfolioB.TAG_DATA_LABEL_APPLICANT_NAME;

        tabValueDefaults.put(tagLabelAddressLine1, client.getAddressLine1()); // Address line 1
        tabValueDefaults.put(tagLabelAddressLine2, client.getAddressLine2()); // Address line 2
        tabValueDefaults.put(tagLabelAddressLine3, client.getAddressLine3());  // Address line 3
        tabValueDefaults.put(tagLabelFullName, client.getName()); // Full name
        tabValueDefaults.put(tagLabelClientNumber, client.getId()); // Client number
        tabValueDefaults.put(tagLabelInvestmentAmount, client.getInvestmentAmount()); // Investment amount
        tabValueDefaults.put(tagLabelApplicantName, client.getName()); // Name of applicant

        List<DSTextCustomField> textCustomFields = getTextCustomFields(client);
        List<DSListCustomField> listCustomFields = new ArrayList<>();
        DSCustomFields customFields = new DSCustomFields(listCustomFields, textCustomFields);

        // DS: Prefill Envelope Defaults
        return new DSEnvelopeDefaults(
                recipientDefaults,
                templateName,
                emailBlurb,
                envelopeTitle,
                tabValueDefaults,
                customFields
        );
    }

    private static List<Text> getEnvelopeDefinitionTextTabs(@NonNull Client client) {
        List<Text> tabs = new ArrayList<>();

        Text addressLine1 = new Text();
        addressLine1.setDocumentId("3");
        addressLine1.setRecipientId("1");
        addressLine1.setPageNumber("1");
        addressLine1.setXPosition("370");
        addressLine1.setYPosition("110");
        addressLine1.setValue(client.getAddressLine1());

        tabs.add(addressLine1);

        Text addressLine2 = new Text();
        addressLine2.setDocumentId("3");
        addressLine2.setRecipientId("1");
        addressLine2.setPageNumber("1");
        addressLine2.setXPosition("370");
        addressLine2.setYPosition("140");
        addressLine2.setValue(client.getAddressLine2());

        tabs.add(addressLine2);

        Text addressLine3 = new Text();
        addressLine3.setDocumentId("3");
        addressLine3.setRecipientId("1");
        addressLine3.setPageNumber("1");
        addressLine3.setXPosition("370");
        addressLine3.setYPosition("165");
        addressLine3.setValue(client.getAddressLine3());

        tabs.add(addressLine3);

        Text name = new Text();
        name.setDocumentId("3");
        name.setRecipientId("1");
        name.setPageNumber("1");
        name.setXPosition("50");
        name.setYPosition("165");
        name.setValue(client.getName());

        tabs.add(name);

        Text clientId = new Text();
        clientId.setDocumentId("3");
        clientId.setRecipientId("1");
        clientId.setPageNumber("1");
        clientId.setXPosition("370");
        clientId.setYPosition("550");
        clientId.setValue(client.getId());

        tabs.add(clientId);

        Text investmentAmount = new Text();
        investmentAmount.setDocumentId("3");
        investmentAmount.setRecipientId("1");
        investmentAmount.setPageNumber("1");
        investmentAmount.setXPosition("370");
        investmentAmount.setYPosition("630");
        investmentAmount.setValue(client.getInvestmentAmount());

        tabs.add(investmentAmount);

        Text signatureName = new Text();
        signatureName.setDocumentId("3");
        signatureName.setRecipientId("1");
        signatureName.setPageNumber("1");
        signatureName.setXPosition("50");
        signatureName.setYPosition("730");
        signatureName.setValue(client.getName());

        tabs.add(signatureName);

        return tabs;
    }
}
