package com.skillbuilder.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skillbuilder.app.R;
import com.skillbuilder.app.adapters.SubstepAdapter;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.models.Substep;
import com.skillbuilder.app.storage.SkillDatabaseHelper;

import java.util.List;

public class SkillDetailActivity extends AppCompatActivity {

    private TextView textProgress;
    private ProgressBar progressBar;
    private SubstepAdapter substepAdapter;
    private Skill currentSkill;  // Hold current skill
    private SkillDatabaseHelper dbHelper; // DB helper
    private boolean hasUnsavedChanges;
    int currentItemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_detail);

        hasUnsavedChanges =  false;

        TextView textSkillName = findViewById(R.id.textSkillName);
        TextView textCreatedAt = findViewById(R.id.textCreatedAt);
        textProgress = findViewById(R.id.textProgress);
        RecyclerView recyclerViewSteps = findViewById(R.id.recyclerViewSteps);
        FloatingActionButton btnAddStep = findViewById(R.id.fabAddStep);
        progressBar = findViewById(R.id.progressBar);
        Button btnUpdate = findViewById(R.id.update_substep_button);

        btnUpdate.setOnClickListener(v -> this.updateProgressFromSubsteps());


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (hasUnsavedChanges) {
                            new android.app.AlertDialog.Builder(SkillDetailActivity.this)
                                    .setTitle("Discard Changes?")
                                    .setMessage("Changes are not saved. Are you sure you want to go back?")
                                    .setPositiveButton("Yes", (dialog, which) -> finish())
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }else {
                            finish();
                        }
                    }
                });

        dbHelper = SkillDatabaseHelper.getInstance(this);
        currentSkill = (Skill) getIntent().getSerializableExtra("skill_data");
//        currentItemPosition = parseInt(Objects.requireNonNull(getIntent().getStringExtra("skill_position")));
        currentItemPosition = getIntent().getIntExtra("skill_position",-1);

        if (currentSkill != null) {

            List<Substep> stepList = dbHelper.getSubstepsForSkills(currentSkill.getId());
            String outputForProgress;

            textSkillName.setText(currentSkill.getNameOfSkill());
            String ouputForCreatedAt = "Created at " + currentSkill.getCreatedAt();
            textCreatedAt.setText(ouputForCreatedAt);
            outputForProgress = "Progress: " + currentSkill.getProgress() + " %";
            if (stepList.isEmpty()){outputForProgress = "Progress : 0%";}
            textProgress.setText(outputForProgress);

            progressCalculation(stepList);

            recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));
            substepAdapter = new SubstepAdapter(stepList);
            recyclerViewSteps.setAdapter(substepAdapter);
            substepAdapter.setOnSubstepChangedListener(this::updateProgressForUIOnly);
        }
        btnAddStep.setOnClickListener(v -> showAddStepDialog());
    }

    private void updateProgressForUIOnly(){
        List<Substep> substepList = substepAdapter.getUpdatedSubstepsList();
        hasUnsavedChanges = true;
        progressCalculation(substepList);
    }

    private void updateProgressFromSubsteps() {
        List<Substep> updatedSubstepsList = substepAdapter.getUpdatedSubstepsList();

        if (updatedSubstepsList.isEmpty()){
            finish();
        }

        for (Substep s : updatedSubstepsList){
            if(s.isNew() && !s.isDeleted()){
                dbHelper.insertSubstepWithId(s);
            } else if (!s.isNew() && s.isDeleted()){
                dbHelper.deleteSubstep(s.getId());
            } else if (!s.isNew() && !s.isDeleted()) {
                // For updating the checkbox Values.
                dbHelper.updateSubstepCompletion(s.getId(), s.isCompleted());
            }
            s.setNew(false);
            s.setDeleted(false);
        }

        int progressPercent;
        progressPercent = progressCalculation(updatedSubstepsList);

        dbHelper.updateSkillProgress(currentSkill.getId(), progressPercent);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_skill_position",currentItemPosition);
        resultIntent.putExtra("updated_skill_progress",currentSkill.getProgress());
        setResult(Activity.RESULT_OK,resultIntent);
        finish();

    }

    private void showAddStepDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add Step")
                .setMessage("Describe the Step:")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String description = input.getText().toString().trim();
                    if (!description.isEmpty()) {
                        Substep newSubstep = new Substep();
                        newSubstep.setSkillId(currentSkill.getId()); // ✅ Link to correct skill
                        newSubstep.setDescription(description);
                        newSubstep.setCompleted(false);
                        newSubstep.setNew(true);

                        substepAdapter.addSubstep(newSubstep); // Add to adapter
                        updateProgressForUIOnly();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private int progressCalculation(List<Substep> stepList){
        double progressPerSubstep;
        int noOfDoneSteps = 0;

        int total = stepList.size();
        if (total == 0) progressPerSubstep = 0;
        else  progressPerSubstep = (float) 100 /total;


        for (Substep s : stepList){
            s.setProgress( (int) progressPerSubstep);
            if(s.isCompleted()) {
                noOfDoneSteps++;
            }
        }
        float rawProgress = (noOfDoneSteps * 100f) / total;
        int progressPercent;
        // Round down if not fully complete (optional safeguard)
        if(total != 0) {
            progressPercent = (noOfDoneSteps == total) ? 100 : (int) Math.floor(rawProgress); // truncate decimal (e.g., 99.99 -> 99)
        } else {
            progressPercent = 0;
        }
        String placeholderForProgress = "Progress: " + progressPercent + "%";
        textProgress.setText(placeholderForProgress);
        currentSkill.setProgress(progressPercent);
        progressBar.setProgress(progressPercent);
        return progressPercent;
    }

}
