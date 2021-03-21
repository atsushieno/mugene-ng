# mugene-ng: mugene MML compiler, next-gen

## What is this?

This is an ongoing port of my [mugene](https://github.com/atsushieno/mugene/) [Music Macro Language](https://en.wikipedia.org/wiki/Music_Macro_Language) compiler to Kotlin (from C#), so that I can go forward and improve the entire ecosystem and toolchains.

While it is named as next-gen, there is nothing improved from former C# project so far.  But at this state, future development will happen here, not in C# repo.

Everything, including the project name, is subject to change at this moment, so if you want to use it you might want to use some revisions off of the `main` branch.

The entire language is not documented well, but since it is (so far) port of atsushieno/mugene, those docs are useful.

## Building

At this state, you have to run a couple of Gradle tasks to build whatever you'll likely need:

```
./gradlew mugene:generateKotlinCommonGrammarSource build mugene-console:jar
```

This generates some syntax file support code, builds libs and tools, then package a single jar for JVM desktop.

## Using command-line compiler

Once you are done with `mugene-console;jar` Gradle build task, there will be `mugene-console/build/libs/mugene*.jar` (depends on version name). You can then run it to compile mugene MML files like:

```
java -jar mugene-console/build/libs/mugene-console-0.1.0-SNAPSHOT.jar samples/escape.mugene 
```

## Limitations

When porting from C#, I disabled `__STORE_FORMAT` primitive operation (because it had C# `String.Format()` as its premise), which effectively killed Vocaloid2 VSQ support and around the feature. Vocaloid2 is too ancient anyways, so there wouldn't be significant drawback.

## License and dependencies

mugene-ng is distributed under the MIT License.

mugene-ng depends on [my fork](https://github.com/atsushieno/antlr-kotlin/tree/main) of [Strumenta/antlr-kotliin](https://github.com/Strumenta/antlr-kotlin) which is distributed under the Apache 2.0 License.

mugene-ng depends on [atsushieno/ktmidi](https://github.com/atsushieno/ktmidi) which is distributed under the MIT License.

