package io.github.igorosors.markdown.internal.parser

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Document
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.parser.Parser

private val parser: Parser = Parser.builder()
    .extensions(
        listOf(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            AutolinkExtension.create(),
        )
    )
    .build()

internal fun parseMarkdown(markdown: String, baseUrl: String?): Document =
    (parser.parse(markdown) as Document).apply {
        if (baseUrl != null) resolveRelativeUrls(baseUrl)
        wrapInlineHtmlTags()
    }

private fun Document.resolveRelativeUrls(baseUrl: String) {
    fun resolve(url: String): String = when {
        url.startsWith("//") -> "https:$url"
        url.startsWith("/") -> baseUrl.trimEnd('/') + url
        else -> url
    }

    accept(object : AbstractVisitor() {
        override fun visit(link: Link) {
            link.destination = resolve(link.destination)
            visitChildren(link)
        }

        override fun visit(image: Image) {
            image.destination = resolve(image.destination)
            visitChildren(image)
        }
    })
}
