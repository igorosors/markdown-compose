package io.github.igorosors.markdown

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil3.ImageLoader
import coil3.SingletonImageLoader
import io.github.igorosors.markdown.internal.ProvideMarkdownContext
import io.github.igorosors.markdown.internal.blocks.MarkdownBlocks

/**
 * Shows [markdown] with all blocks at once in a Column.
 * For large documents use [LazyMarkdownColumn].
 *
 * @param markdown markdown text.
 * @param onLinkClick called with the URL of a clicked link or linked image.
 * @param modifier modifier of the container.
 * @param colors element colors, see [MarkdownDefaults.colors].
 * @param typography element text styles, see [MarkdownDefaults.typography].
 * @param blockSpacing vertical spacing between blocks.
 * @param baseUrl prepended to link and image URLs that start with a single slash, like /path.
 * URLs like //host/path get https. Other URLs are kept as is. If null, all URLs are kept.
 * @param imageLoader Coil image loader
 */
@Composable
public fun MarkdownColumn(
    markdown: String,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: MarkdownColors = MarkdownDefaults.colors(),
    typography: MarkdownTypography = MarkdownDefaults.typography(),
    blockSpacing: Dp = MarkdownDefaults.BlockSpacing,
    baseUrl: String? = null,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
) {
    val document = remember(markdown, baseUrl) { MarkdownDocument.parseBlocking(markdown, baseUrl) }
    MarkdownColumn(
        document = document,
        onLinkClick = onLinkClick,
        modifier = modifier,
        colors = colors,
        typography = typography,
        blockSpacing = blockSpacing,
        imageLoader = imageLoader,
    )
}

/** Shows [document] with all blocks at once in a Column */
@Composable
public fun MarkdownColumn(
    document: MarkdownDocument,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: MarkdownColors = MarkdownDefaults.colors(),
    typography: MarkdownTypography = MarkdownDefaults.typography(),
    blockSpacing: Dp = MarkdownDefaults.BlockSpacing,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
) {
    ProvideMarkdownContext(colors, typography, blockSpacing, imageLoader, onLinkClick) {
        Column(modifier) {
            MarkdownBlocks(document.blocks)
        }
    }
}
