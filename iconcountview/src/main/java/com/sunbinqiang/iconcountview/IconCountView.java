package com.sunbinqiang.iconcountview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by sunbinqiang on 16/10/2017.
 */

public class IconCountView extends LinearLayout {

    private ImageView mImageView;
    private CountView mCountView;
    private int mNormalRes;
    private int mSelectedRes;
    private Rect mDrawableBounds;

    public IconCountView(Context context) {
        this(context, null);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.icon_count_view, this);
        mCountView = view.findViewById(R.id.count_view);
        mImageView = view.findViewById(R.id.image_view);
    }

    //初始化icon
    public void setIconRes(int normalRes, int selectedRes) {
        mNormalRes = normalRes;
        mSelectedRes = selectedRes;
        mImageView.setImageResource(mNormalRes);
    }

    //初始化count
    public void setCount(long count) {
        mCountView.setCount(count);
    }

    //状态变化
    public void setPraised(boolean isPraised) {
        //icon变化
        mImageView.setImageResource(isPraised ? mSelectedRes : mNormalRes);
        animImageView(isPraised);
        //数字变化
        if (isPraised) {
            mCountView.addCount();
        } else {
            mCountView.minusCount();
        }
    }

    private void animImageView(boolean isPraised) {
        //图片动画
        float toScale = isPraised ? 1.2f : 0.9f;
        PropertyValuesHolder propertyValuesHolderX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, toScale, 1.0f);
        PropertyValuesHolder propertyValuesHolderY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, toScale, 1.0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mImageView,
                propertyValuesHolderX, propertyValuesHolderY);
        objectAnimator.start();
    }
}
