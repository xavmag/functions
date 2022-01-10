package ru.xavmag

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.collections.GdxArray
import ktx.graphics.use
import ktx.math.minus
import ktx.math.vec2

class GridRenderer(val minUnitSizeInPixels: Int = 100,
                   val maxUnitSizeInPixels: Int = 200) {

    private val shapeRenderer = ShapeRenderer()

    var unitLengthInMeters = 2.0f
    private set

    private var currentUnitLengthOrder = 0

    var color = Color.LIGHT_GRAY
    var innerColor = Color.GRAY
    var xAxisColor = Color.GREEN
    var yAxisColor = Color.RED

    private val xCoords = GdxArray<Float>()
    private val yCoords = GdxArray<Float>()
    private val screenRect = Rectangle()

    val xPositions: GdxArray<Vector2>
    get() {
        val array = GdxArray<Vector2>()
        xCoords.forEach { array.add(vec2(it, 0f)) }
        return array
    }

    val yPositions: GdxArray<Vector2>
    get() {
        val array = GdxArray<Vector2>()
        yCoords.forEach { array.add(vec2(0f, it)) }
        return array
    }

    fun draw(viewport: Viewport) {
        shapeRenderer.use(ShapeRenderer.ShapeType.Line, viewport.camera) {
            val (p1, p2) = viewport.unprojectCorners()
            screenRect.set(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y)
            val startLeft = unitLengthInMeters.toBigDecimal() * ((p1.x / unitLengthInMeters).toInt() - 1).toBigDecimal()
            val startBottom = unitLengthInMeters.toBigDecimal() * ((p1.y / unitLengthInMeters).toInt() - 1).toBigDecimal()

            val (innerGapLength, numOfInnerGaps) = unitLengthByOrder(currentUnitLengthOrder - 2)

            it.color = innerColor

            var fromLeftToRight = startLeft
            while (fromLeftToRight.toFloat() < p2.x) {
                repeat(numOfInnerGaps - 1) { i ->
                    val x = (fromLeftToRight.toFloat() + innerGapLength * (i+1))
                    it.line(x, p1.y, x, p2.y)
                }
                fromLeftToRight += unitLengthInMeters.toBigDecimal()
            }
            var fromBottomToTop = startBottom
            while (fromBottomToTop.toFloat() < p2.y) {
                repeat(numOfInnerGaps - 1) { i ->
                    val y = (fromBottomToTop.toFloat() + innerGapLength * (i+1))
                    it.line(p1.x, y, p2.x, y)
                }
                fromBottomToTop += unitLengthInMeters.toBigDecimal()
            }

            it.color = color

            xCoords.clear()
            fromLeftToRight = startLeft
            while (fromLeftToRight.toFloat() < p2.x) {
                it.line(fromLeftToRight.toFloat(), p1.y, fromLeftToRight.toFloat(), p2.y)
                xCoords.add(fromLeftToRight.toFloat())
                fromLeftToRight += unitLengthInMeters.toBigDecimal()
            }
            yCoords.clear()
            fromBottomToTop = startBottom
            while (fromBottomToTop.toFloat() < p2.y) {
                it.line(p1.x, fromBottomToTop.toFloat(), p2.x, fromBottomToTop.toFloat())
                yCoords.add(fromBottomToTop.toFloat())
                fromBottomToTop += unitLengthInMeters.toBigDecimal()
            }

            it.color = xAxisColor
            it.line(0f, p1.y, 0f, p2.y)

            it.color = yAxisColor
            it.line(p1.x, 0f, p2.x, 0f)
        }
    }

    fun increaseScale(viewport: Viewport) {
        val unitLengthInMetersSup = viewport.run {
            unproject(vec2(screenX + maxUnitSizeInPixels.toFloat())) - unproject(vec2(screenX.toFloat()))
        }.x
        if (unitLengthInMeters > unitLengthInMetersSup) {
            while (unitLengthInMeters > unitLengthInMetersSup) {
                currentUnitLengthOrder -= 1
                unitLengthInMeters = unitLengthByOrder(currentUnitLengthOrder).first
            }
        }
    }

    fun decreaseScale(viewport: Viewport) {
        val unitLengthInMetersInf = viewport.run {
            unproject(vec2(screenX + minUnitSizeInPixels.toFloat())) - unproject(vec2(screenX.toFloat()))
        }.x
        if (unitLengthInMeters < unitLengthInMetersInf) {
            while (unitLengthInMeters < unitLengthInMetersInf) {
                currentUnitLengthOrder += 1
                unitLengthInMeters = unitLengthByOrder(currentUnitLengthOrder).first
            }
        }
    }


    private fun unitLengthByOrder(i: Int): Pair<Float, Int> {
        val (mantissa, numOfGaps) = when(i % 3) {
            0 -> Pair(1f, 5)
            1, -2 -> Pair(2f, 5)
            2, -1 -> Pair(5f, 4)
            else -> error("???")
        }
        return Pair("${mantissa}E${(if (i >= 0) i else { i - 2 }) / 3 }F".toFloat(), numOfGaps)
    }
}