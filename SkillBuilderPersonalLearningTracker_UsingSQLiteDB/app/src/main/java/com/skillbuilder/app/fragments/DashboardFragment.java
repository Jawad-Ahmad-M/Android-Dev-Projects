package com.skillbuilder.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skillbuilder.app.R;
import com.skillbuilder.app.custom.MiniPieChartView;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.models.Substep;
import com.skillbuilder.app.storage.SkillDatabaseHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DashboardFragment extends Fragment {


    TextView tvSkillNo1;
    TextView tvSkillNo2;
    TextView tvSkillNo3;
    Skill skillNo1 = null;
    Skill skillNo2 = null;
    Skill skillNo3 = null;


    private static final List<String> QUOTES_0_9 = Arrays.asList(
            "Every journey starts now.",
            "Begin before you’re ready.",
            "Start small. Dream big.",
            "One click can change everything.",
            "Your potential is waiting."
    );
    private static final List<String> QUOTES_10_39 = Arrays.asList(
            "You’ve started — now build.",
            "Keep the spark alive.",
            "Learning begins with doing.",
            "Small wins shape big skills.",
            "You're on your way.",
            "Progress beats perfection.",
            "The engine has started — keep driving.",
            "You’ve planted the seed. Keep watering.",
            "Discipline builds progress.",
            "Every step makes you stronger.",
            "Keep showing up.",
            "You’re building momentum — don’t stop now.",
            "One sub-step at a time.",
            "Tiny actions. Big outcomes.",
            "You're becoming consistent — that’s rare."
    );

    private static final List<String> QUOTES_40_69 = Arrays.asList(
            "Halfway is a promise to finish.",
            "Momentum is your power.",
            "Keep going. You’re building strength.",
            "You’ve crossed the toughest part.",
            "Almost isn’t far — keep pushing.",
            "The middle matters the most.",
            "You're not stuck — you're stabilizing.",
            "Halfway means you’ve got rhythm.",
            "Keep building. The skill is forming.",
            "Now’s the time to double down.",
            "You’ve passed the dip — finish strong.",
            "Every rep counts.",
            "Consistency creates breakthroughs.",
            "You're shaping mastery.",
            "Stay with it. The finish line is catching up."
    );

    private static final List<String> QUOTES_70_89 = Arrays.asList(
            "You’re closer than ever.",
            "Finish what you started.",
            "This is your final climb.",
            "Completion is in sight.",
            "Wrap it up like a champion."
    );

    private static final List<String> QUOTES_90_100 = Arrays.asList(
            "Skill unlocked. Mastery earned.",
            "You made it — brilliantly.",
            "Mastery is momentum sustained.",
            "You’ve done the hard work.",
            "This is excellence in action."
    );

    TextView motivationalQuote;

    public DashboardFragment() {
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
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String DEBUG_TAG = "Dashboard Debugging";

        //    private SummaryPieChartView summaryPieChartView;
        TextView totalSkills = view.findViewById(R.id.tvTotalSkillsValue);
        TextView totalCompletedSkills = view.findViewById(R.id.tvCompletedSkillsValue);
        TextView totalCompletedSubsteps = view.findViewById(R.id.tvSubstepsValue); // format: comp / total
        TextView averageProgress = view.findViewById(R.id.tvAvgProgressValue);
        motivationalQuote = view.findViewById(R.id.tvMotivationalQuote);

        tvSkillNo1 = view.findViewById(R.id.tv_skill_no_1);
        tvSkillNo2 = view.findViewById(R.id.tv_skill_no_2);
        tvSkillNo3 = view.findViewById(R.id.tv_skill_no_3);



        List<Skill> skillList;
        try (SkillDatabaseHelper dbHelper = SkillDatabaseHelper.getInstance(view.getContext())) {
            skillList = dbHelper.getAllSkills();
        }


        int noOfTotalSkills = skillList.size();
        int noOfTotalSubsteps = 0;
        int noOfCompletedSkills = 0;
        int noOfCompletedSubsteps = 0;

        // Written By Me But not completely Efficient

//        List<Skill> completedSkillsList = new ArrayList<>();
//        List<Skill> partiallyCompletedSkillList = new ArrayList<>();

//        List<Substep> substepList;
//        for (Skill s : skillList){
//            if(s.getProgress() == 100){
//                noOfCompletedSkills++;
//                completedSkillsList.add(s);
//            } else if (s.getProgress() != 0 && s.getProgress() != 100) {
//                partiallyCompletedSkillList.add(s);
//            }
//            noOfTotalSubsteps += s.getSubsteps(requireContext()).size();
//        }
//
//        // It counts the steps of completed skills
//
//        for (Skill s : completedSkillsList){
//            Log.d(DEBUG_TAG,"The list of substeps" +s.getSubsteps(requireContext()));
//            noOfCompletedSubsteps += s.getSubsteps(requireContext()).size();
//        }
//
//        // It counts the steps of partially completed skills with filtration of completed steps.
//        for(Skill s : partiallyCompletedSkillList){
//            substepList = s.getSubsteps(requireContext());
//            for (Substep substep : substepList){
//                if (substep.isCompleted()){
//                    noOfCompletedSubsteps++;
//                }
//            }
//        }


        for (Skill skill : skillList) {
            List<Substep> substeps = skill.getSubsteps(requireContext()); // Optional but useful if you later need it

            noOfTotalSubsteps += substeps.size();

            int completedForSkill = 0;
            for (Substep sub : substeps) {
                if (sub.isCompleted()) completedForSkill++;
            }

            noOfCompletedSubsteps += completedForSkill;

            if (!substeps.isEmpty() && completedForSkill == substeps.size()) {
                noOfCompletedSkills++;
            }

            //Logic for calculating the top 3 Learning Skills

            if (skill.getProgress() == 100) continue;

            if (skillNo1 == null || skill.getProgress() > skillNo1.getProgress()) {
                skillNo3 = skillNo2;
                skillNo2 = skillNo1;
                skillNo1 = skill;
            } else if (skillNo2 == null || skill.getProgress() > skillNo2.getProgress()) {
                skillNo3 = skillNo2;
                skillNo2 = skill;
            } else if (skillNo3 == null || skill.getProgress() > skillNo3.getProgress()) {
                skillNo3 = skill;
            }
            // Ended


        }

        Log.d(DEBUG_TAG, "The value of completed Substeps is : " + noOfCompletedSubsteps);
        Log.d(DEBUG_TAG, "The value of total Substeps is : " + noOfTotalSubsteps);

        totalSkills.setText(String.valueOf(noOfTotalSkills));
        totalCompletedSkills.setText(String.valueOf(noOfCompletedSkills));
        String formatSubstepsRatio = noOfCompletedSubsteps + " / " + noOfTotalSubsteps;
        totalCompletedSubsteps.setText(formatSubstepsRatio);

        float averagePercent = averageFunction(skillList);

        String formatAveragePercent = averagePercent + " %";
        averageProgress.setText(formatAveragePercent);

        motivationalQuoteMaker(averagePercent);

        MiniPieChartView pieChartView = view.findViewById(R.id.pieChartView);

        pieChartView.setProgress(averagePercent);
        pieChartView.animateProgressTo(averagePercent,2000);
        topSkillsSetterFunction();

    }

    private int averageFunction(List<Skill> values){
        if (values.isEmpty()) return 0;
        int sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i).getProgress();
        }
        return sum / values.size();
    }

    private void motivationalQuoteMaker(float avgPercent){
        Random random = new Random();
        List<String> quote;
        if (avgPercent >= 0f && avgPercent < 10f){
            quote = QUOTES_0_9;
        } else if (avgPercent > 10f && avgPercent < 40f){
            quote = QUOTES_10_39;
        } else if (avgPercent > 40f && avgPercent < 70f){
            quote = QUOTES_40_69;
        } else if (avgPercent > 70f && avgPercent < 80f){
            quote = QUOTES_70_89;
        } else {
            quote = QUOTES_90_100;
        }
        motivationalQuote.setText(quote.get(random.nextInt(quote.size())));
    }

    private void topSkillsSetterFunction(){

        tvSkillNo2.setVisibility(View.VISIBLE);
        tvSkillNo3.setVisibility(View.VISIBLE);

        if (skillNo1 == null && skillNo2 == null && skillNo3 == null){
            tvSkillNo1.setText(R.string.there_are_no_other_learning_skills_at_present_time);
            tvSkillNo2.setVisibility(View.GONE);
            tvSkillNo3.setVisibility(View.GONE);

        } else if (skillNo1 != null && skillNo2 == null && skillNo3 == null){
            tvSkillNo1.setText(skillNo1.getNameOfSkill());
            tvSkillNo2.setText(R.string.there_are_no_other_learning_skills_at_present_time);
            tvSkillNo3.setVisibility(View.GONE);

        } else if (skillNo1 != null && skillNo2 != null  && skillNo3 == null){
            tvSkillNo1.setText(skillNo1.getNameOfSkill());
            tvSkillNo2.setText(skillNo2.getNameOfSkill());
            tvSkillNo3.setText(R.string.there_are_no_other_learning_skills_at_present_time);

        } else {
            assert skillNo1 != null;
            tvSkillNo1.setText(skillNo1.getNameOfSkill());
            tvSkillNo2.setText(skillNo2.getNameOfSkill());
            tvSkillNo3.setText(skillNo3.getNameOfSkill());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        applyTextVisibility(requireView());
    }

    private void applyTextVisibility(View view) {
        TextView motivationQuotes = view.findViewById(R.id.tvMotivationalQuote);
        TextView motivationQuotesTitle = view.findViewById(R.id.tv_motivational_quote_title);

        if (motivationQuotes != null) {
            SharedPreferences prefs = requireContext().getSharedPreferences("skillbuilder_prefs", Context.MODE_PRIVATE);
            boolean show = prefs.getBoolean("show_dashboard_text", true);
            motivationQuotes.setVisibility(show ? View.VISIBLE : View.GONE);
            motivationQuotesTitle.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}