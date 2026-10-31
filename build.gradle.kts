import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("fabric-loom") version("1.13-SNAPSHOT")
    kotlin("jvm") version ("2.4.21")
}

group = property("maven_group")!!
version = property("mod_version")!!

base {
    archivesName.set(property("archives_base_name").toString())
}

repositories {
    mavenCentral()
    maven("https://maven.impactdev.net/repository/development/")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    // Fabric Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    // Cobblemon
    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks {
    processResources {
        val modVersion = project.version.toString()
        inputs.property("version", modVersion)

        filesMatching("fabric.mod.json") {
            expand("version" to modVersion)
        }
    }

    jar {
        from("LICENSE")
    }
}
