package io.github.igorosors.markdown.internal.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.LocalMarkdownTextStyle
import org.commonmark.node.BlockQuote

@Composable
internal fun MarkdownBlockQuote(quote: BlockQuote, modifier: Modifier = Modifier) {
    val markdown = LocalMarkdownContext.current
    Row(modifier.height(IntrinsicSize.Min)) {
        Box(
            modifier = Modifier
                .width(BORDER_WIDTH)
                .fillMaxHeight()
                .background(markdown.colors.quoteBorder)
        )

        Column(Modifier.padding(start = CONTENT_PADDING)) {
            CompositionLocalProvider(
                LocalMarkdownTextStyle provides markdown.typography.quote.copy(color = markdown.colors.quoteText),
            ) {
                MarkdownBlocks(quote)
            }
        }
    }
}

private val BORDER_WIDTH = 4.dp
private val CONTENT_PADDING = 12.dp
