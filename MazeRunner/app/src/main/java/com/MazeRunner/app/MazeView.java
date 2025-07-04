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

import org.jetbrains.annotations.NotNull;


public class MazeView extends View {
    private final MazeCellMaker[][] mazegrid;
    private final int rows, columns;
    private float cellSize;

    Rect destRect = new Rect();

    @Override
    protected void onSizeChanged(int width, int height, int oldWeight, int oldHeight) {
        super.onSizeChanged(width, height, oldWeight, oldHeight);
        cellSize = Math.min(width / columns, height / rows);
    }
    private int playerX, playerY;

    private final Bitmap wallBitmap;
    private final Bitmap pathBitmap;
    private final Bitmap goalBitmap;
    private final Bitmap playerBitmap;
    private final Bitmap fogBitmap;
    private final Bitmap itemBitmap;



    public MazeView(Context context, MazeCellMaker[][] mazegrid, int rows, int columns, int playerX, int playerY) {
        super(context);
        this.mazegrid = mazegrid;
        this.rows = rows;
        this.columns = columns;
        this.playerX = playerX;
        this.playerY = playerY;

        wallBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        pathBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.paths);
        goalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.wincup);
        playerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.character);
        fogBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fog2);
        itemBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.item);

        /* If we want to use colors than these will be used
        instead of bitmaps.
         */
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
    protected void onDraw(@NotNull Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                float left = j * cellSize;
                float top = i * cellSize;

                destRect.set((int) left, (int) top, (int) (left + cellSize), (int) (top + cellSize));

                if (!mazegrid[i][j].isVisible()) {
                    canvas.drawBitmap(fogBitmap, null, destRect, null);
                } else if (mazegrid[i][j].isWall()) {
                    canvas.drawBitmap(wallBitmap, null, destRect, null);
                } else if (mazegrid[i][j].isGoal()) {
                    canvas.drawBitmap(goalBitmap, null, destRect, null);
                } else if (mazegrid[i][j].isHasItem()) {
                    canvas.drawBitmap(itemBitmap, null, destRect, null);
                } else {
                    canvas.drawBitmap(pathBitmap, null, destRect, null);
                }
                if (i == playerX && j == playerY) {
                    canvas.drawBitmap(playerBitmap, null, destRect, null);
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