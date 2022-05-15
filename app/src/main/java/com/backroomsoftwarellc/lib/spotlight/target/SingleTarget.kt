package com.backroomsoftwarellc.lib.spotlight.target

import android.graphics.PointF
import android.view.View
import com.backroomsoftwarellc.lib.spotlight.OnTargetRenderListener
import com.backroomsoftwarellc.lib.spotlight.OnTargetListener
import com.backroomsoftwarellc.lib.spotlight.effect.Effect
import com.backroomsoftwarellc.lib.spotlight.shape.Shape

/**
 * [Target] represents the spot that Spotlight will cast.
 * @param anchor the group anchor for the target location (also used as a reference for drawing the message)
 * @param shape the shape of the target
 * @param effect the visual effect for the target
 * @param overlay the view to be overlaid the current activity to draw the target on
 * @param listener [OnTargetListener] that allows custom actions to be taken after the [Target] is shown or before it is removed
 * @param renderListener [OnTargetRenderListener] that allows custom drawing to be done before or after target is rendered
 * @param message custom message to be draw on screen when target is rendered
 */
class SingleTarget private constructor (
  override val anchor: PointF,
  override val shape: Shape,
  override val effect: Effect,
  override val overlay: View?,
  override val listener: OnTargetListener?,
  override val renderListener: OnTargetRenderListener?,
  override val message: String
) : Target {

  override fun getTargetCount(): Int = 1

  class Builder: Target.Builder() {

    /**
     * Sets anchor pointer to start a [Target] to center of view.
     */
    override fun setAnchor(view: View): Target.Builder = apply {
      val location = IntArray(2)
      view.getLocationInWindow(location)
      val x = location[0] + view.width / 2f
      val y = location[1] + view.height / 2f
      setAnchor(x, y)
    }

    /**
     * Sets anchor pointer to start a [Target] to center of view plus an offset.
     */
    override fun setAnchorWithOffset(view: View, xoff: Float, yoff: Float): Target.Builder = apply {
      val location = IntArray(2)
      view.getLocationInWindow(location)
      val x = (location[0] + view.width / 2f) + xoff
      val y = (location[1] + view.height / 2f) + yoff
      setAnchor(x, y)
    }

    /**
     * Sets an anchor point to start [Target].
     */
    override fun setAnchor(x: Float, y: Float): Target.Builder = apply {
      setAnchor(PointF(x, y))
    }

    /**
     * Sets an anchor point to start [Target].
     */
    override fun setAnchor(anchor: PointF): Target.Builder = apply {
      this.anchor = anchor
    }

    override fun build(): Target = SingleTarget(
      anchor = anchor,
      shape = shape,
      effect = effect,
      overlay = overlay,
      listener = listener,
      renderListener = renderListener,
      message = message
    )
  }
}