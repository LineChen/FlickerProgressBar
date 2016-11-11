package com.beiing.flikerprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chenliu on 2016/11/11.<br/>
 * 描述：支持圆角
 * </br>
 */
public class FlikerBar extends View implements Runnable{
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private int DEFAULT_HEIGHT_DP = 35;

    private int BORDER_WIDTH_DP;

    private float MAX_PROGRESS = 100f;

    private Paint textPaint;

    private Paint bgPaint;

    private String progressText;

    private Rect textBouds;

    /**
     * 左右来回移动的滑块
     */
    private Bitmap flikerBitmap;

    /**
     * 滑块移动最左边位置，作用是控制移动
     */
    private float flickerLeft;

    /**
     * 进度条 bitmap ，包含滑块
     */
    private Bitmap pgBitmap;

    private Canvas pgCanvas;

    /**
     * 当前进度
     */
    private float progress;

    private boolean isFinish;

    private boolean isStop;

    /**
     * 下载中颜色
     */
    private int loadingColor;

    /**
     * 暂停时颜色
     */
    private int stopColor;

    /**
     * 进度文本、边框、进度条颜色
     */
    private int progressColor;

    private int textSize;

    private Thread thread;

    BitmapShader bitmapShader;

    private int radius;

    public FlikerBar(Context context) {
        this(context, null, 0);
    }

    public FlikerBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlikerBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.FlikerProgressBar);
//            textSize = (int) ta.getDimension(R.styleable.FlikerProgressBar_textSize, dp2px(12));
//            loadingColor = ta.getColor(R.styleable.FlikerProgressBar_loadingColor, Color.parseColor("#40c4ff"));
//            stopColor = ta.getColor(R.styleable.FlikerProgressBar_stopColor, Color.parseColor("#ff9800"));
//            ta.recycle();
//        }
        textSize = dp2px(12);
        loadingColor = Color.parseColor("#40c4ff");
        stopColor = Color.parseColor("#ff9800");
        radius = dp2px(10);

        BORDER_WIDTH_DP = dp2px(1);
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textBouds = new Rect();

        if(isStop){
            progressColor = stopColor;
        } else{
            progressColor = loadingColor;
        }

        flikerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flicker);
        flickerLeft = -flikerBitmap.getWidth();

        initPgBimap();
    }

    private void initPgBimap() {
        pgBitmap = Bitmap.createBitmap(getMeasuredWidth() - BORDER_WIDTH_DP, getMeasuredHeight() - BORDER_WIDTH_DP, Bitmap.Config.ARGB_8888);
        pgCanvas = new Canvas(pgBitmap);
        thread = new Thread(this);
        thread.start();
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

        if(pgBitmap == null){
            init();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //背景
        drawBackGround(canvas);

        //进度
        drawProgress(canvas);

         //进度text
        drawProgressText(canvas);

        //变色处理
        drawColorProgressText(canvas);
    }

    /**
     * 边框
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(progressColor);
        bgPaint.setStrokeWidth(BORDER_WIDTH_DP);
        canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, bgPaint);
    }

    /**
     * 进度
     */
    private void drawProgress(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStrokeWidth(0);
        bgPaint.setColor(progressColor);

        float right = (progress / MAX_PROGRESS) * getMeasuredWidth();
        pgCanvas.save(Canvas.CLIP_SAVE_FLAG);
        pgCanvas.clipRect(BORDER_WIDTH_DP, BORDER_WIDTH_DP, right - BORDER_WIDTH_DP, getMeasuredHeight() - BORDER_WIDTH_DP);
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();

        if(!isStop){
            bgPaint.setXfermode(xfermode);
            pgCanvas.drawBitmap(flikerBitmap, flickerLeft, 0, bgPaint);
            bgPaint.setXfermode(null);
        }

        bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bgPaint.setShader(bitmapShader);
        canvas.drawRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), 20, 20, bgPaint);
    }

    /**
     * 进度提示文本
     * @param canvas
     */
    private void drawProgressText(Canvas canvas) {
        textPaint.setColor(progressColor);
        progressText = getProgressText();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textBouds);
        int tWidth = textBouds.width();
        int tHeight = textBouds.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        canvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
    }

    /**
     * 变色处理
     * @param canvas
     */
    private void drawColorProgressText(Canvas canvas) {
        textPaint.setColor(Color.WHITE);
        int tWidth = textBouds.width();
        int tHeight = textBouds.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        float progressWidth = (progress / MAX_PROGRESS) * getMeasuredWidth();
        if(progressWidth > xCoordinate){
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            float right = Math.min(progressWidth, xCoordinate + tWidth * 1.1f);
            canvas.clipRect(xCoordinate, 0, right, getMeasuredHeight());
            canvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
            canvas.restore();
        }
    }

    public void setProgress(float progress){
        if(!isStop){
            this.progress = progress;
            invalidate();
        }
    }

    public float getProgress() {
        return progress;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if(isStop){
            progressColor = stopColor;
        } else {
            progressColor = loadingColor;
            thread = new Thread(this);
            thread.start();
        }
        invalidate();
    }

    public void finishLoad() {
        isFinish = true;
        setStop(true);
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void toggle(){
        if(!isFinish){
            if(isStop){
                setStop(false);
            } else {
                setStop(true);
            }
        }
    }

    @Override
    public void run() {
        int width = flikerBitmap.getWidth();
        while (!isStop){
            flickerLeft += dp2px(5);
            float progressWidth = (progress / MAX_PROGRESS) * getMeasuredWidth();
            if(flickerLeft >= progressWidth){
                flickerLeft = -width;
            }
            postInvalidate();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getProgressText() {
        String text= "";
        if(!isFinish){
            if(!isStop){
                text = "下载中" + progress + "%";
            } else {
                text = "继续";
            }
        } else{
            text = "下载完成";
        }

        return text;
    }

    /**
     * 重置
     */
    public void reset(){
        progress = 0;
        isFinish = false;
        isStop = false;
        progressColor = loadingColor;
        progressText = "";

        initPgBimap();
    }

    private int dp2px(int dp){
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
