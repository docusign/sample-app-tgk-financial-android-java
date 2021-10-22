package com.docusign.sdksamplejava.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSTemplateDelegate;
import com.docusign.androidsdk.dsmodels.DSEnvelopeDefaults;
import com.docusign.androidsdk.dsmodels.DSTemplate;
import com.docusign.androidsdk.dsmodels.DSTemplateDefinition;
import com.docusign.androidsdk.dsmodels.DSTemplates;
import com.docusign.androidsdk.dsmodels.DSTemplatesFilter;
import com.docusign.androidsdk.exceptions.DSSigningException;
import com.docusign.androidsdk.exceptions.DSTemplateException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSCacheTemplateListener;
import com.docusign.androidsdk.listeners.DSGetCachedTemplateListener;
import com.docusign.androidsdk.listeners.DSOfflineUseTemplateListener;
import com.docusign.androidsdk.listeners.DSOnlineUseTemplateListener;
import com.docusign.androidsdk.listeners.DSTemplateListListener;
import com.docusign.androidsdk.listeners.DSTemplateListener;
import com.docusign.sdksamplejava.livedata.CacheTemplateModel;
import com.docusign.sdksamplejava.livedata.GetTemplatesModel;
import com.docusign.sdksamplejava.livedata.RemoveCachedTemplateModel;
import com.docusign.sdksamplejava.livedata.RetrieveCachedTemplateModel;
import com.docusign.sdksamplejava.livedata.Status;
import com.docusign.sdksamplejava.livedata.UseTemplateOfflineModel;
import com.docusign.sdksamplejava.livedata.UseTemplateOnlineModel;
import com.docusign.sdksamplejava.utils.Utils;

public class TemplatesViewModel extends ViewModel {

    private static final String TAG = TemplatesViewModel.class.getSimpleName();

    @NonNull
    private final MutableLiveData<GetTemplatesModel> templatesLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<CacheTemplateModel> cacheTemplateLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<RemoveCachedTemplateModel> removeCachedTemplateLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<RetrieveCachedTemplateModel> retrieveCachedTemplateLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<UseTemplateOnlineModel> useTemplateOnlineLiveData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<UseTemplateOfflineModel> useTemplateOfflineLiveData = new MutableLiveData<>();

    @Nullable
    private DSTemplateDelegate templateDelegate;

    public TemplatesViewModel() {
        try {
            templateDelegate = DocuSign.getInstance().getTemplateDelegate();
        } catch (DocuSignNotInitializedException e) {
            templateDelegate = null;
        }
    }

    public void getTemplates(@NonNull Context context, @NonNull DSTemplatesFilter filter) {
        if (templateDelegate != null) {
            if (Utils.isNetworkAvailable(context)) {
                // DS: Get templates
                templateDelegate.getTemplates(filter, new DSTemplateListListener() {
                    @Override
                    public void onStart() {
                        GetTemplatesModel getTemplatesModel = new GetTemplatesModel(Status.START, null, null);
                        templatesLiveData.setValue(getTemplatesModel);
                    }

                    @Override
                    public void onComplete(@NonNull DSTemplates dsTemplates) {
                        GetTemplatesModel getTemplatesModel = new GetTemplatesModel(Status.COMPLETE, dsTemplates, null);
                        templatesLiveData.setValue(getTemplatesModel);
                    }

                    @Override
                    public void onError(@NonNull DSTemplateException exception) {
                        GetTemplatesModel getTemplatesModel = new GetTemplatesModel(Status.ERROR, null, exception);
                        templatesLiveData.setValue(getTemplatesModel);
                    }
                });
            } else {
                // DS: Retrieve downloaded templates
                templateDelegate.retrieveDownloadedTemplates(new DSTemplateListListener() {
                    @Override
                    public void onStart() {
                        GetTemplatesModel getTemplatesModel = new GetTemplatesModel(Status.START, null, null);
                        templatesLiveData.setValue(getTemplatesModel);
                    }

                    @Override
                    public void onComplete(@NonNull DSTemplates dsTemplates) {
                        GetTemplatesModel getTemplatesModel = new GetTemplatesModel(Status.COMPLETE, dsTemplates, null);
                        templatesLiveData.setValue(getTemplatesModel);
                    }

                    @Override
                    public void onError(@NonNull DSTemplateException exception) {
                        GetTemplatesModel getTemplatesModel = new GetTemplatesModel(Status.ERROR, null, exception);
                        templatesLiveData.setValue(getTemplatesModel);
                    }
                });
            }
        }
    }

    public void cacheTemplate(@NonNull String templateId, int position) {
        // DS: Get template
        if (templateDelegate != null) {
            templateDelegate.getTemplate(templateId, null, new DSTemplateListener() {

                @Override
                public void onComplete(@NonNull DSTemplateDefinition template) {

                    if (template.getCacheable().isCacheable()) {
                        // DS: Cache template
                        templateDelegate.cacheTemplate(
                                template.getTemplateId(),
                                new DSCacheTemplateListener() {
                                    @Override
                                    public void onStart() {
                                        CacheTemplateModel cacheTemplateModel = new
                                                CacheTemplateModel(Status.START, null, position, null);
                                        cacheTemplateLiveData.setValue(cacheTemplateModel);
                                    }

                                    @Override
                                    public void onComplete(@NonNull DSTemplate template) {
                                        CacheTemplateModel cacheTemplateModel = new
                                                CacheTemplateModel(Status.COMPLETE, template, position, null);
                                        cacheTemplateLiveData.setValue(cacheTemplateModel);
                                    }

                                    @Override
                                    public void onError(@NonNull DSTemplateException exception) {
                                        CacheTemplateModel cacheTemplateModel = new
                                                CacheTemplateModel(Status.ERROR, null, position, exception);
                                        cacheTemplateLiveData.setValue(cacheTemplateModel);
                                    }
                                });
                    } else {
                        DSTemplateException exception = new DSTemplateException("Template not cacheable");
                        CacheTemplateModel cacheTemplateModel = new
                                CacheTemplateModel(Status.ERROR, null, position, exception);
                        cacheTemplateLiveData.setValue(cacheTemplateModel);
                    }
                }

                @Override
                public void onError(@NonNull DSTemplateException exception) {
                    CacheTemplateModel cacheTemplateModel = new CacheTemplateModel(Status.ERROR, null, position, exception);
                    cacheTemplateLiveData.setValue(cacheTemplateModel);
                }
            });
        }
    }

    public void removeCachedTemplate(@NonNull String templateId, int position) {
        // DS: Retrieve cached template
        if (templateDelegate != null) {
            templateDelegate.retrieveCachedTemplate(templateId, new DSGetCachedTemplateListener() {

                @Override
                public void onComplete(@Nullable DSTemplateDefinition template) {
                    if (template != null) {
                        // DS: Remove cached template
                        templateDelegate.removeCachedTemplate(
                                template,
                                isRemoved -> {
                                    if (isRemoved) {
                                        RemoveCachedTemplateModel removeCachedTemplateModel = new RemoveCachedTemplateModel(
                                                Status.COMPLETE,
                                                template,
                                                position,
                                                null
                                        );
                                        removeCachedTemplateLiveData.setValue(removeCachedTemplateModel);
                                    } else {
                                        DSTemplateException exception = new DSTemplateException("Template not removed");
                                        RemoveCachedTemplateModel removeCachedTemplateModel = new RemoveCachedTemplateModel(
                                                Status.ERROR,
                                                null,
                                                position,
                                                exception
                                        );
                                        removeCachedTemplateLiveData.setValue(removeCachedTemplateModel);
                                    }
                                });
                    }
                }

                @Override
                public void onError(@NonNull DSTemplateException exception) {
                    RemoveCachedTemplateModel removeCachedTemplateModel = new
                            RemoveCachedTemplateModel(Status.ERROR, null, position, exception);
                    removeCachedTemplateLiveData.setValue(removeCachedTemplateModel);
                }
            });
        }
    }

    public void retrieveCachedTemplate(@NonNull String templateId, int position) {
        // DS: Retrieve cached template
        if (templateDelegate != null) {
            templateDelegate.retrieveCachedTemplate(templateId, new DSGetCachedTemplateListener() {
                @Override
                public void onComplete(@Nullable DSTemplateDefinition template) {
                    if (template != null) {
                        RetrieveCachedTemplateModel retrieveCachedTemplateModel = new
                                RetrieveCachedTemplateModel(Status.COMPLETE, template, position, null);
                        retrieveCachedTemplateLiveData.setValue(retrieveCachedTemplateModel);
                    }
                }

                @Override
                public void onError(@NonNull DSTemplateException exception) {
                    RetrieveCachedTemplateModel retrieveCachedTemplateModel = new
                            RetrieveCachedTemplateModel(Status.ERROR, null, position, exception);
                    retrieveCachedTemplateLiveData.setValue(retrieveCachedTemplateModel);
                }

            });
        }
    }

    public void useTemplateOnline(
            @NonNull Context context,
            @NonNull String templateId,
            @Nullable DSEnvelopeDefaults envelopeDefaults
    ) {
        // DS: Online signing using template
        try {
            if (templateDelegate != null) {
                templateDelegate.useTemplateOnline(
                        context,
                        templateId,
                        envelopeDefaults,
                        new DSOnlineUseTemplateListener() {

                            @Override
                            public void onStart(@NonNull String envelopeId) {
                                UseTemplateOnlineModel useTemplateOnlineModel = new
                                        UseTemplateOnlineModel(Status.START, envelopeId, null);
                                useTemplateOnlineLiveData.setValue(useTemplateOnlineModel);
                            }

                            @Override
                            public void onCancel(@NonNull String envelopeId, @NonNull String recipientId) {
                                /* NO-OP */
                            }

                            @Override
                            public void onComplete(@NonNull String envelopeId, boolean onlySent) {
                                UseTemplateOnlineModel useTemplateOnlineModel = new
                                        UseTemplateOnlineModel(Status.COMPLETE, envelopeId, null);
                                useTemplateOnlineLiveData.setValue(useTemplateOnlineModel);
                            }

                            @Override
                            public void onError(@Nullable String envelopeId, @NonNull DSTemplateException exception) {
                                UseTemplateOnlineModel useTemplateOnlineModel = new
                                        UseTemplateOnlineModel(Status.ERROR, envelopeId, exception);
                                useTemplateOnlineLiveData.setValue(useTemplateOnlineModel);
                            }

                            @Override
                            public void onRecipientSigningError(
                                    @NonNull String envelopeId,
                                    @NonNull String recipientId,
                                    @NonNull DSTemplateException exception
                            ) {
                                /* NO-OP */
                            }

                            @Override
                            public void onRecipientSigningSuccess(@NonNull String envelopeId, @NonNull String recipientId) {
                                /* NO-OP */
                            }
                        });
            }
        } catch (DSSigningException e) {
            Log.d(TAG, "Sign in failed");
        }
    }

    public void useTemplateOffline(
            @NonNull Context context,
            @NonNull String templateId,
            @Nullable DSEnvelopeDefaults envelopeDefaults
    ) {
        // DS: Offline signing using template
        try {
            if (templateDelegate != null) {
                templateDelegate.useTemplateOffline(
                        context,
                        templateId,
                        envelopeDefaults,
                        new DSOfflineUseTemplateListener() {
                            @Override
                            public void onCancel(@NonNull String templateId, @Nullable String envelopeId) {
                                /* NO-OP */
                            }

                            @Override
                            public void onComplete(@NonNull String envelopeId) {
                                UseTemplateOfflineModel useTemplateOfflineModel = new
                                        UseTemplateOfflineModel(Status.COMPLETE, envelopeId, null);
                                useTemplateOfflineLiveData.setValue(useTemplateOfflineModel);
                            }

                            @Override
                            public void onError(@NonNull DSTemplateException exception) {
                                UseTemplateOfflineModel useTemplateOfflineModel = new
                                        UseTemplateOfflineModel(Status.ERROR, null, exception);
                                useTemplateOfflineLiveData.setValue(useTemplateOfflineModel);
                            }
                        });
            }
        } catch (DSSigningException e) {
            Log.d(TAG, "Sign in failed");
        }
    }

    @NonNull
    public MutableLiveData<GetTemplatesModel> getTemplatesLiveData() {
        return templatesLiveData;
    }

    @NonNull
    public MutableLiveData<CacheTemplateModel> getCacheTemplateLiveData() {
        return cacheTemplateLiveData;
    }

    @NonNull
    public MutableLiveData<RemoveCachedTemplateModel> getRemoveCachedTemplateLiveData() {
        return removeCachedTemplateLiveData;
    }

    @NonNull
    public MutableLiveData<RetrieveCachedTemplateModel> getRetrieveCachedTemplateLiveData() {
        return retrieveCachedTemplateLiveData;
    }

    @NonNull
    public MutableLiveData<UseTemplateOnlineModel> getUseTemplateOnlineLiveData() {
        return useTemplateOnlineLiveData;
    }

    @NonNull
    public MutableLiveData<UseTemplateOfflineModel> getUseTemplateOfflineLiveData() {
        return useTemplateOfflineLiveData;
    }
}
