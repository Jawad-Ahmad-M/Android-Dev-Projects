package com.MazeRunner.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class difficultyLevelScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen);
        buttonCreation();


//        Intent intent = new Intent();
//        intent.setClass(difficultyLevelScreen.this, MainActivity.class);
    }

    private void buttonCreation(){
        Button difficultyEasy = findViewById(R.id.btnEasy);
        Button difficultyStandard = findViewById(R.id.btnStandard);
        Button difficultyDifficult = findViewById(R.id.btnDifficult);
        Button difficultyUltraHard = findViewById(R.id.btnUltraHard);

        buttonFunctions(difficultyEasy,9);
        buttonFunctions(difficultyStandard,15);
        buttonFunctions(difficultyDifficult,21);
        buttonFunctions(difficultyUltraHard,27);
    }


    public void buttonFunctions(Button btn , int scale){
        btn.setOnClickListener(v -> passValueOfScaleThroughDifficultyButton(scale));
    }

    private void passValueOfScaleThroughDifficultyButton(int value){
        Intent mainWindow = new Intent(difficultyLevelScreen.this, MainActivity.class);
        mainWindow.putExtra("scaleValue",value);
        startActivity(mainWindow);
    }
}
