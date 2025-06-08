
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            extensions.configure<DetektExtension> {
                toolVersion = "1.22.0"

                parallel = true
                config.setFrom("$rootDir/config/detekt/detekt.yml")
                baseline = file("$rootDir/config/detekt/baseline.xml")

                source = files(projectDir)

                ignoredBuildTypes = listOf("release")
            }

            tasks.withType<DetektCreateBaselineTask>().configureEach {
                ignoreFailures.set(true)
                parallel.set(true)
                setSource(files(projectDir))
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
                baseline.set(file("$rootDir/config/detekt/baseline.xml"))

            }

            val reportMerge by tasks.registering(ReportMergeTask::class) {
                output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
            // or "reports/detekt/merge.sarif"
            }

            subprojects {
                tasks.withType<Detekt>().configureEach {

                    reports {
                        // Enable/Disable XML report (default: true)
                        xml.required.set(true)
                        // Enable/Disable HTML report (default: true)
                        html.required.set(true)
                        // Enable/Disable TXT report (default: true)
                        txt.required.set(false)
                        sarif.required.set(true)
                    }
                }

                plugins.withType<DetektPlugin> {
                    tasks.withType<Detekt> { // Sadly it has to be eager.
                        finalizedBy(reportMerge)

                        reportMerge.configure {
                            input.from(xmlReportFile) // or .sarifReportFile
                        }
                    }
                }
            }
        }
    }
}
