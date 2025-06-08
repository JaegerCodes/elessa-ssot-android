import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.llamasoft.elessa.configureModuleJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationJacocoConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.gradle.jacoco")

            val extension = extensions.getByType<ApplicationAndroidComponentsExtension>()
            configureModuleJacoco(extension)
        }
    }
}
