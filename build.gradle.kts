plugins {
	kotlin("multiplatform") version "1.8.0"
	kotlin("plugin.serialization") version "1.8.0"
	id("com.android.library") version "7.3.1"
}

group = "dev.epicsquid"
version = "1.0-SNAPSHOT"

repositories {
	google()
	mavenCentral()
	gradlePluginPortal()
}

kotlin {
	android {
		compilations.all {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}


	jvm("desktop") {
		compilations.all {
			kotlinOptions.jvmTarget = "1.8"
		}
		testRuns["test"].executionTask.configure {
			useJUnitPlatform()
		}
	}


//	js(IR) {
//		browser()
//		nodejs()
//	}

	val macosX64 = macosX64()
	val macosArm64 = macosArm64()
	val iosArm64 = iosArm64()
	val iosX64 = iosX64()
	val iosSimulatorArm64 = iosSimulatorArm64()
	val appleTargets = listOf(
		macosX64, macosArm64,
		iosArm64, iosX64, iosSimulatorArm64
	)

	appleTargets.forEach { target ->
		with(target) {
			binaries {
				framework {
					baseName = "surrealkt"
				}
			}
		}
	}

//	linuxX64("linux")
//	mingwX64("windows")


	sourceSets {
		val ktorVersion = "2.2.4"

		val commonMain by getting {
			dependencies {
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
				implementation("io.ktor:ktor-client-core:$ktorVersion")
				implementation("io.ktor:ktor-client-websockets:$ktorVersion")
				implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
			}
		}

//		val commonTest by getting {
//			dependencies {
//				implementation(kotlin("test"))
//				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
//			}
//		}

		val androidMain by getting {
			dependencies {
				implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
			}
		}
//		val androidTest by getting {
//			dependencies {
//				implementation("junit:junit:4.13.2")
//				implementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
//				implementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
//				implementation("androidx.test.ext:junit:1.1.5")
//			}
//		}

		val desktopMain by getting {
			dependsOn(commonMain)
			dependencies {
				implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
			}
		}
		val desktopTest by getting {
			dependencies {
				implementation("junit:junit:4.13.2")
				implementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
				implementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
			}
		}


//		val jsMain by getting {
//			dependencies {
//			}
//		}
//		val jsTest by getting

		val appleMain by creating {
			dependsOn(commonMain)
			dependencies {
				implementation("io.ktor:ktor-client-darwin:$ktorVersion")
			}
		}
//		val appleTest by creating

		appleTargets.forEach { target ->
			getByName("${target.targetName}Main") { dependsOn(appleMain) }
//			getByName("${target.targetName}Test") { dependsOn(appleTest) }
		}

//		val linuxMain by getting {
//			dependencies {
//			}
//		}
//
//		val linuxTest by getting
//
//		val windowsMain by getting {
//			dependencies {
//			}
//		}
//
//		val windowsTest by getting
	}
}

android {
	compileSdk = 33
	defaultConfig {
		minSdk = 21
		targetSdk = 33
	}

	sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
}
