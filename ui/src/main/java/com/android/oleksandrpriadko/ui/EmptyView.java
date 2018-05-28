package com.android.oleksandrpriadko.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

public class EmptyView extends ConstraintLayout {

    private ImageView mImageViewIcon;
    private TextView mTextViewMessage;

    public EmptyView(Context context) {
        super(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        inflate(getContext(), R.layout.layout_empty_view, this);

        mImageViewIcon = findViewById(R.id.imageView_icon);
        mTextViewMessage = findViewById(R.id.textView_message);
    }

    public void setMessage(String message) {
        mTextViewMessage.setText(message);
    }

    public void setMessage(@StringRes int stringId) {
        mTextViewMessage.setText(stringId);
    }

    public void setIcon(Drawable drawable) {
        if (drawable != null) {
            mImageViewIcon.setImageDrawable(drawable);
        }
    }

    public void setIcon(@DrawableRes int drawableId) {
        mImageViewIcon.setImageResource(drawableId);
    }
}
