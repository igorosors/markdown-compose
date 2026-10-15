package io.github.igorosors.markdown

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.SingletonImageLoader
import io.github.igorosors.markdown.internal.ProvideMarkdownContext
import io.github.igorosors.markdown.internal.blocks.MarkdownBlock

/**
 * Shows [markdown] in a LazyColumn. The text is parsed in the background.
 * To keep a large document, parse it with [MarkdownDocument.parse] in a ViewModel
 */
@Composable
public fun LazyMarkdownColumn(
    markdown: String,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: MarkdownColors = MarkdownDefaults.colors(),
    typography: MarkdownTypography = MarkdownDefaults.typography(),
    blockSpacing: Dp = MarkdownDefaults.BlockSpacing,
    baseUrl: String? = null,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    loading: @Composable LazyItemScope.() -> Unit = {},
) {
    val document = rememberMarkdownDocument(markdown, baseUrl)
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
    ) {
        if (document != null) {
            markdownItems(
                document = document,
                onLinkClick = onLinkClick,
                colors = colors,
                typography = typography,
                blockSpacing = blockSpacing,
                imageLoader = imageLoader,
            )
        } else {
            item(content = loading)
        }
    }
}

/**
 * Shows [document] in a LazyColumn
 * Use [markdownItems] to add markdown to your own LazyColumn
 */
@Composable
public fun LazyMarkdownColumn(
    document: MarkdownDocument,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: MarkdownColors = MarkdownDefaults.colors(),
    typography: MarkdownTypography = MarkdownDefaults.typography(),
    blockSpacing: Dp = MarkdownDefaults.BlockSpacing,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
    ) {
        markdownItems(
            document = document,
            onLinkClick = onLinkClick,
            colors = colors,
            typography = typography,
            blockSpacing = blockSpacing,
            imageLoader = imageLoader,
        )
    }
}

/** Adds each top level block of [document] as a list item */
public fun LazyListScope.markdownItems(
    document: MarkdownDocument,
    onLinkClick: (String) -> Unit,
    colors: MarkdownColors = MarkdownDefaults.colors(),
    typography: MarkdownTypography = MarkdownDefaults.typography(),
    blockSpacing: Dp = MarkdownDefaults.BlockSpacing,
    imageLoader: ImageLoader? = null,
) {
    val blocks = document.blocks
    itemsIndexed(
        items = blocks,
        contentType = { _, block -> block::class },
    ) { index, block ->
        ProvideMarkdownContext(
            colors = colors,
            typography = typography,
            blockSpacing = blockSpacing,
            imageLoader = imageLoader ?: SingletonImageLoader.get(LocalContext.current),
            onLinkClick = onLinkClick,
        ) {
            MarkdownBlock(
                block = block,
                modifier = if (index < blocks.lastIndex) Modifier.padding(bottom = blockSpacing) else Modifier,
            )
        }
    }
}
