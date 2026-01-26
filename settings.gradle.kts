rootProject.name = "stream-weaver"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("libs:common-models")
include("services:ingester-service")