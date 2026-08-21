plugins {
    id("maven-publish")
}

afterEvaluate {
    val publishedArtifactId = when (project.name) {
        "oclock-core" -> "core"
        "oclock-watchface-renderer" -> "watchface-renderer"
        else -> project.name
    }
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = project.group.toString()
                artifactId = publishedArtifactId
                version = project.version.toString()
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/ahugenb333/compose-oclock")
                credentials {
                    username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                    password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
