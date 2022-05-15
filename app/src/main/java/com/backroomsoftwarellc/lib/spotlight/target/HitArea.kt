package com.backroomsoftwarellc.lib.spotlight.target

import android.graphics.PointF
import android.graphics.Rect
import java.util.*

/**
 * [HitArea] class to check for collsions with [Target] collision areas for rendering custom text.
 * @params shape [Int] value denoting whether [HitArea] is a shape or rectangle
 * @params anchor [PointF] denoting center of circle or top left corner of rectangle for [HitArea]
 * @params width [Float] denoting the width/radius of [HitArea]
 * @params height [Float] denoting the height of [HitArea]
 */
class HitArea(val shape: Int = CIRCLE, val anchor: PointF = PointF(0f,0f), val width: Float = 1f, val height: Float = 1f) {
    init {
        require(width > 0f) { "Width of hit area must be greater than 0." }
        require(height >= 0f) { "Height of hit area must be non-negative." }
    }

    fun checkIntersection(area: Rect): Boolean {
        when(shape){
            CIRCLE -> {
                val p1 = PointF(area.left.toFloat(), area.top.toFloat())
                val p2 = PointF(area.right.toFloat(), area.top.toFloat())
                val p3 = PointF(area.left.toFloat(), area.bottom.toFloat())
                val p4 = PointF(area.right.toFloat(), area.bottom.toFloat())

                var check = getCircleLineIntersectionPoint(p1, p2, anchor, width.toDouble())
                if(check.isNotEmpty()) {
                    check.forEach {
                        if(it != null)
                            if ((it.x >= area.left) && (it.x <= area.right) && (it.y >= area.top) && (it.y <= area.bottom)) return true
                    }
                }
                check = getCircleLineIntersectionPoint(p1, p3, anchor, width.toDouble())
                if(check.isNotEmpty()) {
                    check.forEach {
                        if(it != null)
                            if((it.x >= area.left) && (it.x <= area.right) && (it.y >= area.top) && (it.y <= area.bottom)) return true
                    }
                }
                check = getCircleLineIntersectionPoint(p2, p4, anchor, width.toDouble())
                if(check.isNotEmpty()) {
                    check.forEach {
                        if(it != null)
                            if((it.x >= area.left) && (it.x <= area.right) && (it.y >= area.top) && (it.y <= area.bottom)) return true
                    }
                }
                check = getCircleLineIntersectionPoint(p3, p4, anchor, width.toDouble())
                if(check.isNotEmpty()) {
                    check.forEach {
                        if(it != null)
                            if((it.x >= area.left) && (it.x <= area.right) && (it.y >= area.top) && (it.y <= area.bottom)) return true
                    }
                }
            }
            RECT -> {
                val le = anchor.x
                val re = anchor.x + width
                val te = anchor.y
                val be = anchor.y + height
                if((le > area.left) && (le < area.right) && (te > area.top) && (te < area.bottom)) return true
                if((le > area.left) && (le < area.right) && (be > area.top) && (be < area.bottom)) return true
                if((re > area.left) && (re < area.right) && (te > area.top) && (te < area.bottom)) return true
                if((re > area.left) && (re < area.right) && (be > area.top) && (be < area.bottom)) return true
            }
        }
        return false
    }

    private fun getCircleLineIntersectionPoint(
        pointA: PointF,
        pointB: PointF, center: PointF, radius: Double
    ): List<PointF?> {
        val baX: Double = pointB.x.toDouble() - pointA.x
        val baY: Double = pointB.y.toDouble() - pointA.y
        val caX: Double = center.x.toDouble() - pointA.x
        val caY: Double = center.y.toDouble() - pointA.y
        val a = baX * baX + baY * baY
        val bBy2 = baX * caX + baY * caY
        val c = caX * caX + caY * caY - radius * radius
        val pBy2 = bBy2 / a
        val q = c / a
        val disc = pBy2 * pBy2 - q
        if (disc < 0) {
            return Collections.emptyList()
        }
        // if disc == 0 ... dealt with later
        val tmpSqrt = Math.sqrt(disc)
        val abScalingFactor1 = -pBy2 + tmpSqrt
        val abScalingFactor2 = -pBy2 - tmpSqrt
        val p1 = PointF(
            (pointA.x - baX * abScalingFactor1).toFloat(),
            (pointA.y - baY * abScalingFactor1).toFloat()
        )
        if (disc == 0.0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1)
        }
        val p2 = PointF(
            (pointA.x - baX * abScalingFactor2).toFloat(),
            (pointA.y - baY * abScalingFactor2).toFloat()
        )
        return Arrays.asList(p1, p2)
    }

    companion object {
        const val CIRCLE = 0
        const val RECT = 1
    }
}