package com.scriptsbundle.nokri.candidate.edit.models;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by Glixen Technologies on 26/01/2018.
 */

public class Nokri_SkillsModel {
private int id;
private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
