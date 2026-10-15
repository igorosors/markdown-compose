package io.github.igorosors.markdown.sample

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.igorosors.markdown.MarkdownColumn

@Composable
internal fun FeaturesSample(modifier: Modifier) {
    val context = LocalContext.current
    val markdown = remember { context.assets.open("samples/features.md").bufferedReader().use { it.readText() } }

    MarkdownColumn(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        markdown = markdown,
        onLinkClick = rememberOpenLink(),
        baseUrl = ASSETS_URL,
    )
}

@Composable
internal fun rememberOpenLink(): (String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { url ->
            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
                .onFailure { Toast.makeText(context, url, Toast.LENGTH_SHORT).show() }
        }
    }
}
