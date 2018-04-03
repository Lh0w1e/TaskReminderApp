package com.app.taskreminderapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.taskreminderapp.Models.TrashModel;
import com.app.taskreminderapp.R;
import com.app.taskreminderapp.Utils.OnTapListener;
import com.app.taskreminderapp.Utils.TrashViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Colinares on 11/9/2017.
 */
public class TrashAdapter extends RecyclerView.Adapter<TrashViewHolder> {

    List<TrashModel> trashModels = Collections.emptyList();
    private OnTapListener onTapListener;

    public TrashAdapter(List<TrashModel> archiveModels) {
        this.trashModels = archiveModels;
    }

    public void refreshList(ArrayList<TrashModel> archiveModelArrayList ){
        this.trashModels.clear();
        this.trashModels.addAll(archiveModelArrayList);
        notifyDataSetChanged();
    }

    @Override
    public TrashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_trash_list, parent, false);

        return new TrashViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(TrashViewHolder holder, final int position) {

        holder.reminder_name.setText(trashModels.get(position).getTitle());
        holder.reminder_date.setText(trashModels.get(position).getDate_created());

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
        return trashModels.size();
    }

    public void setOnTapListener(OnTapListener onTapListener){
        this.onTapListener = onTapListener;
    }

}
