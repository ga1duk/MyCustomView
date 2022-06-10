package ru.company.customviewtest

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.math.min
import kotlin.random.Random.Default.nextInt

private const val customStrokeWidth = 15F
private const val customTextSize = 40F
private const val dotRadius = 20F

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context, attrs, defStyleAttr, defStyleRes
) {

    private var radius: Float = 0F
    private var center: PointF = PointF()
    private var oval = RectF()

    private val strokeSize = AndroidUtils.convertDpToPx(context, customStrokeWidth)

    private var colors: List<Int> = emptyList()

    private var progress = 0F
    private var animator: Animator? = null

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = strokeSize
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = AndroidUtils.convertDpToPx(context, customTextSize)
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.green)
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = strokeSize
        color = resources.getColor(R.color.gray)
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            arcPaint.strokeWidth =
                getDimension(R.styleable.StatsView_strokeWidth, arcPaint.strokeWidth)
            circlePaint.strokeWidth =
                getDimension(R.styleable.StatsView_strokeWidth, circlePaint.strokeWidth)
            textPaint.textSize = getDimension(R.styleable.StatsView_textSize, textPaint.textSize)
            colors = listOf(
                getColor(R.styleable.StatsView_color1, getRandomColor()),
                getColor(R.styleable.StatsView_color2, getRandomColor()),
                getColor(R.styleable.StatsView_color3, getRandomColor()),
                getColor(R.styleable.StatsView_color4, getRandomColor())
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - arcPaint.strokeWidth / 2F
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        canvas.drawCircle(center.x, center.y, radius, circlePaint)

        var startAngle = -90F + progress * 360
        data.forEachIndexed { index, datum ->
            val angle = datum * 360
            arcPaint.color = colors.getOrElse(index) { getRandomColor() }
            canvas.drawArc(oval, startAngle, angle * progress, false, arcPaint)
            startAngle += angle
        }

//        canvas.drawCircle(center.x + 5F, center.y - radius, dotRadius, dotPaint)


        canvas.drawText(
            "%.2f%%".format(data.sum() * 100F),
            center.x,
            center.y + textPaint.textSize / 3F,
            textPaint
        )
    }

    private fun update() {
        animator?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress = animatedValue as Float
                invalidate()
            }
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun getRandomColor() = nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}