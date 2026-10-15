package io.github.igorosors.markdown.internal.parser

import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownParserTest {

    @Test
    fun `root relative URLs get the base URL`() {
        assertEquals("https://example.com/path", linkUrl("[a](/path)", baseUrl = "https://example.com"))
    }

    @Test
    fun `trailing slash of the base URL is not doubled`() {
        assertEquals("https://example.com/path", linkUrl("[a](/path)", baseUrl = "https://example.com/"))
    }

    @Test
    fun `base URL is prepended with its path`() {
        assertEquals("https://example.com/docs/a.png", imageUrl("![a](/a.png)", baseUrl = "https://example.com/docs"))
    }

    @Test
    fun `scheme relative URLs get https`() {
        assertEquals("https://host/path", linkUrl("[a](//host/path)", baseUrl = "https://example.com"))
    }

    @Test
    fun `other URLs are kept`() {
        val baseUrl = "https://example.com"
        assertEquals("https://other.com/x", linkUrl("[a](https://other.com/x)", baseUrl))
        assertEquals("images/a.png", linkUrl("[a](images/a.png)", baseUrl))
        assertEquals("../a.png", linkUrl("[a](../a.png)", baseUrl))
        assertEquals("#anchor", linkUrl("[a](#anchor)", baseUrl))
    }

    @Test
    fun `without a base URL all URLs are kept`() {
        assertEquals("/path", linkUrl("[a](/path)", baseUrl = null))
        assertEquals("//host/path", linkUrl("[a](//host/path)", baseUrl = null))
    }

    @Test
    fun `tables are parsed`() {
        val document = parseMarkdown("| a | b |\n|---|---|\n| 1 | 2 |", baseUrl = null)
        assertTrue(document.firstChild is TableBlock)
    }

    @Test
    fun `strikethrough with one and two tildes is parsed`() {
        assertEquals(2, parseMarkdown("~~a~~ and ~b~", baseUrl = null).descendants<Strikethrough>().size)
    }

    @Test
    fun `bare URLs and emails become links without trailing punctuation`() {
        val links = parseMarkdown("See https://example.com/a?q=1, www.example.com and mail@example.com.", baseUrl = null)
            .descendants<Link>()
            .map { it.destination }
        assertEquals(listOf("https://example.com/a?q=1", "http://www.example.com", "mailto:mail@example.com"), links)
    }

    @Test
    fun `a domain without www and scheme is not a link`() {
        assertTrue(parseMarkdown("example.com", baseUrl = null).descendants<Link>().isEmpty())
    }

    private fun linkUrl(markdown: String, baseUrl: String?): String =
        parseMarkdown(markdown, baseUrl).descendants<Link>().single().destination

    private fun imageUrl(markdown: String, baseUrl: String?): String =
        parseMarkdown(markdown, baseUrl).descendants<Image>().single().destination
}

internal inline fun <reified T : Node> Node.descendants(): List<T> = allDescendants().filterIsInstance<T>().toList()

internal fun Node.allDescendants(): Sequence<Node> = children.flatMap { sequenceOf(it) + it.allDescendants() }
