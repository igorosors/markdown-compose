package io.github.igorosors.markdown.internal.text

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import coil3.ImageLoader
import coil3.toBitmap
import io.github.igorosors.markdown.internal.image.ImageSize
import io.github.igorosors.markdown.internal.image.imageSize
import io.github.igorosors.markdown.internal.image.markdownImageRequest
import kotlinx.coroutines.launch

internal sealed interface InlineImageState {

    data object Failed : InlineImageState

    class Loaded(val bitmap: ImageBitmap, val size: ImageSize) : InlineImageState
}

@Composable
internal fun rememberInlineImages(
    urls: List<String>,
    imageLoader: ImageLoader,
    maxWidthPx: Int,
): Map<String, InlineImageState> {
    val context = LocalContext.current
    val density = LocalDensity.current
    var images by remember { mutableStateOf(emptyMap<String, InlineImageState>()) }

    LaunchedEffect(urls, imageLoader, density, maxWidthPx) {
        images = images.filterKeys { it in urls }
        if (urls.isEmpty() || maxWidthPx == 0) return@LaunchedEffect

        // The effect runs on the single main thread, and the update of images has no suspension point,
        // so loads that finish together cannot overwrite each other. A multithreaded dispatcher would break this
        for (url in urls) {
            launch {
                val state = imageLoader.loadInlineImage(context, density, url, maxWidthPx)
                images = images + (url to state)
            }
        }
    }
    return images
}

internal suspend fun ImageLoader.loadInlineImage(
    context: Context,
    density: Density,
    url: String,
    maxWidthPx: Int,
): InlineImageState {
    val image = execute(markdownImageRequest(context, url, maxWidthPx)).image
        ?: return InlineImageState.Failed
    val size = image.imageSize().fitWidth(maxWidth = maxWidthPx / density.density)
    return InlineImageState.Loaded(image.toBitmap().asImageBitmap(), size)
}
