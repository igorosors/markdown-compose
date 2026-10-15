package io.github.igorosors.markdown

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle

/**
 * Created with [MarkdownDefaults.typography]. Block elements use [TextStyle], inline ones use [SpanStyle]
 * applied on top of the block style. Colors come from [MarkdownColors].
 *
 * @property text paragraphs and lists
 * @property emphasis italic (`*…*`, `<i>`)
 * @property strong bold (`**…**`, `<b>`)
 * @property strikethrough strikethrough (`~~…~~`, `<del>`)
 * @property code inline code (text in backticks)
 */
@Immutable
public data class MarkdownTypography(
    val text: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,
    val emphasis: SpanStyle,
    val strong: SpanStyle,
    val strikethrough: SpanStyle,
    val link: SpanStyle,
    val code: SpanStyle,
    val codeBlock: TextStyle,
    val quote: TextStyle,
    val tableHeader: TextStyle,
    val tableText: TextStyle,
) {

    /** Style of a heading of [level], where 6 and above use [h6]. */
    public fun heading(level: Int): TextStyle = when (level) {
        1 -> h1
        2 -> h2
        3 -> h3
        4 -> h4
        5 -> h5
        else -> h6
    }
}
