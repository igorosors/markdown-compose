package io.github.igorosors.markdown.internal.text

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.igorosors.markdown.MarkdownColors
import io.github.igorosors.markdown.MarkdownTypography
import io.github.igorosors.markdown.internal.parser.HtmlInlineSpan
import io.github.igorosors.markdown.internal.parser.altText
import io.github.igorosors.markdown.internal.parser.ancestors
import io.github.igorosors.markdown.internal.parser.children
import io.github.igorosors.markdown.internal.parser.isLineBreak
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.HardLineBreak
import org.commonmark.node.HtmlInline
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.Image as ImageNode

/**
 * @property inlineContent images inside the text. They belong to this text and are released with it.
 * @property tallestImageHeight height of the tallest image, or [TextUnit.Unspecified] if there are none.
 */
internal class InlineText(
    val text: AnnotatedString,
    val inlineContent: Map<String, InlineTextContent>,
    val tallestImageHeight: TextUnit,
) {
    companion object {
        val Empty = InlineText(AnnotatedString(""), emptyMap(), TextUnit.Unspecified)
    }
}

internal object InlineAnnotations {
    /** Ranges for the rounded background, see [InlineCodeBackground]. */
    const val CODE = "markdown_inline_code"
}

/** Builds the text of a paragraph, heading or table cell */
internal class InlineTextBuilder(
    private val typography: MarkdownTypography,
    private val colors: MarkdownColors,
    private val density: Density,
    private val onLinkClick: (String) -> Unit,
) {
    private companion object {
        const val CODE_PADDING = "\u202F"

        val MARK_BACKGROUND = Color(0x66FFD54F)
    }

    fun build(block: Node, images: Map<String, InlineImageState>): InlineText = Session(images).build(block)

    private inner class Session(private val images: Map<String, InlineImageState>) {
        private val out = AnnotatedString.Builder()
        private val inlineContent = mutableMapOf<String, InlineTextContent>()
        private var tallestImageHeight = TextUnit.Unspecified

        fun build(block: Node): InlineText {
            appendChildren(block)
            return InlineText(out.toAnnotatedString(), inlineContent, tallestImageHeight)
        }

        private fun appendChildren(node: Node) {
            node.children.forEach(::appendNode)
        }

        private fun appendNode(node: Node) {
            when (node) {
                is Text -> out.append(node.literal)
                is SoftLineBreak -> out.append(" ")
                is HardLineBreak -> out.append("\n")
                is Emphasis -> appendStyled(typography.emphasis, node)
                is StrongEmphasis -> appendStyled(typography.strong.copy(color = colors.strong), node)
                is Strikethrough -> appendStyled(typography.strikethrough, node)
                is Code -> appendCode { out.append(node.literal) }
                is Link -> appendLink(node.destination, node)
                is ImageNode -> appendImage(node)
                is HtmlInlineSpan -> appendHtmlSpan(node)
                is HtmlInline -> if (node.isLineBreak()) out.append("\n")
                else -> appendChildren(node)
            }
        }

        private fun appendStyled(style: SpanStyle, node: Node) {
            out.withStyle(style) { appendChildren(node) }
        }

        /** BasicText handles the click, focus and accessibility of the link itself */
        private fun appendLink(url: String, node: Node) {
            val link = LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(style = typography.link.copy(color = colors.link)),
                linkInteractionListener = { onLinkClick(url) },
            )
            out.withLink(link) { appendChildren(node) }
        }

        private inline fun appendCode(content: () -> Unit) {
            out.pushStringAnnotation(tag = InlineAnnotations.CODE, annotation = "")
            out.append(CODE_PADDING)
            out.withStyle(typography.code.copy(color = colors.code)) { content() }
            out.append(CODE_PADDING)
            out.pop()
        }

        private fun appendHtmlSpan(node: HtmlInlineSpan) {
            when (node.tag) {
                "b", "strong" -> appendStyled(typography.strong.copy(color = colors.strong), node)
                "i", "em", "cite", "var", "dfn" -> appendStyled(typography.emphasis, node)
                "u", "ins" -> appendStyled(SpanStyle(textDecoration = TextDecoration.Underline), node)
                "s", "strike", "del" -> appendStyled(typography.strikethrough, node)
                "sub" -> appendStyled(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.75.em), node)
                "sup" -> appendStyled(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.75.em), node)
                "small" -> appendStyled(SpanStyle(fontSize = 0.85.em), node)
                "mark" -> appendStyled(SpanStyle(background = MARK_BACKGROUND), node)
                "code", "kbd", "samp", "tt" -> appendCode { appendChildren(node) }
                "a" -> if (node.href != null) appendLink(node.href, node) else appendChildren(node)
                else -> appendChildren(node)
            }
        }

        private fun appendImage(node: ImageNode) {
            val altText = node.altText()
            val image = when (val state = images[node.destination]) {
                null -> return
                InlineImageState.Failed -> {
                    out.append(altText)
                    return
                }

                is InlineImageState.Loaded -> state
            }

            val id = "markdown_image_${inlineContent.size}"
            val width = with(density) { image.size.width.dp.toSp() }
            val height = with(density) { image.size.height.dp.toSp() }
            val link = node.ancestors.firstOrNull { it is Link } as? Link

            inlineContent[id] = InlineTextContent(Placeholder(width, height, PlaceholderVerticalAlign.Center)) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (link != null) Modifier.clickable { onLinkClick(link.destination) } else Modifier),
                    bitmap = image.bitmap,
                    contentDescription = altText.trim().ifEmpty { null },
                    contentScale = ContentScale.Fit,
                )
            }
            if (tallestImageHeight == TextUnit.Unspecified || height.value > tallestImageHeight.value) {
                tallestImageHeight = height
            }
            if (altText.isEmpty()) out.appendInlineContent(id) else out.appendInlineContent(id, alternateText = altText)
        }
    }
}
