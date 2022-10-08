plugins {
    id("java-library")
    id("maven-publish")
    id("io.freefair.lombok") version "6.5.1"
}

group = "com.javaflow"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // TcpIn, TcpOut depends
    api("org.apache.mina:mina-core:2.2.1")

    // HttpIn, HttpOut depends
    api("com.sparkjava:spark-core:2.9.4")

    api("com.google.guava:guava:31.1-jre")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveClassifier.set("")
}
