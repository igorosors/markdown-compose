package io.github.igorosors.markdown.internal.blocks

import io.github.igorosors.markdown.MarkdownDocument
import org.commonmark.node.BulletList
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.Paragraph
import org.junit.Assert.assertEquals
import org.junit.Test

class BlocksTest {

    @Test
    fun `link reference definitions and HTML comments are not blocks`() {
        val markdown = """
            # Title

            Text with a [link][ref].

            [ref]: https://example.com

            <!-- comment -->

            <div>visible</div>

            - item
        """.trimIndent()

        val blocks = MarkdownDocument.parseBlocking(markdown, baseUrl = null).blocks
        assertEquals(
            listOf(Heading::class, Paragraph::class, HtmlBlock::class, BulletList::class),
            blocks.map { it::class },
        )
    }

    @Test
    fun `img tags are replaced with their alt text`() {
        assertEquals(
            """<a href="x">Logo</a>""",
            """<a href="x"><img src="a.png" alt="Logo" width="10"></a>""".replaceImagesWithAltText(),
        )
        assertEquals("Logo", """<IMG ALT='Logo' SRC="a.png"/>""".replaceImagesWithAltText())
    }

    @Test
    fun `img tags without alt are removed`() {
        assertEquals("a  b", """a <img src="a.png"> b""".replaceImagesWithAltText())
    }
}
