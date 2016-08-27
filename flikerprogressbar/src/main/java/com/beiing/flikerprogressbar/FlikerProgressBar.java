package com.beiing.flikerprogressbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chenliu on 2016/8/26.<br/>
 * 描述：
 * </br>
 */
public class FlikerProgressBar extends View {
    private final PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    private int DEFAULT_HEIGHT_DP = 40;

    private Paint textPaint;

    private Paint bgPaint;

    private Canvas newCanvas;

    private Bitmap newBitmap;


    public FlikerProgressBar(Context context) {
        this(context, null, 0);
    }

    public FlikerProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlikerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initTextPaint();

        initBgPaint();
    }

    private void initBgPaint() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(dp2px(1));
        bgPaint.setColor(Color.BLUE);
    }

    private void initTextPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(dp2px(16));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode){
            case MeasureSpec.AT_MOST:
                height = (int) dp2px(DEFAULT_HEIGHT_DP);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        initNew();

    }

    private void initNew() {
        newBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        newCanvas = new Canvas(newBitmap);
        newCanvas.drawText("正在加载正在加载正在加载正在加载正在加载正在加载", 0, getHeight(), textPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

        drawProgress();

        canvas.drawBitmap(newBitmap, 0, 0, null);
    }

    private void drawProgress() {
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        newCanvas.drawRect(0, 0 , dp2px(100), getHeight(), textPaint);
    }


    private float dp2px(int dp){
        float density = getContext().getResources().getDisplayMetrics().density;
        return dp * density;
    }
}













