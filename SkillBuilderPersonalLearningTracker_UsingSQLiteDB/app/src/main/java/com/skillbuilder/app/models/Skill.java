package com.skillbuilder.app.models;

import android.content.Context;

import com.skillbuilder.app.storage.SkillDatabaseHelper;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public class Skill implements Serializable {
    private int id;
    private String nameOfSkill;
    private String createdAt;
    private int progress;
    public List<Substep> getSubsteps(Context context) {
        try (SkillDatabaseHelper dbHelper = SkillDatabaseHelper.getInstance(context)){
            return dbHelper.getSubstepsBySkillId(id);
        }
    }


    public Skill() {
    }

    public Skill(int id, String nameOfSkill, String createdAt) {
        this.id = id;
        this.nameOfSkill = nameOfSkill;
        this.createdAt = createdAt;
    }

    public Skill(String nameOfSkill, String createdAt) {
        this.nameOfSkill = nameOfSkill;
        this.createdAt = createdAt;
        this.progress = 0;
    }

    public Skill(int id, String nameOfSkill, String createdAt, int progress) {
        this.id = id;
        this.nameOfSkill = nameOfSkill;
        this.createdAt = createdAt;
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfSkill() {
        return nameOfSkill;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
    @NotNull
    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + nameOfSkill + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", progress=" + progress +
                '}';
    }

}
