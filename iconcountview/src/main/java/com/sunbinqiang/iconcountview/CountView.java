package com.sunbinqiang.iconcountview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
    private final int DEFAULT_TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.text_normal_size);

    private long mCurCount; // 当前数量
    private long mNewCount; // 即将变化的数量
    private String mStrNewCount;
    private List<String> mCurDigitalList = new ArrayList<>(); // 当前数量各位数字的列表
    private List<String> mNewDigitalList = new ArrayList<>(); // 即将变化数量各位数字列表
    private ValueAnimator mObjectAnimator;
    private float mCurAniValue;    //当前属性动画数值

    private Rect mRect = new Rect(); // 当前文字的区域
    private Rect mDigitalRect = new Rect(); // 单个数字的区域

    private String mZeroText; //当数字为0时显示文字

    private Paint mTextNormalPaint;
    private Paint mTextSelectedPaint;

    private boolean mIsSelected; //当前状态是否选中



    public CountView(Context context) {
        this(context, null);
    }

    public CountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextNormalPaint = new Paint();
        mTextSelectedPaint = new Paint();
        mTextNormalPaint.setColor(getResources().getColor(R.color.text_gray));
        mTextNormalPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextNormalPaint.setStyle(Paint.Style.FILL);
        mTextNormalPaint.setAntiAlias(true);
        mTextSelectedPaint.setColor(getResources().getColor(R.color.text_gray));
        mTextSelectedPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextSelectedPaint.setStyle(Paint.Style.FILL);
        mTextSelectedPaint.setAntiAlias(true);
        mTextNormalPaint.getTextBounds("0", 0, 1, mDigitalRect);
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
     *
     * @param count
     */
    public void setCount(long count) {
        mCurCount = count;
        changeCount(0);
    }



    /**
     * 设置数字为0时的文本
     * @param zeroText
     */
    public void setZeroText(String zeroText) {
        mZeroText = zeroText;
    }

    public void setTextNormalColor(int normalColor) {
        mTextNormalPaint.setColor(normalColor);
    }

    public void setTextSelectedColor(int selectedColor) {
        mTextSelectedPaint.setColor(selectedColor);
    }

    public void setTextSize(int textSize) {
        mTextNormalPaint.setTextSize(textSize);
        mTextSelectedPaint.setTextSize(textSize);
    }

    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    /**
     * +1
     */
    public void addCount() {
        changeCount(1);
        mIsSelected = true;
    }

    /**
     * -1
     */
    public void minusCount() {
        changeCount(-1);
        mIsSelected = false;
    }

    /**
     * 数量发生变化
     *
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
        if (mNewCount > 0) {
            mStrNewCount = String.valueOf(mNewCount);
        } else {
            mStrNewCount = mZeroText;
        }
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
        for (int i = 0; i < len; i++) {
            String newDigital = mNewDigitalList.get(i);
            String oldDigital = "";
            if (mCurDigitalList.size() > i) {
                oldDigital = mCurDigitalList.get(i);
            }
            float x = (mDigitalRect.width() + PADDING_SPACE) * i;
            if (newDigital.equals(oldDigital)) {
                //只绘制新的数字
                canvas.drawText(String.valueOf(newDigital), x, y, getCurPaint(mIsSelected));
            } else if (mNewCount > mCurCount) {
                //旧数字消失动画
                if (!TextUtils.isEmpty(oldDigital)) {
                    drawOut(canvas, oldDigital, x, y - (mCurAniValue * PADDING_HEIGHT));
                }
                //新数字进入动画绘制
                drawIn(canvas, newDigital, x, y + (PADDING_HEIGHT - mCurAniValue * PADDING_HEIGHT));
            } else {
                if (!TextUtils.isEmpty(oldDigital)) {
                    drawOut(canvas, oldDigital, x, y + (mCurAniValue * PADDING_HEIGHT));
                }
                drawIn(canvas, newDigital, x, y - (PADDING_HEIGHT - mCurAniValue * PADDING_HEIGHT));
            }
        }
    }

    /**
     * @param canvas
     * @param digital
     * @param x
     * @param y
     */
    private void drawIn(Canvas canvas, String digital, float x, float y) {
        Paint inPaint = getCurPaint(mIsSelected);
        inPaint.setAlpha((int) (mCurAniValue * 255));
        inPaint.setTextSize(DEFAULT_TEXT_SIZE * (mCurAniValue * 0.5f + 0.5f));
        canvas.drawText(digital, x, y, inPaint);
        inPaint.setAlpha(255);
        inPaint.setTextSize(DEFAULT_TEXT_SIZE);
    }

    /**
     * @param canvas
     * @param digital
     * @param x
     * @param y
     */
    private void drawOut(Canvas canvas, String digital, float x, float y) {
        Paint outPaint = getCurPaint(!mIsSelected);
        outPaint.setAlpha(255 - (int) (mCurAniValue * 255));
        outPaint.setTextSize(DEFAULT_TEXT_SIZE * (1.0f - mCurAniValue * 0.5f));
        canvas.drawText(digital, x, y, outPaint);
        outPaint.setAlpha(255);
        outPaint.setTextSize(DEFAULT_TEXT_SIZE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRect.setEmpty();
        mTextNormalPaint.getTextBounds(mStrNewCount, 0, mStrNewCount.length(), mRect);
        int textWidth = mRect.width() + PADDING_WIDTH * 2;
        int textHeight = mRect.height() + PADDING_HEIGHT * 2;
        final int dw = resolveSizeAndState(textWidth, widthMeasureSpec, 0);
        final int dh = resolveSizeAndState(textHeight, heightMeasureSpec, 0);
        setMeasuredDimension(dw, dh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        changeCount(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mObjectAnimator.end();
    }

    private void toDigitals(long num, List<String> digitalList) {
        digitalList.clear();
        if (num == 0) {
            digitalList.add(mZeroText);
        }
        while (num > 0) {
            digitalList.add(0, String.valueOf(num % 10));
            num = num / 10;
        }
    }

    private Paint getCurPaint(boolean isSelected) {
        return isSelected ? mTextSelectedPaint : mTextNormalPaint;
    }
}
