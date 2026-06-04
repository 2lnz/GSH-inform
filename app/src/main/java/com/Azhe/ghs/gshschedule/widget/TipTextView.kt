package com.Azhe.ghs.gshschedule.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import splitties.dimensions.dp

@SuppressLint("ViewConstructor")
class TipTextView(context: Context) : View(context) {

    var tipVisibility = 0
        set(value) {
            field = value
            invalidate()
        }

    /** When true, the top edge has no inset and no rounded corner — used for
     *  middle/continuation rows of a multi-node course so they stack seamlessly. */
    var flatTop = false
    /** When true, the bottom edge has no inset and no rounded corner. */
    var flatBottom = false

    /** Set to true to skip drawing the stroke border (used for interior rows
     *  of a multi-node course so the border doesn't create visible seams). */
    var noStroke = false

    private var text = ""
    private var mStaticLayout: StaticLayout? = null
    private lateinit var mTextPaint: TextPaint
    private lateinit var mPaint: Paint
    private lateinit var bgPaint: Paint
    private lateinit var strokePaint: Paint
    private val path = Path()
    private val rect = RectF()
    private val dpUnit = dp(1)
    private var otherWeekTextAlpha = 255
    private var otherWeekBgAlpha = 255
    private var otherWeekStrokeAlpha = 255

    fun init(text: String, txtSize: Int, txtColor: Int, bgColor: Int, bgAlpha: Int, stroke: Int) {
        this.text = text
        mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            // textSize = mSize * resources.displayMetrics.scaledDensity
            textSize = txtSize * dpUnit
            typeface = Typeface.DEFAULT_BOLD
            color = txtColor
        }
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = txtColor
            isDither = true
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2 * dpUnit
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            isDither = true
            style = Paint.Style.FILL
            alpha = bgAlpha
        }
        strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = stroke
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = 2 * dpUnit
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        otherWeekTextAlpha = (mTextPaint.alpha * 0.3).toInt()
        otherWeekBgAlpha = (bgPaint.alpha * 0.3).toInt()
        otherWeekStrokeAlpha = (strokePaint.alpha * 0.3).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Tell the parent layout how big this view would like to be
        // but still respect any requirements (measure specs) that are passed down.

        // determine the width
        val width = MeasureSpec.getSize(widthMeasureSpec)
        // determine the height
        val height = MeasureSpec.getSize(heightMeasureSpec)
        rect.left = dpUnit
        rect.right = width.toFloat() - dpUnit
        rect.top = if (flatTop) 0f else dpUnit
        rect.bottom = if (flatBottom) height.toFloat() else height.toFloat() - dpUnit
        // Required call: set width and height
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (tipVisibility == TIP_OTHER_WEEK) {
            mTextPaint.alpha = otherWeekTextAlpha
            mPaint.alpha = otherWeekTextAlpha
            strokePaint.alpha = otherWeekStrokeAlpha
            bgPaint.alpha = otherWeekBgAlpha
        }
        if (mStaticLayout == null) {
            mStaticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout
                        .Builder
                        .obtain(text, 0, text.length, mTextPaint, width - paddingRight - paddingLeft)
                        .setIncludePad(false)
                        .build()
            } else {
                StaticLayout(
                        text,
                        mTextPaint,
                        width - paddingRight - paddingLeft,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.0f,
                        0f,
                        false
                )
            }
        }
        // Draw rounded rect with per-corner radii — flatTop / flatBottom
        // suppress the radius on edges that must connect to adjacent rows.
        val cr = 4 * dpUnit
        path.addRoundRect(rect, floatArrayOf(
            if (flatTop) 0f else cr, if (flatTop) 0f else cr,
            if (flatTop) 0f else cr, if (flatTop) 0f else cr,
            if (flatBottom) 0f else cr, if (flatBottom) 0f else cr,
            if (flatBottom) 0f else cr, if (flatBottom) 0f else cr
        ), Path.Direction.CW)
        canvas.drawPath(path, bgPaint)
        if (!noStroke) {
            canvas.drawPath(path, strokePaint)
        }
        path.reset()
        canvas.clipRect(rect)
        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        mStaticLayout!!.draw(canvas)
        canvas.restore()
        if (tipVisibility == 1) {
            path.moveTo(width - 12 * dpUnit, height - 6 * dpUnit) // 此点为多边形的起点
            path.lineTo(width - 6 * dpUnit, height - 6 * dpUnit)
            path.lineTo(width - 6 * dpUnit, height - 12 * dpUnit)
            path.close() // 使这些点构成封闭的多边形
            canvas.drawPath(path, mPaint)
        } else if (tipVisibility == -1) {
            canvas.drawLine(width - 12 * dpUnit,
                    height - 6 * dpUnit,
                    width - 6 * dpUnit,
                    height - 12 * dpUnit, mPaint)
            canvas.drawLine(width - 6 * dpUnit,
                    height - 6 * dpUnit,
                    width - 12 * dpUnit,
                    height - 12 * dpUnit, mPaint)
        }
    }

    companion object {
        const val TIP_INVISIBLE = 0
        const val TIP_VISIBLE = 1
        const val TIP_ERROR = -1
        const val TIP_OTHER_WEEK = 2
    }
}
