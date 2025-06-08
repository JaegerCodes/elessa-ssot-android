import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention

plugins {
    alias(libs.plugins.jfrog.artifactory) version libs.versions.jfrogArtifactoryVersion.get() apply true
    `maven-publish`
}

allprojects {
    group = "com.llamasoft.elessa.buildlogic"
    version = "0.0.1"

    apply(plugin = "com.jfrog.artifactory")
    apply(plugin = "maven-publish")
}

subprojects {
    configure<ArtifactoryPluginConvention> {
        publish {
            contextUrl = "https://cb4d-179-6-6-210.ngrok-free.app/artifactory"
            repository {
                repoKey = "libs-release-local"
                username = "admin"
                password = "P7WxdLiZKSWD2yf"
            }
            defaults {
                publications("maven")
                setPublishArtifacts(true)
                setPublishPom(true)
            }
        }
    }
}

tasks.register("publishAll") {
    dependsOn(":convention:artifactoryPublish")
    group = "publishing"
    description = "Publish all modules to Artifactory"
}