package com.skillbuilder.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skillbuilder.app.R;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.storage.SkillDatabaseHelper;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    private List<Skill> skillList;
    private final OnSkillClickListener listener;
    private final OnSkillDeletedListener deleteListener;

    public interface OnSkillClickListener {
        void onSkillClick(Skill skill,int position);
    }

    public SkillAdapter(List<Skill> skillList, OnSkillClickListener listener, OnSkillDeletedListener deleteListener) {
        this.skillList = skillList;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    public interface OnSkillDeletedListener {
        void onSkillDeleted(int remainingCount);
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_skill, parent, false);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        Skill skill = skillList.get(position);
        holder.skillNameText.setText(skill.getNameOfSkill());
        String progressText = "Progress : " + skill.getProgress() + " %";
        holder.skillProgressText.setText(progressText);
        holder.progressBar.setProgress(skill.getProgress());

        // ✅ Click to open details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSkillClick(skillList.get(position),position); // skillList.get(position) is same as skill object
            }
        });

        // Delete logic
        holder.deleteButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            SkillDatabaseHelper dbHelper = SkillDatabaseHelper.getInstance(context);
            showConfirmationDeleteDialog(position, skill, context, dbHelper);
        });
    }

    private void showConfirmationDeleteDialog(int position, Skill skill, Context context, SkillDatabaseHelper dbHelper){
        new AlertDialog.Builder(context)
                .setTitle("Delete Skill")
                .setMessage("Are you sure you want to remove \"" + skill.getNameOfSkill() + "\"?")
                .setPositiveButton("Yes",(dialog, which) -> {
                    dbHelper.deleteSkill(skill.getId());
                    skillList.remove(position);
                    notifyItemRemoved(position);

                    if (deleteListener != null) {
                        deleteListener.onSkillDeleted(skillList.size()); // 👈 Pass updated count
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

    public void updateList(List<Skill> newSkills) {
        this.skillList = newSkills;
    }

    public static class SkillViewHolder extends RecyclerView.ViewHolder {
        TextView skillNameText;
        TextView skillProgressText;
        FloatingActionButton deleteButton;
        ProgressBar progressBar;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            skillNameText = itemView.findViewById(R.id.textViewSkillName);
            skillProgressText = itemView.findViewById(R.id.textViewSkillProgress);
            deleteButton = itemView.findViewById(R.id.fabDeleteSkill);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
