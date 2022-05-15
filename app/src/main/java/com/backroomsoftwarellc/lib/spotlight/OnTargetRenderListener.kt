package com.backroomsoftwarellc.lib.spotlight

import android.graphics.Canvas
import android.graphics.Paint
import com.backroomsoftwarellc.lib.spotlight.target.Target

interface OnTargetRenderListener {
    fun onPreRender(c: Canvas, t: Target, p: Paint) { }
    fun onRendered(c: Canvas, t: Target, p: Paint)
}