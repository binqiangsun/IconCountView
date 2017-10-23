package com.sunbinqiang.iconcountview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static com.sunbinqiang.iconcountview.R.attr.count;

/**
 * Created by sunbinqiang on 16/10/2017.
 */

public class IconCountView extends LinearLayout {

    private boolean mIsSelected;
    private ImageView mImageView;
    private CountView mCountView;
    private int mNormalRes;
    private int mSelectedRes;
    private OnSelectedStateChangedListener mSelectedStateChanged;

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
        boolean isSelected = a.getBoolean(R.styleable.IconCountView_state, false);
        int normalRes = a.getResourceId(R.styleable.IconCountView_normalRes, R.drawable.icon_praise_normal);
        int selectedRes = a.getResourceId(R.styleable.IconCountView_selectedRes, R.drawable.icon_praise_selected);
        long count = a.getInt(R.styleable.IconCountView_count, 0);
        String zeroText = a.getString(R.styleable.IconCountView_zeroText);
        int textNormalColor = a.getColor(R.styleable.IconCountView_textNormalColor, getResources().getColor(R.color.text_gray));
        int textSelectedColor = a.getColor(R.styleable.IconCountView_textSelectedColor, getResources().getColor(R.color.text_gray));
        int textSize = a.getDimensionPixelSize(R.styleable.IconCountView_textSize, getResources().getDimensionPixelOffset(R.dimen.text_normal_size));
        a.recycle();

        //设置初始状态
        setIconRes(normalRes, selectedRes);
        initCountView(zeroText, count, textNormalColor, textSelectedColor, textSize, isSelected);
        setSelected(isSelected);


        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                praiseChange(!mIsSelected);
            }
        });
    }

    //初始化icon
    public void setIconRes(int normalRes, int selectedRes) {
        mNormalRes = normalRes;
        mSelectedRes = selectedRes;
        mImageView.setImageResource(mNormalRes);
    }

    //初始化countView
    private void initCountView(String zeroText,
                               long count,
                               int textNormalColor,
                               int textSelectedColor,
                               int textSize,
                               boolean isSelected) {
        mCountView.setZeroText(zeroText);
        mCountView.setCount(count);
        mCountView.setTextNormalColor(textNormalColor);
        mCountView.setTextSelectedColor(textSelectedColor);
        mCountView.setTextSize(textSize);
        mCountView.setIsSelected(isSelected);
    }

    //初始化数量
    public void setCount(long count) {
        mCountView.setCount(count);
    }

    //初始化数量为0时的文字
    public void setZeroText(String zeroText) {
        mCountView.setZeroText(zeroText);
    }

    //初始化状态
    public void setState(boolean isSelected) {
        mIsSelected = isSelected;
        mImageView.setImageResource(mIsSelected ? mSelectedRes : mNormalRes);
    }

    /**
     * 设置回调接口
     * @param onStateChangedListener
     */
    public void setOnStateChangedListener (OnSelectedStateChangedListener onStateChangedListener) {
        mSelectedStateChanged = onStateChangedListener;
    }

    //状态变化
    private void praiseChange(boolean isPraised) {
        mIsSelected = isPraised;
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
        if (mSelectedStateChanged != null) {
            mSelectedStateChanged.select(mIsSelected);
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
    interface OnSelectedStateChangedListener {
        void select(boolean isSelected);
    }
}
