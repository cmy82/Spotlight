package com.backroomsoftwarellc.lib.spotlight.effect

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorInt
import java.util.concurrent.TimeUnit

/**
 * Draws a pulse effect around the target.
 * @param offset  [Triple<Float,Float,Float>] where first is offset from center for pulse animation
 *                second is the width and third is the height (for EFFECT_SHAPE_RECTANGLE and EFFECT_SHAPE_CUSTOM)
 * @param radius  radius of the pulse
 * @param color   color of the pulse overlay
 * @param duration  length in milliseconds of the animation
 * @param interpolator  TimeInterpolator to manage the flow of the animation (easing)
 * @param repeatMode  how the animation should repeat (defaults to reverse)
 */
class PulseEffect @JvmOverloads constructor(
    //private val offset: Float,
    private val offset: Triple<Float,Float,Float>,
    private val radius: Float,
    @ColorInt private val color: Int,
    override val duration: Long = DEFAULT_DURATION,
    override val interpolator: TimeInterpolator = DEFAULT_INTERPOLATOR,
    override val repeatMode: Int = DEFAULT_REPEAT_MODE,
    override val effectShape: Int = EFFECT_SHAPE_CIRCLE
) : Effect {

  init {
      when(effectShape) {
          EFFECT_SHAPE_CIRCLE -> require(offset.first < radius) { "holeRadius should be bigger than rippleRadius." }
      }
  }

  override fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint) {
    when(effectShape) {
        EFFECT_SHAPE_CIRCLE -> {
            val radius = offset.first + ((radius - offset.first) * value)
            val alpha = (255 - value * 255).toInt()
            paint.color = color
            paint.alpha = alpha
            canvas.drawCircle(point.x, point.y, radius, paint)
        }
        EFFECT_SHAPE_RECTANGLE -> {
            val halfWidth = offset.second / 2 * value
            val halfHeight = offset.third / 2 * value
            val left = (point.x - halfWidth) - offset.first
            val top = (point.y - halfHeight) - offset.first
            val right = offset.first + (point.x + halfWidth)
            val bottom = offset.first + (point.y + halfHeight)
            val rect = RectF(left, top, right, bottom)
            val alpha = (255 - value * 255).toInt()
            paint.color = color
            paint.alpha = alpha
            canvas.drawRoundRect(rect, radius, radius, paint)
        }
        else -> {

        }
    }
  }

  companion object {
    val DEFAULT_DURATION = TimeUnit.MILLISECONDS.toMillis(1000)
    val DEFAULT_INTERPOLATOR = DecelerateInterpolator(1f)
    const val DEFAULT_REPEAT_MODE = ObjectAnimator.REVERSE
  }
}