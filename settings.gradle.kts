pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Countries"

// App
include(":app")

// Core modules
include(":core:core-domain")
include(":core:core-data")
include(":core:core-ui")
include(":core:core-bottom-sheet")
include(":core:core-testing")

// Feature modules
include(":feature:feature-list")
include(":feature:feature-details")
