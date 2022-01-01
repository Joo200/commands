/*
 * Copyright (c) 2016-2021 Daniel Ennis (Aikar) - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

plugins {
    `java-library`
}

applyPlatformAndCoreConfiguration()
applyCommonArtifactoryConfig()

description = "ACF (Bukkit)"

repositories {
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://libraries.minecraft.net") }
    maven { url = uri("https://libraries.minecraft.net") }
}

dependencies {
    api(project(":acf-core"))
    implementation("co.aikar:minecraft-timings:1.0.4")
    compileOnly("io.papermc.paper:paper-api:${Versions.PAPER}")
    compileOnly("io.papermc.paper:paper-mojangapi:${Versions.PAPER}")
    api("net.kyori:adventure-text-minimessage:${Versions.ADVENTURE}")
    compileOnly("com.mojang:brigadier:1.0.17")
}

tasks {
  processResources {
    from("${project.projectDir}/../languages/minecraft/")
  }
}
