package io.github.igorosors.markdown.internal.image

import coil3.Image
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.request.Options
import coil3.request.maxBitmapSize
import coil3.size.Size
import coil3.size.pxOrElse
import coil3.svg.Svg
import coil3.svg.SvgDecoder
import coil3.svg.height
import coil3.svg.width
import coil3.toBitmap
import kotlin.math.roundToInt

/**
 * SVG decoder that keeps the size declared in the file. The default decoder replaces width/height
 * with 100% before rendering, so the size is captured at parse time and returned in [SvgWithSourceSize].
 */
@OptIn(ExperimentalCoilApi::class)
internal val svgDecoderFactory = SvgDecoder.Factory(
    parser = { source -> SourceSizeSvg(Svg.Parser.DEFAULT.parse(source)) },
    useViewBoundsAsIntrinsicSize = false,
    renderToBitmap = false,
)

internal class SvgWithSourceSize(
    bitmap: Image,
    val sourceWidth: Float,
    val sourceHeight: Float,
    val viewBoxWidth: Float,
    val viewBoxHeight: Float,
) : Image by bitmap

@OptIn(ExperimentalCoilApi::class)
private class SourceSizeSvg(private val svg: Svg) : Svg by svg {
    private val sourceWidth = svg.width
    private val sourceHeight = svg.height
    private val viewBoxWidth = svg.viewBox?.width ?: -1f
    private val viewBoxHeight = svg.viewBox?.height ?: -1f

    /** The request's bitmap size limit, the same one the decoder applies to sized SVGs. */
    private var maxBitmapSize: Size = Size.ORIGINAL

    override fun options(options: Options) {
        maxBitmapSize = options.maxBitmapSize
        svg.options(options)
    }

    /**
     * For an SVG with width/height the decoder already computes a proportional size within the limit.
     * Without them the decoder takes the size from the request, and in a scrollable list the requested height
     * is Int.MAX_VALUE. So the height is derived from the viewBox aspect ratio, or from the default
     * if there is no viewBox, and the size is scaled down proportionally to fit the limit.
     */
    override fun asImage(width: Int, height: Int): Image {
        val (bitmapWidth, bitmapHeight) = when {
            sourceWidth > 0f && sourceHeight > 0f -> width to height
            viewBoxWidth > 0f && viewBoxHeight > 0f -> fitMaxBitmapSize(width, viewBoxWidth / viewBoxHeight)
            else -> {
                // Without a viewBox the content is not scaled, so use the default area
                svg.viewBox = Svg.ViewBox(0f, 0f, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT)
                fitMaxBitmapSize(width, DEFAULT_IMAGE_WIDTH / DEFAULT_IMAGE_HEIGHT)
            }
        }

        return SvgWithSourceSize(
            bitmap = svg.asImage(bitmapWidth, bitmapHeight).toBitmap().asImage(),
            sourceWidth = sourceWidth,
            sourceHeight = sourceHeight,
            viewBoxWidth = viewBoxWidth,
            viewBoxHeight = viewBoxHeight,
        )
    }

    /** Bitmap of [width] with [aspectRatio], scaled down proportionally to fit [maxBitmapSize] */
    private fun fitMaxBitmapSize(width: Int, aspectRatio: Float): Pair<Int, Int> {
        val height = width / aspectRatio
        val maxWidth = maxBitmapSize.width.pxOrElse { Int.MAX_VALUE }
        val maxHeight = maxBitmapSize.height.pxOrElse { Int.MAX_VALUE }
        val scale = minOf(1f, maxWidth / width.toFloat(), maxHeight / height)
        return (width * scale).roundToInt().coerceAtLeast(1) to (height * scale).roundToInt().coerceAtLeast(1)
    }
}
