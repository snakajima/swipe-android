package com.sys1yagi.swipe.core.view

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.DisplayMetrics
import android.view.WindowManager
import com.sys1yagi.swipe.core.entity.swipe.SwipeDocument
import com.sys1yagi.swipe.core.entity.swipe.SwipeElement
import com.sys1yagi.swipe.core.testtool.ServiceLocator
import com.sys1yagi.swipe.core.testtool.TestAssetsUtils
import com.sys1yagi.swipe.core.tool.SwipeEntityDecoder
import com.sys1yagi.swipe.core.tool.json.JsonConverter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipeRendererTest {

    lateinit var swipeDocument: SwipeDocument

    lateinit var renderer: SwipeRenderer

    @Before
    fun setUp() {
        val swipeEntityDecoder = SwipeEntityDecoder()
        swipeDocument = swipeEntityDecoder.decodeToSwipe(JsonConverter.getInstance(),
                TestAssetsUtils.loadFromAssets(
                        InstrumentationRegistry.getContext(),
                        "swipe.swipe")!!)
        renderer = SwipeRenderer(swipeDocument)
        renderer.displaySize = Rect(0, 0, 1794, 1080)
    }

    @Test
    fun measureMarkdown() {
        val paint = Paint()
        val scale = renderer.scale(renderer.displaySize, swipeDocument.dimension)
        val manager = ServiceLocator.getSystemService(Context.WINDOW_SERVICE, WindowManager::class.java)
        val metrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(metrics)

        run {
            renderer.displaySize = Rect(0, 0, 1794, 1080)
            paint.textSize = 92f * scale

            val height = renderer.measureMarkdownHeight(swipeDocument, swipeDocument.pages[0].elements[0], swipeDocument.pages[0].elements[0].markdown)
            assertThat(height).isEqualTo(paint.fontSpacing)
        }

        run {
            renderer.displaySize = Rect(0, 0, 1794, 1080)
            var expected = 0.0f
            paint.textSize = 60f * scale
            expected += paint.fontSpacing
            paint.textSize = 42f * scale
            expected += paint.fontSpacing * 4
            paint.textSize = 48f * scale
            expected += paint.fontSpacing * 2

            val height = renderer.measureMarkdownHeight(swipeDocument, swipeDocument.pages[1].elements[0], swipeDocument.pages[1].elements[0].markdown)
            assertThat(height).isEqualTo(expected)
        }
    }

    @Test
    fun scale() {
        run {
            renderer.displaySize = Rect(0, 0, 1794, 1080)
            val expected = 1794f / 1280f
            assertThat(renderer.scale(renderer.displaySize, swipeDocument.dimension))
                    .isEqualTo(expected)
        }
        run {
            renderer.displaySize = Rect(0, 0, 800, 1080)
            assertThat(renderer.scale(renderer.displaySize, swipeDocument.dimension))
                    .isEqualTo(1f)
        }
    }

    @Test
    fun elementWidth() {
        renderer.displaySize = Rect(0, 0, 1794, 1080)
        val scale = 1794f / 1280f
        val expected = (1280f * 66.7f / 100f) * scale

        val element = swipeDocument.pages[0].elements[0]
        assertThat(renderer.measureElement(swipeDocument, element, { it.w }))
                .isEqualTo(expected)
    }

    @Test
    fun elementHeight() {
        renderer.displaySize = Rect(0, 0, 1794, 1080)
        val scale = 1794f / 1280f
        val expected = 176f * scale
        val element = swipeDocument.pages[0].elements[1].elements!![0]
        assertThat(renderer.measureElement(swipeDocument, element, { it.h }))
                .isEqualTo(expected)
    }

    @Test
    fun calculateElementRect() {
        renderer.displaySize = Rect(0, 0, 1794, 1080)
        val scale = 1794f / 1280f
        val element = SwipeElement()
        element.x = "10"
        element.y = "10"
        element.w = "100"
        element.h = "100"

        val rect = renderer.calculateElementRect(swipeDocument, element, renderer.displaySize)
        assertThat(rect.left).isEqualTo((10 * scale).toInt())
        assertThat(rect.top).isEqualTo((10 * scale).toInt())
        assertThat(rect.right).isEqualTo((110 * scale).toInt())
        assertThat(rect.bottom).isEqualTo((110 * scale).toInt())
        assertThat(rect.width()).isEqualTo((100 * scale).toInt())
        assertThat(rect.height()).isEqualTo((100 * scale).toInt())
    }

}
