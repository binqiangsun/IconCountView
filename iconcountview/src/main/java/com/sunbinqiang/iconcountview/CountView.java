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

/**
 * Created by sunbinqiang on 15/10/2017.
 */

public class CountView extends View implements CountConstract.View {

    private static final int PADDING_WIDTH = 12;
    private static final int PADDING_HEIGHT = 60;
    private static final int PADDING_SPACE = 3;
    private final int DEFAULT_TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.text_normal_size);

    private CountConstract.Presenter mPresenter;

    private ValueAnimator mObjectAnimator;
    private float mCurAniValue;    //当前属性动画数值

    private Rect mRect = new Rect(); // 当前文字的区域
    private Rect mDigitalRect = new Rect(); // 单个数字的区域

    private Paint mTextNormalPaint;
    private Paint mTextSelectedPaint;

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
        new CountPresenter(this);
        mPresenter.start();
    }

    @Override
    public void setPresenter(CountConstract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initView() {
        requestLayout();
    }

    @Override
    public void initAnimator() {
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
                mPresenter.updateCount();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void startAnimator() {
        if (mObjectAnimator != null) {
            mObjectAnimator.start();
        }
    }

    @Override
    public void endAnimator() {
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
        }
    }

    @Override
    public void drawText(Canvas canvas, String strDigital, float x, float y, boolean isSelected) {
        canvas.drawText(strDigital, x, y, getCurPaint(isSelected));
    }

    /**
     * initial mCurCount
     *
     * @param count
     */
    public void setCount(long count) {
        mPresenter.initCount(count);
    }

    /**
     * 设置数字为0时的文本
     * @param zeroText
     */
    public void setZeroText(String zeroText) {
        mPresenter.setZeroText(zeroText);
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
        mPresenter.setIsSelected(isSelected);
    }

    /**
     * +1
     */
    public void addCount() {
        mPresenter.changeCount(1);
    }

    /**
     * -1
     */
    public void minusCount() {
        mPresenter.changeCount(-1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float y = PADDING_HEIGHT + mDigitalRect.height();
        mPresenter.draw(canvas, 0.0f, y, mDigitalRect.width() + PADDING_SPACE, mCurAniValue);
    }

    /**
     * @param canvas
     * @param digital
     * @param x
     * @param y
     */
    @Override
    public void drawIn(Canvas canvas, String digital, float x, float y, boolean isSelected) {
        Paint inPaint = getCurPaint(isSelected);
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
    @Override
    public void drawOut(Canvas canvas, String digital, float x, float y, boolean isSelected) {
        Paint outPaint = getCurPaint(!isSelected);
        outPaint.setAlpha(255 - (int) (mCurAniValue * 255));
        outPaint.setTextSize(DEFAULT_TEXT_SIZE * (1.0f - mCurAniValue * 0.5f));
        canvas.drawText(digital, x, y, outPaint);
        outPaint.setAlpha(255);
        outPaint.setTextSize(DEFAULT_TEXT_SIZE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRect.setEmpty();
        mTextNormalPaint.getTextBounds(mPresenter.getText(), 0, mPresenter.getText().length(), mRect);
        int textWidth = mRect.width() + PADDING_WIDTH * 2;
        int textHeight = mRect.height() + PADDING_HEIGHT * 2;
        final int dw = resolveSizeAndState(textWidth, widthMeasureSpec, 0);
        final int dh = resolveSizeAndState(textHeight, heightMeasureSpec, 0);
        setMeasuredDimension(dw, dh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mObjectAnimator.end();
    }



    private Paint getCurPaint(boolean isSelected) {
        return isSelected ? mTextSelectedPaint : mTextNormalPaint;
    }
}
