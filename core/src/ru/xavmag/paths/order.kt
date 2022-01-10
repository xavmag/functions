package ru.xavmag.paths

import kotlin.math.abs
import kotlin.math.max


infix fun Double.eq(other: Double) =
    abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 2

infix fun Double.neq(other: Double) =
    abs(this - other) > max(Math.ulp(this), Math.ulp(other)) * 2

infix fun Double.ge(other: Double) = this > other || this.eq(other)

infix fun Double.le(other: Double) = this < other || this.eq(other)


infix fun Float.eq(other: Float) =
    abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 2

infix fun Float.neq(other: Float) =
    abs(this - other) > max(Math.ulp(this), Math.ulp(other)) * 2

infix fun Float.ge(other: Float) = this > other || this.eq(other)

infix fun Float.le(other: Float) = this < other || this.eq(other)