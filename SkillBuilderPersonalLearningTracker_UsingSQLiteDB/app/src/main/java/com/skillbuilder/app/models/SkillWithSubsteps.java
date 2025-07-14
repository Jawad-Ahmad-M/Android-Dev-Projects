package com.skillbuilder.app.models;

import java.util.List;

public class SkillWithSubsteps {
    Skill skill;
    List<Substep> substeps;


    public SkillWithSubsteps(Skill skill, List<Substep> substeps) {
        this.skill = skill;
        this.substeps = substeps;
    }

    private int getCompletedSteps() {
        int count = 0;
        for (int i = 0; i < substeps.size(); i++){
            if(substeps.get(i).isCompleted()) count++;
        }
        return count;
    }

    private int getTotalSteps(){
        return substeps.size();
    }

    public int getProgressPercent() {
        if (substeps.isEmpty()) return 0;
        return (getCompletedSteps() * 100) / getTotalSteps();
    }

    public Skill getSkill() {
        return skill;
    }

    public List<Substep> getSubsteps() {
        return substeps;
    }
}













