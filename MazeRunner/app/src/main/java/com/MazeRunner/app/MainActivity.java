package com.MazeRunner.app;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
//import android.view.View;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private  MazeCellMaker[][] mazegrid;

    int scale;
    int X_coordinates;
    int Y_coordinates;
    private MazeView mazeView;

    private int rows,columns;
    Random rand = new Random();

    private void new_mazes(int scale){
        initialize_Maze_Game(scale,scale);
        generate_Maze_with_Prims_algorithm();

        mazeView = new MazeView(this, mazegrid,rows,columns,X_coordinates,Y_coordinates);
        mazeView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        FrameLayout mazeContainer = findViewById(R.id.frame_layout_container_for_maze);
        mazeContainer.removeAllViews();
        mazeContainer.addView(mazeView);
        place_goal_cell();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent scaleValueImported = getIntent();
        scale = scaleValueImported.getIntExtra("scaleValue",19);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new_mazes(scale);

        Button btn_up = findViewById(R.id.btnUp);
        Button btn_down = findViewById(R.id.btnDown);
        Button btn_left = findViewById(R.id.btnLeft);
        Button btn_right = findViewById(R.id.btnRight);
        Button btnBack = findViewById(R.id.btnBack);

        btn_up.setOnClickListener(v -> movement("up"));
        btn_down.setOnClickListener(v -> movement("down"));
        btn_right.setOnClickListener(v -> movement("right"));
        btn_left.setOnClickListener(v -> movement("left"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setDifficultyLevelActivity = new Intent(MainActivity.this, difficultyLevelScreen.class);
                startActivity(setDifficultyLevelActivity);
            }
        });
    }


    private void place_goal_cell(){
        int goal_row = 1 + 2 * rand.nextInt((rows - 1) / 2);
        int goal_column = 1 + 2 * rand.nextInt((columns - 1) / 2);
        if (X_coordinates != goal_row || Y_coordinates != goal_column){
            mazegrid[(goal_row)][goal_column].setGoal(true);
        } else{
            place_goal_cell();
        }
    }

    private void revealFog(int XOrdinates, int YOrdinates){
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1 ; j++) {
                int newX = i + XOrdinates;
                int newY = j + YOrdinates;
                mazegrid[newX][newY].setVisible(true);
            }
        }
    }


    private void movement(String direction){
        int new_X = X_coordinates;
        int new_Y = Y_coordinates;
        switch (direction) {
            case "up":
                new_X = new_X - 1;
                break;
            case "down":
                new_X = new_X + 1;
                break;
            case "left":
                new_Y = new_Y - 1;
                break;
            case "right":
                new_Y = new_Y + 1;
                break;
        }
        if (isInBounds(new_X,new_Y) && !mazegrid[new_X][new_Y].isWall()){
            X_coordinates = new_X;
            Y_coordinates = new_Y;
            mazeView.updatePlayerPosition(X_coordinates,Y_coordinates);
            revealFog(X_coordinates,Y_coordinates);
            
            if (mazegrid[X_coordinates][Y_coordinates].isGoal()){
                Toast.makeText(this, "\uD83C\uDFAF You reached the goal! Level Complete!", Toast.LENGTH_SHORT).show();
                new_mazes(scale);
            }
        }
        else{
            Toast.makeText(this, "There is wall ahead, Can't Move There.",Toast.LENGTH_LONG).show();
        }
    }



    public void initialize_Maze_Game(int rows, int columns){
        this.rows = rows;
        this.columns = columns;

        mazegrid = new MazeCellMaker[rows][columns];
        initializeMaze();
    }



    private  void initializeMaze(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                mazegrid[i][j] = new MazeCellMaker( true,true,false,false);
            }
        }
    }

    private void generate_Maze_with_Prims_algorithm() {

        List<int[]> frontier = new ArrayList<>();
        int start_row = 1 + 2 * rand.nextInt((rows - 1) / 2);
        int start_column = 1 + 2 * rand.nextInt((columns - 1) / 2);

        X_coordinates = start_row;
        Y_coordinates = start_column;
        revealFog(X_coordinates,Y_coordinates);

        mazegrid[start_row][start_column].setWall(false);
        add_frontier(start_row, start_column, frontier);

        while (!frontier.isEmpty()) {
            int[] wall = frontier.remove(rand.nextInt(frontier.size()));
            int row = wall[0];
            int column = wall[1];

            List<int[]> neighbors = getVisitedNeighbors(row, column);

            if (!neighbors.isEmpty()) {
                int[] neighbor = neighbors.get(rand.nextInt(neighbors.size()));
                int wall_row = (row + neighbor[0]) / 2;
                int wall_column = (column + neighbor[1]) / 2;

                mazegrid[row][column].setWall(false);
                mazegrid[wall_row][wall_column].setWall(false);
                mazegrid[neighbor[0]][neighbor[1]].setWall(false);

                add_frontier(row, column, frontier);
            }
        }
    }


    private void add_frontier(int row, int column, List<int[]> frontier){
        int[][] directions = {{2,0},{0,2},{-2,0},{0,-2}};
        for (int[] d : directions){
            int next_row = row + d[0];
            int next_column = column + d[1];

            if (isInBounds(next_row,next_column) && mazegrid[next_row][next_column].isWall()) {
                boolean alreadyInFrontier = false;
                for (int[] f : frontier) {
                    if (f[0] == next_row && f[1] == next_column) {
                        alreadyInFrontier = true;
                        break;
                    }
                }
                if (!alreadyInFrontier) {
                    frontier.add(new int[]{next_row,next_column});
                }
            }
        }
    }


    private boolean isInBounds(int row, int column){
        return row > 0 && column > 0 && row<rows-1 && column <columns-1;
    }


    private List<int[]> getVisitedNeighbors(int row, int column){
        List<int[]> neighbors = new ArrayList<>();
        int [][] directions = {{2,0},{-2,0},{0,2},{0,-2}};
        for (int[] d : directions){
            int next_row = row + d[0], next_column = column + d[1];
            if (isInBounds(next_row,next_column) && !mazegrid[next_row][next_column].isWall()){
                neighbors.add(new int[]{next_row,next_column});
            }
        }
        return neighbors;
    }
}

