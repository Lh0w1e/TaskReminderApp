package com.app.taskreminderapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.taskreminderapp.Models.ArchiveModel;
import com.app.taskreminderapp.R;
import com.app.taskreminderapp.Utils.ArchiveViewHolder;
import com.app.taskreminderapp.Utils.OnTapListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Colinares on 11/9/2017.
 */
public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveViewHolder> {

    List<ArchiveModel> archiveModels = Collections.emptyList();
    private OnTapListener onTapListener;

    public ArchiveAdapter(List<ArchiveModel> archiveModels) {
        this.archiveModels = archiveModels;
    }

    public void refreshList(ArrayList<ArchiveModel> archiveModelArrayList ){
        this.archiveModels.clear();
        this.archiveModels.addAll(archiveModelArrayList);
        notifyDataSetChanged();
    }

    @Override
    public ArchiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_archive_list, parent, false);

        return new ArchiveViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ArchiveViewHolder holder, final int position) {

        holder.reminder_name.setText(archiveModels.get(position).getTitle());
        holder.reminder_date.setText(archiveModels.get(position).getDate_created());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onTapListener != null){
                    onTapListener.onTapView(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return archiveModels.size();
    }

    public void setOnTapListener(OnTapListener onTapListener){
        this.onTapListener = onTapListener;
    }

}
