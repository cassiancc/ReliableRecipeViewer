@file:Suppress("UnstableApiUsage")

plugins {
    id("net.fabricmc.fabric-loom")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

val minecraft = stonecutter.current.version
val mcVersion = stonecutter.current.project.substringBeforeLast('-')

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
        this["accesswidener"] = "rrv.classtweaker"
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
base.archivesName = "reliable-recipe-viewer"

loom {
    accessWidenerPath = file("src/main/resources/${property("mod.id")}.classtweaker")
}

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

repositories {
    mavenLocal()
    maven {
        name = "Terraformers (Mod Menu)"
        url = uri("https://maven.terraformersmc.com/releases/")
        content {
            includeGroupAndSubgroups("com.terraformersmc")
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
    maven {
        name = "Sisby Maven"
        url = uri("https://repo.sleeping.town/")
        content {
            includeGroupAndSubgroups("folk.sisby")
        }
    }
    maven {
        name = "Cassian's Maven"
        url = uri("https://maven.cassian.cc/")
        content {
            includeGroupAndSubgroups("cc.cassian")
        }
    }
    maven {
        name = "WTHIT"
        url = uri("https://maven2.bai.lol")
        content {
            includeGroupAndSubgroups("mcp.mobius.waila")
            includeGroupAndSubgroups("lol.bai")
        }
    }
    maven {
        // location of the maven that hosts JEI files since January 2023
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")

    implementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")
    compileOnly("maven.modrinth:modmenu:${property("deps.modmenu")}")

    compileOnly("dev.isxander:controlify:${property("deps.controlify")}") {
        exclude(group = "com.terraformersmc")
        exclude(group = "maven.modrinth")
        exclude(group = "net.caffeinemc")
    }
    compileOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")

    compileOnly("folk.sisby:kaleido-config:0.3.3+1.3.2")

    compileOnly("eu.pb4:polymer-core:${property("deps.polymer")}")
    compileOnly("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
    compileOnly("eu.pb4:polydex:${property("deps.polydex")}")
    compileOnly("maven.modrinth:jade:${property("deps.jade")}")
    compileOnly("mcp.mobius.waila:wthit:fabric-${property("deps.wthit")}")
    if (hasProperty("deps.badpackets")) {
        localRuntime("mcp.mobius.waila:wthit:fabric-${property("deps.wthit")}")
        localRuntime("lol.bai:badpackets:fabric-${property("deps.badpackets")}")
    }
    compileOnly("cc.cassian.item-descriptions:item-descriptions-fabric:${property("deps.item_descriptions")}") {
        exclude(group = "mcp.mobius.waila")
        exclude(group = "lol.bai")
    }


    if (stonecutter.eval(mcVersion, "<26.3")) {

        compileOnly("mezz.jei:jei-${property("deps.minecraft")}-fabric:${property("deps.jei")}")
        localRuntime("mezz.jei:jei-${property("deps.minecraft")}-fabric:${property("deps.jei")}")

        localRuntime("cc.cassian.item-descriptions:item-descriptions-fabric:${property("deps.item_descriptions")}") {
            exclude(group = "mcp.mobius.waila")
            exclude(group = "lol.bai")
        }
//        localRuntime("maven.modrinth:modmenu:${property("deps.modmenu")}")
//        localRuntime("eu.pb4:polydex:${property("deps.polydex")}")
        localRuntime("eu.pb4:polymer-core:${property("deps.polymer")}")
        localRuntime("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
        localRuntime("eu.pb4:polymer-resource-pack-extras:${property("deps.polymer")}")
        localRuntime("eu.pb4:polymer-virtual-entity:${property("deps.polymer")}")
        localRuntime("maven.modrinth:jade:${property("deps.jade")}")
    } else {
        compileOnly("mezz.jei:jei-26.2-fabric:30.11.0.66")
    }
}

stonecutter {
    replacements.string {
        direction = eval(current.version, ">26.1")
        replace("EntityType.", "EntityTypes.")
    }
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/rrv.neoforge.mixins.json", "**/accesstransformer.cfg", "**/mods.toml", "rrv.neoforge.mixins.json",)
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = JavaVersion.VERSION_25
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
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    type = if (stonecutter.eval(stonecutter.current.version, ">=26.3")) {
        BETA
    } else {
        STABLE
    }
    displayName = "RRV ${property("mod.version")} for ${stonecutter.current.version} Fabric"
    version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
    changelog = provider { rootProject.file("CHANGELOG-LATEST.md").readText() }
    modLoaders.add("fabric")

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(property("deps.minecraft").toString())
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(property("publish.curseforge_minecraft_version").toString())
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
        client = true
        server = true
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