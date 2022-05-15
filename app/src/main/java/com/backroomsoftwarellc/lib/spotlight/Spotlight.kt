package com.backroomsoftwarellc.lib.spotlight

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.backroomsoftwarellc.lib.spotlight.target.Target
import java.util.concurrent.TimeUnit

/**
 * Holds all of the [Target]s and [SpotlightView] to show/hide [Target], [SpotlightView] properly.
 * [SpotlightView] can be controlled with [start]/[finish].
 * All of the [Target]s can be controlled with [next]/[previous]/[show].
 *
 * Once you finish the current [Spotlight] with [finish], you can not start the [Spotlight] again
 * unless you create a new [Spotlight] to start again.
 */
class Spotlight private constructor(
  private val spotlight: SpotlightView,
  private val targets: Array<Target>,
  private val duration: Long,
  private val interpolator: TimeInterpolator,
  private val container: ViewGroup,
  private val spotlightListener: OnSpotlightListener?,
  private val closeOnTouch: Boolean,
  private val touchListener: View.OnTouchListener?
) {

  private var currentIndex = NO_POSITION
  private val internalTouchListener = View.OnTouchListener { v, e ->
    touchListener?.onTouch(v,e)
    if(closeOnTouch) {
      finishSpotlight()
    }
    return@OnTouchListener true
  }

  init {
    container.addView(spotlight, MATCH_PARENT, MATCH_PARENT)
    spotlight.setOnTouchListener(internalTouchListener)
  }

  /**
   * Starts [SpotlightView] and show the first [Target].
   */
  fun start() {
    startSpotlight()
  }

  /**
   * Closes the current [Target] if exists, and shows a [Target] at the specified [index].
   * If target is not found at the [index], it will throw an exception.
   */
  fun show(index: Int) {
    showTarget(index)
  }

  /**
   * Closes the current [Target] if exists, and shows the next [Target].
   * If the next [Target] is not found, Spotlight will finish.
   */
  fun next() {
    showTarget(currentIndex + 1)
  }

  /**
   * Closes the current [Target] if exists, and shows the previous [Target].
   * If the previous target is not found, it will throw an exception.
   */
  fun previous() {
    showTarget(currentIndex - 1)
  }

  /**
   * Closes Spotlight and [SpotlightView] will remove all children and be removed from the [container].
   */
  fun finish() {
    finishSpotlight()
  }

  /**
   * Starts Spotlight.
   */
  private fun startSpotlight() {
    spotlight.startSpotlight(duration, interpolator, object : AnimatorListenerAdapter() {
      override fun onAnimationStart(animation: Animator) {
        spotlightListener?.onStarted()
      }

      override fun onAnimationEnd(animation: Animator) {
        showTarget(0)
      }
    })
  }

  /**
   * Closes the current [Target] if exists, and show the [Target] at [index].
   */
  private fun showTarget(index: Int) {
    if (currentIndex == NO_POSITION) {
      val target = targets[index]
      currentIndex = index
      spotlight.startTarget(target)
      target.listener?.onStarted()
    } else if(index > -1) {
      spotlight.finishTarget(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          val previousIndex = currentIndex
          val previousTarget = targets[previousIndex]
          previousTarget.listener?.onEnded()
          if (index < targets.size) {
            val target = targets[index]
            currentIndex = index
            spotlight.startTarget(target)
            target.listener?.onStarted()
          } else {
            finishSpotlight()
          }
        }
      })
    }
  }

  /**
   * Closes Spotlight.
   */
  private fun finishSpotlight() {
    spotlight.finishSpotlight(duration, interpolator, object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        spotlight.cleanup()
        container.removeView(spotlight)
        spotlightListener?.onEnded()
      }
    })
  }

  companion object {
    private const val NO_POSITION = -1
  }

  /**
   * Builder to build [Spotlight].
   * All parameters should be set in this [Builder].
   */
  class Builder(private val activity: Activity) {

    private var targets: Array<Target>? = null
    private var duration: Long = DEFAULT_DURATION
    private var interpolator: TimeInterpolator = DEFAULT_ANIMATION
    @ColorInt private var backgroundColor: Int = DEFAULT_OVERLAY_COLOR
    private var container: ViewGroup? = null
    private var listener: OnSpotlightListener? = null
    private var closeOnTouch: Boolean = false
    private var touchListener: View.OnTouchListener? = null

    /**
     * Sets [Target]s to show on [Spotlight].
     */
    fun setTargets(vararg targets: Target): Builder = apply {
      require(targets.isNotEmpty()) { "targets should not be empty. " }
      this.targets = arrayOf(*targets)
    }

    /**
     * Sets [Target]s to show on [Spotlight].
     */
    fun setTargets(targets: List<Target>): Builder = apply {
      require(targets.isNotEmpty()) { "targets should not be empty. " }
      this.targets = targets.toTypedArray()
    }

    /**
     * Sets [duration] to start/finish [Spotlight].
     */
    fun setDuration(duration: Long): Builder = apply {
      this.duration = duration
    }

    /**
     * Sets [backgroundColor] resource on [Spotlight].
     */
    fun setBackgroundColorRes(@ColorRes backgroundColorRes: Int): Builder = apply {
      this.backgroundColor = ContextCompat.getColor(activity, backgroundColorRes)
    }

    /**
     * Sets [backgroundColor] on [Spotlight].
     */
    fun setBackgroundColor(@ColorInt backgroundColor: Int): Builder = apply {
      this.backgroundColor = backgroundColor
    }

    /**
     * Sets [interpolator] to start/finish [Spotlight].
     */
    fun setAnimation(interpolator: TimeInterpolator): Builder = apply {
      this.interpolator = interpolator
    }

    /**
     * Sets [container] to hold [SpotlightView]. DecoderView will be used if not specified.
     */
    fun setContainer(container: ViewGroup) = apply {
      this.container = container
    }

    /**
     * Sets [OnSpotlightListener] to notify the state of [Spotlight].
     */
    fun setOnSpotlightListener(listener: OnSpotlightListener): Builder = apply {
      this.listener = listener
    }

    /**
     * Sets [OnSpotlightListener] to notify the state of [Spotlight].
     */
    fun setCloseOnTouch(status: Boolean): Builder = apply {
      this.closeOnTouch = status
    }

    /**
     * Sets [OnSpotlightListener] to notify the state of [Spotlight].
     */
    fun setOnTouchListener(listener: View.OnTouchListener): Builder = apply {
      this.touchListener = listener
    }

    fun build(): Spotlight {

      val spotlight = SpotlightView(activity, null, 0, backgroundColor)
      val targets = requireNotNull(targets) { "targets should not be null. " }
      val container = container ?: activity.window.decorView as ViewGroup

      return Spotlight(
          spotlight = spotlight,
          targets = targets,
          duration = duration,
          interpolator = interpolator,
          container = container,
          spotlightListener = listener,
          closeOnTouch = closeOnTouch,
          touchListener = touchListener
      )
    }

    companion object {
      private val DEFAULT_DURATION = TimeUnit.SECONDS.toMillis(1)
      private val DEFAULT_ANIMATION = DecelerateInterpolator(2f)
      @ColorInt private val DEFAULT_OVERLAY_COLOR: Int = 0x6000000
    }
  }
}
