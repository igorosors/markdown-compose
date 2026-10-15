package io.github.igorosors.markdown.internal.text

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

/** Reports the available width from the constraints, not the measured width, because text wraps its content */
internal fun Modifier.onAvailableWidth(onWidth: (Int) -> Unit): Modifier = layout { measurable, constraints ->
    if (constraints.hasBoundedWidth) onWidth(constraints.maxWidth)
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}
