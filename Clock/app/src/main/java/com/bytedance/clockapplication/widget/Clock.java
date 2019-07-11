package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    private int outerRadius, innerRadius;
    private float outerWidth;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        size = Math.min(widthWithoutPadding, heightWithoutPadding);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = Math.min(getWidth(), getHeight());

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        this.innerRadius = (int)(mRadius * 0.02);
        this.outerWidth = 10f;
        this.outerRadius = (int)(mRadius * 0.022 + outerWidth / 2.0);

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.08f);
        textPaint.setColor(hoursValuesColor);
        textPaint.setAntiAlias(true);

        int hoursRadius = (int) (mRadius * 0.74);

        for (int i = 30; i <= FULL_ANGLE; i += 30) {
            int posX = (int)(mCenterX + hoursRadius * Math.sin(Math.toRadians(i)));
            int posY = (int)(mCenterY - hoursRadius * Math.cos(Math.toRadians(i)));
            //Log.d("[POS]", "X:" + posX + " Y:" + posY);
            String text = String.format(Locale.getDefault(),"%02d", (int) (i / 30));
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            canvas.drawText(text, posX - rect.width() / 2.0f, posY + rect.height() / 2.0f, textPaint);
//            Paint paint = new Paint();
//            paint.setColor(secondsNeedleColor);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(10f);
//            canvas.drawPoint(posX, posY, paint);
//            //canvas.drawCircle(mCenterX, mCenterY, hoursRadius, paint);
        }

    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR); // 12-H
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int hourDeg = (int)(FULL_ANGLE * (hour / 12.0 + minute / (60.0 * 12)  + second / (60.0 * 60.0 * 12.0)));
        int minuteDeg = (int)(FULL_ANGLE * (minute / 60.0 + second / (60.0 * 60.0)));
        int secondDeg = (int)(FULL_ANGLE * (second / 60.0));
        int hourRadius = (int)(mRadius * 0.40);
        int minuteRadius = (int)(mRadius * 0.48);
        int secondRadius = (int)(mRadius * 0.60);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        paint.setColor(hoursNeedleColor);
        paint.setStrokeWidth(15f);
        drawNeedle(canvas, hourRadius, hourDeg, paint);

        paint.setColor(minutesNeedleColor);
        paint.setStrokeWidth(15f);
        drawNeedle(canvas, minuteRadius, minuteDeg, paint);

        paint.setColor(secondsNeedleColor);
        paint.setStrokeWidth(10f);
        drawNeedle(canvas, secondRadius, secondDeg, paint);


    }

    private void drawNeedle(Canvas canvas, int radius, int deg, Paint paint) {
        int endX, endY, startX, startY;
        endX = (int)(mCenterX + radius * Math.sin(Math.toRadians(deg)));
        endY = (int)(mCenterY - radius * Math.cos(Math.toRadians(deg)));

        startX = (int)(mCenterX + (innerRadius + outerWidth) * Math.sin(Math.toRadians(deg)));
        startY = (int)(mCenterY - (innerRadius + outerWidth) * Math.cos(Math.toRadians(deg)));

        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint paint = new Paint();
        paint.setAntiAlias(true);


        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(outerWidth);
        paint.setColor(centerOuterColor);
        canvas.drawCircle(mCenterX, mCenterY, outerRadius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(centerInnerColor);
        canvas.drawCircle(mCenterX, mCenterY, innerRadius, paint);

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}