package com.celapps.filmica.view.util

import android.animation.Animator

class AnimatorEndListener(
    val callback: (Animator) -> Unit
) : Animator.AnimatorListener {
    override fun onAnimationRepeat(p0: Animator?) {

    }

    override fun onAnimationEnd(animator: Animator) {
        callback.invoke(animator)
    }

    override fun onAnimationCancel(p0: Animator?) {

    }

    override fun onAnimationStart(p0: Animator?) {

    }

}