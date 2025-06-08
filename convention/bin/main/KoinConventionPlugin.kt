import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.android")
        pluginManager.apply("com.google.devtools.ksp")
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies {
            add("implementation", libs.findLibrary("koin-core").get())
            add("implementation", libs.findLibrary("koin-android").get())
            add("implementation", libs.findLibrary("koin-annotations").get())
            add("ksp", libs.findLibrary("koin-ksp-compiler").get())

            add("testImplementation", libs.findLibrary("koin-test").get())
            add("testImplementation", libs.findLibrary("kotlin-test").get())
        }
    }
}
