package io.github.igorosors.markdown.internal.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.LocalMarkdownTextStyle
import io.github.igorosors.markdown.internal.parser.altText
import org.commonmark.node.Image

/** If the image fails to load, its alt text is shown instead, like in a browser. */
@Composable
internal fun MarkdownImage(
    image: Image,
    onClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val altText = remember(image) { image.altText().trim() }
    var size by remember(image.destination) { mutableStateOf<ImageSize?>(null) }
    var failed by remember(image.destination) { mutableStateOf(false) }
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    if (failed) {
        if (altText.isNotEmpty()) ImageAltText(altText, isLink = onClick != null, modifier = clickModifier)
        return
    }

    AsyncImage(
        modifier = Modifier
            .imageSize(size)
            .then(clickModifier),
        model = markdownImageRequest(context, image.destination),
        imageLoader = LocalMarkdownContext.current.imageLoader,
        contentDescription = altText.ifEmpty { null },
        contentScale = ContentScale.Fit,
        onSuccess = { state -> size = state.result.image.imageSize() },
        onError = { failed = true },
    )
}

@Composable
private fun ImageAltText(text: String, isLink: Boolean, modifier: Modifier) {
    val markdown = LocalMarkdownContext.current
    val style = LocalMarkdownTextStyle.current
    BasicText(
        modifier = modifier,
        text = text,
        style = if (isLink) style.merge(markdown.typography.link.copy(color = markdown.colors.link)) else style,
    )
}

/**
 * Width and aspect ratio. With ContentScale.Fit and unbounded height the painter
 * does not scale the bitmap beyond its pixel height, while the image is shown in dp, which are larger.
 */
private fun Modifier.imageSize(size: ImageSize?): Modifier = when {
    size == null -> {
        this
    }

    size.fillMaxWidth -> {
        this
            .fillMaxWidth()
            .aspectRatio(size.aspectRatio)
    }

    else -> {
        this
            .widthIn(max = size.width.dp)
            .fillMaxWidth()
            .aspectRatio(size.aspectRatio)
    }
}
