package ru.xavmag.paths

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Path
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.collections.GdxFloatArray
import ktx.graphics.use
import ktx.math.minus
import ktx.math.vec2

class PathRenderer {

    private val shapeRenderer = ShapeRenderer()

    fun draw(viewport: Viewport, path: Path<Vector2>, t0: Float, t1: Float, samples: Int, color: Color) {
        if (t1 <= t0) throw IllegalArgumentException("t1 must not be less than t0!")

        shapeRenderer.use(ShapeRenderer.ShapeType.Line, viewport.camera) {
            val vertices = GdxFloatArray()
            val d = t1 - t0
            repeat(samples) { i ->
                val t = d * i / samples
                val v = path.valueAt(vec2(), t)
                vertices.add(v.x, v.y)
            }
            vertices.shrink()
            it.color = color
            it.polyline(vertices.items)
        }
    }
}