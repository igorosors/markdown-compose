import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "io.github.igorosors.markdown"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // 1.7 is the first version with AnnotatedString.fromHtml
    api("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("androidx.core:core-ktx:1.13.1")

    api("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-svg:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    implementation("org.commonmark:commonmark:0.30.1")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.30.1")
    implementation("org.commonmark:commonmark-ext-gfm-strikethrough:0.30.1")
    implementation("org.commonmark:commonmark-ext-autolink:0.30.1")

    testImplementation("junit:junit:4.13.2")
}

kotlin {
    explicitApi()

    // Apps on Kotlin 2.1 can use the library. Coil 3.3 is built with Kotlin 2.2, which Kotlin 2.1 still reads
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_1
        apiVersion = KotlinVersion.KOTLIN_2_1
    }
    coreLibrariesVersion = "2.1.21"
}

mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources(),
            variant = "release",
        )
    )
    publishToMavenCentral()
    // Maven Central requires signing, while CI and local builds have no key
    if (providers.gradleProperty("signingInMemoryKey").isPresent || providers.gradleProperty("signing.keyId").isPresent) {
        signAllPublications()
    }

    coordinates("io.github.igorosors", "markdown-compose", "1.0.0")

    pom {
        name = "markdown-compose"
        description = "Markdown rendering for Jetpack Compose based on commonmark-java"
        inceptionYear = "2026"
        url = "https://github.com/igorosors/markdown-compose"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "igorosors"
                name = "igorosors"
                url = "https://github.com/igorosors"
            }
        }
        scm {
            url = "https://github.com/igorosors/markdown-compose"
            connection = "scm:git:git://github.com/igorosors/markdown-compose.git"
            developerConnection = "scm:git:ssh://git@github.com/igorosors/markdown-compose.git"
        }
    }
}
