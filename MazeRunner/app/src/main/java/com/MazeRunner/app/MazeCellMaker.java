package com.MazeRunner.app;

public class MazeCellMaker {
    private  boolean isWall;
    private boolean isVisible;
//    private  boolean hasKey;
    private boolean isGoal;
    private boolean isVisited;
//


    public MazeCellMaker(boolean isWall, boolean isVisible, boolean isGoal, boolean isVisited) {
        this.isWall = isWall;
        this.isVisible = isVisible;
        this.isGoal = isGoal;
        this.isVisited = isVisited;
    }

    public boolean isWall() {
        return isWall;
    }

    public void setWall(boolean wall) {
        isWall = wall;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isGoal() {
        return isGoal;
    }

    public void setGoal(boolean goal) {
        isGoal = goal;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}
