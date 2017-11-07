package com.sunbinqiang.iconcountview;

import android.graphics.Canvas;

/**
 * Created by sunbinqiang on 30/10/2017.
 */

public interface CountConstract {

    interface Presenter {

        void start();

        /**
         * 初始化数值
         * @param count
         */
        void initCount(long count);

        /**
         * 动画结束， 更新当前数值
         */
        void updateCount();

        /**
         * 设置当数字为0时， 相应的文字
         * @param zeroText
         */
        void setZeroText(String zeroText);

        /**
         * 数字发生变化
         * @param change
         */
        void changeCount(long change);

        /**
         * 绘制
         * @param canvas
         */
        void draw(Canvas canvas, float initX, float initY, float unitX, float curAniValue);

        /**
         *
         * @param isSelected
         */
        void setIsSelected(boolean isSelected);

        String getText();

        void end();

    }

    interface View {

        void setPresenter(Presenter presenter);

        void initView();

        void initAnimator();

        void startAnimator();

        void endAnimator();

        void drawText(Canvas canvas, String strDigital, float x, float y, boolean isSelected);

        void drawIn(Canvas canvas, String digital, float x, float y, boolean isSelected);

        void drawOut(Canvas canvas, String digital, float x, float y, boolean isSelected);

    }

}
