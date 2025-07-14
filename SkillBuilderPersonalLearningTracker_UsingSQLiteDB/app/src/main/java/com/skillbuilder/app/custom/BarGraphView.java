package com.skillbuilder.app.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
//import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BarGraphView extends View {

    private List<String> skillNames = new ArrayList<>();
    private List<Integer> values = new ArrayList<>();

    private Paint barPaint;
    private TextPaint labelPaint;
    private TextPaint percentPaint;
    private TextPaint emptyPaint;

    private float animationProgress = 0f;

    public enum FilterType {
        ALL, COMPLETED, INCOMPLETE
    }

    private final FilterType currentFilter = FilterType.ALL;

    private static final int BAR_WIDTH = 150;
    private static final int BAR_SPACING = 80;
    private static final int MAX_BAR_HEIGHT = 500;
    private static final int BOTTOM_PADDING = 120;
    private static final int TOP_PADDING = 0;
    private static final int SPACE_ABOVE_THE_LEGEND_OF_BAR_GRAPH = 30;

    public BarGraphView(Context context) {
        super(context);
        init();
    }

    public BarGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);

        labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.DKGRAY);
        labelPaint.setTextSize(spToPx(10));
        labelPaint.setTextAlign(Paint.Align.CENTER);

        percentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        percentPaint.setColor(Color.BLACK);
        percentPaint.setTextSize(spToPx(11));
        percentPaint.setTextAlign(Paint.Align.CENTER);

        emptyPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setColor(Color.LTGRAY);
        emptyPaint.setTextSize(spToPx(14));
        emptyPaint.setTextAlign(Paint.Align.CENTER);
    }

    private float spToPx(float sp){
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }

    public void setSkillData(List<String> skills, List<Integer> valueList) {
        this.skillNames = (skills != null) ? skills : new ArrayList<>();
        this.values = (valueList != null) ? valueList : new ArrayList<>();
        startAnimation();
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    private boolean shouldShowBar(int progress) {
        switch (currentFilter) {
            case ALL:
                return true;
            case COMPLETED:
                return progress == 100;
            case INCOMPLETE:
                return progress < 100;
        }
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (skillNames == null || values == null || skillNames.isEmpty()) {
            String message = "No skills to display";

            // Available drawing width after padding
            float contentWidth = getWidth() - (2 * 32);

            // Horizontal center within padded area
            float textX = (contentWidth / 2f);

            // Vertically center using font metrics
            Paint.FontMetrics fm = emptyPaint.getFontMetrics();
            float textY = getHeight() / 2f - (fm.ascent + fm.descent) / 2f;

            canvas.drawText(message, textX, textY, emptyPaint);
            return;
        }


        // Check if anything should be drawn after filter
        boolean hasValidBars = false;
        for (int i = 0; i < values.size(); i++) {
            if (shouldShowBar(values.get(i))) {
                hasValidBars = true;
                break;
            }
        }

        if (!hasValidBars) {
            drawEmptyState(canvas);
            return;
        }

        int barCount = 0;
        for (int i = 0; i < values.size(); i++) {
            if (shouldShowBar(values.get(i))) barCount++;
        }
//        int totalWidth = barCount * BAR_WIDTH + (barCount - 1) * BAR_SPACING;
//        int currentX = (getWidth() - totalWidth) / 2;
        int currentX = BAR_SPACING;


        for (int i = 0; i < skillNames.size() && i < values.size(); i++) {
            int progress = values.get(i);
            String label = skillNames.get(i);

            if (!shouldShowBar(progress)) continue;

            float barHeight = (progress * MAX_BAR_HEIGHT) / 100f;
            float top = getHeight() - BOTTOM_PADDING - barHeight;
            float bottom = getHeight() - BOTTOM_PADDING;
            float animatedTop = top + barHeight * (1f - animationProgress);

            if (progress == 100) barPaint.setColor(Color.parseColor("#4CAF50"));
            else if (progress >= 50) barPaint.setColor(Color.parseColor("#FFC107"));
            else barPaint.setColor(Color.parseColor("#F44336"));

            canvas.drawRect(currentX, animatedTop, currentX + BAR_WIDTH, bottom, barPaint);

            String percentText = progress + "%";
            float percentY = animatedTop - 10f;
            canvas.drawText(percentText, currentX + BAR_WIDTH / 2f, percentY, percentPaint);

            List<String> lines = breakTextIntoLines(label, labelPaint);
            float lineHeight = labelPaint.getTextSize() + 4f;
            float baseY = bottom + SPACE_ABOVE_THE_LEGEND_OF_BAR_GRAPH;

            for (int j = 0; j < lines.size(); j++) {
                canvas.drawText(lines.get(j), currentX + BAR_WIDTH / 2f, baseY + j * lineHeight, labelPaint);
            }

            currentX += BAR_WIDTH + BAR_SPACING;
        }
    }

    private void drawEmptyState(Canvas canvas) {
        String message = "No skill progress data available";
        float x = getWidth() / 2f;
        float y = getHeight() / 2f - ((emptyPaint.descent() + emptyPaint.ascent()) / 2f);
        canvas.drawText(message, x, y, emptyPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxLines = 1;
        for (String name : skillNames) {
            List<String> lines = breakTextIntoLines(name, labelPaint);
            maxLines = Math.max(maxLines, lines.size());
        }

        int dynamicHeight = MAX_BAR_HEIGHT + BOTTOM_PADDING + TOP_PADDING + (maxLines * 34);

        // Set fallback height
        if (skillNames == null || skillNames.isEmpty()) {
            dynamicHeight = 300;
        }

        // ✅ Force width to screen size if empty (so text is centered inside visible screen)
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int barContentWidth = (BAR_WIDTH + BAR_SPACING) * skillNames.size();
        int desiredWidth = resolveSize(skillNames.isEmpty() ? screenWidth : barContentWidth, widthMeasureSpec);

        int desiredHeight = resolveSize(dynamicHeight, heightMeasureSpec);
        setMeasuredDimension(desiredWidth, desiredHeight);
    }





    private List<String> breakTextIntoLines(String text, TextPaint paint) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String tryLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float lineWidth = paint.measureText(tryLine);
            if (lineWidth <= BarGraphView.BAR_WIDTH || currentLine.length() == 0) {
                currentLine = new StringBuilder(tryLine);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) lines.add(currentLine.toString());
        return lines;
    }
}
