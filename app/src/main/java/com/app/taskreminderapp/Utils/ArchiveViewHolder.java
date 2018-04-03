package com.app.taskreminderapp.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.app.taskreminderapp.R;

/**
 * Created by Colinares on 11/7/2017.
 */
public class ArchiveViewHolder extends RecyclerView.ViewHolder {

    public TextView reminder_name, reminder_date;

    public ArchiveViewHolder(View itemView) {
        super(itemView);

        reminder_name = (TextView) itemView.findViewById(R.id.custom_archive_title);
        reminder_date = (TextView) itemView.findViewById(R.id.custom_archive_date);

    }
}
