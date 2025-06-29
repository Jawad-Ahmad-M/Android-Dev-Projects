package com.MazeRunner.app;

//import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
import android.graphics.Rect;
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
//    private final Paint wallPaint;
//    private final Paint pathPaint;
//    private final Paint playerPaint;
//    private final Paint goalPaint;
//
//    private final Paint fogPaint;

    private final Bitmap wallBitmap;
    private final Bitmap pathBitmap;
    private final Bitmap goalBitmap;
    private final Bitmap playerBitmap;
//    private final Bitmap fogBitmap;


    public MazeView(Context context, MazeCellMaker[][] mazegrid, int rows, int columns, int playerX, int playerY) {
        super(context);
        this.mazegrid = mazegrid;
        this.rows = rows;
        this.columns = columns;
        this.playerX = playerX;
        this.playerY = playerY;

        wallBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        pathBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.paths);
        goalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cup_image);
        playerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.character);
//        fogBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fog);

//        wallPaint = new Paint();
//        wallPaint.setColor(Color.BLACK);
//
//        pathPaint = new Paint();
//        pathPaint.setColor(Color.WHITE);
//
//        playerPaint = new Paint();
//        playerPaint.setColor(Color.RED);
//
//        goalPaint = new Paint();
//        goalPaint.setColor(Color.parseColor("#FFD700"));
//
//        fogPaint = new Paint();
//        fogPaint.setColor(Color.parseColor("#808080"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                float left = j * cellSize;
                float top = i * cellSize;

                Rect dest = new Rect((int) left, (int) top, (int) (left + cellSize), (int) (top + cellSize));

                if (!mazegrid[i][j].isVisible()) {
//                    canvas.drawBitmap(fogBitmap, null, dest, null);
                } else if (mazegrid[i][j].isWall()) {
                    canvas.drawBitmap(wallBitmap, null, dest, null);
                } else if (mazegrid[i][j].isGoal()) {
                    canvas.drawBitmap(goalBitmap, null, dest, null);
                } else {
                    canvas.drawBitmap(pathBitmap, null, dest, null);
                }

                if (i == playerX && j == playerY) {
                    canvas.drawBitmap(playerBitmap, null, dest, null);
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