plugins {
    `maven-publish`
}

allprojects {
    group = "com.llamasoft.elessa"
    version = "0.0.1"
}

tasks.register("publishAll") {
    dependsOn(":convention:publishAllPublicationsToArtifactoryRepository")
    group = "publishing"
    description = "Publish all modules to Artifactory"
}
