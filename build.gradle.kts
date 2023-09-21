buildscript {
    repositories {
        mavenCentral()
        google()
        maven {
            url = uri("https://maven.google.com")
        }
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        // Add any other necessary dependencies for your project
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven {
            url = uri("https://maven.google.com")
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}
