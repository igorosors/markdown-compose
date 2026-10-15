package io.github.igorosors.markdown.internal.image

import android.graphics.Canvas
import coil3.Image
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageSizeTest {

    @Test
    fun `image narrower than the limit keeps its size`() {
        val size = ImageSize(200f, 100f, fillMaxWidth = false)
        assertSame(size, size.fitWidth(300f))
    }

    @Test
    fun `wide image is scaled down proportionally`() {
        val size = ImageSize(1600f, 600f, fillMaxWidth = false).fitWidth(400f)
        assertEquals(400f, size.width, DELTA)
        assertEquals(150f, size.height, DELTA)
    }

    @Test
    fun `aspect ratio is width to height`() {
        assertEquals(2f, ImageSize(300f, 150f, fillMaxWidth = false).aspectRatio, DELTA)
    }

    @Test
    fun `bitmap keeps its pixel size`() {
        val size = FakeImage(64, 32).imageSize()
        assertEquals(64f, size.width, DELTA)
        assertEquals(32f, size.height, DELTA)
        assertFalse(size.fillMaxWidth)
    }

    @Test
    fun `bitmap without a size gets the default size`() {
        assertDefaultSize(FakeImage(0, 0).imageSize())
    }

    @Test
    fun `SVG with width and height keeps that size`() {
        val size = svg(sourceWidth = 208f, sourceHeight = 128f, viewBoxWidth = 10f, viewBoxHeight = 10f).imageSize()
        assertEquals(208f, size.width, DELTA)
        assertEquals(128f, size.height, DELTA)
        assertFalse(size.fillMaxWidth)
    }

    @Test
    fun `SVG with only a viewBox fills the width with its proportions`() {
        val size = svg(sourceWidth = -1f, sourceHeight = -1f, viewBoxWidth = 400f, viewBoxHeight = 100f).imageSize()
        assertTrue(size.fillMaxWidth)
        assertEquals(4f, size.aspectRatio, DELTA)
    }

    @Test
    fun `SVG without a size and viewBox gets the default size`() {
        assertDefaultSize(svg(sourceWidth = -1f, sourceHeight = -1f, viewBoxWidth = -1f, viewBoxHeight = -1f).imageSize())
    }

    private fun assertDefaultSize(size: ImageSize) {
        assertEquals(DEFAULT_IMAGE_WIDTH, size.width, DELTA)
        assertEquals(DEFAULT_IMAGE_HEIGHT, size.height, DELTA)
        assertFalse(size.fillMaxWidth)
    }

    private fun svg(sourceWidth: Float, sourceHeight: Float, viewBoxWidth: Float, viewBoxHeight: Float) =
        SvgWithSourceSize(FakeImage(1, 1), sourceWidth, sourceHeight, viewBoxWidth, viewBoxHeight)

    private class FakeImage(override val width: Int, override val height: Int) : Image {
        override val size: Long = 0
        override val shareable: Boolean = true
        override fun draw(canvas: Canvas) = Unit
    }

    private companion object {
        const val DELTA = 0.001f
    }
}
