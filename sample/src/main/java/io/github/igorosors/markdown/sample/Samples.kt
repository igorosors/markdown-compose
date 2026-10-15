package io.github.igorosors.markdown.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class Sample(
    val title: String,
    val content: @Composable (Modifier) -> Unit,
) {
    Document(
        title = "Document in Column",
        content = { FeaturesSample(it) },
    ),
    LazyDocument(
        title = "Document in LazyColumn",
        content = { LazyMarkdownSample(it) },
    ),
    LazyDocumentWithVM(
        title = "Document in LazyColumn with ViewModel",
        content = { LargeDocumentSample(it) },
    ),
}

internal const val ASSETS_URL = "file:///android_asset"
