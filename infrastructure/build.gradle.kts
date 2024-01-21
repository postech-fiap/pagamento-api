dependencies {
    implementation(project(":domain"))

    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.12.0")
    testImplementation(project(mapOf("path" to ":presentation")))
}
