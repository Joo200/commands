import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*

fun Project.applyPlatformAndCoreConfiguration() {
    applyCommonConfiguration()
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.artifactory")

    tasks
        .withType<JavaCompile>()
        .matching { it.name == "compileJava" || it.name == "compileTestJava" }
        .configureEach {
            val disabledLint = listOf(
                "processing", "path", "fallthrough", "serial"
            )
            options.release.set(8)
            options.compilerArgs.addAll(listOf("-Xlint:all") + disabledLint.map { "-Xlint:-$it" })
            options.isDeprecation = true
            options.encoding = "UTF-8"
            options.compilerArgs.add("-parameters")
        }


    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        "compileOnly"("com.google.guava:guava:15.0")
        "compileOnly"("org.jetbrains:annotations:15.0")

        "testImplementation"("org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}")
        "testImplementation"("org.junit.jupiter:junit-jupiter-params:${Versions.JUNIT}")
        "testImplementation"("org.mockito:mockito-core:${Versions.MOCKITO}")
        "testImplementation"("org.mockito:mockito-junit-jupiter:${Versions.MOCKITO}")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}")

        "testImplementation"("com.google.guava:guava:15.0")
    }

    the<JavaPluginExtension>().withSourcesJar()
    //the<JavaPluginExtension>().withJavadocJar()

    configure<PublishingExtension> {
        publications {
            register<MavenPublication>("maven") {
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
                from(components["java"])
            }
        }
    }

    applyCommonArtifactoryConfig()
}
