package io.github.igorosors.markdown.internal.parser

import org.commonmark.node.CustomNode
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Node

private fun String.hrefAttribute(): String? {
    return HREF_ATTRIBUTE.find(this)?.groupValues?.drop(2)?.firstOrNull { it.isNotEmpty() }
}

private val OPEN_TAG = Regex("""<([a-zA-Z][a-zA-Z0-9]*)(\s[^>]*)?>""")
private val CLOSE_TAG = Regex("""</([a-zA-Z][a-zA-Z0-9]*)\s*>""")
private val BR_TAG = Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE)
private val HREF_ATTRIBUTE = Regex("""href\s*=\s*("([^"]*)"|'([^']*)'|([^\s>]+))""", RegexOption.IGNORE_CASE)
private val HTML_COMMENT = Regex("""<!--.*?-->""", RegexOption.DOT_MATCHES_ALL)

/**
 * An inline HTML tag with its content as children. commonmark emits the opening and closing tags as separate
 * [HtmlInline] siblings. Grouping them into this node checks that the tags are paired and lets the content be
 * styled like markdown emphasis
 */
internal class HtmlInlineSpan(val tag: String, val href: String?) : CustomNode()

internal val SUPPORTED_INLINE_HTML_TAGS = setOf(
    "b", "strong",
    "i", "em", "cite", "var", "dfn",
    "u", "ins",
    "s", "strike", "del",
    "code", "kbd", "samp", "tt",
    "sub", "sup",
    "small",
    "mark",
    "a",
)

internal fun HtmlInline.isLineBreak(): Boolean = BR_TAG.matches(literal.trim())

internal fun HtmlBlock.isInvisible(): Boolean = literal.replace(HTML_COMMENT, "").isBlank()

/** Groups content between paired supported tags into [HtmlInlineSpan] */
internal fun Node.wrapInlineHtmlTags() {
    var child = firstChild
    while (child != null) {
        val open = (child as? HtmlInline)?.let { OPEN_TAG.matchEntire(it.literal.trim()) }
        val tag = open?.groupValues?.get(1)?.lowercase()
        val close = tag?.takeIf { it in SUPPORTED_INLINE_HTML_TAGS }?.let { child.findClosingTag(it) }

        if (open == null || tag == null || close == null) {
            child.wrapInlineHtmlTags()
            child = child.next
            continue
        }

        val span = HtmlInlineSpan(tag, href = open.groupValues[2].hrefAttribute())
        var inner = child.next
        while (inner != null && inner !== close) {
            val next = inner.next
            span.appendChild(inner)
            inner = next
        }
        child.insertAfter(span)
        child.unlink()
        close.unlink()

        span.wrapInlineHtmlTags()
        child = span.next
    }
}

/** The closing [tag] among the following siblings, skipping nested tags with the same name. */
private fun Node.findClosingTag(tag: String): Node? {
    var depth = 0
    var sibling = next
    while (sibling != null) {
        if (sibling is HtmlInline) {
            val literal = sibling.literal.trim()
            when {
                OPEN_TAG.matchEntire(literal)?.groupValues?.get(1).equals(tag, ignoreCase = true) -> depth++
                CLOSE_TAG.matchEntire(literal)?.groupValues?.get(1).equals(tag, ignoreCase = true) -> {
                    if (depth == 0) return sibling
                    depth--
                }
            }
        }
        sibling = sibling.next
    }
    return null
}
