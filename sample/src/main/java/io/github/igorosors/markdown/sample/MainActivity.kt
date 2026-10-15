package io.github.igorosors.markdown.sample

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        setContent {
            MaterialTheme {
                SampleApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleApp() {
    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = Sample.entries.firstOrNull { it.name == selectedName }

    BackHandler(enabled = selected != null) { selectedName = null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selected?.title ?: "Markdown Compose") },
                navigationIcon = {
                    if (selected != null) {
                        IconButton(onClick = { selectedName = null }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = null,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        val modifier = Modifier
            .padding(padding)
            .fillMaxSize()

        if (selected == null) {
            SampleList(
                modifier = modifier,
                onSelect = { selectedName = it.name },
            )
        } else {
            selected.content(modifier)
        }
    }
}

@Composable
private fun SampleList(
    modifier: Modifier,
    onSelect: (Sample) -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Sample.entries.forEach { sample ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(sample) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = sample.title,
                style = MaterialTheme.typography.titleMedium,
            )

            HorizontalDivider()
        }
    }
}
