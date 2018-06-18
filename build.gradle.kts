/**
 * Uses Gradle Kotlin DSL
 */

val hk2Version = "2.5.0-b61"
val dwVersion = "1.3.3"
plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compile("io.dropwizard:dropwizard-core:$dwVersion")
    compile("org.glassfish.hk2:hk2-api:$hk2Version")
    compile("org.glassfish.hk2:hk2-locator:$hk2Version")
    compile("org.glassfish.hk2:hk2-extras:$hk2Version")
}
