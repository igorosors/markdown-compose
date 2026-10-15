package io.github.igorosors.markdown.internal.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.parser.children
import io.github.igorosors.markdown.internal.parser.isInvisible
import io.github.igorosors.markdown.internal.text.MarkdownText
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.BlockQuote
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.LinkReferenceDefinition
import org.commonmark.node.ListBlock
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.node.ThematicBreak

@NonRestartableComposable
@Composable
internal fun MarkdownBlocks(
    parent: Node,
    spacing: Dp = LocalMarkdownContext.current.blockSpacing,
) {
    MarkdownBlocks(remember(parent) { parent.visibleBlocks() }, spacing)
}

@Composable
internal fun MarkdownBlocks(
    blocks: List<Node>,
    spacing: Dp = LocalMarkdownContext.current.blockSpacing,
) {
    blocks.forEachIndexed { index, block ->
        MarkdownBlock(
            block = block,
            modifier = if (index < blocks.lastIndex) Modifier.padding(bottom = spacing) else Modifier,
        )
    }
}

internal fun Node.visibleBlocks(): List<Node> = children.filter { it.isVisible() }.toList()

@Composable
internal fun MarkdownBlock(block: Node, modifier: Modifier = Modifier) {
    when (block) {
        is Heading -> {
            val markdown = LocalMarkdownContext.current
            MarkdownText(
                block = block,
                modifier = modifier,
                style = markdown.typography.heading(block.level).copy(color = markdown.colors.heading),
            )
        }

        is Paragraph -> ParagraphContent(block, modifier)
        is ListBlock -> MarkdownList(block, modifier)
        is BlockQuote -> MarkdownBlockQuote(block, modifier)
        is FencedCodeBlock -> MarkdownCodeBlock(block.literal, modifier)
        is IndentedCodeBlock -> MarkdownCodeBlock(block.literal, modifier)
        is TableBlock -> MarkdownTable(block, modifier)
        is HtmlBlock -> MarkdownHtmlBlock(block.literal, modifier)
        is ThematicBreak -> MarkdownThematicBreak(modifier)
        else -> Column(modifier) { MarkdownBlocks(block) }
    }
}

private fun Node.isVisible(): Boolean = when (this) {
    is LinkReferenceDefinition -> false
    is HtmlBlock -> !isInvisible()
    else -> true
}

@Composable
private fun MarkdownThematicBreak(modifier: Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(LocalMarkdownContext.current.colors.divider)
    )
}
