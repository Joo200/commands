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

description = "ACF (Core)"

dependencies {
    api("co.aikar:Table:1.0.0-SNAPSHOT")
    api("co.aikar:locales:1.0-SNAPSHOT")
    api("net.jodah:expiringmap:0.5.10")
    implementation("org.jetbrains:annotations:24.0.0")

    implementation("co.aikar:Table:1.0.0-SNAPSHOT")
    implementation("co.aikar:locales:1.0-SNAPSHOT")
    implementation("net.jodah:expiringmap:0.5.10")

    api("net.kyori:adventure-text-minimessage:${Versions.ADVENTURE}")
    api("net.kyori:adventure-api:${Versions.ADVENTURE}")

}

tasks {
  processResources {
    from("${project.projectDir}/../languages/core/")
  }
}
