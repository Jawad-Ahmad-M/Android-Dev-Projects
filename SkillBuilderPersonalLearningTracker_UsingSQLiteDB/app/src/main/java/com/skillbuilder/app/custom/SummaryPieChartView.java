package com.skillbuilder.app.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SummaryPieChartView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private List<Integer> values = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private List<String> legendLabels = new ArrayList<>();

    private float animatedFraction = 0f;

    public SummaryPieChartView(Context context) {
        super(context);
    }

    public SummaryPieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryPieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Set data + legends
    public void setData(List<Integer> values, List<Integer> colors, List<String> legends) {
        this.values = (values != null) ? values : new ArrayList<>();
        this.colors = (colors != null) ? colors : new ArrayList<>();
        this.legendLabels = (legends != null) ? legends : new ArrayList<>();
        startAnimation();
    }

    public void setData(List<Integer> values, List<Integer> colors) {
        setData(values, colors, null);
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> {
            animatedFraction = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (values == null || values.isEmpty() || getTotal(values) == 0) {
            drawEmptyState(canvas);
            return;
        }

        float dp = getResources().getDisplayMetrics().density;
        int chartSize = (int) (200 * dp); // 200dp converted to pixels
        int viewWidth = getWidth();

        int paddingTop = (int) (20 * dp); // Top margin
        float left = (viewWidth - chartSize) / 2f;
        float top = paddingTop;
        float right = left + chartSize;
        float bottom = top + chartSize;

        rectF.set(left, top, right, bottom);

        int total = getTotal(values);
        float startAngle = -90f;

        for (int i = 0; i < values.size(); i++) {
            float rawSweep = (values.get(i) * 360f) / total;
            float animatedSweep = rawSweep * animatedFraction;

            int color = (i < colors.size()) ? colors.get(i) : Color.GRAY;
            paint.setColor(color);

            canvas.drawArc(rectF, startAngle, animatedSweep, true, paint);
            startAngle += rawSweep;
        }

        // Draw legends right below the chart (with spacing)
        float legendStartY = bottom + (int)(20 * dp);
        drawLegends(canvas, legendStartY);
    }


    public void drawLegends(Canvas canvas, float startY) {
        if (legendLabels == null || legendLabels.isEmpty()) return;

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(32f);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextAlign(Paint.Align.LEFT);

        float boxSize = 30f;
//        float spacing = 20f;
        float textSpacing = 10f;
        float lineSpacing = 20f;

        float x = 40f;
        float y = startY;
        int itemsInRow = 0;

        for (int i = 0; i < legendLabels.size(); i++) {
            if (i < colors.size()) {
                // Draw colored square
                paint.setColor(colors.get(i));
                canvas.drawRect(x, y, x + boxSize, y + boxSize, paint);

                // Draw label
                float textX = x + boxSize + textSpacing;
                float textY = y + boxSize - 5f;
                canvas.drawText(legendLabels.get(i), textX, textY, textPaint);

                itemsInRow++;
                if (itemsInRow == 2) {
                    // Move to next row
                    x = 40f;
                    y += boxSize + lineSpacing;
                    itemsInRow = 0;
                } else {
                    // Next column
                    x += 300f;  // Horizontal spacing between columns
                }
            }
        }
    }

    private int getTotal(List<Integer> values) {
        int sum = 0;
        for (int v : values) sum += v;
        return sum;
    }

    private void drawEmptyState(Canvas canvas) {
        String message = "No summary data";
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextSize(42f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        float x = getWidth() / 2f;
        float y = getHeight() / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f);
        canvas.drawText(message, x, y, textPaint);
    }
}
