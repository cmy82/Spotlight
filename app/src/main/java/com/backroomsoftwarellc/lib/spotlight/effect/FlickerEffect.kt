package com.backroomsoftwarellc.lib.spotlight.effect

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import java.util.concurrent.TimeUnit

/**
 * Draws an flicker effects.
 */
class FlickerEffect @JvmOverloads constructor(
    //private val offset: Float,
    private val radius: Triple<Float,Float,Float>,
    @ColorInt private val color: Int,
    override val duration: Long = DEFAULT_DURATION,
    override val interpolator: TimeInterpolator = DEFAULT_INTERPOLATOR,
    override val repeatMode: Int = DEFAULT_REPEAT_MODE,
    override val effectShape: Int = EFFECT_SHAPE_CIRCLE
) : Effect {

  override fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint) {
      when(effectShape) {
          EFFECT_SHAPE_CIRCLE -> {
              paint.color = color
              paint.alpha = (value * 255).toInt()
              canvas.drawCircle(point.x, point.y, radius.first, paint)
          }
          EFFECT_SHAPE_RECTANGLE -> {
              val halfWidth = radius.second / 2
              val halfHeight = radius.third / 2
              val left = (point.x - halfWidth)
              val top = (point.y - halfHeight)
              val right = (point.x + halfWidth)
              val bottom = (point.y + halfHeight)
              val rect = RectF(left, top, right, bottom)
              val alpha = (255 - value * 255).toInt()
              paint.color = color
              paint.alpha = alpha
              canvas.drawRoundRect(rect, radius.first, radius.first, paint)
          }
          else -> {

          }
      }
  }

  companion object {

    val DEFAULT_DURATION = TimeUnit.MILLISECONDS.toMillis(1000)

    val DEFAULT_INTERPOLATOR = LinearInterpolator()

    const val DEFAULT_REPEAT_MODE = ObjectAnimator.REVERSE
  }
}