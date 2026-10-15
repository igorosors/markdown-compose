package io.github.igorosors.markdown.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import coil3.ImageLoader
import io.github.igorosors.markdown.MarkdownColors
import io.github.igorosors.markdown.MarkdownTypography

@Immutable
internal class MarkdownContext(
    val colors: MarkdownColors,
    val typography: MarkdownTypography,
    val blockSpacing: Dp,
    val imageLoader: ImageLoader,
    val onLinkClick: (String) -> Unit,
)

internal val LocalMarkdownContext = staticCompositionLocalOf<MarkdownContext> {
    error("LocalMarkdownContext is not provided")
}

internal val LocalMarkdownTextStyle = staticCompositionLocalOf<TextStyle> {
    error("LocalMarkdownTextStyle is not provided")
}

@Composable
internal fun ProvideMarkdownContext(
    colors: MarkdownColors,
    typography: MarkdownTypography,
    blockSpacing: Dp,
    imageLoader: ImageLoader,
    onLinkClick: (String) -> Unit,
    content: @Composable () -> Unit,
) {
    val currentOnLinkClick by rememberUpdatedState(onLinkClick)
    val stableOnLinkClick = remember { { url: String -> currentOnLinkClick(url) } }
    val context = remember(colors, typography, blockSpacing, imageLoader) {
        MarkdownContext(
            colors = colors,
            typography = typography,
            blockSpacing = blockSpacing,
            imageLoader = imageLoader,
            onLinkClick = stableOnLinkClick,
        )
    }

    CompositionLocalProvider(
        LocalMarkdownContext provides context,
        LocalMarkdownTextStyle provides typography.text.copy(color = colors.text),
        content = content,
    )
}
