package io.github.igorosors.markdown.internal.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.igorosors.markdown.internal.LocalMarkdownContext

private val CORNER_RADIUS = 6.dp
private val CONTENT_PADDING = 12.dp

@Composable
internal fun MarkdownCodeBlock(code: String, modifier: Modifier = Modifier) {
    val markdown = LocalMarkdownContext.current
    BasicText(
        modifier = modifier
            .fillMaxWidth()
            .background(markdown.colors.codeBlockBackground, RoundedCornerShape(CORNER_RADIUS))
            .horizontalScroll(rememberScrollState())
            .padding(CONTENT_PADDING),
        text = code.trimEnd('\n'),
        style = markdown.typography.codeBlock.copy(color = markdown.colors.code),
        softWrap = false,
    )
}
