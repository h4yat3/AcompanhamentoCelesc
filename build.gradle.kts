import org.jetbrains.compose.desktop.application.dsl.TargetFormat



plugins {
    kotlin("jvm") version "1.9.22" // Latest stable Kotlin
    id("org.jetbrains.compose") version "1.6.0" // Latest Compose
}

group = "org.Hayate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.google.com")  // Add this line
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.foundation:foundation-desktop:1.6.0")
    implementation("androidx.collection:collection-ktx:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AcompanhamentoCelesc"
            packageVersion = "1.0.0"
        }
    }
}
