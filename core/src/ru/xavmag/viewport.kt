package ru.xavmag

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.math.vec2


fun Viewport.unprojectCorners(): Pair<Vector2, Vector2> = Pair(
    unproject(vec2(screenX.toFloat(), screenY.toFloat() + screenHeight)),
    unproject(vec2(screenX + screenWidth.toFloat(), screenY.toFloat()))
)