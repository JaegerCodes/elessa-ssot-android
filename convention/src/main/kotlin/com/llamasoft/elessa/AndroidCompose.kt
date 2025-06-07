package com.llamasoft.elessa

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("composeVersion").get().toString()
        }

        dependencies {
            val bom = libs.findLibrary("compose-bom").get()

            add("implementation", platform(bom))
            add("implementation", libs.findBundle("compose-bundle-ui").get())

            add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
            add("debugImplementation", libs.findLibrary("compose-test-manifest").get())

            add("androidTestImplementation", platform(bom))
            add("androidTestImplementation", libs.findLibrary("compose-ui-test").get())
        }
    }
}
