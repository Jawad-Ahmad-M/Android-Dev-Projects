package com.skillbuilder.app.models;

import java.util.List;

public class ExportData {
    private final List<Skill> skills;
    private final List<Substep> substeps;

    public ExportData(List<Skill> skills, List<Substep> substeps) {
        this.skills = skills;
        this.substeps = substeps;
    }

    public List<Substep> getSubsteps() {
        return substeps;
    }

    public List<Skill> getSkills() {
        return skills;
    }
}
