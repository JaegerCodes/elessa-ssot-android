plugins {
    `kotlin-dsl`
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
