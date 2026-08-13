@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.kikugie.loom-back-compat")
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
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")
    modCompileOnly("maven.modrinth:modmenu:${property("deps.modmenu")}")

    modCompileOnly("dev.isxander:controlify:${property("deps.controlify")}") {
        exclude(group = "com.terraformersmc")
        exclude(group = "maven.modrinth")
        exclude(group = "net.caffeinemc")
    }
    modCompileOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")

    compileOnly("folk.sisby:kaleido-config:0.3.3+1.3.2")

    modCompileOnly("eu.pb4:polymer-core:${property("deps.polymer")}")
    modCompileOnly("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
    modCompileOnly("maven.modrinth:jade:${property("deps.jade")}")
    modCompileOnly("mcp.mobius.waila:wthit:fabric-${property("deps.wthit")}")
    modCompileOnly("cc.cassian.item-descriptions:item-descriptions-fabric:${property("deps.item_descriptions")}") {
        exclude(group = "mcp.mobius.waila")
        exclude(group = "lol.bai")
    }
    modLocalRuntime("cc.cassian.item-descriptions:item-descriptions-fabric:${property("deps.item_descriptions")}") {
        exclude(group = "mcp.mobius.waila")
        exclude(group = "lol.bai")
    }

    if (stonecutter.eval(mcVersion, ">26")) {
        modCompileOnly("eu.pb4:polydex:${property("deps.polydex")}")
    } else {
        modCompileOnly("maven.modrinth:polydex:${property("deps.polydex")}")
    }

    if (stonecutter.eval(mcVersion, "<26.3")) {
        modLocalRuntime("maven.modrinth:modmenu:${property("deps.modmenu")}")

        modCompileOnly("mezz.jei:jei-${property("deps.minecraft")}-fabric:${property("deps.jei")}")
        modLocalRuntime("mezz.jei:jei-${property("deps.minecraft")}-fabric:${property("deps.jei")}")
//        modLocalRuntime("eu.pb4:polydex:${property("deps.polydex")}")
        modLocalRuntime("eu.pb4:polymer-core:${property("deps.polymer")}")
        modLocalRuntime("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
        modLocalRuntime("eu.pb4:polymer-resource-pack-extras:${property("deps.polymer")}")
        modLocalRuntime("eu.pb4:polymer-virtual-entity:${property("deps.polymer")}")
        modLocalRuntime("maven.modrinth:jade:${property("deps.jade")}")
        if (hasProperty("deps.badpackets")) {
            localRuntime("mcp.mobius.waila:wthit:fabric-${property("deps.wthit")}")
            localRuntime("lol.bai:badpackets:fabric-${property("deps.badpackets")}")
        }
    } else {
        compileOnly("mezz.jei:jei-26.2-fabric:30.11.0.66")
    }
}

stonecutter {
    replacements.string {
        direction = eval(current.version, ">26.1")
        replace("EntityType.", "EntityTypes.")
    }
    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("GuiGraphics ", "GuiGraphicsExtractor ")
        replace("GuiGraphics.", "GuiGraphicsExtractor.")
        replace("GuiGraphics;", "GuiGraphicsExtractor;")
    }
    replacements.string {
        direction = eval(current.version, "<26.1")
        replace(".fakeItem(", ".renderFakeItem(")
        replace(".horizontalLine(", ".hLine(")
        replace(".verticalLine(", ".vLine(")
        replace("guiGraphics.text(", "guiGraphics.drawString(")
        replace("guiGraphics().text(", "guiGraphics().drawString(")
        replace("guiGraphics.textWithWordWrap(", "guiGraphics.drawWordWrap(")
        replace("guiGraphics().textWithWordWrap(", "guiGraphics().drawWordWrap(")
        replace("net.minecraft.world.item.ItemStackTemplate", "cc.cassian.rrv.backport.ItemStackTemplate")
        replace("extractBackground", "renderBackground")
        replace("net.fabricmc.fabric.api.networking.v1.context.PacketContext", "xyz.nucleoid.packettweaker.PacketContext")
        replace("centeredText", "drawCenteredString")
    }
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/rrv.neoforge.mixins.json", "**/accesstransformer.cfg", "**/mods.toml", "rrv.neoforge.mixins.json",)
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(loomx.modJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=26")) {
        JavaVersion.VERSION_25
    } else {
        JavaVersion.VERSION_21
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
    file = loomx.modJar.map { it.archiveFile.get() }
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
        additionalFile(loomx.modSourcesJar) {
            type.set(SOURCES_JAR)
        }
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(property("deps.minecraft").toString())
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
        environment = CLIENT_AND_SERVER
    }

    curseforge {
        additionalFiles.from(loomx.modSourcesJar.map { it.archiveFile.get() })
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