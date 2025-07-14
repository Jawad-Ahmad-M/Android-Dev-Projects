package com.skillbuilder.app.fragments;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skillbuilder.app.R;
import com.skillbuilder.app.adapters.ProgressSkillAdapter;
import com.skillbuilder.app.custom.BarGraphView;
import com.skillbuilder.app.custom.SummaryPieChartView;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.models.Substep;
import com.skillbuilder.app.models.SkillWithSubsteps;
import com.skillbuilder.app.storage.SkillDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private SkillDatabaseHelper dbHelper;
    private final List<SkillWithSubsteps> skillDataList = new ArrayList<>();
    private final List<SkillWithSubsteps> filteredSkillDataList = new ArrayList<>();
    private SummaryPieChartView pieChartView;
    private BarGraphView barGraphView;
    private RecyclerView recyclerView;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_progress, container, false);

        pieChartView = binding.findViewById(R.id.summaryPieChart);
        barGraphView = binding.findViewById(R.id.verticalBarGraph);
        recyclerView = binding.findViewById(R.id.recyclerViewProgress);

        Button allFilter = binding.findViewById(R.id.btnAllSkillsFilter);
        Button completedFilter = binding.findViewById(R.id.btnCompletedSkillsFilter);
        Button inCompletedFilter = binding.findViewById(R.id.btnInCompletedSkillsFilter);

        allFilter.setOnClickListener(v -> updateBarGraph());
        completedFilter.setOnClickListener(v -> setFilter(true));
        inCompletedFilter.setOnClickListener(v -> setFilter(false));

        dbHelper = SkillDatabaseHelper.getInstance(requireContext());
        loadData();
        return binding;
    }

    private void setFilter(boolean filterValue){

        filteredSkillDataList.clear();
        List<Skill> allSkills = dbHelper.getAllSkills();
        for (Skill skill : allSkills) {
            if (filterValue){
                if (skill.getProgress() == 100) {
                    List<Substep> substeps = dbHelper.getSubstepsForSkills(skill.getId());
                    filteredSkillDataList.add(new SkillWithSubsteps(skill, substeps));
                }
            }
            else{
                if (skill.getProgress() < 100){
                    List<Substep> substeps = dbHelper.getSubstepsForSkills(skill.getId());
                    filteredSkillDataList.add(new SkillWithSubsteps(skill,substeps));
                }
                }
        }

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (SkillWithSubsteps s : filteredSkillDataList){
            labels.add(s.getSkill().getNameOfSkill());
            values.add(s.getProgressPercent());
        }
        barGraphView.setSkillData(labels,values);
    }

    private void loadData() {
        skillDataList.clear();
        List<Skill> allSkills = dbHelper.getAllSkills();

        for (Skill skill : allSkills) {
            List<Substep> substeps = dbHelper.getSubstepsForSkills(skill.getId());
            skillDataList.add(new SkillWithSubsteps(skill, substeps));
        }
        updatePieChart();
        updateBarGraph();
        updateRecyclerView();
    }

    private void updatePieChart() {
        int completed = 0, partialAbove50 = 0, partialBelow50 = 0, uncompleted = 0;

        for (SkillWithSubsteps s : skillDataList) {
            int progress = s.getProgressPercent();
            if (progress == 100) completed++;
            else if (progress >= 50) partialAbove50++;
            else if (progress > 0) partialBelow50++;
            else uncompleted++;
        }

        List<Integer> values = List.of(completed, partialAbove50, partialBelow50, uncompleted);
        List<Integer> colors = List.of(
                Color.parseColor("#4CAF50"),   // green
                Color.parseColor("#FFC107"),   // amber
                Color.parseColor("#FF9800"),   // orange
//                Color.parseColor("#F44336")    // red
                Color.LTGRAY
        );
        List<String> legends = List.of("Completed", "Above 50%", "Below 50%", "Unstarted");

        pieChartView.setData(values, colors,legends);

    }

    private void updateBarGraph() {
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (SkillWithSubsteps s : skillDataList) {
            labels.add(s.getSkill().getNameOfSkill());
            values.add(s.getProgressPercent());
        }

        barGraphView.setSkillData(labels, values);
//        barGraphView.setSkillData(new ArrayList<>(),new ArrayList<>());
    }

    private void updateRecyclerView() {
        ProgressSkillAdapter adapter = new ProgressSkillAdapter(skillDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
