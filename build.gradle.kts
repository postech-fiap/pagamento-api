import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	id("org.jetbrains.kotlinx.kover") version "0.5.0"
	id("org.sonarqube") version "4.4.1.3373"
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
}

sonar {
	properties {
		property("sonar.projectKey", "postech-fiap_pagamento")
		property("sonar.organization", "postech-fiap")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.sourceEncoding", "UTF-8")
	}
}

java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
	group = "br.com.fiap"
	version = "0.0.1-SNAPSHOT"

	apply(plugin = "kotlin")
	apply(plugin = "project-report")
	apply(plugin = "org.sonarqube")

	repositories {
		mavenCentral()
	}
}

subprojects {

	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "kotlin")
	apply(plugin = "org.sonarqube")

	sonar {
		properties {
			property("sonar.projectKey", "postech-fiap_pagamento")
			property("sonar.organization", "postech-fiap")
			property("sonar.host.url", "https://sonarcloud.io")
			property("sonar.sourceEncoding", "UTF-8")
		}
	}

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

		developmentOnly("org.springframework.boot:spring-boot-devtools")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("io.cucumber:cucumber-java8:7.15.0")
		testImplementation("io.cucumber:cucumber-junit:7.15.0")
		testImplementation("org.junit.platform:junit-platform-suite-api:1.10.1")
		testImplementation("io.rest-assured:rest-assured:5.4.0")
	}

	tasks {
		withType<KotlinCompile> {
			kotlinOptions {
				freeCompilerArgs = listOf("-Xjsr305=strict")
				jvmTarget = "17"
			}
		}

		withType<Test> {
			useJUnitPlatform()
			testLogging {
				events("PASSED", "SKIPPED", "FAILED")
			}
		}

		withType<Copy> {
			duplicatesStrategy = DuplicatesStrategy.INCLUDE
		}

	}

}

tasks.withType<BootJar> {
	group = "build"
	dependsOn(":presentation:bootJar")
	doLast {
		ant.withGroovyBuilder {
			val jarPath = "${rootProject.buildDir}/../presentation/build/libs/app.jar"
			val jarDestination = "${rootProject.buildDir}/libs"
			"move"("file" to jarPath, "todir" to jarDestination)
		}
	}
}

tasks.withType<BootRun> {
	group = "application"
	dependsOn(":presentation:bootRun")
}

kover {
	isDisabled = false                      // true to disable instrumentation of all test tasks in all projects
	coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ) // change instrumentation agent and reporter
	intellijEngineVersion.set("1.0.656")    // change version of IntelliJ agent and reporter
	jacocoEngineVersion.set("0.8.8")        // change version of JaCoCo agent and reporter
	generateReportOnCheck = true            // false to do not execute `koverMergedReport` task before `check` task
	disabledProjects = setOf()              // setOf("project-name") or setOf(":project-name") to disable coverage for project with path `:project-name` (`:` for the root project)
	instrumentAndroidPackage = false        // true to instrument packages `android.*` and `com.android.*`
	runAllTestsForProjectTask = false       // true to run all tests in all projects if `koverHtmlReport`, `koverXmlReport`, `koverReport`, `koverVerify` or `check` tasks executed on some project
}

val includeCoverage = listOf(
	"br.com.fiap.pagamento.*",
)

val excludeCoverage = listOf(
	"**/dtos/*",
	"**/enums/*",
	"**/entities/*",
	"**/exceptions/*",
	"**/config/*"
)

tasks.test {
	extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
		isDisabled = false
		binaryReportFile.set(file("${layout.buildDirectory}/custom/result.bin"))
		includes = includeCoverage
		excludes = excludeCoverage
	}
}

tasks.koverMergedHtmlReport {
	isEnabled = true
	htmlReportDir.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
	includes = includeCoverage
	excludes = excludeCoverage
}

tasks.koverMergedXmlReport {
	isEnabled = true
	xmlReportFile.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
	includes = includeCoverage
	excludes = excludeCoverage
}

tasks.register("jacocoTestReport") {
	dependsOn("test", "koverMergedHtmlReport", "koverMergedXmlReport")
}
