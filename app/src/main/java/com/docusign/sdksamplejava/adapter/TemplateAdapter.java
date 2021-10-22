package com.docusign.sdksamplejava.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.docusign.androidsdk.dsmodels.DSTemplate;
import com.docusign.sdksamplejava.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    @NonNull
    private final TemplateClickListener templateClickListener;

    @NonNull
    private final List<DSTemplate> templates = new ArrayList<>();

    @NonNull
    private final Map<String, Boolean> cacheTemplateMap = new HashMap<>();

    public TemplateAdapter(@NonNull TemplateClickListener templateClickListener) {
        this.templateClickListener = templateClickListener;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TemplateViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        holder.bind(templates.get(position), position);
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public void addItems(@NonNull List<DSTemplate> templateList) {
        templates.addAll(templateList);
        notifyDataSetChanged();
    }

    public void updateItem(int position, @NonNull String templateId, boolean cached) {
        cacheTemplateMap.put(templateId, cached);
        notifyItemChanged(position);
    }

    public interface TemplateClickListener {
        void downloadTemplate(@NonNull String templateId, int position);

        void retrieveDownloadedTemplate(@NonNull String templateId, int position);

        void removeDownloadedTemplate(@NonNull String templateId, int position);

        void templateSelected(@NonNull String templateId, @Nullable String templateName);
    }

    class TemplateViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView templateNameTextView;

        @NonNull
        private final Button templateDownloadButton;

        private boolean deleteTemplate;

        public TemplateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_template, parent, false));

            templateNameTextView = itemView.findViewById(R.id.template_name_text_view);
            templateDownloadButton = itemView.findViewById(R.id.template_download_button);
        }

        public void bind(@NonNull DSTemplate template, int position) {
            templateNameTextView.setText(template.getTemplateName());

            itemView.setOnClickListener(view -> {
                deleteTemplate = false;
                if (cacheTemplateMap.get(template.getTemplateId()) != null && cacheTemplateMap.get(template.getTemplateId()) == true) {
                    templateDownloadButton.setBackgroundResource(R.drawable.download_done);
                } else {
                    templateDownloadButton.setBackgroundResource(R.drawable.download);
                }
                templateClickListener.templateSelected(template.getTemplateId(), template.getTemplateName());
            });

            if (cacheTemplateMap.get(template.getTemplateId()) != null && cacheTemplateMap.get(template.getTemplateId()) == true) {
                templateDownloadButton.setBackgroundResource(R.drawable.download_done);
            } else {
                templateDownloadButton.setBackgroundResource(R.drawable.download);
                templateClickListener.retrieveDownloadedTemplate(template.getTemplateId(), position);
            }

            templateDownloadButton.setOnClickListener(view -> {
                if (cacheTemplateMap.get(template.getTemplateId()) == null || (cacheTemplateMap.get(template.getTemplateId()) != null && cacheTemplateMap.get(template.getTemplateId()) != true)) {
                    deleteTemplate = false;
                    templateClickListener.downloadTemplate(template.getTemplateId(), position);
                } else {
                    if (deleteTemplate) {
                        templateClickListener.removeDownloadedTemplate(template.getTemplateId(), position);
                        deleteTemplate = false;
                    } else {
                        templateDownloadButton.setBackgroundResource(R.drawable.ic_delete);
                        deleteTemplate = true;
                    }
                }
            });
        }
    }
}
