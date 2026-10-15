package io.github.igorosors.markdown.internal.parser

import org.commonmark.node.Image
import org.commonmark.node.Node
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NodesTest {

    @Test
    fun `alt text joins text of formatted alt`() {
        assertEquals("a bold alt", paragraph("![a **bold** alt](x.png)").descendants<Image>().single().altText())
    }

    @Test
    fun `alt text is empty without alt`() {
        assertEquals("", paragraph("![](x.png)").descendants<Image>().single().altText())
    }

    @Test
    fun `image URLs are distinct and keep order`() {
        assertEquals(listOf("b.png", "a.png"), paragraph("![](b.png) ![](a.png) [![](b.png)](x)").imageUrls())
    }

    @Test
    fun `paragraph of images and linked images shows them as blocks`() {
        val images = paragraph("![a](a.png)\n[![b](b.png)](https://x.com)").imagesOnly()!!
        assertEquals(listOf("a.png", "b.png"), images.map { it.image.destination })
        assertNull(images[0].link)
        assertEquals("https://x.com", images[1].link?.destination)
    }

    @Test
    fun `paragraph with text is not an image block`() {
        assertNull(paragraph("Text ![a](a.png)").imagesOnly())
    }

    @Test
    fun `link with text and image is not an image block`() {
        assertNull(paragraph("[text ![a](a.png)](https://x.com)").imagesOnly())
    }

    @Test
    fun `paragraph without images is not an image block`() {
        assertNull(paragraph("Text").imagesOnly())
    }

    private fun paragraph(markdown: String): Node = parseMarkdown(markdown, baseUrl = null).firstChild
}
