/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn how to create Gradle builds at https://guides.gradle.org/creating-new-gradle-builds/
 */

val dropwizardVersion = "1.3.2"
//val weldVersion = "3.0.4.Final"
val jettyVersion = "9.3.0.M2"
val weldVersion = "3.0.4.Final"
val jerseyVersion = "2.14"
plugins {
    java
}

repositories {
    mavenCentral()
//    maven("https://jitpack.io")
}

dependencies {
    implementation("io.dropwizard:dropwizard-bom:1.3.3")
    compile("io.dropwizard:dropwizard-core")
    compile("org.glassfish.hk2:hk2-api:2.5.0-b61")
    compile("org.glassfish.hk2:hk2-locator:2.5.0-b61")
    compile("org.glassfish.hk2:hk2-extras:2.5.0-b61")
//    compile("com.github.alex-shpak:dropwizard-hk2bundle:0.6.0")
}
