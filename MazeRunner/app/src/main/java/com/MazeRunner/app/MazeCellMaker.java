package com.MazeRunner.app;

public class MazeCellMaker {
    private  boolean isWall;
    private boolean isVisible;
//    private  boolean hasKey;
    private boolean isGoal;
    private boolean isVisited;
    private boolean hasItem;
    private boolean isPath;
//


    public MazeCellMaker(boolean isWall, boolean isVisible, boolean isGoal, boolean isVisited,boolean hasItem,boolean isPath) {
        this.isWall = isWall;
        this.isVisible = isVisible;
        this.isGoal = isGoal;
        this.isVisited = isVisited;
        this.hasItem = hasItem;
        this.isPath = isPath;
    }

    public boolean isPath() {
        return isPath;
    }

    public void setPath(boolean path) {
        isPath = path;
    }

    public boolean isHasItem() {
        return hasItem;
    }

    public void setHasItem(boolean hasItem) {
        this.hasItem = hasItem;
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
