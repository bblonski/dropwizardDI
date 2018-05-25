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
}

dependencies {
    compile("io.dropwizard:dropwizard-core:$dropwizardVersion")
//    compile("org.jboss.weld.se:weld-se-shaded:$weldVersion")
//    compile("org.jboss.weld.servlet:weld-servlet-shaded:$weldVersion")
//    compile("javax.servlet.jsp:jsp-api:2.2")
//    compile("javax.enterprise:cdi-api:2.0")
//    compile("org.glassfish.jersey.containers.glassfish:jersey-gf-cdi:$jerseyVersion")
//    compile("javax.transaction:javax.transaction-api:1.2")
    compile("org.glassfish.hk2:hk2-extras:2.5.0-b61")
//    compile("org.eclipse.jetty:jetty-cdi:$jettyVersion")
}
