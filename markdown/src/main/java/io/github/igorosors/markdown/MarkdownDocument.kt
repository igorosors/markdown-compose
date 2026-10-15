package io.github.igorosors.markdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.igorosors.markdown.internal.blocks.visibleBlocks
import io.github.igorosors.markdown.internal.parser.parseMarkdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.commonmark.node.Node

@Immutable
public class MarkdownDocument internal constructor(
    internal val blocks: List<Node>,
) {

    public companion object {

        /**
         * Parses [markdown] off the main thread
         * @param baseUrl prepended to link and image URLs that start with a single slash, like /path
         * URLs like //host/path get https. Other URLs are kept as is. If null, all URLs are kept
         */
        public suspend fun parse(markdown: String, baseUrl: String? = null): MarkdownDocument {
            return withContext(Dispatchers.Default) { parseBlocking(markdown, baseUrl) }
        }

        internal fun parseBlocking(markdown: String, baseUrl: String?): MarkdownDocument {
            return MarkdownDocument(parseMarkdown(markdown, baseUrl).visibleBlocks())
        }
    }
}

/**
 * Parses [markdown] in the background. To keep a large document, parse it with [MarkdownDocument.parse] in a ViewModel
 * @param baseUrl prepended to link and image URLs that start with a single slash, like /path.
 * URLs like //host/path get https. Other URLs are kept as is. If null, all URLs are kept
 */
@Composable
public fun rememberMarkdownDocument(markdown: String, baseUrl: String? = null): MarkdownDocument? {
    var document by remember { mutableStateOf<MarkdownDocument?>(null) }
    LaunchedEffect(markdown, baseUrl) {
        document = MarkdownDocument.parse(markdown, baseUrl)
    }
    return document
}
