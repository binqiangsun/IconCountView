package com.sunbinqiang.iconcountview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunbinqiang on 15/10/2017.
 */

public class CountView extends View {

    private static final int PADDING_WIDTH = 12;
    private static final int PADDING_HEIGHT = 60;
    private static final int PADDING_SPACE = 3;

    private long mCurCount; // 当前数量
    private long mNewCount; // 即将变化的数量
    private String mStrNewCount;
    private List<Integer> mCurDigitalList = new ArrayList<>(); // 当前数量各位数字的列表
    private List<Integer> mNewDigitalList = new ArrayList<>(); // 即将变化数量各位数字列表
    private ValueAnimator mObjectAnimator;
    private float mCurAniValue;    //当前属性动画数值

    private Rect mRect = new Rect(); // 当前文字的区域
    private Rect mDigitalRect = new Rect(); // 单个数字的区域

    private Paint mTextPaint;
    private float mTextSize = 36;


    public CountView(Context context) {
        this(context, null);
    }

    public CountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint = new Paint();
        mTextPaint.setColor(getResources().getColor(R.color.text_gray));
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.getTextBounds("0", 0, 1, mDigitalRect);
        initAnimator();
        postInvalidate();
    }

    private void initAnimator() {
        mObjectAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mObjectAnimator.setDuration(500);
        mObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAniValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mObjectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //动画结束， 数值更新
                mCurCount = mNewCount;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * initial mCurCount
     * @param count
     */
    public void setCount(long count) {
        mCurCount = count;
        changeCount(0);
    }

    /**
     * +1
     */
    public void addCount() {
        changeCount(1);
    }

    /**
     * -1
     */
    public void minusCount() {
        changeCount(-1);
    }

    /**
     * 数量发生变化
     * @param change
     */
    private void changeCount(long change) {
        //先结束当前动画
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
        }
        this.mNewCount = mCurCount + change;
        toDigitals(mCurCount, mCurDigitalList);
        toDigitals(mNewCount, mNewDigitalList);
        mStrNewCount = String.valueOf(mNewCount);
        if (mObjectAnimator != null && mNewCount != mCurCount) {
            mObjectAnimator.start();
        } else {
            //初始化调用该方法， 重新布局
            requestLayout();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int len = mNewDigitalList.size();
        float y = PADDING_HEIGHT + mDigitalRect.height();
        for (int i = 0; i < len; i ++) {
            int newDigital = mNewDigitalList.get(i);
            int oldDigital = -1;
            if (mCurDigitalList.size() > i) {
                oldDigital = mCurDigitalList.get(i);
            }
            float x = (mDigitalRect.width() + PADDING_SPACE) * i;
            if (newDigital == oldDigital){
                //只绘制新的数字
                canvas.drawText(String.valueOf(newDigital), x, y, mTextPaint);
            } else if (mNewCount > mCurCount) {
                //旧数字消失动画
                if (oldDigital != -1) {
                    drawOut(canvas, oldDigital, x, y - (mCurAniValue * PADDING_HEIGHT));
                }
                //新数字进入动画绘制
                drawIn(canvas, newDigital, x, y + (PADDING_HEIGHT - mCurAniValue * PADDING_HEIGHT));
            } else {
                if (oldDigital != -1) {
                    drawOut(canvas, oldDigital, x, y + (mCurAniValue * PADDING_HEIGHT));
                }
                drawIn(canvas, newDigital, x, y - (PADDING_HEIGHT - mCurAniValue * PADDING_HEIGHT));
            }
        }
    }

    /**
     *
     * @param canvas
     * @param digital
     * @param x
     * @param y
     */
    private void drawIn(Canvas canvas, int digital, float x, float y) {
        mTextPaint.setAlpha((int)(mCurAniValue * 255));
        mTextPaint.setTextSize(mTextSize * (mCurAniValue * 0.5f + 0.5f));
        canvas.drawText(String.valueOf(digital), x, y, mTextPaint);
        mTextPaint.setAlpha(255);
        mTextPaint.setTextSize(mTextSize);
    }

    /**
     *
     * @param canvas
     * @param digital
     * @param x
     * @param y
     */
    private void drawOut(Canvas canvas, int digital, float x, float y) {
        mTextPaint.setAlpha(255 - (int)(mCurAniValue * 255));
        mTextPaint.setTextSize(mTextSize * (1.0f - mCurAniValue * 0.5f));
        canvas.drawText(String.valueOf(digital), x, y, mTextPaint);
        mTextPaint.setAlpha(255);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRect.setEmpty();
        mTextPaint.getTextBounds(mStrNewCount, 0, mStrNewCount.length(), mRect);
        int textWidth = mRect.width() + PADDING_WIDTH * 2;
        int textHeight = mRect.height() + PADDING_HEIGHT * 2;
        final int dw = resolveSizeAndState(textWidth, widthMeasureSpec, 0);
        final int dh = resolveSizeAndState(textHeight, heightMeasureSpec, 0);
        setMeasuredDimension(dw, dh);
    }

    private void toDigitals(long num, List<Integer> digitalList) {
        digitalList.clear();
        if (num == 0) {
            digitalList.add(0);
        }
        while (num > 0) {
            digitalList.add(0, (int) (num % 10));
            num = num / 10;
        }
    }


    public void onDestroy() {
        if (mObjectAnimator != null) {
            mObjectAnimator.end();
        }
    }
}
