package com.skillbuilder.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skillbuilder.app.R;
import com.skillbuilder.app.models.Substep;

import java.util.List;

public class SubstepAdapter extends RecyclerView.Adapter<SubstepAdapter.StepViewHolder> {
    private final List<Substep> substepList;

    public SubstepAdapter(List<Substep> stepList) {
        this.substepList = stepList;
    }

    public List<Substep> getUpdatedSubstepsList(){
        return substepList;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subitem_skill, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {

        Substep substep = substepList.get(position);
        holder.stepText.setText(substep.getDescription());

        holder.checkBoxComplete.setOnCheckedChangeListener(null); // prevent unwanted triggers
        holder.checkBoxComplete.setChecked(substep.isCompleted());  // 🔥 Set correct state
        applyStrikethrough(holder.stepText, substep.isCompleted());

        holder.checkBoxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            substep.setCompleted(isChecked);
            applyStrikethrough(holder.stepText, isChecked);
            if (listener != null) listener.onSubstepChanged();
//            Intent intent = new Intent(SubstepAdapter., SkillDetailActivity.class);/
        });


        holder.deleteButton.setOnClickListener(v -> {
            Context context = v.getContext();
            new AlertDialog.Builder(context)
                    .setTitle("Delete Step")
                    .setMessage("Are you sure you want to delete this step?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        substep.setDeleted(true);
//                            dbHelper.deleteSubstep(substep.getId());
                        // Remove from list and update UI
                        int removedPosition = holder.getAdapterPosition();
                        substepList.remove(removedPosition);
                        notifyItemRemoved(removedPosition);
                        if (listener != null) listener.onSubstepChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }


    private void applyStrikethrough(TextView textView, boolean isCompleted) {
        if (isCompleted) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    public interface OnSubstepChangedListener {
        void onSubstepChanged();
    }

    private OnSubstepChangedListener listener;

    public void setOnSubstepChangedListener(OnSubstepChangedListener listener) {
        this.listener = listener;
    }


    @Override
    public int getItemCount() {
        return substepList.size();
    }

    public void addSubstep(Substep step) {
        substepList.add(step);
        notifyItemInserted(substepList.size() - 1);
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView stepText;
        FloatingActionButton deleteButton;
        CheckBox checkBoxComplete;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepText = itemView.findViewById(R.id.textViewSkillDescription);
            deleteButton = itemView.findViewById(R.id.fabDeleteSubstep);
            checkBoxComplete = itemView.findViewById(R.id.checkBoxComplete);

        }
    }

}

