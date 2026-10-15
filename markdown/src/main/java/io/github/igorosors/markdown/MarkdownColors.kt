package io.github.igorosors.markdown

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Created with [MarkdownDefaults.colors].
 *
 * @property strong bold text (`**…**`, `<b>`).
 * @property code text of inline code and code blocks.
 * @property quoteBorder bar on the left of a quote.
 * @property divider horizontal rule (`---`).
 */
@Immutable
public data class MarkdownColors(
    val text: Color,
    val heading: Color,
    val strong: Color,
    val link: Color,
    val code: Color,
    val codeBackground: Color,
    val codeBlockBackground: Color,
    val quoteText: Color,
    val quoteBorder: Color,
    val tableText: Color,
    val tableBorder: Color,
    val divider: Color,
)
