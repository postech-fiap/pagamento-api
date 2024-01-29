dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("com.ninja-squad:springmockk:4.0.2")
}
