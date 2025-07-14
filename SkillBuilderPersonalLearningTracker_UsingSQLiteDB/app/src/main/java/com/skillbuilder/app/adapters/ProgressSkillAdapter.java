package com.skillbuilder.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillbuilder.app.R;
import com.skillbuilder.app.models.SkillWithSubsteps;
import com.skillbuilder.app.custom.MiniPieChartView;

import java.util.List;

public class ProgressSkillAdapter extends RecyclerView.Adapter<ProgressSkillAdapter.SkillProgressViewHolder> {

    private final List<SkillWithSubsteps> skillList;

    public ProgressSkillAdapter(List<SkillWithSubsteps> skillList) {
        this.skillList = skillList;
    }

    @NonNull
    @Override
    public SkillProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress_skill, parent, false);
        return new SkillProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillProgressViewHolder holder, int position) {
        SkillWithSubsteps skillData = skillList.get(position);

        holder.skillName.setText(skillData.getSkill().getNameOfSkill());

        int total = skillData.getSubsteps().size();
        int completed = 0;
        for (var sub : skillData.getSubsteps()) {
            if (sub.isCompleted()) completed++;
        }

        float progress = (total == 0) ? 0f : ((completed * 100f) / total);
        String stepInfoDesc = completed + " / " + total + " Steps Done";
        holder.stepsInfo.setText(stepInfoDesc);

        holder.pieChart.setProgress(progress);
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

//    public void updateList(List<SkillWithSubsteps> newList) {
//        this.skillList = newList;
//        notifyDataSetChanged();
//    }

    static class SkillProgressViewHolder extends RecyclerView.ViewHolder {
        TextView skillName, stepsInfo;
        MiniPieChartView pieChart;

        public SkillProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            skillName = itemView.findViewById(R.id.textSkillName);
            stepsInfo = itemView.findViewById(R.id.textStepsDone);
            pieChart = itemView.findViewById(R.id.miniPieChart);
        }
    }
}
