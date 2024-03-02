dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.amqp:spring-rabbit:3.1.1")

    testImplementation("com.ninja-squad:springmockk:4.0.2")
}
