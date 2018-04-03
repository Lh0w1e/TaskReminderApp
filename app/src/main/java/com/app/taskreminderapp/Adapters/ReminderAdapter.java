package com.app.taskreminderapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.taskreminderapp.Models.ReminderModel;
import com.app.taskreminderapp.R;
import com.app.taskreminderapp.Utils.ReminderViewHolder;
import com.app.taskreminderapp.Utils.OnTapListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Colinares on 11/7/2017.
 */
public class ReminderAdapter extends RecyclerView.Adapter<ReminderViewHolder> {

    List<ReminderModel> reminderModels = Collections.emptyList();
    private OnTapListener onTapListener;

    public ReminderAdapter(List<ReminderModel> reminderModels) {
        this.reminderModels = reminderModels;
    }

    public void refreshList(ArrayList<ReminderModel> reminderModelArrayList){
        this.reminderModels.clear();
        this.reminderModels.addAll(reminderModelArrayList);
        notifyDataSetChanged();
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_reminder_list, parent, false);

        return new ReminderViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, final int position) {

        holder.reminder_name.setText(reminderModels.get(position).getTitle());
        holder.reminder_date.setText(reminderModels.get(position).getDate_created());

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
        return reminderModels.size();
    }

    public void setOnTapListener(OnTapListener onTapListener){
        this.onTapListener = onTapListener;
    }


}
