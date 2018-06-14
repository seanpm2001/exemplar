plugins {
    groovy
}

dependencies {
    constraints {
        compile("org.codehaus.groovy:groovy-all:2.4.12")
    }
    api(project(":sample-discovery"))
    api(Libraries.JUNIT)
    compileOnly(Libraries.JSR305)
    implementation(Libraries.COMMONS_IO)
    implementation(Libraries.COMMONS_LANG3)
    implementation(gradleTestKit())
    runtime(Libraries.SLF4J) {
        because("This allows use of composite build + dependency locking with gradle/gradle")
    }
    testCompile(Libraries.SPOCK_CORE)
}

// Add samples as inputs for testing
java.sourceSets["test"].resources.srcDirs("src/test/samples")
