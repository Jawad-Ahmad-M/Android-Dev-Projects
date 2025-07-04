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
    int xCoordinates;
    int yCoordinates;
    private MazeView mazeView;

    private int rows,columns;

    private static final int  NO_OF_ITEMS = 3;
    Random rand = new Random();

    private void new_mazes(int scale){
        initializeMazeGame(scale,scale);
        generateMazeWithPrimsAlgorithm();

        mazeView = new MazeView(this, mazegrid,rows,columns, xCoordinates, yCoordinates);
        mazeView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        FrameLayout mazeContainer = findViewById(R.id.frame_layout_container_for_maze);
        mazeContainer.removeAllViews();
        mazeContainer.addView(mazeView);
        placeGoalCell();
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

        btnBack.setOnClickListener(v -> {
            Intent setDifficultyLevelActivity = new Intent(MainActivity.this, difficultyLevelScreen.class);
            startActivity(setDifficultyLevelActivity);
        });
    }


    private void placeItemCells(){
        int placed = 0;
        List<String> usedPositions = new ArrayList<>();

        while (placed < NO_OF_ITEMS){
            int itemRow = 1 + rand.nextInt(rows-1);
            int itemColumn = 1 + rand.nextInt(columns - 1);

            if (mazegrid[itemRow][itemColumn].isGoal()
            || mazegrid[itemRow][itemColumn].isWall()
            || (itemRow == xCoordinates && itemColumn == yCoordinates)
            ||  usedPositions.contains(itemRow + "," + itemColumn)){
                mazegrid[itemRow][itemColumn].setHasItem(true);
                usedPositions.add(itemRow + "," + itemColumn);
                placed++;
            }
        }

    }

    private void placeGoalCell(){
        int goalRow = 1 + 2 * rand.nextInt((rows - 1) / 2);
        int goalColumn = 1 + 2 * rand.nextInt((columns - 1) / 2);
        if (xCoordinates != goalRow || yCoordinates != goalColumn){
            mazegrid[(goalRow)][goalColumn].setGoal(true);
        } else{
            placeGoalCell();
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
        int new_X = xCoordinates;
        int new_Y = yCoordinates;
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
            xCoordinates = new_X;
            yCoordinates = new_Y;
            mazeView.updatePlayerPosition(xCoordinates, yCoordinates);
            revealFog(xCoordinates, yCoordinates);
            
            if (mazegrid[xCoordinates][yCoordinates].isGoal()){
                Toast.makeText(this, "\uD83C\uDFAF You reached the goal! Level Complete!", Toast.LENGTH_SHORT).show();
                new_mazes(scale);
            }
        }
        else{
            Toast.makeText(this, "There is wall ahead, Can't Move There.",Toast.LENGTH_LONG).show();
        }
    }



    public void initializeMazeGame(int rows, int columns){
        this.rows = rows;
        this.columns = columns;

        mazegrid = new MazeCellMaker[rows][columns];
        initializeMaze();
//        revealBoundaryFog();
    }



    private  void initializeMaze(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                mazegrid[i][j] = new MazeCellMaker( true,false,false,false,false);
            }
        }
        revealBoundaryFog();
    }

    private void generateMazeWithPrimsAlgorithm() {

        List<int[]> frontier = new ArrayList<>();
        int start_row = 1 + 2 * rand.nextInt((rows - 1) / 2);
        int start_column = 1 + 2 * rand.nextInt((columns - 1) / 2);

        xCoordinates = start_row;
        yCoordinates = start_column;
        revealFog(xCoordinates, yCoordinates);

        mazegrid[start_row][start_column].setWall(false);
        addFrontier(start_row, start_column, frontier);

        while (!frontier.isEmpty()) {
            int[] wall = frontier.remove(rand.nextInt(frontier.size()));
            int row = wall[0];
            int column = wall[1];

            List<int[]> neighbors = getVisitedNeighbors(row, column);

            if (!neighbors.isEmpty()) {
                int[] neighbor = neighbors.get(rand.nextInt(neighbors.size()));
                int wallRow = (row + neighbor[0]) / 2;
                int wallColumn = (column + neighbor[1]) / 2;

                mazegrid[row][column].setWall(false);
                mazegrid[wallRow][wallColumn].setWall(false);
                mazegrid[neighbor[0]][neighbor[1]].setWall(false);

                addFrontier(row, column, frontier);
            }
        }
    }


    private void addFrontier(int row, int column, List<int[]> frontier){
        int[][] directions = {{2,0},{0,2},{-2,0},{0,-2}};
        for (int[] d : directions){
            int nextRow = row + d[0];
            int nextColumn = column + d[1];

            if (isInBounds(nextRow,nextColumn) && mazegrid[nextRow][nextColumn].isWall()) {
                boolean alreadyInFrontier = false;
                for (int[] f : frontier) {
                    if (f[0] == nextRow && f[1] == nextColumn) {
                        alreadyInFrontier = true;
                        break;
                    }
                }
                if (!alreadyInFrontier) {
                    frontier.add(new int[]{nextRow,nextColumn});
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

    private void revealBoundaryFog(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ( i == 0 || j == 0 || i == rows-1 || j == columns-1){
                    mazegrid[i][j].setVisible(true);
                }
            }
        }
    }
}



