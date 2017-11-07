package com.sunbinqiang.iconcountview;

import android.graphics.Canvas;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunbinqiang on 30/10/2017.
 */

public class CountPresenter implements CountConstract.Presenter {

    private static final int PADDING_WIDTH = 12;
    private static final int PADDING_HEIGHT = 60;
    private static final int PADDING_SPACE = 3;

    private long mCurCount; // 当前数量
    private long mNewCount; // 即将变化的数量
    private String mStrNewCount = "0";
    private String mZeroText = "0"; //当数字为0时显示文字
    private List<String> mCurDigitalList = new ArrayList<>(); // 当前数量各位数字的列表
    private List<String> mNewDigitalList = new ArrayList<>(); // 即将变化数量各位数字列表
    private boolean mIsSelected; //当前状态是否选中

    private CountConstract.View mView;

    public CountPresenter(CountConstract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.initAnimator();
    }

    @Override
    public void initCount(long count) {
        mCurCount = count;
        changeCount(0);
    }

    @Override
    public void updateCount() {
        mCurCount = mNewCount;
    }

    @Override
    public void setZeroText(String zeroText) {
        if (zeroText == null) {
            return;
        }
        mZeroText = zeroText;
    }

    @Override
    public void changeCount(long change) {
        //先结束当前动画
        mView.endAnimator();
        this.mNewCount = mCurCount + change;
        toDigitals(mCurCount, mCurDigitalList);
        toDigitals(mNewCount, mNewDigitalList);
        if (mNewCount > 0) {
            mStrNewCount = String.valueOf(mNewCount);
        } else {
            mStrNewCount = mZeroText;
        }
        if (mNewCount != mCurCount) {
            mView.startAnimator();
        } else {
            mView.initView();
        }
    }

    @Override
    public void draw(Canvas canvas, float initX, float initY, float unitX, float curAniValue) {
        int len = mNewDigitalList.size();
        for (int i = 0; i < len; i++) {
            String newDigital = mNewDigitalList.get(i);
            String oldDigital = "";
            if (mCurDigitalList.size() > i) {
                oldDigital = mCurDigitalList.get(i);
            }
            float x = unitX * i + initX;
            if (newDigital.equals(oldDigital)) {
                //只绘制新的数字
                mView.drawText(canvas, String.valueOf(newDigital), x, initY, mIsSelected);
            } else if (mNewCount > mCurCount) {
                //旧数字消失动画
                if (!TextUtils.isEmpty(oldDigital)) {
                    mView.drawOut(canvas, oldDigital, x, initY - (curAniValue * PADDING_HEIGHT), mIsSelected);
                }
                //新数字进入动画绘制
                mView.drawIn(canvas, newDigital, x, initY + (PADDING_HEIGHT - curAniValue * PADDING_HEIGHT), mIsSelected);
            } else {
                if (!TextUtils.isEmpty(oldDigital)) {
                    mView.drawOut(canvas, oldDigital, x, initY + (curAniValue * PADDING_HEIGHT), mIsSelected);
                }
                mView.drawIn(canvas, newDigital, x, initY - (PADDING_HEIGHT - curAniValue * PADDING_HEIGHT), mIsSelected);
            }
        }
    }

    @Override
    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    @Override
    public String getText() {
        return mStrNewCount;
    }

    @Override
    public void end() {

    }

    /**
     * 数字转为字符串列表
     * @param num
     * @param digitalList
     */
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
}
