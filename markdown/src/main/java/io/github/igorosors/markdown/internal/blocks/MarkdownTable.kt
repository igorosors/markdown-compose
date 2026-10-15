package io.github.igorosors.markdown.internal.blocks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import io.github.igorosors.markdown.internal.LocalMarkdownContext
import io.github.igorosors.markdown.internal.parser.children
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.node.Node

private val BORDER_WIDTH = 1.dp
private val CELL_PADDING_HORIZONTAL = 8.dp
private val CELL_PADDING_VERTICAL = 4.dp

@Composable
internal fun MarkdownTable(table: TableBlock, modifier: Modifier = Modifier) {
    Column(modifier) {
        for (section in table.children) {
            val isHeader = section is TableHead
            for (row in section.children) {
                TableRow(row, isHeader)
            }
        }
    }
}

@Composable
private fun TableRow(row: Node, isHeader: Boolean) {
    val cells = remember(row) { row.children.filterIsInstance<TableCell>().toList() }
    if (cells.isEmpty()) return
    val borderColor = LocalMarkdownContext.current.colors.tableBorder

    Layout(
        modifier = Modifier
            .fillMaxWidth()
            .cellBorders(cells.size, borderColor),
        content = {
            for (cell in cells) TableCellContent(cell, isHeader)
        },
    ) { measurables, constraints ->
        val cellWidth = constraints.maxWidth / measurables.size
        val placeables = measurables.mapIndexed { index, measurable ->
            val width = if (index == measurables.lastIndex) constraints.maxWidth - cellWidth * index else cellWidth
            measurable.measure(Constraints.fixedWidth(width))
        }
        val rowHeight = placeables.maxOf { it.height }

        layout(constraints.maxWidth, rowHeight) {
            var x = 0
            for (placeable in placeables) {
                placeable.placeRelative(x, (rowHeight - placeable.height) / 2)
                x += placeable.width
            }
        }
    }
}

@Composable
private fun TableCellContent(cell: TableCell, isHeader: Boolean) {
    val markdown = LocalMarkdownContext.current
    val (alignment, textAlign) = when (cell.alignment) {
        TableCell.Alignment.CENTER -> Alignment.Center to TextAlign.Center
        TableCell.Alignment.RIGHT -> Alignment.CenterEnd to TextAlign.End
        TableCell.Alignment.LEFT, null -> Alignment.CenterStart to TextAlign.Start
    }
    val style = (if (isHeader) markdown.typography.tableHeader else markdown.typography.tableText)
        .copy(color = markdown.colors.tableText, textAlign = textAlign)

    Box(
        modifier = Modifier.padding(CELL_PADDING_HORIZONTAL, CELL_PADDING_VERTICAL),
        contentAlignment = alignment,
    ) {
        // A cell is built as one text like a paragraph, otherwise styling spanning several inline nodes would break
        ParagraphContent(cell, style = style)
    }
}

private fun Modifier.cellBorders(cellCount: Int, color: Color): Modifier = drawBehind {
    val cellWidth = size.width / cellCount
    val stroke = Stroke(width = BORDER_WIDTH.toPx())
    repeat(cellCount) { index ->
        drawRect(
            color = color,
            topLeft = Offset(cellWidth * index, 0f),
            size = Size(cellWidth, size.height),
            style = stroke,
        )
    }
}
