package com.skillbuilder.app.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.skillbuilder.app.models.ExportData;
import com.skillbuilder.app.models.Skill;
import com.skillbuilder.app.models.Substep;

import java.util.ArrayList;
import java.util.List;

public class SkillDatabaseHelper extends SQLiteOpenHelper {

    // Database name and Version
    private static final String DATABASE_NAME = "skill builder.db";
    private static final int DATABASE_VERSION = 1;


    // Singleton instance
    private static SkillDatabaseHelper instance;

    // Private constructor (so no other class can instantiate it directly)
    private SkillDatabaseHelper(@NonNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Public method to get the singleton instance
    public static synchronized SkillDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SkillDatabaseHelper(context);
        }
        return instance;
    }



    // Table and Columns Names

    private static final String TABLE_SKILLS = "Skills";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CREATED_AT = "created_at";

    private static final String COLUMN_PROGRESS = "progress";

    private static final String TABLE_SUBSTEPS = "Substeps";
    private static final String COLUMN_SUBSTEP_ID = "id";
    private static final String COLUMN_SKILL_ID = "skill_id";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COMPLETED = "completed"; // 0 or 1

    /** When a new database is created without any new table existing,
    also after upgrading version of database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SKILLS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_CREATED_AT + " TEXT,  " +
                COLUMN_PROGRESS + " INTEGER DEFAULT 0" + ")";

        String CREATE_SUBSTEPS_TABLE = "CREATE TABLE " + TABLE_SUBSTEPS + "(" +
                COLUMN_SUBSTEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SKILL_ID + " INTEGER, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_SKILL_ID + ") REFERENCES " +
                TABLE_SKILLS + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_SUBSTEPS_TABLE);
    }

    ///When the Database Version increases then this function will be called
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKILLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSTEPS);
        onCreate(db);
    }

    public void addSkill(Skill skill){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,skill.getNameOfSkill());
        values.put(COLUMN_CREATED_AT,skill.getCreatedAt());
        values.put(COLUMN_PROGRESS,skill.getProgress());
        db.insert(TABLE_SKILLS,null, values);
        db.close();
    }

    public List<Substep> getSubstepsForSkills(int skillId){
        List<Substep> substeps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_SUBSTEPS,
                null,
                COLUMN_SKILL_ID + "=?",
                new String[]{String.valueOf(skillId)},
                null, null,
                COLUMN_SUBSTEP_ID + " ASC"
                );

        if(cursor.moveToFirst()){
            do {
                Substep s = new Substep();
                s.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSTEP_ID)));
                s.setSkillId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SKILL_ID)));
                s.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                int completedValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED));
                s.setCompleted(completedValue == 1);
                substeps.add(s);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return substeps;
    }

    public ExportData getExportDataForSkillIds(List<Integer> selectedSkillIds) {
        List<Skill> skills = new ArrayList<>();
        List<Substep> substeps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        for (int skillId : selectedSkillIds) {
            Cursor skillCursor = db.rawQuery("SELECT * FROM skills WHERE id = ?", new String[]{String.valueOf(skillId)});
            if (skillCursor.moveToFirst()) {
                String name = skillCursor.getString(skillCursor.getColumnIndexOrThrow("name"));
                String createdAt = skillCursor.getString(skillCursor.getColumnIndexOrThrow("created_at"));
                int progress = skillCursor.getInt(skillCursor.getColumnIndexOrThrow("progress"));

                Skill skill = new Skill(skillId, name, createdAt, progress);
                skills.add(skill);
            }
            skillCursor.close();

            Cursor subCursor = db.rawQuery("SELECT * FROM substeps WHERE skill_id = ?", new String[]{String.valueOf(skillId)});
            while (subCursor.moveToNext()) {
                int subId = subCursor.getInt(subCursor.getColumnIndexOrThrow("id"));
                String desc = subCursor.getString(subCursor.getColumnIndexOrThrow("description"));
                boolean completed = subCursor.getInt(subCursor.getColumnIndexOrThrow("completed")) == 1;

                Substep substep = new Substep(subId, skillId, desc, completed);
                substeps.add(substep);
            }
            subCursor.close();
        }

        return new ExportData(skills, substeps);
    }



    public List<Skill> getAllSkills() {
        List<Skill> skills = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("skills", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

                // Fetch substeps for this skill
                List<Substep> substeps = getSubstepsBySkillId(id);

                // Calculate progress
                int completed = 0;
                for (Substep s : substeps) {
                    if (s.isCompleted()) completed++;
                }
                int progress = !substeps.isEmpty() ? (completed * 100 / substeps.size()) : 0;

                Skill skill = new Skill(id, name, createdAt);
                skill.setProgress(progress);  // ensure your Skill model supports this

                skills.add(skill);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return skills;
    }

    public List<Substep> getSubstepsBySkillId(int skillId) {
        List<Substep> substeps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SUBSTEPS,  // your substep table name
                null,
                COLUMN_SKILL_ID + "= ?",
                new String[]{String.valueOf(skillId)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("completed")) == 1;

                Substep substep = new Substep(id, skillId, description, isCompleted);
                substeps.add(substep);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return substeps;
    }



    public void deleteSkill(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SKILLS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteSubstep(int substepID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSTEPS,COLUMN_SUBSTEP_ID + "=?", new String[]{String.valueOf(substepID)});
        db.close();
    }


    public void updateSubstepCompletion(int substepId, boolean isCompleted){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_SUBSTEPS,values,COLUMN_SUBSTEP_ID + "=?",
                new String[]{String.valueOf(substepId)});
        db.close();
    }

    public void updateSkillProgress(int skillId, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("progress", progress);
        db.update("Skills", values, "id = ?", new String[]{String.valueOf(skillId)});
        db.close();
    }

    public void deleteAllData(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SUBSTEPS,null,null);
        db.delete(TABLE_SKILLS,null,null);
        db.close();
    }

    public void insertSkillWithId(Skill skill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", skill.getId());  // Force the correct ID
        values.put("name", skill.getNameOfSkill());
        values.put("created_at", skill.getCreatedAt());
        values.put("progress", skill.getProgress());
        db.insert("skills", null, values);
    }

    public void insertSubstepWithId(Substep substep) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", substep.getId());
        values.put("skill_id", substep.getSkillId());
        values.put("description", substep.getDescription());
        values.put("completed", substep.isCompleted() ? 1 : 0);
        db.insert("substeps", null, values);
    }


}
