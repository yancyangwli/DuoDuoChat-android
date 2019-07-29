package com.woniu.core.utils.signature;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.woniu.core.R;

public class ElecSignNew extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private Paint mBoderPaint;

    /** 是否是有效的签名 */
    public static boolean isValid = false;

    /** 交易特征码（水印） */
    private String waterMarkText;
    private TextPaint waterMarkPaint = null;
    /** 水印文字起始点，默认center显示 */
    private float waterMarkTextX, waterMarkTextY;
    /** 水印文字长和宽，用于计算笔迹是否划过的痕迹 */
    private float wmTextHeight, wmTextWidth;

    /** Hint文字, 点击屏幕消失 */
    private String hintText;
    private int hintTextSize;
    private TextPaint hintTextPaint;

    /**请在框内签名*/
    private String tipText;
    private int tipTextSize;
    private TextPaint tipTextPaint;

    /**水印下方提示*/
    private String tipTextUnder;
    private int tipTextUnderSize;
    private TextPaint tipTextUnderPaint;

    private Context context;
    private float density;

    // 签名文字的最小包裹范围
    private float left;
    private float right;
    private float top;
    private float bottom;
    /** 是否需要剪裁,通过setBitmap设置的签名不需要剪切 */
    private boolean isCut = true;
    /** 是否笔迹已经划过水印区 */
    private boolean isViaWaterMask = false;
    /** 是否签名 */
    private boolean isSigned = false;

    // 笔画
    private int strokeCount = 0;

    public boolean isSigned() {
        return isSigned;
    }

    public void setWaterMarkText(String waterMarkText) {
        this.waterMarkText = waterMarkText;
    }

    public boolean isViaWaterMask() {
        return isViaWaterMask;
    }

    public void settipText(String tipText) {
        this.tipText = tipText;
    }

    public ElecSignNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        strokeCount = 0;
        Display localDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        localDisplay.getMetrics(dm);
        density = dm.density;// 屏幕密度

        // 获取属性集合
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.esHintView);
        // 初始化Hint文字
        CharSequence s = a.getString(R.styleable.esHintView_esHintText);
        hintText = s == null ? "" : s.toString();
        hintTextPaint = new TextPaint();
        // 设置画笔无锯齿
        hintTextPaint.setAntiAlias(true);
        // 初始化Hint文字颜色
        hintTextPaint.setColor(a.getColor(R.styleable.esHintView_esHintTextColor, 0xFFFEFEFE));
        // 初始化Hint尺寸
        hintTextSize = a.getDimensionPixelOffset(R.styleable.esHintView_esHintTextSize, 40);
        if (hintTextSize > 0) {
            hintTextPaint.setTextSize(hintTextSize);
        } else {
            hintTextPaint.setTextSize(40);
        }

        // 初始化水印文字
        CharSequence cs = a.getString(R.styleable.esHintView_esText);
        waterMarkText = cs == null ? "" : cs.toString();
        waterMarkPaint = new TextPaint();
        // 设置画笔无锯齿
        waterMarkPaint.setAntiAlias(true);
        // 初始化水印文字颜色
        waterMarkPaint.setColor(a.getColor(R.styleable.esHintView_esTextColor, 0xE5E5E5));
        // 初始化水印尺寸
        int textSize = a.getDimensionPixelOffset(R.styleable.esHintView_esTextSize, 20);
        if (textSize > 0) {
            waterMarkPaint.setTextSize(textSize);
        } else {
            waterMarkPaint.setTextSize(20);
        }

        //初始化tip(请在框内签名)
        CharSequence tipCS = a.getString(R.styleable.esHintView_esTipText);
        tipText = tipCS == null ? "" : tipCS.toString();
        tipTextPaint = new TextPaint();
        // 设置画笔无锯齿
        tipTextPaint.setAntiAlias(true);
        // 初始化tip文字颜色
        tipTextPaint.setColor(a.getColor(R.styleable.esHintView_esTipTextColor, 0xCCCCCC));
        // 初始化tip尺寸
        tipTextSize = a.getDimensionPixelOffset(R.styleable.esHintView_esTipTextSize, 20);
        if (tipTextSize > 0) {
            tipTextPaint.setTextSize(tipTextSize);
        } else {
            tipTextPaint.setTextSize(20);
        }

        //初始化tip(下方提示)
        CharSequence tipCSUnder = a.getString(R.styleable.esHintView_esTipTextUnder);
        tipTextUnder = tipCSUnder == null ? "" : tipCSUnder.toString();
        tipTextUnderPaint = new TextPaint();
        // 设置画笔无锯齿
        tipTextUnderPaint.setAntiAlias(true);
        // 初始化tip文字颜色
        tipTextUnderPaint.setColor(a.getColor(R.styleable.esHintView_esTipTextUnderColor, 0xCCCCCC));
        // 初始化tip尺寸
        tipTextUnderSize = a.getDimensionPixelOffset(R.styleable.esHintView_esTipTextSize, 20);
        if (tipTextUnderSize > 0) {
            tipTextUnderPaint.setTextSize(tipTextSize);
        } else {
            tipTextUnderPaint.setTextSize(20);
        }

        // Give back a previously retrieved StyledAttributes, for later re-use.
        a.recycle();
    }

    public ElecSignNew(Context context) {
        super(context);
        this.context = context;
        strokeCount = 0;
    }

    private void initCanvasPaint(Context c) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.black));
        mPaint.setStyle(Paint.Style.STROKE);// 线条实心风格（STROKE空心风格）
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mPaint.setStrokeWidth(5);

        mBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawARGB(0, 0, 0, 0);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        // 初始化水印位置
        Rect waterMarkTextBounds = new Rect();
        waterMarkPaint.getTextBounds(waterMarkText, 0, waterMarkText.length(), waterMarkTextBounds);

        wmTextHeight = waterMarkTextBounds.height();
        wmTextWidth = waterMarkTextBounds.width();
        waterMarkTextX = (viewWidth - wmTextWidth) / 2;
        waterMarkTextY = viewHeight / 2;

        // Hint文字
        if (!TextUtils.isEmpty(hintText)) {
            // 初始化Hint位置
            Rect hintTextBounds = new Rect();
            hintTextPaint.getTextBounds(hintText, 0, hintText.length(), hintTextBounds);
            // viewHeight490,viewWidth720
            float hintTextX = (viewWidth - hintTextBounds.width()) / 2;
            float hintTextY = viewHeight / 2;

            mCanvas.drawText(hintText, hintTextX, hintTextY, hintTextPaint);
        } else {
            if (!TextUtils.isEmpty(waterMarkText)) {
                mCanvas.drawText(waterMarkText, waterMarkTextX, waterMarkTextY, waterMarkPaint);
                //请在框内签名
                if(!TextUtils.isEmpty(tipText)) {
                    // 初始化tip位置
                    Rect tipTextBounds = new Rect();
                    tipTextPaint.getTextBounds(tipText, 0, tipText.length(), tipTextBounds);
                    float tipX = (viewWidth - tipTextBounds.width()) / 2;
                    //float tipY = viewHeight / 2 - wmTextHeight - 20;
                    float tipY = dp2px(context,40);
                    mCanvas.drawText(tipText, tipX, tipY, tipTextPaint);
                }
                //下方提示
                if(!TextUtils.isEmpty(tipTextUnder)) {
                    // 初始化tip位置
                    Rect tipTextBounds = new Rect();
                    tipTextUnderPaint.getTextBounds(tipTextUnder, 0, tipTextUnder.length(), tipTextBounds);
                    float tipX = (viewWidth - tipTextBounds.width()) / 2;
                    float tipY = viewHeight / 2 + wmTextHeight - 80;
                    mCanvas.drawText(tipTextUnder, tipX, tipY, tipTextUnderPaint);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            // 之所以放在这里初始化，因为bitmap需要onMeasure测出的宽和高
            initCanvasPaint(context);
        }

        DashPathEffect effects = new DashPathEffect(new float[]{15,15,15,15},1);//设置虚线的间隔和点的长度
        mBoderPaint = new Paint();
        mBoderPaint.setPathEffect(effects);
        mBoderPaint.setAntiAlias(true);
        mBoderPaint.setDither(true);
        mBoderPaint.setColor(getResources().getColor(R.color.msg_center_content_div_color));
        mBoderPaint.setStyle(Paint.Style.STROKE);// 线条实心风格（STROKE空心风格）
        mBoderPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mBoderPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mBoderPaint.setStrokeWidth(5);
        mCanvas.drawRect(0,0,viewWidth,viewHeight,mBoderPaint);


        // 画布的背景色
        canvas.drawColor(0xFFFFFFFF);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private boolean isInitStatus = true;

    private void touch_start(float x, float y) {
        if (isInitStatus) {
            isInitStatus = false;
            clearCanvas();
        }
        // Log.d("lefu", "x:" + x + ",y:" + y) ;
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        // Log.d("lefu", "x:" + x + ",y:" + y) ;
        // 定位笔迹的最小闭包
        filterWarpContent(x, y);
        // 判断笔迹是否划过水印区
        judgeIsViaWaterText(x, y);
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        isSigned = true;
    }

    /**
     * 判断笔迹是否划过水印区
     */
    private void judgeIsViaWaterText(float x, float y) {
        if (waterMarkTextX < x && x < wmTextWidth + waterMarkTextX) {
            if (waterMarkTextY < y && y < waterMarkTextY + wmTextHeight) {
                isViaWaterMask = true;
            }
        }
    }

    /**
     * 动态计算签名的包裹范围
     */
    private void filterWarpContent(float x, float y) {
        left = left == 0 ? x : (left > x ? x : left);
        right = right == 0 ? x : (right < x ? x : right);
        top = top == 0 ? y : (top > y ? y : top);
        bottom = bottom == 0 ? y : (bottom < y ? y : bottom);
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();

    }

    private float lastX, lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(x - lastX) > 80 || Math.abs(y - lastY) > 80) {
                    strokeCount++;
                }
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    private int viewWidth;
    private int viewHeight;

    public int getViewHeight() {
        return this.viewHeight;
    }

    public int getViewWidth() {
        return this.viewWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = measureWidth(widthMeasureSpec);
        viewHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = super.getWidth();
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = super.getHeight();
        }
        return result;
    }

    /**
     * 把View的对象转换成bitmap
     *
     * @param isCut
     *            需要最小包含文字，剪切，否则不剪切
     * @return
     */
    public Bitmap getViewBitmap(boolean isCut) {
        clearFocus();
        setPressed(false);

        // 能画缓存就返回false
        boolean willNotCache = willNotCacheDrawing();
        setWillNotCacheDrawing(false);
        int color = getDrawingCacheBackgroundColor();
        setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            destroyDrawingCache();
        }
        buildDrawingCache();
        Bitmap cacheBitmap = getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = null;
        if (isCut && this.isCut) {
            // 剪切签名的最小闭包范围，并四周扩大5个像素点作为边缘,但是不能超过边界
            int x = (int) left - 5;
            x = x > 0 ? x : 0;
            x = x > viewWidth ? viewWidth : x;
            int y = (int) top - 5;
            y = y > 0 ? y : 0;
            y = y > viewHeight ? viewHeight : y;
            int width = (int) (right - left) + 10;
            width = (width + x) > viewWidth ? (viewWidth - x) : width;
            int height = (int) (bottom - top) + 10;
            height = (height + y) > viewHeight ? (viewHeight - y) : height;

            bitmap = Bitmap.createBitmap(cacheBitmap, x, y, width, height);
        } else {
            bitmap = Bitmap.createBitmap(cacheBitmap);
        }
        // Restore the view
        destroyDrawingCache();
        setWillNotCacheDrawing(willNotCache);
        setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    public void clearCanvas() {

        isSigned = false;
        isCut = true;
        isViaWaterMask = false;

        left = 0;
        top = 0;
        right = 0;
        bottom = 0;

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        if (!TextUtils.isEmpty(waterMarkText)) {
            mCanvas.drawText(waterMarkText, waterMarkTextX, waterMarkTextY, waterMarkPaint);
        }

        invalidate();
    }

    public void setBitmap(Bitmap bitmap, int viewWidth, int viewHeight) {
        clearCanvas();
        isCut = false;
        if (bitmap != null) {
            isSigned = true;
            isViaWaterMask = true;
            mCanvas.drawBitmap(bitmap, null, new Rect(0, 0, viewWidth, viewHeight), null);
        }
    }

    public void setStrokeCount(int strokeCount) {
        this.strokeCount = strokeCount;
    }

    public int getStrokeCount() {
        return strokeCount;
    }

    public static int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }
}
