package com.backroomsoftwarellc.lib.spotlight.effect

import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF

/**
 * Additional effect drawing in loop to Shape.
 */
interface Effect {

  /**
   * [duration] to draw Effect.
   */
  val duration: Long

  /**
   * [interpolator] to draw Effect.
   */
  val interpolator: TimeInterpolator

  /**
   * [repeatMode] to draw Effect.
   */
  val repeatMode: Int

  /**
   * [effectShape] to draw Effect.
   */
  val effectShape: Int

  /**
   * Draw the Effect.
   *
   * @param value the animated value from 0 to 1 and this value is looped until Target finishes.
   */
  fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint)

}

const val EFFECT_SHAPE_NONE = 0
const val EFFECT_SHAPE_CIRCLE = 1
const val EFFECT_SHAPE_RECTANGLE = 2
const val EFFECT_SHAPE_CUSTOM = 3
