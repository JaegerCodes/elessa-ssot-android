import org.gradle.api.Plugin
import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

@Suppress("TooGenericExceptionCaught")
class ReleaseNotesConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            tasks.register("generateReleaseNotes") {
                group = "ReleaseNotes"
                description = "Generate release notes with conventional commits support"

                doLast {
                    try {
                        generateReleaseNotes()
                    } catch (e: Exception) {
                        logger.error("Failed to generate release notes: ${e.message}")
                        throw e
                    }
                }
            }

            tasks.register("bumpVersion") {
                group = "ReleaseNotes"
                description = "Bump version based on type (major/minor/patch)"

                doLast {
                    val versionType = project.findProperty("versionType") as? String ?: "patch"
                    bumpVersion(versionType)
                }
            }
        }
    }

    private fun Project.generateReleaseNotes() {
        println("🚀 Generating release notes...")

        val appVersionFile = rootProject.file("delivery/version.properties")
        check(appVersionFile.exists()) {
            "version.properties file not found at ${appVersionFile.absolutePath}"
        }

        val versionProperties = Properties().apply {
            load(appVersionFile.inputStream())
        }

        val releaseNotesDir = file("firebase-app-distribution")
        releaseNotesDir.mkdirs()

        val releaseNotes = releaseNotesDir.resolve("CHANGELOG.md")

        val versionName = System.getenv("VERSION_NAME")
            ?: "${versionProperties["MAJOR"]}.${versionProperties["MINOR"]}.${versionProperties["PATCH"]}"

        val versionDate = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()).format(Date())

        // Get the last tag to determine range
        val lastTag = getLastTag() ?: "HEAD~10" // fallback to last 10 commits

        val changelog = generateChangelogFromCommits(lastTag)
        val categorizedChanges = categorizeCommits(changelog)

        val content = buildString {
            appendLine("# Release Notes - Version $versionName")
            appendLine("**Release Date:** $versionDate")
            appendLine()

            if (categorizedChanges.features.isNotEmpty()) {
                appendLine("## ✨ New Features")
                categorizedChanges.features.forEach { appendLine("* $it") }
                appendLine()
            }

            if (categorizedChanges.fixes.isNotEmpty()) {
                appendLine("## 🐛 Bug Fixes")
                categorizedChanges.fixes.forEach { appendLine("* $it") }
                appendLine()
            }

            if (categorizedChanges.improvements.isNotEmpty()) {
                appendLine("## 🔧 Improvements")
                categorizedChanges.improvements.forEach { appendLine("* $it") }
                appendLine()
            }

            if (categorizedChanges.others.isNotEmpty()) {
                appendLine("## 📝 Other Changes")
                categorizedChanges.others.forEach { appendLine("* $it") }
                appendLine()
            }

            appendLine("---")
            appendLine("*This file was automatically generated on $versionDate*")
        }

        releaseNotes.writeText(content)
        println("✅ Release notes generated at: ${releaseNotes.absolutePath}")
    }

    private fun Project.getLastTag(): String? {
        return try {
            val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
                .directory(rootProject.projectDir)
                .start()

            if (process.waitFor() == 0) {
                process.inputStream.bufferedReader().readText().trim()
            } else null
        } catch (e: Exception) {
            logger.warn("Could not get last git tag: ${e.message}")
            null
        }
    }

    private fun Project.generateChangelogFromCommits(since: String): List<String> {
        val process = ProcessBuilder("git", "log", "--format=%an|%s", "$since..HEAD")
            .directory(rootProject.projectDir)
            .start()

        return if (process.waitFor() == 0) {
            process.inputStream.bufferedReader().readLines()
                .filter { it.isNotBlank() }
                .map { line ->
                    val parts = line.split("|", limit = 2)
                    if (parts.size == 2) "[${parts[0]}] ${parts[1]}" else line
                }
        } else {
            logger.warn("Could not retrieve git commits")
            emptyList()
        }
    }

    private fun categorizeCommits(commits: List<String>): CategorizedChanges {
        val features = mutableListOf<String>()
        val fixes = mutableListOf<String>()
        val improvements = mutableListOf<String>()
        val others = mutableListOf<String>()

        commits.forEach { commit ->
            val lowerCommit = commit.lowercase()
            when {
                lowerCommit.contains("feat:") || lowerCommit.contains("feature:") -> features.add(commit)
                lowerCommit.contains("fix:") || lowerCommit.contains("bug:") -> fixes.add(commit)
                lowerCommit.contains("refactor:") || lowerCommit.contains("improve:") ||
                lowerCommit.contains("perf:") -> improvements.add(commit)
                else -> others.add(commit)
            }
        }

        return CategorizedChanges(features, fixes, improvements, others)
    }

    private fun Project.bumpVersion(type: String) {
        val appVersionFile = rootProject.file("delivery/version.properties")
        val versionProperties = Properties().apply {
            load(appVersionFile.inputStream())
        }

        val major = versionProperties.getProperty("MAJOR").toInt()
        val minor = versionProperties.getProperty("MINOR").toInt()
        val patch = versionProperties.getProperty("PATCH").toInt()
        val code = versionProperties.getProperty("CODE").toInt()

        when (type.lowercase()) {
            "major" -> {
                versionProperties.setProperty("MAJOR", (major + 1).toString())
                versionProperties.setProperty("MINOR", "0")
                versionProperties.setProperty("PATCH", "0")
                versionProperties.setProperty("CODE", (code + 1).toString())
            }
            "minor" -> {
                versionProperties.setProperty("MINOR", (minor + 1).toString())
                versionProperties.setProperty("PATCH", "0")
                versionProperties.setProperty("CODE", (code + 1).toString())
            }
            "patch" -> {
                versionProperties.setProperty("PATCH", (patch + 1).toString())
                versionProperties.setProperty("CODE", (code + 1).toString())
            }
            else -> throw IllegalArgumentException("Version type must be: major, minor, or patch")
        }

        versionProperties.store(appVersionFile.outputStream(), "Updated by bumpVersion task")

        val newVersion = "${versionProperties["MAJOR"]}.${versionProperties["MINOR"]}.${versionProperties["PATCH"]}"
        println("✅ Version bumped to: $newVersion (code: ${versionProperties["CODE"]})")
    }

    data class CategorizedChanges(
        val features: List<String>,
        val fixes: List<String>,
        val improvements: List<String>,
        val others: List<String>
    )
}
