package com.antaresnav.navigation.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antaresnav.navigation.demo.StyleUrlListAdapter.StyleUrlViewHolder;

import java.util.List;

public class StyleUrlListAdapter extends RecyclerView.Adapter<StyleUrlViewHolder> {

    private final LayoutInflater inflater;
    private final StyleUrlViewModel styleUrlViewModel;
    private List<StyleUrl> styleUrls; // Cached copy of styleUrls
    private OnItemClickListener onItemClickListener;

    StyleUrlListAdapter(Context context, StyleUrlViewModel styleUrlViewModel) {
        this.inflater = LayoutInflater.from(context);
        this.styleUrlViewModel = styleUrlViewModel;
    }

    @NonNull
    @Override
    public StyleUrlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new StyleUrlViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StyleUrlViewHolder holder, int position) {
        if (styleUrls != null) {
            final StyleUrl current = styleUrls.get(position);
            holder.nameTV.setText(current.getName());
            holder.urlTV.setText(current.getUrl());
            holder.styleUrl = current;
        }
    }

    @Override
    public int getItemCount() {
        if (styleUrls != null) {
            return styleUrls.size();
        } else {
            return 0;
        }
    }

    void setStyleUrls(List<StyleUrl> styleUrls) {
        this.styleUrls = styleUrls;
        notifyDataSetChanged();
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position, StyleUrl styleUrl);
    }

    class StyleUrlViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTV;
        private final TextView urlTV;
        private final ImageView deleteB;
        private StyleUrl styleUrl;

        private StyleUrlViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            urlTV = itemView.findViewById(R.id.urlTV);
            deleteB = itemView.findViewById(R.id.deleteB);
            deleteB.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    styleUrlViewModel.delete(styleUrl);
                }
            });
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getAdapterPosition(), styleUrl);
                    }
                }
            });
        }
    }
}
