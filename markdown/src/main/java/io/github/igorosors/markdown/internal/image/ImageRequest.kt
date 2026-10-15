package io.github.igorosors.markdown.internal.image

import android.content.Context
import coil3.request.ImageRequest
import coil3.size.Dimension
import coil3.size.Precision
import coil3.size.Size

/**
 * Uses [svgDecoderFactory] to keep the SVG size from the file. With [maxWidthPx] the image is decoded no wider
 * than that, and [Precision.INEXACT] keeps small images from being upscaled
 */
internal fun markdownImageRequest(context: Context, url: String, maxWidthPx: Int? = null): ImageRequest =
    ImageRequest.Builder(context)
        .data(url)
        .decoderFactory(svgDecoderFactory)
        // Separate cache key, so the memory cache never returns an SVG decoded without the source size
        .memoryCacheKeyExtra("markdown_svg", "intrinsic_size")
        .apply {
            if (maxWidthPx != null) {
                size(Size(Dimension(maxWidthPx), Dimension.Undefined))
                precision(Precision.INEXACT)
            }
        }
        .build()
