package com.sunbinqiang.iconcountview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
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

    private boolean mIsPraised;
    private ImageView mImageView;
    private CountView mCountView;
    private int mNormalRes;
    private int mSelectedRes;
    private OnPraiseStateChanged mPraiseStateChanged;

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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconCountView, defStyleAttr, 0);
        boolean isPraised = a.getBoolean(R.styleable.IconCountView_state, false);
        long count = a.getInt(R.styleable.IconCountView_count, 0);
        int normalRes = a.getResourceId(R.styleable.IconCountView_normalRes, R.drawable.icon_praise_normal);
        int selectedRes = a.getResourceId(R.styleable.IconCountView_selectedRes, R.drawable.icon_praise_selected);
        setCount(count);
        setIconRes(normalRes, selectedRes);
        setPraised(isPraised);
        a.recycle();

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                praiseChange(!mIsPraised);
            }
        });
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

    //初始化状态
    public void setPraised(boolean isPraised) {
        mIsPraised = isPraised;
        mImageView.setImageResource(mIsPraised ? mSelectedRes : mNormalRes);
    }

    //状态变化
    private void praiseChange(boolean isPraised) {
        mIsPraised = isPraised;
        //icon变化
        mImageView.setImageResource(isPraised ? mSelectedRes : mNormalRes);
        animImageView(isPraised);
        //数字变化
        if (isPraised) {
            mCountView.addCount();
        } else {
            mCountView.minusCount();
        }
        //接口回调
        if (mPraiseStateChanged != null) {
            mPraiseStateChanged.praise(mIsPraised);
        }
    }

    /**
     * 点赞icon动画
     * @param isPraised
     */
    private void animImageView(boolean isPraised) {
        //图片动画
        float toScale = isPraised ? 1.2f : 0.9f;
        PropertyValuesHolder propertyValuesHolderX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, toScale, 1.0f);
        PropertyValuesHolder propertyValuesHolderY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, toScale, 1.0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mImageView,
                propertyValuesHolderX, propertyValuesHolderY);
        objectAnimator.start();
    }

    /**
     * 外部回调
     * 例如：处理点赞事件的网络请求
     */
    interface OnPraiseStateChanged {
        void praise(boolean isPraised);
    }
}
