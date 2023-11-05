import com.modrinth.minotaur.dependencies.ModDependency

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    id("fabric-loom") version "1.4-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.8.1"
    id("org.quiltmc.quilt-mappings-on-loom") version "4.2.3"
    id("io.github.juuxel.loom-quiltflower") version "1.9.0"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

group = "de.royzer"
version = "1.3.4"

val minecraftVersion = "1.20.2"

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.14.24")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.90.7+1.20.2")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.13+kotlin.1.9.20")
    modApi("com.terraformersmc:modmenu:8.0.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    processResources {
        val props = mapOf("version" to project.version)

        inputs.properties(props)

        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }
}

modrinth {
    token.set(findProperty("modrinth.token").toString())
    projectId.set("perspektive")
    versionNumber.set(rootProject.version.toString())
    versionType.set("release")
    uploadFile.set(tasks.remapJar.get())
    gameVersions.set(listOf(minecraftVersion))
    loaders.addAll(listOf("fabric", "quilt"))

    dependencies.set(
        listOf(
            ModDependency("P7dR8mSH", "required"),
            ModDependency("Ha28R6CL", "required"),
            ModDependency("mOgUt4GM", "optional")
        )
    )
}

curseforge {
    apiKey = findProperty("curseforge.token") ?: ""
    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        mainArtifact(tasks.getByName("remapJar").outputs.files.first())

        id = "501553"
        releaseType = "release"
        addGameVersion(minecraftVersion)

        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("fabric-api")
            requiredDependency("fabric-language-kotlin")
            optionalDependency("modmenu")
        })
    })
    options(closureOf<com.matthewprenger.cursegradle.Options> {
        forgeGradleIntegration = false
    })
}


configurations.all {
    resolutionStrategy {
//        force("net.fabricmc:fabric-loader:0.14.21")
    }
}