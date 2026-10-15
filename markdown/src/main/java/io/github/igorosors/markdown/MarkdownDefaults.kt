package io.github.igorosors.markdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

public object MarkdownDefaults {

    public val BlockSpacing: Dp = 12.dp

    public fun colors(
        text: Color = Color(0xFF1F2328),
        heading: Color = text,
        strong: Color = text,
        link: Color = Color(0xFF0969DA),
        code: Color = text,
        codeBackground: Color = Color(0x1F818B98),
        codeBlockBackground: Color = Color(0x14818B98),
        quoteText: Color = Color(0xFF59636E),
        quoteBorder: Color = Color(0x66818B98),
        tableText: Color = text,
        tableBorder: Color = Color(0xFFD1D9E0),
        divider: Color = tableBorder,
    ): MarkdownColors = MarkdownColors(
        text = text,
        heading = heading,
        strong = strong,
        link = link,
        code = code,
        codeBackground = codeBackground,
        codeBlockBackground = codeBlockBackground,
        quoteText = quoteText,
        quoteBorder = quoteBorder,
        tableText = tableText,
        tableBorder = tableBorder,
        divider = divider,
    )

    public fun typography(
        text: TextStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
        h1: TextStyle = text.heading(scale = 2f),
        h2: TextStyle = text.heading(scale = 1.5f),
        h3: TextStyle = text.heading(scale = 1.25f),
        h4: TextStyle = text.heading(scale = 1f),
        h5: TextStyle = text.heading(scale = 0.875f),
        h6: TextStyle = text.heading(scale = 0.85f),
        emphasis: SpanStyle = SpanStyle(fontStyle = FontStyle.Italic),
        strong: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
        strikethrough: SpanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
        link: SpanStyle = SpanStyle(textDecoration = TextDecoration.Underline),
        code: SpanStyle = SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 0.85.em),
        codeBlock: TextStyle = text.codeBlock(),
        quote: TextStyle = text,
        tableHeader: TextStyle = text.copy(fontWeight = FontWeight.Bold),
        tableText: TextStyle = text,
    ): MarkdownTypography = MarkdownTypography(
        text = text,
        h1 = h1,
        h2 = h2,
        h3 = h3,
        h4 = h4,
        h5 = h5,
        h6 = h6,
        emphasis = emphasis,
        strong = strong,
        strikethrough = strikethrough,
        link = link,
        code = code,
        codeBlock = codeBlock,
        quote = quote,
        tableHeader = tableHeader,
        tableText = tableText,
    )

    private fun TextStyle.baseFontSize(): TextUnit = if (fontSize.isSpecified) fontSize else 16.sp

    private fun TextStyle.codeBlock(): TextStyle {
        val size = baseFontSize() * 0.85f
        return copy(fontFamily = FontFamily.Monospace, fontSize = size, lineHeight = size * 1.45f)
    }

    private fun TextStyle.heading(scale: Float): TextStyle {
        val size = baseFontSize() * scale
        return copy(fontSize = size, lineHeight = size * 1.25f, fontWeight = FontWeight.SemiBold)
    }
}
