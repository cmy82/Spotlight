package com.backroomsoftwarellc.lib.spotlight.effect

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import com.backroomsoftwarellc.lib.spotlight.utils.TiltListener
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.sin

/**
 * Draws an ripple effects.
 */
class RippleEffect @JvmOverloads constructor(
    private val offset: Float,
    private val radius: Float,
    private val animateSize: Boolean = false,
    @ColorInt private val color: Int,
    @ColorInt private val rippleColor: Int,
    private val strokeWidth: Float = DEFAULT_STROKE_WIDTH,
    private val strokeGap: Float = DEFAULT_STROKE_GAP,
    override val duration: Long = DEFAULT_DURATION,
    override val interpolator: TimeInterpolator = DEFAULT_INTERPOLATOR,
    override val repeatMode: Int = DEFAULT_REPEAT_MODE,
    override val effectShape: Int = EFFECT_SHAPE_CIRCLE
) : Effect, TiltListener {

  private var strokeAnimator: ValueAnimator? = null
  private val strokePaint =
      Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.STROKE
      }
  private var strokeRadiusOffset = 0f
  private var maxRadius = 0f
  private var center = PointF(0f, 0f)
  private var initialRadius = 0f


  init {
    require(offset < radius) { "holeRadius should be bigger than rippleRadius." }
    maxRadius = radius
    strokePaint.apply {
      color = rippleColor
      strokeWidth = strokeWidth
    }

    val strokeDuration = (duration * 0.25).toLong()
    strokeAnimator = ValueAnimator.ofFloat(0f, strokeGap + strokeWidth).apply {
      addUpdateListener {
        strokeRadiusOffset = it.animatedValue as Float
      }
      duration = strokeDuration
      repeatMode = ValueAnimator.RESTART
      repeatCount = ValueAnimator.INFINITE
      interpolator = LinearInterpolator()
      start()
    }
  }

  override fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint) {
    val radius = if(animateSize) offset + ((radius - offset) * value)
                 else maxRadius
    val alpha = (255 - value * 255).toInt()
    //val alpha = 255

    paint.color = color
    paint.alpha = alpha
    canvas.drawCircle(point.x, point.y, radius, paint)

    var currentRadius = initialRadius + strokeRadiusOffset
    strokePaint.alpha = alpha

    while (currentRadius < radius) {    // maxRadius
      canvas.drawArc(
          (point.x + center.x) - currentRadius,
          (point.y + center.y) - currentRadius,
          (point.x + center.x) + currentRadius,
          (point.y + center.y) + currentRadius,
          0f, 360f, false, strokePaint
      )
      currentRadius += (strokeGap + strokeWidth)
    }

  }

  override fun onTilt(pitchRollRad: Pair<Double, Double>) {
    val pitchRad = pitchRollRad.first
    val rollRad = pitchRollRad.second

    // Use half view height/width to calculate offset instead of full view/device measurement
    //val maxYOffset = this.height / 2
    //val maxXOffset = this.width / 2
    val maxYOffset = maxRadius / 2
    val maxXOffset = maxRadius / 2

    val yOffset = (sin(pitchRad) * maxYOffset)
    val xOffset = (sin(rollRad) * maxXOffset)

    center.set(max(xOffset.toFloat(), 0f), max(yOffset.toFloat(), 0f))

  }

  companion object {
    val DEFAULT_DURATION = TimeUnit.MILLISECONDS.toMillis(1000)
    val DEFAULT_INTERPOLATOR = DecelerateInterpolator(1f)
    const val DEFAULT_STROKE_WIDTH = 20f
    const val DEFAULT_STROKE_GAP = 10f
    const val DEFAULT_REPEAT_MODE = ObjectAnimator.REVERSE
  }
}