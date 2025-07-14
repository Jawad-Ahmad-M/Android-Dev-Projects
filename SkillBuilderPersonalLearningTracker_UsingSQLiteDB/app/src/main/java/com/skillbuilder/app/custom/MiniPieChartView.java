package com.skillbuilder.app.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.jspecify.annotations.NonNull;

public class MiniPieChartView extends View {

    private Paint completedPaint;
    private Paint remainingPaint;
    private float progressPercent = 0f; // 0 to 100

    public MiniPieChartView(Context context) {
        super(context);
        init();
    }

    public MiniPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MiniPieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        completedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        completedPaint.setStyle(Paint.Style.FILL);

        remainingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        remainingPaint.setStyle(Paint.Style.FILL);
        remainingPaint.setColor(Color.LTGRAY); // default background
    }

    public void setProgress(float percent) {
        this.progressPercent = percent;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float legendHeight = getHeight() * 0.15f; // reserve bottom 15% for legends
        float availableHeight = getHeight() - legendHeight;

        float size = Math.min(getWidth(), availableHeight); // make sure pie doesn't use legend space
        float cx = getWidth() / 2f;
        float cy = availableHeight / 2f; // center within available area (above legends)
        float radius = size / 2f;

        completedPaint.setColor(Color.parseColor("#4CAF50"));

        // Draw full background circle (remaining)
        canvas.drawCircle(cx, cy, radius, remainingPaint);

        // Draw progress arc
        float sweepAngle = (progressPercent / 100f) * 360f;
        canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
                -90, sweepAngle, true, completedPaint);

        // Draw legends below
        drawLegends(canvas);
    }


    private void drawLegends(Canvas canvas) {
        float boxSize = getWidth() * 0.05f;
        float textSize = getWidth() * 0.045f;
        float textSpacing = boxSize * 0.3f; // tighter spacing between box and label
        float legendSpacing = getWidth() * 0.08f; // reduced spacing between legends

        Paint legendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextAlign(Paint.Align.LEFT);

        // === Measure text widths ===
        String completedText = "Completed";
        String remainingText = "Remaining";

        float completedTextWidth = textPaint.measureText(completedText);
        float remainingTextWidth = textPaint.measureText(remainingText);

        float completedLegendWidth = boxSize + textSpacing + completedTextWidth;
        float remainingLegendWidth = boxSize + textSpacing + remainingTextWidth;

        float totalLegendWidth = completedLegendWidth + legendSpacing + remainingLegendWidth;

        // === Center X calculation ===
        float startX = (getWidth() - totalLegendWidth) / 2f;
        float y = getHeight() - boxSize - getHeight() * 0.05f;

        // === Completed Legend ===
        legendPaint.setColor(Color.parseColor("#4CAF50"));
        canvas.drawRect(startX, y, startX + boxSize, y + boxSize, legendPaint);
        canvas.drawText(completedText, startX + boxSize + textSpacing, y + boxSize * 0.85f, textPaint);

        // === Remaining Legend ===
        float x2 = startX + completedLegendWidth + legendSpacing;
        legendPaint.setColor(Color.LTGRAY);
        canvas.drawRect(x2, y, x2 + boxSize, y + boxSize, legendPaint);
        canvas.drawText(remainingText, x2 + boxSize + textSpacing, y + boxSize * 0.85f, textPaint);
    }



    public void animateProgressTo(float targetPercent, int durationMillis) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, targetPercent);
        animator.setDuration(durationMillis);
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            progressPercent = (float) animation.getAnimatedValue();
            invalidate(); // Redraw view
        });

        // Set final color based on target, not current animation state
        if (targetPercent >= 100f) {
            completedPaint.setColor(Color.parseColor("#4CAF50")); // Green
        } else if (targetPercent >= 50f) {
            completedPaint.setColor(Color.parseColor("#FFC107")); // Yellow
        } else {
            completedPaint.setColor(Color.parseColor("#F44336")); // Red
        }

        animator.start();
    }



}

