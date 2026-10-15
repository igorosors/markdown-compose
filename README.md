# markdown-compose

[![CI](https://github.com/igorosors/markdown-compose/actions/workflows/ci.yml/badge.svg)](https://github.com/igorosors/markdown-compose/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.igorosors/markdown-compose)](https://central.sonatype.com/artifact/io.github.igorosors/markdown-compose)

Markdown in Jetpack Compose with a single composable.

The library is written in pure Compose. Text is parsed by
[commonmark-java](https://github.com/commonmark/commonmark-java) following the CommonMark spec, and images are loaded with
[Coil 3](https://coil-kt.github.io/coil/).

## Features

- CommonMark and GFM extensions: tables, strikethrough, autolinks
- Images and SVG, as separate blocks and inside text
- HTML blocks and common inline tags
- Custom colors and styles via `MarkdownDefaults`
- Lazy list for large documents

## Installation

```kotlin
dependencies {
    implementation("io.github.igorosors:markdown-compose:1.0.0")
}
```

Or in `build.gradle`:

```groovy
dependencies {
    implementation 'io.github.igorosors:markdown-compose:1.0.0'
}
```

Images from the network require the permission in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Requirements: minSdk 21, Compose 1.7+, Kotlin 2.1+.

## Usage

```kotlin
MarkdownColumn(
    markdown = text,
    onLinkClick = { url -> openUrl(url) },
)
```

All links are passed to `onLinkClick`, the library does not open anything by itself.

### Styling

```kotlin
MarkdownColumn(
    markdown = text,
    onLinkClick = { url -> openUrl(url) },
    colors = MarkdownDefaults.colors(text = Color.DarkGray, link = Color.Red),
    typography = MarkdownDefaults.typography(text = TextStyle(fontSize = 15.sp, lineHeight = 22.sp)),
    blockSpacing = 16.dp,
    baseUrl = "https://example.com",
    imageLoader = imageLoader,
)
```

| Parameter | Description |
|---|---|
| `colors` | text colors |
| `typography` | text styles |
| `blockSpacing` | spacing between blocks |
| `baseUrl` | prepended to URLs like `/path` |
| `imageLoader` | Coil image loader |

### Large documents

```kotlin
LazyMarkdownColumn(
    markdown = text,
    onLinkClick = { url -> openUrl(url) },
    loading = { CircularProgressIndicator() },
)
```

To parse a document only once, do it in a ViewModel

```kotlin
val document = MarkdownDocument.parse(text)
```

```kotlin
LazyMarkdownColumn(
    document = document, 
    onLinkClick = { url -> openUrl(url) },
)
```

To add markdown to your own `LazyColumn`, use `markdownItems`:

```kotlin
LazyColumn {
    markdownItems(
        document = document, 
        onLinkClick = { url -> openUrl(url) },
    )
}
```

## Not supported

- Task lists `- [ ]`
- Syntax highlighting in code blocks
- `<img>` inside HTML: the `alt` text is shown instead

## Sample

The `sample` module shows all features of the library.

```bash
./gradlew :sample:installDebug
```

## Dependencies

[commonmark-java](https://github.com/commonmark/commonmark-java) (BSD-2-Clause),
[autolink-java](https://github.com/robinst/autolink-java) (MIT),
[Coil 3](https://github.com/coil-kt/coil) with [AndroidSVG](https://github.com/BigBadaboom/androidsvg)
and [OkHttp](https://github.com/square/okhttp) (Apache 2.0),
[Jetpack Compose](https://developer.android.com/jetpack/compose) (Apache 2.0).

## License

```
Copyright 2026 igorosors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
