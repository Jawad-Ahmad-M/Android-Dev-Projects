package com.skillbuilder.app.models;

public class Substep {
    private int progress;
    private int id;
    private int skillId;
    private String description;
    private boolean isCompleted;
    private boolean isNew = false;
    private boolean isDeleted = false;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Substep() {}

    public Substep(int id, int skillId, String description, boolean isCompleted) {
        this.id = id;
        this.skillId = skillId;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
