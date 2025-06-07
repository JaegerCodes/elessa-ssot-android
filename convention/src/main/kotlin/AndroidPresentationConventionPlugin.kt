import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidPresentationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("llamasoft.android.library")
        pluginManager.apply("llamasoft.library.jacoco")
        pluginManager.apply("llamasoft.android.compose")
        pluginManager.apply("llamasoft.koin")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            add("implementation", project(":core:model"))
            add("implementation", project(":core:accessibility"))
            add("implementation", project(":core:navigation"))
            add("implementation", project(":elessa-ui"))
            add("implementation", libs.findLibrary("lottie").get())
            add("implementation", libs.findLibrary("koin-androidx-compose").get())
        }
    }
}
