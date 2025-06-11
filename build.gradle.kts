plugins {
    java
}

allprojects {
    group = "hse"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    dependencies {
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}