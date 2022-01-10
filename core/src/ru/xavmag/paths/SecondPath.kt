package ru.xavmag.paths

import com.badlogic.gdx.math.Path
import com.badlogic.gdx.math.Vector2
import kotlin.math.pow

class SecondPath : Path<Vector2> {
    override fun derivativeAt(out: Vector2, t: Float): Vector2 {
        TODO("Not yet implemented")
    }

    override fun valueAt(out: Vector2, t: Float): Vector2 {
        return out.set(
            2f.pow(t - 1),
            0.25f * (t * t * t + 1)
        )
    }

    override fun approximate(v: Vector2): Float {
        TODO("Not yet implemented")
    }

    override fun locate(v: Vector2): Float {
        TODO("Not yet implemented")
    }

    override fun approxLength(samples: Int): Float {
        TODO("Not yet implemented")
    }
}