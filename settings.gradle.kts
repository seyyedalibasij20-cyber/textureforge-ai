pluginManagement {
    repositories {
        google()
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

rootProject.name = "TextureForgeAI"

include(
    ":app",

    ":core:common",
    ":core:domain",
    ":core:data",
    ":core:ai",
    ":core:designsystem",

    ":feature:home",
    ":feature:analyze",
    ":feature:qa",
    ":feature:workflow",
    ":feature:prompt",
    ":feature:library",
    ":feature:projects",
    ":feature:settings",
":feature:onboarding",
)
