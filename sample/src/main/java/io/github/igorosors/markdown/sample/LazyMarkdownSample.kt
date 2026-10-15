package io.github.igorosors.markdown.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.igorosors.markdown.markdownItems
import io.github.igorosors.markdown.rememberMarkdownDocument

@Composable
internal fun LazyMarkdownSample(modifier: Modifier) {
    val context = LocalContext.current
    val markdown = remember { context.assets.open("samples/features.md").bufferedReader().use { it.readText() } }
    val document = rememberMarkdownDocument(markdown = markdown, baseUrl = ASSETS_URL)
    val onLinkClick = rememberOpenLink()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
    ) {
        if (document == null) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            markdownItems(
                document = document,
                onLinkClick = onLinkClick,
            )
        }
    }
}
