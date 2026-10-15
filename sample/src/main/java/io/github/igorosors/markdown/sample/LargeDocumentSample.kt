package io.github.igorosors.markdown.sample

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.igorosors.markdown.LazyMarkdownColumn
import io.github.igorosors.markdown.MarkdownDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class LargeDocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val _document = MutableStateFlow<MarkdownDocument?>(null)
    val document: StateFlow<MarkdownDocument?> = _document.asStateFlow()

    init {
        val markdown = application.readAsset("samples/features.md")
        viewModelScope.launch {
            _document.value = MarkdownDocument.parse(markdown = markdown, baseUrl = ASSETS_URL)
        }
    }
}

@Composable
internal fun LargeDocumentSample(modifier: Modifier) {
    val viewModel: LargeDocumentViewModel = viewModel()
    val document = viewModel.document.collectAsState().value

    if (document != null) {
        LazyMarkdownColumn(
            document = document,
            modifier = modifier,
            onLinkClick = rememberOpenLink(),
            contentPadding = PaddingValues(16.dp),
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

internal fun Application.readAsset(path: String): String = assets.open(path).bufferedReader().use { it.readText() }
