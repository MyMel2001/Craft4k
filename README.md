# Craft4k
A *highly* accurate recreation of Notch's "Minecraft 4k" content created by NodeMixaholic. (Renamed so Microsoft doesn't come after me.)

## Notes
* This project has no relation to "Minecraft", "Mojang", "Microsoft", or "Notch". Just a simple dev trying to do stupid stuffs.
* I have no proof, however this was painstakingly decompiled (from *highly* obfuscated code), recreated (from *highly* outdated code), and remastered (so it's *slightly* less wonky) by NodeMixaholic.
* I have deep respect for Notch's work - even if I don't agree with everything he may or may not have/will said/say.
* This project does not have a focus on being 4kb in size. I know, boo-hoo.
* Shoutouts to ChatGPT for being smart enough to aide me through this task.
* I grabbed the original .jar file from [here](https://web.archive.org/web/20141101124251/https://www.mojang.com/notch/j4k/minecraft4k/), I think.
* This is mostly rewritten.
  - The majority of the functions are rewritten from some semi-deobfuscated code.
  - However, this code was also obfuscated in a way - such as variables having different names. This is normal for so-called "deobfuscated code".
  - And, thanks to ChatGPT getting a bit confused due to context length (since I did this logged out), and me asking for a version that can run in Java 25, it did mostly a rewrite using my semi-deobfuscated code as a reference.
  - TLDR: Everything for this project has had to be rewritten and modernized due to it's design - from the rendering (painting/repainting), to the launching of the game, the movement, etc.

## How to compile

1. Grab a copy of the source code and OpenJDK 25.
2. Use the command ```javac Craft4k.java Launcher.java``` compile.
3. Use ```jar cfm Craft4k.jar manifest.txt *.class``` if you want to jar for distribution!
4. use ```java -jar Craft4k.jar``` to run if made for distribution, ```java Launcher``` if not (note: no ".class"!)

## There MUST be an easier way to run this, right?

There sure is! Just go [here](https://github.com/MyMel2001/Craft4k/releases) to download a pre-compiled, (hopefully) semi-stable jar, and go directly to step 4!

## What does it look like?
<img width="906" height="567" alt="image" src="https://github.com/user-attachments/assets/7febcd06-0d45-42d1-b5e0-7cdc3271522b" />
