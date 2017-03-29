package com.ujujzk.trymenuanim.customView;

import android.view.animation.Animation;

/**
 *
 * Proxy for {@link Animation} that allows use functional interface {@link AnimationEndListener}
 * instead of {@link Animation.AnimationListener}
 *
 *
 * @author ujujzk@gmail.com
 *
 */

public class LambdableAnimation {
    private Animation animation;

    private LambdableAnimation(final Animation animation) {
        this.animation = animation;
    }

    public static LambdableAnimation with(final Animation animation) {
        return new LambdableAnimation(animation);
    }



    public void setOnAnimationListener (final AnimationStartListener asl, final AnimationEndListener ael, final  AnimationRepeatListener arl) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { asl.onAnimationStarts(animation); }

            @Override
            public void onAnimationEnd(Animation animation) {
                ael.onAnimationEnds(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { arl.onAnimationRepeats(animation); }
        });
    }


    public void setOnAnimationListener (final AnimationStartListener asl, final AnimationEndListener ael) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { asl.onAnimationStarts(animation); }

            @Override
            public void onAnimationEnd(Animation animation) {
                ael.onAnimationEnds(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { /*do nothing*/ }
        });
    }


    public interface AnimationStartListener {
        void onAnimationStarts(Animation animation);
    }

    public interface AnimationEndListener {
        void onAnimationEnds(Animation animation);
    }

    public interface AnimationRepeatListener {
        void onAnimationRepeats(Animation animation);
    }
}


