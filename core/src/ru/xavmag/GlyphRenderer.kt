package ru.xavmag

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.collections.GdxArray
import ktx.math.vec2
import ru.xavmag.paths.eq
import ru.xavmag.paths.neq


class GlyphRenderer(private val worldViewport: Viewport) {

    private val batch = SpriteBatch()
    private val font = BitmapFont()

    var color: Color = Color.WHITE
    set(value) {
        field = value
        outOfAxisColor.set(value.r, value.g, value.b, value.a * 0.7f)
    }
    private val outOfAxisColor = Color.WHITE.cpy().apply { a = 0.7f }

    fun draw(xPositions: GdxArray<Vector2>, yPositions: GdxArray<Vector2>) {
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        batch.begin()
        batch.projectionMatrix.setToOrtho2D(0f, 0f, width, height)
        xPositions.forEach {
            val text = it.x.let { x -> "${if ((x % 1f) eq 0f) x.toInt() else x}" }
            val textData = GlyphLayout(font, text)
            val pos = worldViewport.project(vec2(it.x, it.y))

            if (it.x neq 0f) {
                font.color = setupFontColor(pos.y, textData.height, height)
                if ((pos.y - textData.height < 0f)) {
                    pos.y = textData.height
                } else if ((pos.y > height)) {
                    pos.y = height
                }
                pos.x -= textData.width * 0.5f
            }
            font.draw(batch, text, pos.x, pos.y)
            font.color = color
        }
        yPositions.forEach {
            val text = it.y.let { y -> "${if ((y % 1f) eq 0f) y.toInt() else y}" }
            val textData = GlyphLayout(font, text)
            val pos = worldViewport.project(vec2(it.x, it.y))

            if (it.y neq 0f) {
                font.color = setupFontColor(pos.x, textData.width, width)
                if ((pos.x + textData.width > width)) {
                    pos.x = width - textData.width
                } else if (pos.x < 0f) {
                    pos.x = 0f
                }
                pos.y += textData.height * 0.5f
                font.draw(batch, text, pos.x, pos.y)
            }
            font.color = color
        }
        batch.end()
    }


    private fun setupFontColor(t: Float, t0: Float, t1: Float) =
        if (t < t0 || t > t1) { outOfAxisColor } else { color }
}