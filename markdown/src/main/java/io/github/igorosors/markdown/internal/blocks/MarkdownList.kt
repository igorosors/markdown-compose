package io.github.igorosors.markdown.internal.blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.LocalMarkdownTextStyle
import io.github.igorosors.markdown.internal.parser.ancestors
import io.github.igorosors.markdown.internal.parser.children
import org.commonmark.node.ListBlock
import org.commonmark.node.Node
import org.commonmark.node.OrderedList

private val BULLETS = listOf("•", "◦", "▪")
private val MARKER_MIN_WIDTH = 20.dp
private val TIGHT_LIST_SPACING = 4.dp

@Composable
internal fun MarkdownList(list: ListBlock, modifier: Modifier = Modifier) {
    val spacing = if (list.isTight) TIGHT_LIST_SPACING else LocalMarkdownContext.current.blockSpacing
    val bullet = BULLETS[minOf(list.ancestors.count { it is ListBlock }, BULLETS.lastIndex)]
    val firstNumber = (list as? OrderedList)?.markerStartNumber ?: 1

    Column(modifier, verticalArrangement = Arrangement.spacedBy(spacing)) {
        list.children.forEachIndexed { index, item ->
            val marker = when (list) {
                is OrderedList -> "${firstNumber + index}${list.markerDelimiter ?: "."}"
                else -> bullet
            }
            ListItem(marker, item, spacing)
        }
    }
}

@Composable
private fun ListItem(marker: String, item: Node, spacing: Dp) {
    Row {
        BasicText(
            modifier = Modifier
                .widthIn(min = MARKER_MIN_WIDTH)
                .padding(end = 4.dp)
                .alignByBaseline(),
            text = marker,
            style = LocalMarkdownTextStyle.current.copy(textAlign = TextAlign.End),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .alignByBaseline(),
        ) {
            MarkdownBlocks(item, spacing)
        }
    }
}
