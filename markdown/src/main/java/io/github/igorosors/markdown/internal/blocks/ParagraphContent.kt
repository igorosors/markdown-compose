package io.github.igorosors.markdown.internal.blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.LocalMarkdownTextStyle
import io.github.igorosors.markdown.internal.image.MarkdownImage
import io.github.igorosors.markdown.internal.parser.LinkedImage
import io.github.igorosors.markdown.internal.parser.imagesOnly
import io.github.igorosors.markdown.internal.text.MarkdownText
import org.commonmark.node.Node

/** A block of only images (and linked images) shows them as separate blocks, otherwise as text with inline images. */
@Composable
internal fun ParagraphContent(
    block: Node,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTextStyle.current,
) {
    val images = remember(block) { block.imagesOnly() }
    if (images != null) {
        ImageBlocks(images, modifier)
    } else {
        MarkdownText(block, modifier, style)
    }
}

@Composable
private fun ImageBlocks(images: List<LinkedImage>, modifier: Modifier) {
    val markdown = LocalMarkdownContext.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(markdown.blockSpacing)) {
        for (linkedImage in images) {
            MarkdownImage(
                image = linkedImage.image,
                onClick = linkedImage.link?.let { link -> { markdown.onLinkClick(link.destination) } },
            )
        }
    }
}
