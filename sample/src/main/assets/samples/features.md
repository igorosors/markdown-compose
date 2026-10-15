# Markdown Compose

A short tour of everything the library renders. Text before the first section is a regular paragraph.

## Headings

# Heading 1
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6

Setext heading 1
================

Setext heading 2
----------------

### Heading with *emphasis*, `code` and a [link](https://example.com)

## Text

*Italic*, **bold**, ***bold italic***, ~~strikethrough~~ and `inline code`.

**Bold with *italic* inside** and *italic with **bold** inside*.

Long inline code wraps across lines with rounded ends: `val result = repository.loadSomething(firstArgument, secondArgument)`.

A soft line break
becomes a space.

A hard line break with two trailing spaces  
and with a backslash\
both start a new line.

Escaped characters: \*not italic\*, \# not a heading, \`not code\`.

Entities: &copy; &amp; &lt;tag&gt; and emoji 🚗 ✅

## Links

An [inline link](https://example.com), a [link with a title](https://example.com "Title"),
a [reference link][docs] and a short one: [docs].

Autolinks: <https://commonmark.org>, <mail@example.com>, https://example.com/path?q=1, www.example.com.

A relative link resolved against `baseUrl`: [about page](/about).

[docs]: https://commonmark.org/help/

## Lists

- Bullet item
- Item with **bold**, `code` and a [link](https://example.com)
  - Nested item
    - Third level
- Back to the first level

1. First
2. Second
   1. Nested ordered
   2. Another one
3. Third

5) A list can start from any number
6) and use a parenthesis

- Loose list item with a paragraph.

  A second paragraph in the same item.

- Another loose item

## Blockquotes

> A quote with **bold**, `code` and a [link](https://example.com).
>
> - A list inside a quote
> - Second item
>
>> A nested quote

## Code

```kotlin
fun greet(name: String) {
    println("Hello, $name")
}
```

~~~
A fenced block with tildes. A very long line scrolls horizontally instead of wrapping: val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
~~~

    An indented code block
    with two lines

## Horizontal rule

---

## Tables

| Left | Center | Right |
|:-----|:------:|------:|
| 1 | **bold** | `code` |
| Long text that wraps inside the cell | *italic* | [link](https://example.com) |
| ![icon](/images/icon-24.png) | ~~old~~ | 42 |

Column | Without outer pipes
-------|--------------------
a | b

## Images

A raster image keeps its own size in dp:

![Small icon](/images/icon-64.png)

A wide image is scaled down to the screen width:

![Wide image](/images/wide-1600x600.png)

SVG with a size, with only a viewBox, and without both:

![Sized SVG](/images/svg-sized-208x128.svg)

![SVG with a viewBox](/images/svg-viewbox-only.svg)

![SVG without a size](/images/svg-no-size.svg)

An inline image ![icon](/images/icon-24.png) inside a sentence.

A linked image:

[![Badge](/images/badge-240x80.png)](https://example.com)

A broken image shows its alt text:

![This image failed to load](https://example.com/not-found.png)

## HTML

<div>
An HTML block with <b>bold</b>, <i>italic</i> and <a href="https://example.com">a link</a>.<br>
A second line after br.
</div>

<!-- An HTML comment is hidden and adds no spacing -->

Inline tags: <b>bold</b>, <i>italic</i>, <u>underline</u>, <s>strikethrough</s>, <mark>mark</mark>, <small>small</small>,
H<sub>2</sub>O, E = mc<sup>2</sup>, <kbd>Ctrl</kbd> and <code>code</code>.

The last paragraph of the document.
