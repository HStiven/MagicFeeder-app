pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Cambia a PREFER_SETTINGS si hay problemas
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SingUp_Login_Firebase" // Nombre del proyecto sin espacios
include(":app")
