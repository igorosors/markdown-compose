package io.github.igorosors.markdown.internal.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.LocalMarkdownTextStyle
import io.github.igorosors.markdown.internal.parser.imageUrls
import org.commonmark.node.Node

/**
 * Text is shown at once. Inline images load async, no wider than the available width, and appear as they
 * arrive, see [rememberInlineImages]
 */
@Composable
internal fun MarkdownText(
    block: Node,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTextStyle.current,
) {
    val markdown = LocalMarkdownContext.current
    val density = LocalDensity.current

    val typography = markdown.typography
    val colors = markdown.colors
    val onLinkClick = markdown.onLinkClick

    val imageUrls = remember(block) { block.imageUrls() }
    var availableWidthPx by remember { mutableIntStateOf(0) }
    val images = rememberInlineImages(imageUrls, markdown.imageLoader, availableWidthPx)

    val inlineText = remember(block, typography, colors, density, onLinkClick, images) {
        InlineTextBuilder(typography, colors, density, onLinkClick).build(block, images)
    }

    val codeBackground = rememberInlineCodeBackground(style, typography.code.fontSize, colors.codeBackground)

    BasicText(
        modifier = modifier
            .onAvailableWidth { availableWidthPx = it }
            .then(codeBackground.modifier),
        text = inlineText.text,
        style = style.fitInlineImages(inlineText.tallestImageHeight),
        inlineContent = inlineText.inlineContent,
        onTextLayout = codeBackground::onTextLayout,
    )
}

/**
 * With a fixed line height a line does not grow for an inline image, and a tall image overlaps the surrounding lines
 * For such text the line height is removed
 */
private fun TextStyle.fitInlineImages(tallestImageHeight: TextUnit): TextStyle {
    if (tallestImageHeight == TextUnit.Unspecified) return this
    val lineHeightSp = when {
        lineHeight.isSp -> lineHeight.value
        lineHeight.isEm && fontSize.isSp -> lineHeight.value * fontSize.value
        else -> return this
    }
    return if (tallestImageHeight.value > lineHeightSp) copy(lineHeight = TextUnit.Unspecified) else this
}
