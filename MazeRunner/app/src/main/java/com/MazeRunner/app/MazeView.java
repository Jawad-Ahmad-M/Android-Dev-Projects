package com.MazeRunner.app;

//import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


public class MazeView extends View {
    private final MazeCellMaker[][] mazegrid;
    private final int rows, columns;
    private float cellSize;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w / columns, h / rows);
    }
    private int playerX, playerY;
    private final Paint wallPaint;
    private final Paint pathPaint;
    private final Paint playerPaint;
    private final Paint goalPaint;


    public MazeView(Context context, MazeCellMaker[][] mazegrid, int rows, int columns, int playerX, int playerY) {
        super(context);
        this.mazegrid = mazegrid;
        this.rows = rows;
        this.columns = columns;
        this.playerX = playerX;
        this.playerY = playerY;

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);

        pathPaint = new Paint();
        pathPaint.setColor(Color.WHITE);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        goalPaint = new Paint();
        goalPaint.setColor(Color.parseColor("#FFD700"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                float left = j * cellSize;
                float top = i * cellSize;
                float right = left + cellSize;
                float bottom = top + cellSize;

                if (mazegrid[i][j].isWall) {
                    canvas.drawRect(left, top, right, bottom, wallPaint);
                } else if (mazegrid[i][j].isGoal) {
                    canvas.drawRect(left,top,right,bottom,goalPaint);
                } else {
                    canvas.drawRect(left, top, right, bottom, pathPaint);
                }

                if (i == playerX && j == playerY) {
                    canvas.drawCircle(left + cellSize / 2, top + cellSize / 2, cellSize / 2, playerPaint);
                }
            }
        }
    }

    public void updatePlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        invalidate(); // Redraw the view
    }
}