# mugene-ng: mugene MML compiler, next-gen

![maven repo](https://img.shields.io/maven-central/v/dev.atsushieno/mugene)

## What is this?

This is the successor of my [mugene](https://github.com/atsushieno/mugene/) [Music Macro Language](https://en.wikipedia.org/wiki/Music_Macro_Language) compiler to Kotlin Multiplatform (from C#), so that I can go forward and improve the entire ecosystem and toolchains. Now it can be used as a JVM library, native library, a JavaScript library, or even a WebAssembly library, as well as standalone native compiler or a JVM standalone jar.

The new version in Kotlin has preliminary support for MIDI 2.0 UMP stream format beyond the past C# version did. Any new features will be developed only in this project.

Everything, including the project name, is subject to change at this moment.

The entire language is partly documented as [docs/UsersGuide.md](./docs/UsersGuide.md).

## Using mugene-ng as a library

mugene-ng is available at Maven Central. If you would like to use it as part of your application, add the following lines in the `dependencies` section in `build.gradle(.kts)`:

```
dependencies {
    implementation "dev.atsushieno:mugene:+" // replace + with the actual version
}
```

## Building and using command-line compiler

To use mugene-ng as command line compiler, run:

```
./gradlew mugene-console:jvmRun [args]
```

Alternatively, if you run `./gradlew build`, then there will be `mugene-console/build/bin/[platform-arch-dir]/debugExecutable/mugene-console.kexe` (or `.exe` on Windows). You can then use it like a standalone executable to compile mugene MML files like: `(the/path/to/)mugene-console.kexe samples/escape.mugene`. For the complete list of command line arguments, just run this executable without arguments.

## mugene-ng VSCode extension

mugene-ng is a Kotlin Multiplatform project that supports Kotlin/JS. We publish mugene-ng as an NPM package, as well as [vscode-mugene-language extension](https://marketplace.visualstudio.com/items?itemName=atsushieno.vscode-language-mugene) (the extension used to support .NET based mugene that only ran the compiler as a "native" executable, but now we are fully JS based).

Though note that due to some flaws in publishing workflow, the vscode extension may not be always the latest.

## mugene-ng applications

While mugene-ng can be evaluated as a standalone command line MML compiler project, it is actually part of the [ktmidi](https://github.com/atsushieno/ktmidi) ecosystem. It is the core part of [augene-ng](https://github.com/atsushieno/augene-ng) project, which makes use of its MML to MIDI 2.0 compiler then to target audio plugins sequencer engine.

mugene-ng is also used in a virtual MIDI 1.0/2.0 keyboard [kmmk](https://github.com/atsushieno/kmmk) project, which is also part of ktmidi ecosystem.

## mugene-ng limitations

When porting from C#, I disabled `__STORE_FORMAT` primitive operation (because it had C# `String.Format()` as its premise), which effectively killed Vocaloid2 VSQ support and around the feature. Vocaloid2 is too ancient anyways, so there wouldn't be significant drawback.

## License and dependencies

mugene-ng is distributed under the MIT License.

mugene-ng depends on [Strumenta/antlr-kotliin](https://github.com/Strumenta/antlr-kotlin) which is distributed under the Apache 2.0 License.

mugene-ng depends on [atsushieno/ktmidi](https://github.com/atsushieno/ktmidi) which is distributed under the MIT License.
