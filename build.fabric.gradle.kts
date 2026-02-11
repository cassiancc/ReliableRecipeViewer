@file:Suppress("UnstableApiUsage")

plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["mod_version"] = "${prop("mod.version")}+${prop("deps.minecraft")}"
        this["minecraft"] = prop("deps.minecraft")
        this["mod_name"] = prop("mod.name")
        this["mod_description"] = prop("mod.description")
        this["mod_authors"] = prop("mod.authors")
        this["mod_license"] = prop("mod.license")
        this["minecraft_version_range"] = prop("deps.minecraft_version_range")
        this["accesswidener"] = "rrv.accesswidener"
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "rrv.neoforge.mixins.json", "META-INF/mods.toml")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
base.archivesName = "reliable-recipe-viewer"

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${property("mod.id")}.accesswidener")
}

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

repositories {
    mavenLocal()
    maven {
        name = "Parchment Mappings"
        url = uri("https://maven.parchmentmc.org")
        content {
            includeGroupAndSubgroups("org.parchmentmc")
        }
    }
    maven {
        name = "Terraformers (Mod Menu)"
        url = uri("https://maven.terraformersmc.com/releases/")
        content {
            includeGroupAndSubgroups("com.terraformersmc")
        }
    }
    maven {
        name = "Gegy"
        url = uri("https://maven.gegy.dev/releases/")
        content {
            includeGroupAndSubgroups("dev.lambdaurora")
        }
    }
    maven {
        name = "Xander Maven"
        url = uri("https://maven.isxander.dev/releases")
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    maven {
        name = "Quilt Maven"
        url = uri("https://maven.quiltmc.org/repository/release/")
        content {
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    maven {
        name = "Nucleoid Maven (Polymer)"
        url = uri("https://maven.nucleoid.xyz/releases")
        content {
            includeGroupAndSubgroups("eu.pb4")
            includeGroupAndSubgroups("xyz.nucleoid")
        }
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroupAndSubgroups("maven.modrinth")
        }
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    mappings(loom.layered {
        officialMojangMappings()
        if (hasProperty("deps.parchment"))
            parchment("org.parchmentmc.data:parchment-${property("deps.parchment")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")
    modCompileOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}")
        modLocalRuntime("com.terraformersmc:modmenu:${property("deps.modmenu")}")

    modCompileOnly("dev.isxander:controlify:${property("deps.controlify")}"){
        exclude(group = "maven.modrinth")
    }
    modCompileOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")
    modCompileOnly("eu.pb4:polymer-core:${property("deps.polymer")}")
        modLocalRuntime("eu.pb4:polymer-core:${property("deps.polymer")}")
    modCompileOnly("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
        modLocalRuntime("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
        modLocalRuntime("eu.pb4:polymer-resource-pack-extras:${property("deps.polymer")}")
    modCompileOnly("maven.modrinth:polydex:${property("deps.polydex")}")
        modLocalRuntime("maven.modrinth:polydex:${property("deps.polydex")}")
    modCompileOnly("eu.pb4:sgui:${property("deps.sgui")}")
        modLocalRuntime("eu.pb4:sgui:${property("deps.sgui")}")
        modLocalRuntime("eu.pb4:placeholder-api:${property("deps.placeholder")}")
        modLocalRuntime("eu.pb4:predicate-api:${property("deps.predicate")}")
        modLocalRuntime("eu.pb4:sidebar-api:${property("deps.sidebar")}")
        modLocalRuntime("eu.pb4:player-data-api:${property("deps.player_data")}")
        modLocalRuntime("me.lucko:fabric-permissions-api:${property("deps.permissions_api")}")
        modLocalRuntime("xyz.nucleoid:server-translations-api:${property("deps.server_translations")}")

    val modules = listOf("command-api-v2", "key-binding-api-v1", "item-api-v1", "rendering-v1", "transitive-access-wideners-v1", "registry-sync-v0", "resource-loader-v0")
    for (it in modules) modImplementation(fabricApi.module("fabric-$it", property("deps.fabric-api") as String))
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}


stonecutter {
    replacements.string {
        direction = eval(current.version, ">1.21.10")
        replace("ResourceLocation", "Identifier")
    }
}

java {
    withSourcesJar()
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=1.21")) {
        JavaVersion.VERSION_21
    } else {
        JavaVersion.VERSION_17
    }
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

publishMods {
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

    type = STABLE
    displayName = "RRV ${property("mod.version")} for ${stonecutter.current.version} Fabric"
    version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
    changelog = provider { rootProject.file("CHANGELOG-LATEST.md").readText() }
    modLoaders.add("fabric")

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "cc.cassian.rrv"
            artifactId = "reliable-recipe-viewer-fabric"
            version = "${property("mod.version")}+${property("deps.minecraft")}"

            from(components["java"])
        }
    }
}