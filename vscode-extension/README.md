Language support for mugene-ng MML compiler: https://github.com/atsushieno/mugene-ng

mugene was originally a C# application project which contained vscode extension too. It is mostly a port of the original, without bunch of .NET-specific parts. Namely, we can simply use KotlinJS version of the compiler and therefore no external dependencies are needed.

To build the extension, run `npm install` and `npm run compile`.
Note that F5 from vscode does not build the KotlinJS part.

