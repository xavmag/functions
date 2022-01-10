package ru.xavmag

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import ktx.actors.*
import ktx.app.KtxApplicationAdapter
import ktx.app.clearScreen
import ktx.math.minus
import ktx.math.vec2
import ktx.scene2d.KWidget
import ktx.scene2d.actor
import ktx.scene2d.actors
import ktx.scene2d.scene2d
import ktx.scene2d.vis.*
import ru.xavmag.paths.*
import kotlin.math.exp



class CustomApplicationListener : KtxApplicationAdapter {

    private val camera = OrthographicCamera()
    private val viewport by lazy { ScreenViewport(camera) }
    private val gridRenderer by lazy { GridRenderer().apply { decreaseScale(viewport) } }
    private val glyphRenderer by lazy { GlyphRenderer(viewport) }
    private val pathRenderer by lazy { PathRenderer() }
    private val firstPath by lazy { FirstPath() }
    private val secondPath by lazy { SecondPath() }

    data class PathParameters(
        var t0: Float = 0f,
        var t1: Float = 1f,
        var samples: Int = 100,
        var color: Color = Color.GREEN
    )

    private val firstParameters = PathParameters()
    private val secondParameters = PathParameters(color = Color.RED)

    private val stage by lazy { stage(viewport = ScreenViewport()) }

    private val bg = Color.DARK_GRAY

    private var linearZoom = -3.2f
    private var zoom = exp(linearZoom)

    private lateinit var inputReceiver: Actor

    private lateinit var firstColorPicker: KVisSelectBox<String>
    private lateinit var secondColorPicker: KVisSelectBox<String>

    override fun create() {
        VisUI.load()
        secondParameters.t0 = 0f
        Gdx.input.inputProcessor = stage
        setupUI()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        inputReceiver.setSize(width.toFloat(), height.toFloat())
        inputReceiver.setPosition(0f, 0f)
        viewport.update(width, height)
    }

    override fun render() {
        camera.zoom = zoom
        viewport.apply(false)
        clearScreen(bg.r, bg.g, bg.b, 1f)
        gridRenderer.draw(viewport)
        pathRenderer.draw(viewport, firstPath,
            firstParameters.t0, firstParameters.t1, firstParameters.samples,
            firstParameters.color
        )
        pathRenderer.draw(viewport, secondPath,
            secondParameters.t0, secondParameters.t1, secondParameters.samples,
            secondParameters.color
        )
        glyphRenderer.draw(gridRenderer.xPositions, gridRenderer.yPositions)

        stage.act()
        stage.draw()
    }

    override fun dispose() {
        VisUI.dispose()
    }

    private fun setupUI() {
        stage.actors {
            inputReceiver = actor(Actor()) {
                width = stage.viewport.worldWidth
                height = stage.viewport.worldHeight
                setScrollFocus(true)
                addListener(object : KtxInputListener() {
                    private var drag = false

                    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        drag = button == Buttons.RIGHT
                        return drag
                    }

                    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                        drag = false
                    }

                    override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                        if (drag) {
                            val pos = viewport.unproject(localToScreenCoordinates(vec2(x, y)))
                            val pos0 = viewport.unproject(localToScreenCoordinates(vec2(x - Gdx.input.deltaX, y + Gdx.input.deltaY)))
                            camera.translate(pos0 - pos)
                        }
                    }

                    override fun scrolled(event: InputEvent, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
                        linearZoom += event.scrollAmountY * 0.05f
                        zoom = exp(linearZoom)
                        if (event.scrollAmountY < 0f) {
                            gridRenderer.increaseScale(viewport)
                        } else if (event.scrollAmountY > 0f) {
                            gridRenderer.decreaseScale(viewport)
                        }
                        return true
                    }
                })
            }
            visWindow("Settings") {
                width = 600f
                height = 160f
                visTable(defaultSpacing = true) {
                    defaults().left().width(65f)
                    setFillParent(true)
                    padTop(20f)
                    visLabel("FIRST").cell(align = Align.center, colspan = 4)
                    visLabel("SECOND").cell(row = true, align = Align.center, colspan = 4)

                    visLabel("Color: ").cell(colspan = 2)
                    firstColorPicker = colorPicker {
                        selected = "Green"
                        onChange {
                            firstParameters.color = Colors.get(selected.uppercase())
                        }
                    }.cell(colspan = 2)
                    visLabel("Color: ").cell(colspan = 2)
                    secondColorPicker = colorPicker {
                        selected = "Red"
                        onChange {
                            secondParameters.color = Colors.get(selected.uppercase())
                        }
                    }.cell(colspan = 2, row = true)

                    validator {
                        visLabel("t0: ")
                        var tField = visValidatableTextField("0")
                        floatNumber(tField, "t0 must be float")
                        notEmpty(tField, "t0 must mot be empty")
                        tField.onChange {
                            try {
                                firstParameters.apply { val nt = text.toFloat(); if (t1 > nt) { t0 = nt } }
                            } catch (e: Exception) { }
                        }

                        visLabel("t1: ")
                        tField = visValidatableTextField("1")
                        floatNumber(tField, "t1 must be float")
                        notEmpty(tField, "t1 must mot be empty")
                        tField.onChange {
                            try {
                                firstParameters.apply { val nt = text.toFloat(); if (nt > t0) { t1 = nt } }
                            } catch (e: Exception) { }
                        }

                        visLabel("t0: ")
                        tField = visValidatableTextField("0")
                        floatNumber(tField, "t0 must be float")
                        notEmpty(tField, "t0 must mot be empty")
                        tField.onChange {
                            try {
                                secondParameters.apply { val nt = text.toFloat(); if (t1 > nt) { t0 = nt } }
                            } catch (e: Exception) { }
                        }

                        visLabel("t1: ")
                        tField = visValidatableTextField("1")
                        floatNumber(tField, "t1 must be float")
                        notEmpty(tField, "t1 must mot be empty")
                        tField.onChange {
                            try {
                                secondParameters.apply { val nt = text.toFloat(); if (nt > t0) { t1 = nt } }
                            } catch (e: Exception) { }
                        }

                        setMessageLabel(scene2d.visLabel(""))
                    }
                    row()

                    validator {
                        visLabel("Samples: ").cell(colspan = 2)
                        var samplesField = visValidatableTextField("100")
                        notEmpty(samplesField, "Enter samples count")
                        integerNumber(samplesField, "Samples must be integer number!")
                        samplesField.onChange {
                            try {
                                firstParameters.samples = text.run { toInt().coerceAtLeast(0) }
                            } catch (e: Exception) {

                            }
                        }
                        samplesField.cell(colspan = 2)

                        visLabel("Samples: ").cell(colspan = 2)
                        samplesField = visValidatableTextField("100")
                        notEmpty(samplesField, "Enter samples count")
                        integerNumber(samplesField, "Samples must be integer number!")
                        samplesField.onChange {
                            try {
                                secondParameters.samples = text.run { toInt().coerceAtLeast(0) }
                            } catch (e: Exception) {

                            }
                        }
                        samplesField.cell(colspan = 2)
                    }
                    row()
                    pack()
                }
            }
        }
    }

    private fun KWidget<*>.colorPicker(init: KVisSelectBox<String>.() -> Unit = {}): KVisSelectBox<String> = visSelectBox {
        -"Blue"
        -"Navy"
        -"Royal"
        -"Slate"
        -"Sky"
        -"Cyan"
        -"Teal"

        -"Green"
        -"Chartreuse"
        -"Lime"
        -"Forest"
        -"Olive"

        -"Yellow"
        -"Gold"
        -"Goldenrod"
        -"Orange"

        -"Brown"
        -"Tan"
        -"Firebrick"

        -"Red"
        -"Scarlet"
        -"Coral"
        -"Salmon"
        -"Pink"
        -"Magenta"

        -"Purple"
        -"Violet"
        -"Maroon"
        init()
    }
}