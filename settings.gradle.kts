pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
		mavenCentral()
	}

	plugins {
		val kotlinVersion = extra["kotlin.version"] as String
		val agpVersion = extra["agp.version"] as String

		kotlin("multiplatform").version(kotlinVersion)
		id("com.android.library").version(agpVersion)
	}
}

rootProject.name = "surrealkt"