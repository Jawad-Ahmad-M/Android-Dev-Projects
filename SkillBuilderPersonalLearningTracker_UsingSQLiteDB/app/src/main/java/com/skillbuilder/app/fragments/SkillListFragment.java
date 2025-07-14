package com.skillbuilder.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.skillbuilder.app.R;
import com.skillbuilder.app.activities.SkillDetailActivity;
import com.skillbuilder.app.adapters.SkillAdapter;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.models.Substep;
import com.skillbuilder.app.storage.SkillDatabaseHelper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SkillListFragment extends Fragment {
    private SkillAdapter adapter;
    private SkillDatabaseHelper dbHelper;
    private List<Skill> skillList;
    private ActivityResultLauncher<Intent> detailLauncher;
    RecyclerView recyclerView;
    FloatingActionButton btnAdditionOfSkill;
//    ProgressBar progressBar;


    public SkillListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skill_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSkillListInteractivity(true);
        recyclerView = view.findViewById(R.id.recyclerViewSkills);
        btnAdditionOfSkill = view.findViewById(R.id.fabAddSkill);
        Button btnLoadSampleData = view.findViewById(R.id.button_load_sample_data_from_assets);


        dbHelper = SkillDatabaseHelper.getInstance(getContext());
        skillList = dbHelper.getAllSkills();
        Log.d("SkillListFragment",""+skillList);

        /*  Sets the visibility of button
            for loading sample data from json  */
        updateVisibilityOfButtonLoadSampleData();

        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
//                    setSkillListInteractivity(true);
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        int position = result.getData().getIntExtra("updated_skill_position", -1);
                        int progress = result.getData().getIntExtra("updated_skill_progress", -1);

                        if (position != -1 && progress != -1) {
                            if (position < skillList.size()) {
                                Skill updatedSkill = skillList.get(position);
                                updatedSkill.setProgress(progress);
                                adapter.notifyItemChanged(position); // Only update that one item
                            }
                        }
                    }
                }
        );

        adapter = new SkillAdapter(skillList,
                (skill, position) -> {
                    setSkillListInteractivity(false);
                    Intent intent = new Intent(getContext(), SkillDetailActivity.class);
                    intent.putExtra("skill_data", skill);
                    intent.putExtra("skill_position", position);
                    detailLauncher.launch(intent);
                },
                (remainingCount) -> {
                    updateVisibilityOfButtonLoadSampleData(); // 👈 still safe since it uses `skillList.size()`
                }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnAdditionOfSkill.setOnClickListener(v -> additionOfSkill());

        btnLoadSampleData.setOnClickListener(v ->loadSampleData());
    }

    private void additionOfSkill(){
        addSkillDialogueFragment dialog = new addSkillDialogueFragment();
        dialog.setOnSkillAddedListener(this::refreshSkillList);
        dialog.show(getChildFragmentManager(), "AddSkillDialog");
    }


    private void loadSkills() {
        List<Skill> skills = dbHelper.getAllSkills();
        adapter.updateList(skills);
    }

    private void loadSampleData() {
        try {
            InputStream is = requireContext().getAssets().open("sample_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            // This assures that the whole file is read without any exception,
            // like not read completely etc.
            int totalRead = 0;
            while (totalRead < size) {
                int bytesRead = is.read(buffer, totalRead, size - totalRead);
                if (bytesRead == -1) break; // EOF reached
                totalRead += bytesRead;
            }
            if (totalRead != size) {
                throw new RuntimeException("Incomplete read of sample_data.json");
            }
            is.close(); // closes the inputStream



            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            SettingsFragment.ImportContainer container = gson.fromJson(json, SettingsFragment.ImportContainer.class);

            for (Skill skill : container.skills) {
                dbHelper.insertSkillWithId(skill);
            }

            for (Substep sub : container.substeps) {
                dbHelper.insertSubstepWithId(sub);
            }

            Toast.makeText(requireContext(), "Sample data loaded!", Toast.LENGTH_SHORT).show();
            loadSkills(); // reload data and update visibility

            Button btnLoadSampleData = requireView().findViewById(R.id.button_load_sample_data_from_assets);
            btnLoadSampleData.setVisibility(View.GONE);

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to load sample: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateVisibilityOfButtonLoadSampleData(){
        Button btnLoadSampleData = requireView().findViewById(R.id.button_load_sample_data_from_assets);
        if (skillList.isEmpty()){
            btnLoadSampleData.setVisibility(View.VISIBLE);
        } else {
            btnLoadSampleData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setSkillListInteractivity(true);
        updateVisibilityOfButtonLoadSampleData();// Set the (Load data button) visible or invisible
    }

    private void setSkillListInteractivity(boolean value){
        if (recyclerView != null){
            recyclerView.setEnabled(value);
        }
        if (btnAdditionOfSkill != null){
            btnAdditionOfSkill.setEnabled(value);
        }
    }

    private void refreshSkillList() {
        skillList = dbHelper.getAllSkills();
        adapter.updateList(skillList);
        updateVisibilityOfButtonLoadSampleData();
    }


}