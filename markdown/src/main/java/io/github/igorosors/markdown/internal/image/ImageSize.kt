package io.github.igorosors.markdown.internal.image

import coil3.Image

/** Size of an image without an intrinsic size */
internal const val DEFAULT_IMAGE_WIDTH = 300f
internal const val DEFAULT_IMAGE_HEIGHT = 150f

internal class ImageSize(
    val width: Float,
    val height: Float,
    val fillMaxWidth: Boolean,
) {
    val aspectRatio: Float get() = width / height

    /** Scales down proportionally so that the width does not exceed [maxWidth] */
    fun fitWidth(maxWidth: Float): ImageSize {
        return if (width <= maxWidth) this else ImageSize(maxWidth, height * maxWidth / width, fillMaxWidth)
    }
}

internal fun Image.imageSize(): ImageSize {
    return when (this) {
        is SvgWithSourceSize -> {
            when {
                sourceWidth > 0f && sourceHeight > 0f -> {
                    ImageSize(
                        width = sourceWidth,
                        height = sourceHeight,
                        fillMaxWidth = false,
                    )
                }

                viewBoxWidth > 0f && viewBoxHeight > 0f -> {
                    ImageSize(
                        width = viewBoxWidth,
                        height = viewBoxHeight,
                        fillMaxWidth = true,
                    )
                }

                else -> {
                    ImageSize(
                        width = DEFAULT_IMAGE_WIDTH,
                        height = DEFAULT_IMAGE_HEIGHT,
                        fillMaxWidth = false,
                    )
                }
            }
        }

        else -> {
            if (width > 0 && height > 0) {
                ImageSize(
                    width = width.toFloat(),
                    height = height.toFloat(),
                    fillMaxWidth = false,
                )
            } else {
                ImageSize(
                    width = DEFAULT_IMAGE_WIDTH,
                    height = DEFAULT_IMAGE_HEIGHT,
                    fillMaxWidth = false,
                )
            }
        }
    }
}
