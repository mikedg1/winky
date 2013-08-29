package com.mikedg.android.glass.winky;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;

public class OverscrollView extends ScrollView {
    private static final long FADE_ANIMATION_DURATION_MILLIS = 150L;
    private static final float FADE_OUT_ALPHA = 0.33F;
    private static final float OVERSCROLL_AMOUNT = 0.49F;
    private float item; //The currently seelcted item
    private ValueAnimator itemSnapAnimator = new ValueAnimator();
    private OverscrollViewListener listener;
    private boolean shouldHighlightSelectedItem;
    private boolean shouldOverscroll;

    public OverscrollView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.itemSnapAnimator.setDuration(250L);
        this.itemSnapAnimator.setInterpolator(new DecelerateInterpolator());
        this.itemSnapAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                OverscrollView.this.setItem(((Float) paramAnonymousValueAnimator.getAnimatedValue())
                        .floatValue());
            }
        });
    }

    private float getItemHeight() {
        if (getNumberOfItems() == 0)
            return 0.0F;
        return ((ViewGroup) getChildAt(0)).getHeight() / getNumberOfItems();
    }

    private int getMaxScrollY() {
        return getChildAt(0).getHeight() - getHeight();
    }

    private int getNumberOfItems() {
        if (getChildCount() == 0)
            return 0;
        return ((ViewGroup) getChildAt(0)).getChildCount();
    }

    private boolean hasItems() {
        return getNumberOfItems() > 0;
    }
    
    private boolean isOverscrolledDown() {
        return this.item > -1 + getNumberOfItems();
    }

    private boolean isOverscrolledUp() {
        return this.item < 0.0F;
    }
//
//    //Appears to set an item in the child right?
    private void setItem(float paramFloat) {
//        int i = getItem();
//        this.item = paramFloat;
//        int j = getItem();
//        float f = this.item * getItemHeight();
//        if (f < 0.0F) {
//            scrollTo(0, 0);
//            translateChildY(-f);
//        }
//        while (true) {
//            if ((j != i) && (this.listener != null))
//                this.listener.onSelectedItemChanged(j);
//            if ((this.shouldHighlightSelectedItem) && (j != i)) {
//                ViewGroup localViewGroup = (ViewGroup) getChildAt(0);
//                localViewGroup.getChildAt(i).setSelected(false);
//                localViewGroup.getChildAt(j).setSelected(true);
//            }
//            return;
//            if (getMaxScrollY() < 0) {
//                scrollTo(0, 0);
//                translateChildY(-f);
//            } else if (f > getMaxScrollY()) {
//                scrollTo(0, getMaxScrollY());
//                translateChildY(-(f - getMaxScrollY()));
//            } else {
//                translateChildY(0.0F);
//                scrollTo(0, (int) f);
//            }
//        }
    }

    private void translateChildY(float paramFloat) {
        getChildAt(0).setTranslationY(paramFloat);
    }

    public int getItem() {
        return Math.round(this.item);
    }

    public boolean isOverscrolled() {
        return (isOverscrolledUp()) || (isOverscrolledDown());
    }

    public void scrollByItem(float paramFloat) {
        //What does this appear to be trying
//        if (!hasItems())
//            ;
//        while (this.itemSnapAnimator.isRunning())
//            return;
//        float f;
//        if (this.item < 0.0F)
//            f = -this.item;
//        while (true) {
//            scrollToItem(paramFloat * (1.0F - f / 0.49F) + this.item);
//            return;
//            boolean bool = this.item < -1 + getNumberOfItems();
//            f = 0.0F;
//            if (bool)
//                f = this.item - (-1 + getNumberOfItems());
//        }
        
        //*****THE DUPE CODE TO CHANGE
//        if (!hasItems())
//            ;
//        while (this.itemSnapAnimator.isRunning())
//            return;
//        float f;
//        if (this.item < 0.0F)
//            f = -this.item;
//        while (true) {
//            scrollToItem(paramFloat * (1.0F - f / 0.49F) + this.item);
//            return;
//            boolean bool = this.item < -1 + getNumberOfItems();
//            f = 0.0F;
//            if (bool)
//                f = this.item - (-1 + getNumberOfItems());
//        }
    }
//
    public void scrollToItem(float paramFloat) {
//        if (!hasItems())
//            return;
//        if (this.shouldOverscroll)
//            ;
//        float f1;
//        for (float f2 = Math.min(Math.max(paramFloat, -0.49F), 0.49F + (-1 + getNumberOfItems()));; f2 =
//                Math.min(Math.max(paramFloat, 0.0F), Math.max(getNumberOfItems() - f1, 0.0F))) {
//            setItem(f2);
//            return;
//            if (getItemHeight() == 0.0F)
//                break;
//            f1 = getHeight() / getItemHeight();
//        }
    }

//    public void setListener(OverscrollViewListener paramOverscrollViewListener) {
//        this.listener = paramOverscrollViewListener;
//    }
//
    public void setShouldHighlightSelectedItem(boolean paramBoolean) {
        if ((!this.shouldHighlightSelectedItem) && (paramBoolean)) {
            //Get the child linear layout
            ViewGroup localViewGroup2 = (ViewGroup) getChildAt(0);
            //Now for every child of that
//            if (j < localViewGroup2.getChildCount()) {
//                //See if the currently selected item if the first?
//                //This really looks like it should hve been a for loop
//                if (getItem() == j)
//                    localViewGroup2.getChildAt(j).setSelected(true);
//                while (true) {
//                    j++;
//                    break;
//                    localViewGroup2.getChildAt(j).setSelected(false);
//                }
//            }
            for (int j = 0; j < localViewGroup2.getChildCount(); j++) {
                //See if the currently selected item if the first?
                //This really looks like it should hve been a for loop
                if (getItem() == j) {
                    localViewGroup2.getChildAt(j).setSelected(true);
                }
                else {
                    localViewGroup2.getChildAt(j).setSelected(false);
                }
            }
        } else if (!paramBoolean) {
            ViewGroup localViewGroup1 = (ViewGroup) getChildAt(0);
            for (int i = 0; i < localViewGroup1.getChildCount(); i++)
                localViewGroup1.getChildAt(i).setSelected(true);
        }
        this.shouldHighlightSelectedItem = paramBoolean;
    }

    public void setShouldOverscroll(boolean paramBoolean) {
        this.shouldOverscroll = paramBoolean;
    }
//
//    public void snapToNearest() {
//        this.itemSnapAnimator.cancel();
//        int i = Math.round(this.item);
//        ValueAnimator localValueAnimator = this.itemSnapAnimator;
//        float[] arrayOfFloat = new float[2];
//        arrayOfFloat[0] = this.item;
//        arrayOfFloat[1] = i;
//        localValueAnimator.setFloatValues(arrayOfFloat);
//        this.itemSnapAnimator.start();
//    }
//
    public static abstract interface OverscrollViewListener {
        public abstract void onSelectedItemChanged(int paramInt);
    }
}

/*
 * Location: /Users/mdigiovanni/tmp/glass/GlassHome-dex2jar.jar Qualified Name:
 * com.google.glass.home.voice.OverscrollView JD-Core Version: 0.6.2
 */