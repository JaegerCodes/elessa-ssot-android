
import com.android.build.api.dsl.ApplicationExtension
import com.llamasoft.elessa.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.FileInputStream
import java.util.Properties

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            val appVersionFile = rootProject.file("delivery/version.properties")
            val versionProperties = Properties()
            versionProperties.load(FileInputStream(appVersionFile))

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig.apply {
                    targetSdk = 35
                    versionCode = versionProperties["CODE"].toString().toInt()
                    versionName =
                        "${versionProperties["MAJOR"]}.${versionProperties["MINOR"]}.${versionProperties["PATCH"]}"
                }
            }
        }
    }
}
