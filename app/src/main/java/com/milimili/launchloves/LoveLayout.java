package com.milimili.launchloves;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import java.util.Random;

public class LoveLayout extends RelativeLayout {

    private Random random = new Random();
    private int mLayoutWidth;
    private int mLayoutHeight;
    private int[] mLovePictures;
    private int mLoveWidth;
    private int mLoveHeight;

    public LoveLayout(Context context) {
        super(context);
    }

    public LoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public LoveLayout initLovePicture(int[] lovePictures) {
        this.mLovePictures = lovePictures;
        int picRes = lovePictures[0];
        Drawable d = ContextCompat.getDrawable(getContext(), picRes);
        mLoveWidth = d.getIntrinsicWidth();
        mLoveHeight = d.getIntrinsicHeight();
        return this;
    }

    public ImageView placeLoveImage() {
        int lovePicture = mLovePictures[random.nextInt(mLovePictures.length - 1)];
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(lovePicture);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addView(imageView, lp);
        return imageView;
    }

    public void launchLove() {
        ImageView imageView = placeLoveImage();
        AnimatorSet animatorSet = getAnimatorSet(imageView);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(imageView);
            }
        });
        animatorSet.start();
    }

    private ObjectAnimator getScaleXAnimator(ImageView v) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 0, 1);
        scaleX.setDuration(1000);
        return scaleX;
    }

    private ObjectAnimator getScaleYAnimator(ImageView v) {
        return ObjectAnimator.ofFloat(v, "scaleY", 0, 1);
    }

    private ObjectAnimator getAlphaAnimator(ImageView v) {
        return ObjectAnimator.ofFloat(v, "alpha", 0, 1);
    }

    private ValueAnimator getBezierTypeEvaluator(ImageView v) {
        PointF point0 = new PointF(mLayoutWidth / 2f - mLoveWidth / 2f, mLayoutHeight - mLoveHeight);
        PointF point1 = new PointF(random.nextInt(mLayoutWidth - mLoveWidth), random.nextInt(mLayoutHeight / 2) + mLayoutHeight / 2f);
        PointF point2 = new PointF(random.nextInt(mLayoutWidth - mLoveWidth), random.nextInt(mLayoutHeight/2));
        PointF point3 = new PointF(random.nextInt(mLayoutWidth - mLoveWidth), 0);
        BezierTypeEvaluator b = new BezierTypeEvaluator(point1, point2);
        ValueAnimator bezierAnimator = ValueAnimator.ofObject(b, point0, point3);
        bezierAnimator.addUpdateListener(animation -> {
            PointF animatedValue = (PointF) animation.getAnimatedValue();
            v.setX(animatedValue.x);
            v.setY(animatedValue.y);

            v.setAlpha((float) (1 - animation.getAnimatedFraction() + 0.2));
        });
        bezierAnimator.setDuration(3000);
        return bezierAnimator;
    }

    private AnimatorSet getAnimatorSet(ImageView v) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.play(getScaleXAnimator(v)).with(getScaleYAnimator(v))
                .with(getAlphaAnimator(v))
                .before(getBezierTypeEvaluator(v));
        return animatorSet;
    }
}
