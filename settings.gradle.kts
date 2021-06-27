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

rootProject.name = "acf"
include(":acf-bukkit")
include(":acf-jda")
include(":acf-paper")
include(":acf-velocity")
include(":acf-core")
include(":acf-sponge")
include(":acf-brigadier")
include(":acf-bungee")
include(":acf-adventure-paper")
include(":acf-adventure-bungee")
project(":acf-bukkit").projectDir = file("bukkit")
project(":acf-jda").projectDir = file("jda")
project(":acf-paper").projectDir = file("paper")
project(":acf-velocity").projectDir = file("velocity")
project(":acf-core").projectDir = file("core")
project(":acf-sponge").projectDir = file("sponge")
project(":acf-brigadier").projectDir = file("brigadier")
project(":acf-bungee").projectDir = file("bungee")
project(":acf-adventure-paper").projectDir = file("adventure/paper")
project(":acf-adventure-bungee").projectDir = file("adventure/bungee")
