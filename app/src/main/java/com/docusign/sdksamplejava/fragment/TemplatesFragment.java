package com.docusign.sdksamplejava.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.docusign.androidsdk.dsmodels.DSTemplate;
import com.docusign.androidsdk.dsmodels.DSTemplatesFilter;
import com.docusign.sdksamplejava.R;
import com.docusign.sdksamplejava.adapter.TemplateAdapter;
import com.docusign.sdksamplejava.viewmodel.TemplatesViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TemplatesFragment extends Fragment implements TemplateAdapter.TemplateClickListener {

    public static final String TAG = TemplatesFragment.class.getSimpleName();

    public static final int TEMPLATE_COUNT = 50;

    private int startPosition = 0;

    private int availableTemplatesTotalSize = 0;

    private int templatesTotalSize = Integer.MAX_VALUE;

    private boolean isLoadingData = false;

    @Nullable
    private TemplateAdapter adapter;

    @Nullable
    private List<DSTemplate> templates;

    @Nullable
    private RecyclerView recyclerView;

    @Nullable
    private LinearLayoutManager layoutManager;

    @Nullable
    private TemplatesViewModel templatesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_templates, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            recyclerView = activity.findViewById(R.id.templates_recycler_view);
            layoutManager = new LinearLayoutManager(activity);
            Objects.requireNonNull(recyclerView).setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
            templatesViewModel = new TemplatesViewModel();
            templates = new ArrayList<>();
            adapter = new TemplateAdapter(this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            availableTemplatesTotalSize = 0;
            startPosition = 0;
            setRecyclerViewScrollListener();
            DSTemplatesFilter filter = new DSTemplatesFilter(TEMPLATE_COUNT, null, null, startPosition);
            isLoadingData = true;
            initLiveDataObservers();
            templatesViewModel.getTemplates(requireContext(), filter);
        }
    }

    @Override
    public void downloadTemplate(@NonNull String templateId, int position) {
        if (templatesViewModel != null)
            templatesViewModel.cacheTemplate(templateId, position);
    }

    @Override
    public void retrieveDownloadedTemplate(@NonNull String templateId, int position) {
        if (templatesViewModel != null)
            templatesViewModel.retrieveCachedTemplate(templateId, position);
    }

    @Override
    public void removeDownloadedTemplate(@NonNull String templateId, int position) {
        if (templatesViewModel != null)
            templatesViewModel.removeCachedTemplate(templateId, position);
    }

    @Override
    public void templateSelected(@NonNull String templateId, @Nullable String templateName) {
        /* NO-OP */
    }

    private void setRecyclerViewScrollListener() {
        if (recyclerView != null)
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    int lastVisibleItemPosition = 0;
                    if (layoutManager != null)
                        lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (!isLoadingData && lastVisibleItemPosition == availableTemplatesTotalSize - 1) {
                        if (availableTemplatesTotalSize < templatesTotalSize) {
                            startPosition = availableTemplatesTotalSize;
                            DSTemplatesFilter filter = new DSTemplatesFilter(TEMPLATE_COUNT, null, null, startPosition);
                            isLoadingData = true;
                            if (templatesViewModel != null)
                                templatesViewModel.getTemplates(requireContext(), filter);
                        }
                    }
                }
            });
    }

    private void toggleProgressBar(boolean isBusy) {
        Activity activity = getActivity();

        if (activity != null) {
            ProgressBar progressBar = activity.findViewById(R.id.templates_progress_bar);
            progressBar.setVisibility(isBusy ? View.VISIBLE : View.GONE);
        }
    }

    private void initLiveDataObservers() {
        if (templatesViewModel != null) {
            templatesViewModel.getTemplatesLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (model.getDsTemplates() != null) {
                            templatesTotalSize = model.getDsTemplates().getTotalTemplatesSize();
                            availableTemplatesTotalSize += model.getDsTemplates().getResultTemplatesSize();
                            isLoadingData = false;
                            adapter.addItems(model.getDsTemplates().getTemplates());
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        templatesTotalSize = 0;
                        isLoadingData = false;
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            templatesViewModel.getCacheTemplateLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (model.getTemplate() != null) {
                            adapter.updateItem(model.getPosition(), model.getTemplate().getTemplateId(), true);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        templatesTotalSize = 0;
                        isLoadingData = false;
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            templatesViewModel.getRemoveCachedTemplateLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        toggleProgressBar(true);
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (model.getTemplateDefinition() != null) {
                            adapter.updateItem(model.getPosition(), model.getTemplateDefinition().getTemplateId(), false);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        templatesTotalSize = 0;
                        isLoadingData = false;
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });

            templatesViewModel.getRetrieveCachedTemplateLiveData().observe(getViewLifecycleOwner(), model -> {
                switch (model.getStatus()) {
                    case START:
                        /* NO-OP */
                        break;
                    case COMPLETE:
                        toggleProgressBar(false);
                        if (model.getTemplateDefinition() != null) {
                            adapter.updateItem(model.getPosition(), model.getTemplateDefinition().getTemplateId(), true);
                        }
                        break;
                    case ERROR:
                        toggleProgressBar(false);
                        templatesTotalSize = 0;
                        isLoadingData = false;
                        if (model.getException() != null) {
                            Log.d(TAG, model.getException().getMessage());
                            Toast.makeText(requireContext(), model.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                }
            });
        }
    }

    public static TemplatesFragment newInstance() {
        return new TemplatesFragment();
    }
}
