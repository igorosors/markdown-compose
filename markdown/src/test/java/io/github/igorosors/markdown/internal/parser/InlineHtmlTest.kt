package io.github.igorosors.markdown.internal.parser

import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InlineHtmlTest {

    @Test
    fun `paired tag wraps its content`() {
        val span = paragraph("a <b>bold</b> c").descendants<HtmlInlineSpan>().single()
        assertEquals("b", span.tag)
        assertEquals("bold", span.text())
    }

    @Test
    fun `paired tags leave no raw HTML nodes`() {
        assertTrue(paragraph("a <b>bold</b> c").descendants<HtmlInline>().isEmpty())
    }

    @Test
    fun `tag names are case insensitive`() {
        assertEquals("b", paragraph("<B>bold</B>").descendants<HtmlInlineSpan>().single().tag)
    }

    @Test
    fun `nested tags are wrapped inside each other`() {
        val outer = paragraph("<b>bold <i>and italic</i></b>").descendants<HtmlInlineSpan>().first()
        assertEquals("b", outer.tag)
        assertEquals("i", outer.descendants<HtmlInlineSpan>().single().tag)
    }

    @Test
    fun `nested tags with the same name are matched by depth`() {
        val outer = paragraph("<b>a <b>b</b> c</b>").firstChild as HtmlInlineSpan
        assertEquals("a b c", outer.text())
        assertEquals("b", outer.descendants<HtmlInlineSpan>().single().text())
    }

    @Test
    fun `unclosed tag is kept as raw HTML`() {
        val paragraph = paragraph("<b>not closed")
        assertTrue(paragraph.descendants<HtmlInlineSpan>().isEmpty())
        assertEquals("<b>", paragraph.descendants<HtmlInline>().single().literal)
    }

    @Test
    fun `unsupported tag is not wrapped`() {
        assertTrue(paragraph("<span>text</span>").descendants<HtmlInlineSpan>().isEmpty())
    }

    @Test
    fun `tags inside emphasis are wrapped`() {
        assertEquals("u", paragraph("*a <u>b</u>*").descendants<HtmlInlineSpan>().single().tag)
    }

    @Test
    fun `link href is read in every quoting style`() {
        assertEquals("https://a.com", paragraph("""<a href="https://a.com">x</a>""").span().href)
        assertEquals("https://b.com", paragraph("""<a href='https://b.com'>x</a>""").span().href)
        assertEquals("https://c.com", paragraph("""<a href=https://c.com>x</a>""").span().href)
        assertEquals("https://d.com", paragraph("""<a target="_blank" HREF = "https://d.com">x</a>""").span().href)
    }

    @Test
    fun `link without href has no URL`() {
        assertNull(paragraph("<a>x</a>").span().href)
    }

    @Test
    fun `br is a line break in every form`() {
        listOf("<br>", "<br/>", "<br />", "<BR>").forEach { tag ->
            assertTrue(tag, paragraph("a${tag}b").descendants<HtmlInline>().single().isLineBreak())
        }
        assertFalse(paragraph("a<b>b").descendants<HtmlInline>().single().isLineBreak())
    }

    @Test
    fun `HTML block of only comments is invisible`() {
        assertTrue(htmlBlock("<!-- comment -->").isInvisible())
        assertTrue(htmlBlock("<!-- one -->\n<!--\ntwo\n-->").isInvisible())
        assertFalse(htmlBlock("<div>text</div>").isInvisible())
    }

    private fun paragraph(markdown: String): Node = parseMarkdown(markdown, baseUrl = null).firstChild

    private fun htmlBlock(markdown: String): HtmlBlock = parseMarkdown(markdown, baseUrl = null).firstChild as HtmlBlock

    private fun Node.span(): HtmlInlineSpan = descendants<HtmlInlineSpan>().single()

    private fun Node.text(): String = descendants<Text>().joinToString("") { it.literal }
}
