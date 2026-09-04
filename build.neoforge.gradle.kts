plugins {
    id("net.neoforged.moddev")
    id ("dev.kikugie.postprocess.jsonlang")
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
        this["mod_id"] = prop("mod.id")
        this["mod_name"] = prop("mod.name")
        this["mod_description"] = prop("mod.description")
        this["mod_authors"] = prop("mod.authors")
        this["mod_license"] = prop("mod.license")
        this["neoforge_loader_version_range"] = "[1,)"
        this["neoforge_version_range"] = prop("deps.neoforge_version_range")
        this["minecraft_version_range"] = prop("deps.minecraft_version_range")
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
base.archivesName = "reliable-recipe-viewer"

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

repositories {
    maven {
        name = "Xander Maven"
        url = uri("https://maven.isxander.dev/releases")
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    mavenCentral()
    maven {
        name = "Quilt Maven"
        url = uri("https://maven.quiltmc.org/repository/release/")
        content {
            includeGroupAndSubgroups("org.quiltmc.parsers")
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
        name = "Sisby Maven"
        url = uri("https://repo.sleeping.town/")
        content {
            includeGroupAndSubgroups("folk.sisby")
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
        name = "WTHIT"
        url = uri("https://maven2.bai.lol")
        content {
            includeGroupAndSubgroups("mcp.mobius.waila")
            includeGroupAndSubgroups("lol.bai")
        }
    }
    maven {
        name = "Sinytra"
        url = uri("https://maven.su5ed.dev/releases")
        content {
            includeGroupAndSubgroups("org.sinytra")
            includeGroupAndSubgroups("dev.su5ed")
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
    compileOnly("dev.isxander:controlify:${property("deps.controlify")}") {
        isTransitive = false;
    }
    compileOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}") {
        isTransitive = false;
    }
    compileOnly("cc.cassian.item-descriptions:item-descriptions-neoforge:${property("deps.item_descriptions")}") {
        isTransitive = false;
    }
    compileOnly("folk.sisby:kaleido-config:0.3.3+1.3.2")
    compileOnly("maven.modrinth:jade:${property("deps.jade")}") {
        isTransitive = false;
    }
    compileOnly("mcp.mobius.waila:wthit:neo-${property("deps.wthit")}")

    // Sinytra Connector support
    if (stonecutter.eval(mcVersion, ">26")) {
        compileOnly("org.sinytra:forgified-fabric-loader:2.5.85+0.19.3+26.1.2")
        compileOnly("org.sinytra.forgified-fabric-api:fabric-recipe-api-v1:9.0.16+a1e31eec4c")
    }

    // JEI support
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge:${property("deps.jei")}")
//    runtimeOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge:${property("deps.jei")}")

}

neoForge {
    version = property("deps.neoforge") as String
    validateAccessTransformers = true

    runs {
        register("client") {
            gameDirectory = file("run/")
            client()
        }
        register("server") {
            gameDirectory = file("run/")
            server()
        }
    }

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets["main"])
        }
    }
    sourceSets["main"].resources.srcDir("src/main/generated")
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/rrv.fabric.mixins.json", "**/*.classtweaker", "**/mods.toml")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
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
        replace(".itemDecorations(", ".renderItemDecorations(")
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
    file = tasks.jar.map { it.archiveFile.get() }

    type = if (stonecutter.eval(stonecutter.current.version, ">=26.3")) {
        BETA
    } else {
        STABLE
    }
    displayName = "RRV ${property("mod.version")} for ${stonecutter.current.version} NeoForge"
    version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
    changelog = provider { rootProject.file("CHANGELOG-LATEST.md").readText() }
    modLoaders.add("neoforge")

    modrinth {
        additionalFile(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar")) {
            type.set(SOURCES_JAR)
        }
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(property("deps.minecraft").toString())
        minecraftVersions.addAll(additionalVersions)
        environment = CLIENT_AND_SERVER
    }

    curseforge {
        additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })
        projectId = property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(property("publish.curseforge_minecraft_version").toString())
        minecraftVersions.addAll(additionalVersions)
        client = true
        server = true
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "cc.cassian.rrv"
            artifactId = "reliable-recipe-viewer-neoforge"
            version = "${property("mod.version")}+${property("deps.minecraft")}"

            from(components["java"])
        }
    }
}