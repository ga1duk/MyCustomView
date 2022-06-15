package ru.company.customviewtest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
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

    private var progress1 = 0F
    private var progress2 = 0F
    private var progress3 = 0F
    private var progress4 = 0F

    private var animator1: Animator? = null
    private var animator2: Animator? = null
    private var animator3: Animator? = null
    private var animator4: Animator? = null

    private var diagramFillingWay = 0

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
            diagramFillingWay = getInteger(R.styleable.StatsView_diagramFillingWay, 0)
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

        when (diagramFillingWay) {
            0 -> drawDiagramParallel(canvas)
            1 -> drawDiagramSequentially(canvas)
            2 -> drawDiagramBidirectionally(canvas)
            3 -> drawDiagramRotating(canvas)
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
        when (diagramFillingWay) {
            0 -> updateDiagramParallel()
            1 -> updateDiagramSequentially()
            2 -> updateDiagramBidirectionally()
            3 -> updateDiagramRotating()
        }
    }

    private fun drawDiagramParallel(canvas: Canvas) {
        var startAngle = -90F
        data.forEachIndexed { index, datum ->
            val angle = datum * 360
            arcPaint.color = colors.getOrElse(index) { getRandomColor() }
            canvas.drawArc(oval, startAngle, angle * progress1, false, arcPaint)
            startAngle += angle
        }
    }

    private fun drawDiagramSequentially(canvas: Canvas) {
        arcPaint.color = colors.getOrElse(0) { getRandomColor() }
        canvas.drawArc(oval, -90F, 90 * progress1, false, arcPaint)

        arcPaint.color = colors.getOrElse(1) { getRandomColor() }
        canvas.drawArc(oval, 0F, 90 * progress2, false, arcPaint)

        arcPaint.color = colors.getOrElse(2) { getRandomColor() }
        canvas.drawArc(oval, 90F, 90 * progress3, false, arcPaint)

        arcPaint.color = colors.getOrElse(3) { getRandomColor() }
        canvas.drawArc(oval, 180F, 90 * progress4, false, arcPaint)
    }

    private fun drawDiagramBidirectionally(canvas: Canvas) {
        var startAngle = -45F
        data.forEachIndexed { index, datum ->
            val angle = datum * 180
            arcPaint.color = colors.getOrElse(index) { getRandomColor() }
            canvas.drawArc(oval, startAngle, angle * progress1, false, arcPaint)
            canvas.drawArc(oval, startAngle, -angle * progress1, false, arcPaint)
            startAngle += 90
        }
    }

    private fun drawDiagramRotating(canvas: Canvas) {
        var startAngle = -90F + progress1 * 360
        data.forEachIndexed { index, datum ->
            val angle = datum * 360
            arcPaint.color = colors.getOrElse(index) { getRandomColor() }
            canvas.drawArc(oval, startAngle, angle * progress1, false, arcPaint)
            startAngle += angle
        }
    }

    private fun updateDiagramParallel() {
        animator1?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator1 = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress1 = animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
            startDelay = 1000
            addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                start()
            }
        })
            start()
        }
    }

    private fun updateDiagramSequentially() {
        animator1?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator2?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator3?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator4?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator1 = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress1 = animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
        }

        animator2 = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress2 = animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
        }

        animator3 = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress3 = animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
        }

        animator4 = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress4 = animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
        }

        AnimatorSet().apply {
            playSequentially(animator1, animator2, animator3, animator4)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animation.startDelay = 1000
                    start()
                }
            })
        }.start()
    }

    private fun updateDiagramBidirectionally() {
        animator1?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator1 = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress1 = animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
            startDelay = 1000
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    start()
                }
            })
            start()
        }
    }

    private fun updateDiagramRotating() {
        animator1?.let {
            it.cancel()
            it.removeAllListeners()
        }

        animator1 = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                progress1 = animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
            startDelay = 1000
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    start()
                }
            })
            start()
        }
    }

    private fun getRandomColor() = nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}