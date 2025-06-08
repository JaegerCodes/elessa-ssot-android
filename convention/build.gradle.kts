plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "llamasoft.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidApplicationCompose") {
            id = "llamasoft.android.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("androidLibrary") {
            id = "llamasoft.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidApplicationJacoco") {
            id = "llamasoft.android.jacoco"
            implementationClass = "AndroidApplicationJacocoConventionPlugin"
        }

        register("androidLibraryJacoco") {
            id = "llamasoft.library.jacoco"
            implementationClass = "AndroidLibraryJacocoConventionPlugin"
        }

        register("releaseNotes") {
            id  = "llamasoft.release.notes"
            implementationClass = "ReleaseNotesConventionPlugin"
        }

        register("detekt") {
            id = "llamasoft.detekt"
            implementationClass = "DetektConventionPlugin"
        }

        register("koin") {
            id = "llamasoft.koin"
            implementationClass = "KoinConventionPlugin"
        }

        register("androidElessaPresentation") {
            id = "llamasoft.library.presentation"
            implementationClass = "AndroidPresentationConventionPlugin"
        }

        register("androidElessaData") {
            id = "llamasoft.library.data"
            implementationClass = "AndroidDataConventionPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("Elessa Build Logic")
                description.set("Shared build logic for Elessa Android projects")
                url.set("https://github.com/llamasoft/elessa-build-logic")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("llamasoft")
                        name.set("Llama Software")
                        email.set("dev@llamasoft.com")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "Artifactory"
            url = uri("https://your-company.jfrog.io/artifactory/gradle-plugins")
            credentials {
                username = project.findProperty("artifactory.username") as String?
                    ?: System.getenv("ARTIFACTORY_USERNAME")
                password = project.findProperty("artifactory.password") as String?
                    ?: System.getenv("ARTIFACTORY_PASSWORD")
            }
        }
    }
}
