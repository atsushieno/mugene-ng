# mugene-ng: mugene MML compiler, next-gen

## What is this?

This is the successor of my [mugene](https://github.com/atsushieno/mugene/) [Music Macro Language](https://en.wikipedia.org/wiki/Music_Macro_Language) compiler to Kotlin Multiplatform (from C#), so that I can go forward and improve the entire ecosystem and toolchains. Now it can be used as a JVM library, native library, or a JavaScript library, as well as standalone native compiler or a JVM standalone jar.

The new version in Kotlin has preliminary support for MIDI 2.0 UMP stream format beyond the past C# version did. Any new features will be developed only in this project.

Everything, including the project name, is subject to change at this moment.

The entire language is partly documented as [docs/UsersGuide.md](./docs/UsersGuide.md).

## Building

At this state, you have to run a couple of Gradle tasks to build whatever you'll likely need:

```
./gradlew mugene:generateKotlinCommonGrammarSource build mugene-console-jvm:jar packJsNpmPublication
```

This generates some syntax file support code, builds libs and tools, package a single jar for JVM desktop, and generate NPM artifacts that are also used by VSCodium extension (under development).

## Using command-line compiler

Once you are done with `build` Gradle build task, there will be `mugene-console/build/bin/native/debugExecutable/mugene-console.kexe` (or `.exe` on Windows). You can then run it to compile mugene MML files like: `(the/path/to/)mugene-console.kexe samples/escape.mugene`. For the complete list of command line arguments, just run this executable without arguments.

## mugene-ng limitations

When porting from C#, I disabled `__STORE_FORMAT` primitive operation (because it had C# `String.Format()` as its premise), which effectively killed Vocaloid2 VSQ support and around the feature. Vocaloid2 is too ancient anyways, so there wouldn't be significant drawback.

## License and dependencies

mugene-ng is distributed under the MIT License.

mugene-ng depends on [my fork](https://github.com/atsushieno/antlr-kotlin/tree/main) of [Strumenta/antlr-kotliin](https://github.com/Strumenta/antlr-kotlin) which is distributed under the Apache 2.0 License. (It will go back to the original version once my contributed change is released in their new version.)

mugene-ng depends on [atsushieno/ktmidi](https://github.com/atsushieno/ktmidi) which is distributed under the MIT License.
