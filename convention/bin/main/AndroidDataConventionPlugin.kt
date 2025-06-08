import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("llamasoft.android.library")
        pluginManager.apply("llamasoft.library.jacoco")
        pluginManager.apply("org.jetbrains.kotlin.android")
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("llamasoft.koin")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            add("implementation", project(":core:model"))
            add("implementation", project(":core:network"))
            // Retrofit + Moshi
            add("implementation", libs.findLibrary("retrofit").get())
            add("implementation", libs.findLibrary("retrofit-moshi").get())
            add("implementation", libs.findLibrary("moshi").get())
            add("implementation", libs.findLibrary("moshi-kotlin").get())
            add("implementation", libs.findLibrary("moshi-adapters").get())
            add("ksp", libs.findLibrary("moshi-ksp").get())
        }
    }
}
