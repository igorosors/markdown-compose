package io.github.igorosors.markdown.internal.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import io.github.igorosors.markdown.MarkdownDefaults
import io.github.igorosors.markdown.internal.parser.parseMarkdown
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InlineTextBuilderTest {

    @Test
    fun `soft line break becomes a space and hard line break a new line`() {
        assertEquals("a b\nc\nd", build("a\nb  \nc\\\nd").text)
    }

    @Test
    fun `br tag becomes a new line`() {
        assertEquals("a\nb", build("a<br>b").text)
    }

    @Test
    fun `emphasis, strong and strikethrough are styled`() {
        val text = build("*i* **b** ~~s~~")
        assertTrue(text.hasStyle("i") { it.fontStyle == FontStyle.Italic })
        assertTrue(text.hasStyle("b") { it.fontWeight == FontWeight.Bold })
        assertTrue(text.hasStyle("s") { it.textDecoration == TextDecoration.LineThrough })
    }

    @Test
    fun `HTML tags are styled like markdown`() {
        val text = build("<b>b</b> <s>s</s> <u>u</u>")
        assertEquals("b s u", text.text)
        assertTrue(text.hasStyle("b") { it.fontWeight == FontWeight.Bold })
        assertTrue(text.hasStyle("s") { it.textDecoration == TextDecoration.LineThrough })
        assertTrue(text.hasStyle("u") { it.textDecoration == TextDecoration.Underline })
    }

    @Test
    fun `link text is a link to its URL`() {
        val text = build("see [docs](https://example.com) now")
        val link = text.getLinkAnnotations(0, text.length).single()
        assertEquals("https://example.com", (link.item as LinkAnnotation.Url).url)
        assertEquals("docs", text.substring(link.start, link.end))
    }

    @Test
    fun `link click is passed to onLinkClick`() {
        val clicked = mutableListOf<String>()
        val text = build("[docs](https://example.com)", onLinkClick = { clicked += it })
        val link = text.getLinkAnnotations(0, text.length).single().item
        link.linkInteractionListener!!.onClick(link)
        assertEquals(listOf("https://example.com"), clicked)
    }

    @Test
    fun `HTML link is a link to its href`() {
        val text = build("""<a href="https://example.com">docs</a>""")
        assertEquals("https://example.com", (text.getLinkAnnotations(0, text.length).single().item as LinkAnnotation.Url).url)
    }

    @Test
    fun `inline code is padded and annotated for the background`() {
        val text = build("a `code` b")
        assertEquals("a \u202Fcode\u202F b", text.text)
        val code = text.getStringAnnotations(InlineAnnotations.CODE, 0, text.length).single()
        assertEquals("\u202Fcode\u202F", text.substring(code.start, code.end))
    }

    @Test
    fun `image is skipped while loading and replaced with alt text after a failure`() {
        assertEquals("a  b", build("a ![alt](x.png) b").text)
        assertEquals("a alt b", build("a ![alt](x.png) b", images = mapOf("x.png" to InlineImageState.Failed)).text)
    }

    private fun build(
        markdown: String,
        images: Map<String, InlineImageState> = emptyMap(),
        onLinkClick: (String) -> Unit = {},
    ): AnnotatedString {
        val paragraph = parseMarkdown(markdown, baseUrl = null).firstChild
        return InlineTextBuilder(
            typography = MarkdownDefaults.typography(),
            colors = MarkdownDefaults.colors(),
            density = Density(1f),
            onLinkClick = onLinkClick,
        ).build(paragraph, images).text
    }

    private fun AnnotatedString.hasStyle(
        substring: String,
        predicate: (androidx.compose.ui.text.SpanStyle) -> Boolean,
    ): Boolean {
        val start = text.indexOf(substring)
        val end = start + substring.length
        return spanStyles.any { it.start <= start && it.end >= end && predicate(it.item) }
    }
}
