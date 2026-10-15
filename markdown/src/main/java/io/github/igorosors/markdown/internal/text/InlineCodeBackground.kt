package io.github.igorosors.markdown.internal.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Stable
internal class InlineCodeBackground(
    private val fontSizePx: Float,
    private val cornerRadiusPx: Float,
    private val color: Color,
) {
    private var path by mutableStateOf<Path?>(null)

    val modifier: Modifier = Modifier.drawBehind {
        path?.let { drawPath(it, color) }
    }

    fun onTextLayout(layout: TextLayoutResult) {
        path = backgroundPath(layout)
    }

    private fun backgroundPath(layout: TextLayoutResult): Path? {
        val text = layout.layoutInput.text
        val ranges = text.getStringAnnotations(InlineAnnotations.CODE, 0, text.length)
        if (ranges.isEmpty()) return null

        val rounded = CornerRadius(cornerRadiusPx)
        return Path().apply {
            for (range in ranges) {
                if (range.start >= range.end) continue
                val firstLine = layout.getLineForOffset(range.start)
                val lastLine = layout.getLineForOffset(range.end - 1)
                // In right to left text the code starts at the right edge
                val isRtl = layout.getParagraphDirection(range.start) == ResolvedTextDirection.Rtl

                for (line in firstLine..lastLine) {
                    val segmentStart = maxOf(range.start, layout.getLineStart(line))
                    val segmentEnd = minOf(range.end, layout.getLineEnd(line))
                    if (segmentStart >= segmentEnd) continue

                    val startBox = layout.getBoundingBox(segmentStart)
                    val endBox = layout.getBoundingBox(segmentEnd - 1)
                    val baseline = layout.getLineBaseline(line)
                    val rect = Rect(
                        left = minOf(startBox.left, endBox.left),
                        top = baseline - fontSizePx * 1.05f,
                        right = maxOf(startBox.right, endBox.right),
                        bottom = baseline + fontSizePx * 0.35f,
                    )

                    val start = if (line == firstLine) rounded else CornerRadius.Zero
                    val end = if (line == lastLine) rounded else CornerRadius.Zero
                    val left = if (isRtl) end else start
                    val right = if (isRtl) start else end
                    addRoundRect(
                        RoundRect(
                            rect = rect,
                            topLeft = left,
                            topRight = right,
                            bottomRight = right,
                            bottomLeft = left,
                        )
                    )
                }
            }
        }
    }
}

@Composable
internal fun rememberInlineCodeBackground(textStyle: TextStyle, codeFontSize: TextUnit, color: Color): InlineCodeBackground {
    val density = LocalDensity.current
    val fontSizePx = with(density) { resolveFontSize(base = textStyle.fontSize, code = codeFontSize).toPx() }
    val cornerRadiusPx = with(density) { CORNER_RADIUS.toPx() }
    return remember(fontSizePx, cornerRadiusPx, color) { InlineCodeBackground(fontSizePx, cornerRadiusPx, color) }
}

private fun resolveFontSize(base: TextUnit, code: TextUnit): TextUnit = when {
    code.isSp -> code
    code.isEm && base.isSp -> (base.value * code.value).sp
    base.isSp -> base
    else -> 14.sp
}

private val CORNER_RADIUS = 4.dp
