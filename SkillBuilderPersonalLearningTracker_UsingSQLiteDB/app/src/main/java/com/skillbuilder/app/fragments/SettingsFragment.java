package com.skillbuilder.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skillbuilder.app.R;
import com.skillbuilder.app.models.ExportData;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.models.Substep;
import com.skillbuilder.app.storage.SkillDatabaseHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private SkillDatabaseHelper dbHelper;
    private Uri selectedExportDirUri;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SkillPrefs";
    private static final String PREF_EXPORT_URI = "ExportFolderUri";

    private static final String PREF_NAME = "skillbuilder_prefs";
    private static final String KEY_SHOW_DASHBOARD_TEXT = "show_dashboard_text";

    private final ActivityResultLauncher<Intent> folderPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        selectedExportDirUri = uri;
                        sharedPreferences.edit().putString(PREF_EXPORT_URI, uri.toString()).apply();
                        Toast.makeText(requireContext(), "Export folder set.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) showImportTypeDialog(uri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = SkillDatabaseHelper.getInstance(requireContext());
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uriStr = sharedPreferences.getString(PREF_EXPORT_URI, null);
        if (uriStr != null) selectedExportDirUri = Uri.parse(uriStr);

        Button btnResetAllData = view.findViewById(R.id.buttonResetData);
        Button btnExportSkills = view.findViewById(R.id.buttonExport);
        Button btnImportSkills = view.findViewById(R.id.buttonImport);

//        SwitchCompat lightDarkMode = view.findViewById(R.id.switchDarkMode);
        SwitchCompat showMotivationalQuotes = view.findViewById(R.id.switchQuotes);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Set the switch to the current saved state
        boolean isVisible = prefs.getBoolean(KEY_SHOW_DASHBOARD_TEXT, true);
        showMotivationalQuotes.setChecked(isVisible);

        // Listener to update preference
        showMotivationalQuotes.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean(KEY_SHOW_DASHBOARD_TEXT, isChecked).apply());

        btnResetAllData.setOnClickListener(v -> confirmReset());
        btnExportSkills.setOnClickListener(v -> {
            if (selectedExportDirUri == null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Select Export Folder")
                        .setMessage("You need to select a folder to save exported JSON files.")
                        .setPositiveButton("Select", (d, w) -> {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            folderPickerLauncher.launch(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else showExportDialog();
        });

        btnImportSkills.setOnClickListener(v -> openFilePicker());
    }

    private void confirmReset() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Reset")
                .setMessage("Are you sure you want to delete all data?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteAllData();
                    Toast.makeText(requireContext(), "All data cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showExportDialog() {
        List<Skill> allSkills = dbHelper.getAllSkills();
        String[] skillNames = new String[allSkills.size()];
        boolean[] selected = new boolean[allSkills.size()];

        for (int i = 0; i < allSkills.size(); i++) {
            skillNames[i] = allSkills.get(i).getNameOfSkill();
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Skills to Export")
                .setMultiChoiceItems(skillNames, selected, (dialog, which, isChecked) -> selected[which] = isChecked)
                .setPositiveButton("Export", (dialog, which) -> {
                    List<Integer> selectedSkillIds = new ArrayList<>();
                    for (int i = 0; i < selected.length; i++) {
                        if (selected[i]) selectedSkillIds.add(allSkills.get(i).getId());
                    }

                    if (selectedSkillIds.isEmpty()) {
                        Toast.makeText(requireContext(), "No skills selected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ExportData exportData = dbHelper.getExportDataForSkillIds(selectedSkillIds);
                    promptForExportName(exportData);
                })
                .setNeutralButton("Pick Folder", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    folderPickerLauncher.launch(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void promptForExportName(ExportData data) {
        final EditText input = new EditText(requireContext());
        input.setHint("Enter file name");

        new AlertDialog.Builder(requireContext())
                .setTitle("File Name")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty() || !name.matches("^[a-zA-Z0-9-_]+$")) {
                        Toast.makeText(requireContext(), "Invalid filename", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    exportJsonData(data, name);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void exportJsonData(ExportData data, String filename) {
        DocumentFile dir = DocumentFile.fromTreeUri(requireContext(), selectedExportDirUri);
        if (dir == null) {
            Toast.makeText(requireContext(), "Invalid export folder", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentFile existing = dir.findFile(filename + ".json");
        if (existing != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("File Exists")
                    .setMessage("Overwrite?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        existing.delete();
                        writeExportFile(dir, filename, data);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            writeExportFile(dir, filename, data);
        }
    }

    private void writeExportFile(DocumentFile dir, String filename, ExportData data) {
        try {
            DocumentFile file = dir.createFile("application/json", filename + ".json");
            if (file == null) {
                Toast.makeText(requireContext(), "Failed to create file", Toast.LENGTH_SHORT).show();
                return;
            }

            Gson gson = new Gson();
            String json = gson.toJson(data);

            try (OutputStream out = requireContext().getContentResolver().openOutputStream(file.getUri());
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
                writer.write(json);
            }

            Toast.makeText(requireContext(), "Exported to " + filename + ".json", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private void showImportTypeDialog(Uri uri) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Import Type")
                .setMessage("Do you want to merge with existing data or replace everything?")
                .setPositiveButton("Merge", (dialog, which) -> importSkillsAndSubsteps(uri, true))
                .setNegativeButton("Replace", (dialog, which) -> importSkillsAndSubsteps(uri, false))
                .show();
    }

    private void importSkillsAndSubsteps(Uri uri, boolean merge) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            Gson gson = new Gson();
            Type type = new TypeToken<ImportContainer>() {}.getType();
            ImportContainer container = gson.fromJson(reader, type);

            if (!merge) dbHelper.deleteAllData();

            for (Skill skill : container.skills) {
                dbHelper.insertSkillWithId(skill);
            }

            for (Substep sub : container.substeps) {
                dbHelper.insertSubstepWithId(sub);
            }

            Toast.makeText(requireContext(), "Import completed.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Import failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    static class ImportContainer {
        List<Skill> skills;
        List<Substep> substeps;
    }
}
