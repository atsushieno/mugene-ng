Language support for mugene-ng MML compiler: https://github.com/atsushieno/mugene-ng

mugene was originally a C# application project which contained vscode extension too. It is mostly a port of the original, without bunch of .NET-specific parts. Namely, we can simply use KotlinJS version of the compiler and therefore no external dependencies are needed.

To build the extension, run `npm install` and `npm run compile`.
Note that F5 from vscode does not build the KotlinJS part.

When you want to make changes to mugene-ng itself and verify the changes in this extension code, replace `"@dev.atsushieno.mugene: (...)"` with:

- `"mugene": "../mugene/build/publications/npm/js/"` for js(LEGACY)
- `"mugene": "../build/js/packages/mugene-ng-mugene"` for js(IR)

(Though I could get this working only with `js(LEGACY)` mode specified in `build.gradle.kts`).
