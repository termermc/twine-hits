plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version("6.0.0")
}

repositories {
    // Use jcenter for resolving dependencies.
    jcenter()
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    build {
        dependsOn("shadowJar")
    }
}

dependencies {
    // Align versions of all Kotlin components
    compileOnly(platform("org.jetbrains.kotlin:kotlin-bom"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")

    // Use the Kotlin JDK 8 standard library.
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Database
    implementation("org.flywaydb:flyway-core:7.3.2")
    implementation("postgresql:postgresql:9.1-901-1.jdbc4")
    implementation("io.vertx:vertx-sql-client-templates:4.0.0")
    implementation("io.vertx:vertx-codegen:4.0.0")
    annotationProcessor("io.vertx:vertx-codegen:4.0.0:processor")

    // JSON decoding
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.0")

    // Validator
    implementation("net.termer.vertx.kotlin.validation:vertx-web-validator-kotlin:1.0.1")

    // Twine, does not get packaged
    compileOnly("net.termer.twine:twine:2.0")
}