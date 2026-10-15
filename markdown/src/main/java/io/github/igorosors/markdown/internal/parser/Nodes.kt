package io.github.igorosors.markdown.internal.parser

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.Text

internal val Node.children: Sequence<Node>
    get() = generateSequence(firstChild) { it.next }

internal val Node.ancestors: Sequence<Node>
    get() = generateSequence(parent) { it.parent }

internal fun Image.altText(): String = buildString {
    accept(object : AbstractVisitor() {
        override fun visit(text: Text) {
            append(text.literal)
        }
    })
}

/** Distinct image URLs, so the same image is loaded once. */
internal fun Node.imageUrls(): List<String> {
    val urls = linkedSetOf<String>()
    accept(object : AbstractVisitor() {
        override fun visit(image: Image) {
            urls += image.destination
        }
    })
    return urls.toList()
}

internal class LinkedImage(val image: Image, val link: Link?)

internal fun Node.imagesOnly(): List<LinkedImage>? {
    val images = mutableListOf<LinkedImage>()
    for (child in children) {
        when (child) {
            is Image -> images += LinkedImage(child, link = null)
            is Link -> images += LinkedImage(child.singleImage() ?: return null, link = child)
            is SoftLineBreak, is HardLineBreak -> Unit
            else -> return null
        }
    }
    return images.ifEmpty { null }
}

private fun Link.singleImage(): Image? = (firstChild as? Image)?.takeIf { it.next == null }
