package com.example.moltaxi.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.moltaxi.R

/**
 * Custom View for 7-Segment LED Display
 * Perfect for displaying taxi meter values (time, distance, fare)
 */
class SevenSegmentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var displayValue: String = "00:00"
    private var segmentOnColor: Int = ContextCompat.getColor(context, R.color.segment_red)
    private var segmentOffColor: Int = ContextCompat.getColor(context, R.color.segment_off)

    private val paintOn = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = segmentOnColor
        style = Paint.Style.FILL
    }

    private val paintOff = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = segmentOffColor
        style = Paint.Style.FILL
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = segmentOnColor
        style = Paint.Style.FILL
        alpha = 50
    }

    // Segment mapping: [top, top-right, bottom-right, bottom, bottom-left, top-left, middle]
    private val digitSegments = mapOf(
        '0' to booleanArrayOf(true, true, true, true, true, true, false),
        '1' to booleanArrayOf(false, true, true, false, false, false, false),
        '2' to booleanArrayOf(true, true, false, true, true, false, true),
        '3' to booleanArrayOf(true, true, true, true, false, false, true),
        '4' to booleanArrayOf(false, true, true, false, false, true, true),
        '5' to booleanArrayOf(true, false, true, true, false, true, true),
        '6' to booleanArrayOf(true, false, true, true, true, true, true),
        '7' to booleanArrayOf(true, true, true, false, false, false, false),
        '8' to booleanArrayOf(true, true, true, true, true, true, true),
        '9' to booleanArrayOf(true, true, true, true, false, true, true),
        ':' to booleanArrayOf(false, false, false, false, false, false, false) // Special case
    )

    fun setValue(value: String) {
        displayValue = value
        invalidate() // Redraw the view
    }

    fun setSegmentColor(color: Int) {
        segmentOnColor = color
        paintOn.color = color
        glowPaint.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val charWidth = width / displayValue.length.toFloat()
        val charHeight = height.toFloat()

        displayValue.forEachIndexed { index, char ->
            val x = index * charWidth
            if (char == ':') {
                drawColon(canvas, x + charWidth / 2, charHeight / 2)
            } else {
                drawDigit(canvas, char, x, 0f, charWidth, charHeight)
            }
        }
    }

    private fun drawDigit(canvas: Canvas, digit: Char, x: Float, y: Float, w: Float, h: Float) {
        val segments = digitSegments[digit] ?: return

        val segmentThickness = w * 0.15f
        val segmentLength = (w * 0.7f)
        val gap = segmentThickness * 0.3f

        val centerX = x + w / 2
        val topY = y + gap + segmentThickness
        val middleY = y + h / 2
        val bottomY = y + h - gap - segmentThickness

        // Draw segments with glow effect
        drawSegmentWithGlow(canvas, segments[0], centerX, topY, segmentLength, segmentThickness, 0f) // Top
        drawSegmentWithGlow(canvas, segments[1], centerX + segmentLength / 2, (topY + middleY) / 2, segmentLength / 2, segmentThickness, 90f) // Top-right
        drawSegmentWithGlow(canvas, segments[2], centerX + segmentLength / 2, (middleY + bottomY) / 2, segmentLength / 2, segmentThickness, 90f) // Bottom-right
        drawSegmentWithGlow(canvas, segments[3], centerX, bottomY, segmentLength, segmentThickness, 0f) // Bottom
        drawSegmentWithGlow(canvas, segments[4], centerX - segmentLength / 2, (middleY + bottomY) / 2, segmentLength / 2, segmentThickness, 90f) // Bottom-left
        drawSegmentWithGlow(canvas, segments[5], centerX - segmentLength / 2, (topY + middleY) / 2, segmentLength / 2, segmentThickness, 90f) // Top-left
        drawSegmentWithGlow(canvas, segments[6], centerX, middleY, segmentLength, segmentThickness, 0f) // Middle
    }

    private fun drawSegmentWithGlow(
        canvas: Canvas,
        isOn: Boolean,
        cx: Float,
        cy: Float,
        length: Float,
        thickness: Float,
        angle: Float
    ) {
        canvas.save()
        canvas.rotate(angle, cx, cy)

        val path = createSegmentPath(cx, cy, length, thickness)

        if (isOn) {
            // Draw glow effect
            canvas.drawPath(path, glowPaint)
            // Draw main segment
            canvas.drawPath(path, paintOn)
        } else {
            canvas.drawPath(path, paintOff)
        }

        canvas.restore()
    }

    private fun createSegmentPath(cx: Float, cy: Float, length: Float, thickness: Float): Path {
        val path = Path()
        val halfLength = length / 2
        val halfThickness = thickness / 2
        val taper = thickness * 0.3f

        // Hexagonal segment shape
        path.moveTo(cx - halfLength + taper, cy)
        path.lineTo(cx - halfLength, cy - halfThickness)
        path.lineTo(cx + halfLength, cy - halfThickness)
        path.lineTo(cx + halfLength - taper, cy)
        path.lineTo(cx + halfLength, cy + halfThickness)
        path.lineTo(cx - halfLength, cy + halfThickness)
        path.close()

        return path
    }

    private fun drawColon(canvas: Canvas, x: Float, y: Float) {
        val dotRadius = width * 0.03f

        // Upper dot
        canvas.drawCircle(x, y - height * 0.15f, dotRadius, paintOn)

        // Lower dot
        canvas.drawCircle(x, y + height * 0.15f, dotRadius, paintOn)
    }
}