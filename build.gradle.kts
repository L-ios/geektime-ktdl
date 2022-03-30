plugins {
    kotlin("jvm") version "1.6.10"
    java
}

group = "io.github.lionseun"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("cn.hutool:hutool-core:5.6.1")
    implementation("cn.hutool:hutool-setting:5.6.1")
    implementation("cn.hutool:hutool-http:5.6.1")
    implementation("cn.hutool:hutool-crypto:5.6.1")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("com.alibaba:fastjson:1.2.47")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.netty:netty-buffer:4.1.65.Final")
    implementation("org.bouncycastle:bcprov-jdk15on:1.68")
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.14.3")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")

    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}