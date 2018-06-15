import org.gradle.internal.deployment.RunApplication

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn how to create Gradle builds at https://guides.gradle.org/creating-new-gradle-builds/
 */

val dropwizardVersion = "1.3.2"
val weldVersion = "3.0.4.Final"
//val weldVersion = "2.4.7.Final"
val jerseyVersion = "2.14"
val owb = "2.0.5"
plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compile("io.dropwizard:dropwizard-core:$dropwizardVersion")
    compile("org.jboss.weld.se:weld-se-shaded:$weldVersion")
//    compile("org.jboss.weld.se:weld-se:$weldVersion")
//    implementation("javax.enterprise:cdi-api:1.2")
    compile("org.glassfish.jersey.containers.glassfish:jersey-gf-cdi:$jerseyVersion")
//    compile("javax.transaction:javax.transaction-api:1.2")
}
