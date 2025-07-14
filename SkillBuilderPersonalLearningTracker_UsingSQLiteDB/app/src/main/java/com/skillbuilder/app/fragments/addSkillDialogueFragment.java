package com.skillbuilder.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skillbuilder.app.R;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.storage.SkillDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class addSkillDialogueFragment extends DialogFragment {

    public interface onSkillAddedListener{
        void onSkillAdded();
    }

    private onSkillAddedListener listener;

    public void setOnSkillAddedListener(onSkillAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.fragment_add_skill_dialogue,null);

        EditText nameInput = view.findViewById(R.id.editTextSkillName);

        Button addButton = view.findViewById(R.id.buttonAddSkill);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        addButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            if(!name.isEmpty()){
                Skill newSkill = new Skill(name,currentTimestamp);
                try (SkillDatabaseHelper db = SkillDatabaseHelper.getInstance(getContext())) {
                    db.addSkill(newSkill);
                } catch (Exception e){
                    Toast.makeText(getContext(), "Skill Not Added Issue present in SkillDatabaseHelper.class",Toast.LENGTH_LONG).show();
                    Log.d("ErrorsInSkillBuilder","Skill Not Added Issue present in SkillDatabaseHelper.class");
                }

                if (listener != null){
                    listener.onSkillAdded();
                }
                alertDialog.dismiss();
            }else {
                nameInput.setError("Skill Name is required.");
            }

        });
        return alertDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_skill_dialogue, container, false);
    }




}