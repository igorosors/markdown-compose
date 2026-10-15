package io.github.igorosors.markdown.internal.blocks

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.LocalMarkdownTextStyle

@Composable
internal fun MarkdownHtmlBlock(html: String, modifier: Modifier = Modifier) {
    val markdown = LocalMarkdownContext.current
    val linkStyle = markdown.typography.link.copy(color = markdown.colors.link)
    val text = remember(html, linkStyle, markdown.onLinkClick) {
        AnnotatedString.fromHtml(
            htmlString = html.replaceImagesWithAltText(),
            linkStyles = TextLinkStyles(style = linkStyle),
            linkInteractionListener = { link -> (link as? LinkAnnotation.Url)?.let { markdown.onLinkClick(it.url) } },
        ).trimWhitespace()
    }

    if (text.isNotEmpty()) {
        BasicText(modifier = modifier, text = text, style = LocalMarkdownTextStyle.current)
    }
}

/** fromHtml does not support images and leaves an object replacement character in place of `<img>` */
internal fun String.replaceImagesWithAltText(): String = replace(IMG_TAG) { tag ->
    IMG_ALT.find(tag.value)?.groups?.let { it[1] ?: it[2] }?.value.orEmpty()
}

private val IMG_TAG = Regex("""<img\b[^>]*>""", RegexOption.IGNORE_CASE)
private val IMG_ALT = Regex("""\balt\s*=\s*(?:"([^"]*)"|'([^']*)')""", RegexOption.IGNORE_CASE)

/** Trims the line breaks that block tags add at the edges, since spacing between blocks comes from [MarkdownBlocks]. */
private fun AnnotatedString.trimWhitespace(): AnnotatedString {
    val start = indexOfFirst { !it.isWhitespace() }
    if (start < 0) return AnnotatedString("")
    val end = indexOfLast { !it.isWhitespace() }
    return subSequence(start, end + 1)
}
