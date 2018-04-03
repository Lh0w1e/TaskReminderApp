package com.app.taskreminderapp.Models;

/**
 * Created by Colinares on 11/7/2017.
 */
public class ArchiveModel {

    private int id;
    private String title,content,selected_time,selected_date,is_archive,is_trashed,date_created;

    public ArchiveModel(int id, String title, String content, String selected_time,
                         String selected_date, String is_archive, String is_trashed,
                         String date_created) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.selected_time = selected_time;
        this.selected_date = selected_date;
        this.is_archive = is_archive;
        this.is_trashed = is_trashed;
        this.date_created = date_created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSelected_time() {
        return selected_time;
    }

    public void setSelected_time(String selected_time) {
        this.selected_time = selected_time;
    }

    public String getSelected_date() {
        return selected_date;
    }

    public void setSelected_date(String selected_date) {
        this.selected_date = selected_date;
    }

    public String getIs_archive() {
        return is_archive;
    }

    public void setIs_archive(String is_archive) {
        this.is_archive = is_archive;
    }

    public String getIs_trashed() {
        return is_trashed;
    }

    public void setIs_trashed(String is_trashed) {
        this.is_trashed = is_trashed;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
