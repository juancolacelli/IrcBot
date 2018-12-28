import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    maven
    kotlin("jvm") version "1.3.11"
}

repositories {
    jcenter()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

allprojects {
    dependencies {
        compile(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")
        compile("com.gitlab.jic:irclib:0.2.0")
        compile("com.google.code.gson:gson:2.8.5")
        compile("org.jsoup:jsoup:1.11.3")

        testCompile("org.junit.jupiter:junit-jupiter-api:5.3.2")
        testCompile("org.junit.jupiter:junit-jupiter-params:5.3.2")
        testCompile("org.awaitility:awaitility:3.1.5")
        // testCompile("org.mockito:mockito-core:2.23.4")
        testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.0")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.1.0")
        testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.2")
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
