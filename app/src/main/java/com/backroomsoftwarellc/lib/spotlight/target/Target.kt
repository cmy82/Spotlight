package com.backroomsoftwarellc.lib.spotlight.target

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import android.view.View
import com.backroomsoftwarellc.lib.spotlight.OnTargetRenderListener
import com.backroomsoftwarellc.lib.spotlight.OnTargetListener
import com.backroomsoftwarellc.lib.spotlight.effect.Effect
import com.backroomsoftwarellc.lib.spotlight.effect.EmptyEffect
import com.backroomsoftwarellc.lib.spotlight.shape.Circle
import com.backroomsoftwarellc.lib.spotlight.shape.Shape

interface Target {

    val anchor: PointF
    val shape: Shape
    val effect: Effect
    val overlay: View?
    val listener: OnTargetListener?
    val renderListener: OnTargetRenderListener?
    val message: String

    fun getTargetCount(): Int
    fun renderTargetMessage(b: Bitmap, txtAnchor: PointF, w: Float, avoid: List<HitArea>, txtSize: Float = 24f): Bitmap {
        val cnv = Canvas(b)
        val lnHeight = spToPx(txtSize + 2)
        val p = Paint().apply {
            textSize = spToPx(txtSize)
        }

        val fpass = ArrayList<String>(message.split("\n"))
        val spass = ArrayList<String>()
        val lines = ArrayList<String>()
        val points = ArrayList<PointF>()
        
        //Log.e("-------------", "Split message into first step by line delimeter with line count: ${fpass.size}")

        //p.apply {
        //    color = Color.WHITE
        //    alpha = 160
        //}
        //avoid.forEach {
        //    when(it.shape){
        //        HitArea.CIRCLE -> {
        //            cnv.drawCircle(it.anchor.x, it.anchor.y, it.width, p)
        //        }
        //        HitArea.RECT -> {
        //            cnv.drawRect(it.anchor.x, it.anchor.y, it.anchor.x + it.width, it.anchor.y + it.height , p)
        //        }
        //    }
        //}
        //p.apply {
        //    color = Color.YELLOW
        //    alpha = 160
        //}
        //cnv.drawRect(txtAnchor.x, txtAnchor.y, txtAnchor.x + w, txtAnchor.y + 300, p)

        var curY = txtAnchor.y
        fpass.forEach { line ->
            val bounds = Rect()
            var curLine = line.trim()
            //Log.w(">====<", "Value of curLine to check \"${curLine}\"")
            var outcheck = 0
            while(curLine.isNotBlank() && outcheck < 500) {
                avoid.forEach { ha ->
                    p.getTextBounds(curLine, 0, curLine.length, bounds)

                    if(txtAnchor.x < anchor.x) {
                        bounds.left = (txtAnchor.x).toInt()
                        bounds.right = (bounds.left + bounds.right)
                    }
                    if(txtAnchor.x > anchor.x) {
                        val wd = bounds.right
                        bounds.right = (txtAnchor.x + w).toInt();
                        bounds.left = (bounds.right - wd)
                    }
                    if(txtAnchor.x == anchor.x){
                        val wd = bounds.right
                        bounds.left = (txtAnchor.x - wd/2).toInt()
                        bounds.right = (txtAnchor.x + wd/2).toInt()
                    }
                    if(txtAnchor.y < anchor.y) {
                        bounds.top = (curY - lnHeight).toInt()
                        bounds.bottom = curY.toInt()
                    }
                    if(txtAnchor.y > anchor.y) {
                        bounds.bottom = (curY + lnHeight).toInt()
                        bounds.top = curY.toInt()
                    }
                    if(txtAnchor.y == anchor.y){ // Always base width on widest part of circle
                        bounds.top = (txtAnchor.y - lnHeight/2).toInt()
                        bounds.bottom = (txtAnchor.y + lnHeight/2).toInt()
                    }
                    //Log.w(">==${outcheck}==<", "curLine has width of ${bounds.right-bounds.left} to fit in a space of $w at ${bounds.left}")

                    var loopcheck = 0
                    while((((bounds.right-bounds.left) > w) || ha.checkIntersection(bounds)) && curLine.isNotBlank() && loopcheck < 300){
                        //val idx = curLine.lastIndexOf(" ", 1)
                        val idx = curLine.lastIndexOf(" ")
                        curLine = if(idx==-1) "" else curLine.substring(0,idx).trim()
                        p.getTextBounds(curLine, 0, curLine.length, bounds)

                        if(txtAnchor.x < anchor.x) {
                            bounds.left = (txtAnchor.x).toInt()
                            bounds.right = (bounds.left + bounds.right)
                        }
                        if(txtAnchor.x > anchor.x) {
                            val wd = bounds.right
                            bounds.right = (txtAnchor.x + w).toInt();
                            bounds.left = (bounds.right - wd)
                        }
                        if(txtAnchor.x == anchor.x){
                            val wd = bounds.right
                            bounds.left = (txtAnchor.x - wd/2).toInt()
                            bounds.right = (txtAnchor.x + wd/2).toInt()
                        }
                        if(txtAnchor.y < anchor.y) {
                            bounds.top = (curY - lnHeight).toInt()
                            bounds.bottom = curY.toInt()
                        }
                        if(txtAnchor.y > anchor.y) {
                            bounds.bottom = (curY + lnHeight).toInt()
                            bounds.top = curY.toInt()
                        }
                        if(txtAnchor.y == anchor.y){ // Always base width on widest part of circle
                            bounds.top = (txtAnchor.y - lnHeight/2).toInt()
                            bounds.bottom = (txtAnchor.y + lnHeight/2).toInt()
                        }

                        //Log.w(">==${outcheck}==${loopcheck}==<", "[$idx] curLine now has width of ${bounds.right-bounds.left} to fit in a space of $w at ${bounds.left}")
                        //Log.w(">==${outcheck}==${loopcheck}==<", "Value of curLine is now \"${curLine}\" at location (${bounds.left},${bounds.top})")
                        loopcheck++
                    }
                }
                spass.add(curLine)
                points.add(PointF(bounds.left.toFloat(), curY))
                if(txtAnchor.y > anchor.y) curY += lnHeight
                else curY -= lnHeight
                //Log.e("======", "Adding line: \"${curLine}\" to second pass array")
                curLine = if(line.indexOf(curLine)+curLine.length < line.length) line.substring(line.indexOf(curLine)+curLine.length).trim()
                          else ""
                outcheck++
            }
            lines.addAll(spass)
            //Log.e("<=============>", "Added ${spass.size} lines to final line array")
            spass.clear()
        }

        p.apply {
            color = Color.WHITE
            alpha = 255
        }
        if(txtAnchor.y > anchor.y)
            lines.forEachIndexed { index, line ->
                val start = points[index]
                //cnv.drawText(line, start.x, txtAnchor.y + spToPx(26f)*index, p)
                cnv.drawText(line, start.x, txtAnchor.y + lnHeight*index, p)
            }
        if(txtAnchor.y < anchor.y) // Reverse list and draw from the bottom up
            lines.reversed().forEachIndexed { index, line ->
                val start = points.reversed()[index]
                //cnv.drawText(line, start.x, txtAnchor.y - spToPx(26f)*index, p)
                cnv.drawText(line, start.x, txtAnchor.y - lnHeight*index, p)
            }
        if(txtAnchor.y == anchor.y) // Center text based on target y
            lines.forEachIndexed { index, line ->
                val start = points[index]
                val compLine = (lines.size/2) - (lines.size-1-index)
                //cnv.drawText(line, start.x, txtAnchor.y + spToPx(26f)*compLine, p)
                cnv.drawText(line, start.x, txtAnchor.y + lnHeight*compLine, p)
            }

        return b
    }

    private fun spToPx(sp: Float): Float {
        if(sp <= 0) return 0f
        return (sp * Resources.getSystem().displayMetrics.scaledDensity)
    }

    abstract class Builder {

        private val DEFAULT_ANCHOR = PointF(0f, 0f)
        private val DEFAULT_SHAPE = Circle(100f)
        private val DEFAULT_EFFECT = EmptyEffect()

        var anchor: PointF = DEFAULT_ANCHOR
        var shape: Shape = DEFAULT_SHAPE
        var effect: Effect = DEFAULT_EFFECT
        var overlay: View? = null
        var listener: OnTargetListener? = null
        var renderListener: OnTargetRenderListener? = null
        var message: String = ""

        /**
         * Sets anchor pointer to start a [Target] to center of view.
         */
        abstract fun setAnchor(view: View): Builder

        /**
         * Sets anchor pointer to start a [Target] to center of view plus an offset.
         */
        abstract fun setAnchorWithOffset(view: View, xoff: Float, yoff: Float): Builder

        /**
         * Sets an anchor point to start [Target].
         */
        abstract fun setAnchor(x: Float, y: Float): Builder

        /**
         * Sets an anchor point to start [Target].
         */
        abstract fun setAnchor(anchor: PointF): Builder

        /**
         * Sets [shape] of the spot of [Target].
         */
        fun setShape(shape: Shape): Builder = apply {
            this.shape = shape
        }

        /**
         * Sets [effect] of the spot of [Target].
         */
        fun setEffect(effect: Effect): Builder = apply {
            this.effect = effect
        }

        /**
         * Sets [overlay] to be laid out to describe [Target].
         */
        fun setOverlay(overlay: View): Builder = apply {
            this.overlay = overlay
        }

        /**
         * Sets [OnTargetListener] to notify the state of [Target].
         */
        fun setOnTargetListener(listener: OnTargetListener): Builder = apply {
            this.listener = listener
        }

        /**
         * Sets [OnTargetRenderListener] allows custom drawing after rendering of [Target].
         */
        fun setOnRenderListener(listener: OnTargetRenderListener): Builder = apply {
            this.renderListener = listener
        }

        /**
         * Sets [message] to describe [Target].
         */
        fun setMessage(msg: String) = apply {
            this.message = msg
        }

        abstract fun build(): Target
    }
}