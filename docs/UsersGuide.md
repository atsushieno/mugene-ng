(This document is mostly done in Japanese, but there are a few parts that are not yet explained, and only first part is translated to English so far. )

# mugene Users Guide

## Introduction

Welcome to mugene! mugene(-ng) is a compiler, a kind of translator, for generating SMF (Standard MIDI File) from text files that contain music operations called MML (music macro language).

MML is a traditional way to compose music on PCs, but it is still suitable for people who are not familiar with DAWs (Digital Audio Workstations), and/or who are not very knowledgeful in audio programming languages and just want to compose simple music using MIDI instruments. In mugene, the syntax is designed to achieve certain degree of grammatical flexibility, while keeping short and efficient music composition still possible.


## About this document

This document describes the MML syntax **of** mugene. To understand the mugene MML syntax described here, you'd need to have some knowledge of basic MIDI operations, basic SMF structure, and a rough idea on what MML is. For more information about MML, please refer to another introduction.

There are many syntactical variations in MMLs. The purpose of this document is to explain what **mugene syntax** is all about.

This document is not intended to be a complete description of the details of how the MML **compiler** works. The source code of mugene is open to the public and can be freely modified and used.

This document is intended for "users" of mugene, not for those who want to use mugene as a library or hack mugene source code. mugene unfortunately does not provide a stable API for users of mugene as a library. Yet, the current API for entrypoints should be fairly easy.


## Basic structure of mugene and mugene MML

Compared to other kinds of MML grammars, mugene's MML grammar is more "generalized", and most of the operations are defined as "default macros", which are a combination of primitive operations (e.g. "output operation byte 90h"). Ultimately, the only operations that cannot be customized by mugene users are the primitive operations provided by `mugene-console.kexe` (or `.exe`), the executable of the MML compiler.

Many operations are described in a file named `default-macro.mml`, which we call "default macro", and it is up to the user whether to use it or not (most users would use it). It is up to the user to decide whether to use it or not. Most users will probably use it as-is, but you can use your own customized `default-macro.mml`, or rebuild it from scratch (although some of the operations provided by `mugene-console.kexe` are not included in the (although some of the operations provided by mugene.exe are specialized for implementing `default-macro.mml` and may not be suitable for you to design your own language altogether).

(Your customized `default-macro.mml` would be useful for example if you want to reverse the effects of octave increase/decrease `>` `<` or relative velocity `)` `(` commands.)

For those who would like to understand how mugene macros work, `default-macro.mml` would be a good starter to read.


## Basics of mugene MML syntax

### MML line types

mugene MML is written as a text file. In principle, its content is interpreted on a line-by-line basis.

There are three types of "MML lines" in mugene MML:

- Directives: they are lines prefixed with `#` and contain various commands other than track.
- Comment: a line starting with `//` are comment that will not be compiled.
- Track: Starts with an optional alphabetic block identifier, followed by a numeric track number, describing the contents of the SMF track.

Any other character will result in an error, except that (1) if a text line starts with space characters, the line is treated as a continuation of the previous track line, and (2) if a text line ends with a `\` (backslash) after removing comments, the following line is also recognized as a continuation of the previous MML line.

```
// Example:
1 cdefg4, \
	2 abc // the content will be cdefg4,2abc
```

Whitespace is defined as a space or a tab. An MML operation and a group of arguments must be included in a single MML line, and no line breaks are allowed (except that multiple lines may be concatenated using the `\` symbol explained above).

Comment lines are used for leaving users' own annotations in the MML, and do not affect the compilation of the MML.

The role of the directive line depends on the identifier that follows the #. The track line is all that is needed to play a sound in MML, but in many cases you will want to specify some sort of directive.

### MML track

An MML track corresponds to the concept of a track in SMF. Tracks and channels are two distinct concepts. A single channel can be controlled from multiple tracks (in general).

In a track line, put the track number first, and then put the MML operations subsequently. The track number is specified by a non-negative numeric (usually integer) value (the mugene compiler does not check the logical limit, but usually a few dozen tracks would suffice). The track specification can be denoted as complex as the following:

- `1,2` : Specify track 1 and track 2
- `2-5` : Specify tracks 2-5 (same as 2,3,4,5)
- `1,3-5,7` : Combination of the above. In this case, specify tracks 1,3,4,5,7

An MML operation can be either (1) a primitive operation or (2) a macro call. Primitive operations are used to define standard macros, usually defined in `default-macro.mml`, and are not used by the user, but can be used in the syntax. A macro call is (literally) an operation that calls a predefined macro.

An MML operation consists of an identifier, which is the operation word, and arguments to the operation. We will describe them more in depth later.


### Identifier

An identifier is a string used in macro names and primitive operation definitions, and is a concept that identifies an operation in MML.

Unlike most programming languages, mugene MML allows many characters as identifier names (because it is designed to allow people who are familiar with MML to assign the characters that match their experience to their favorite operations), but it also has stricter restrictions than most programming languages. On the other hand, it also has stricter restrictions than general programming languages.

The grammatical restrictions of identifier names are as follows:

- Characters that are always forbidden: `0` `1` `2` `3` `4` `5` `6` `7` `8` `9` `"` `,` `{` `}`
- Characters that cannot be used at the beginning: `-` `+` `^` `?`
- Characters that can only be used at the beginning: `/` `:` `(` `)`


### Number, Length, Variable Types

Most MML operations come with arguments (parameters). The note operations that are sent to output (to SMF) depend on what parameters are given.

For example, the operation `o`, which specifies the octave of the current track, takes an argument as the octave height (`o0` to `o10`). Program change, control change (CC), aftertouch (polyphonic or channel), etc., take arguments equivalent to MIDI messages. Multiple arguments are separated by `,` (comma).

Numbers are normally interpreted as decimal numbers, but can be interpreted as hexadecimal numbers by prefixing `#`.

The arguments for a note or a rest command is somewhat special. Such one is a "length" which is treated differently from a "number". For example, if you write `r4`, where `r` is a rest operation, it is a "quarter" rest (`U+1D13D` in Unicode); if you write `r1`, it will be a full rest; if you write `r8.` it will be a dotted eighth rest.

However, `4` in `r4` is not handled internally by the compiler as a number 4. The note length is internally converted to a number called "steps". In the standard state, `r4` becomes a number of 48 steps, `r1` becomes a number of 192 steps, and `r8.` becomes a number of 36 steps (24 * 1.5). This number is obtained by multiplying and dividing the length of the note by the total rest, which is 192 (by default).

Note length can also be written directly as such a number by prefixing the number with `%`. For example, r%48 is the same as a quarter rest.

Incidentally, the number 192, which corresponds to a full rest, can be changed using the directive `#basecount`.

There are other MML directives that take a "string" as an argument. The value of the string is "(...)". and enclosed by two `"` (double quotation) marks. Some characters cannot be represented directly in the string, so they are represented using "escape sequences":

- `\r` : Carriage return (CR)
- `\n` : Line feed (LF)
- `\/` : Slash
- `\"` : double quotation marks
- `\\`: backslash

Finally, about "types" - types are important to understand when defining macros and reading the MML references.

The arguments in any MML operation have "types" respectively. When you use an operation, you have to specify the expected value in the specified type for each argument. mugene MML has the following argument types:

- `number` : a number type for an integer.
- `length` : Note length. It is interpreted as explained earlier. Internally, it is a simple number. Adding a suffix `.` (dot) increases the length by 1.5 (multiple suffixes can be specified: 1.75, 1.875, etc.), and adding `%` directly specifies the number of steps.
- `string` : it is a string type.

There is another `buffer` type used in `default-macro.mml`... this is a special type for special primitive operation `__STORE` that normal mugene users are not supposed to use (internal type).


## Advanced Topics

### The Concept of Time

In MML, when a note or rest operation is executed, it waits for a specified length of time until the next operation. In SMF, this notion of "time between operations" is often called delta time.

The rest operation can also take a negative value as an argument (actually, it is possible for the compiler to process a note operation, but due to the timing of note-on and note-off, such an operation call will result in a strange SMF, so we would not recommend it). You can "rewind" the track sequence with negative-value rest operations.

This is a bit advanced topic, but all MML operations in mugene are associated with a "position on the timeline at the time of the call", which can be referenced and set in MML as a variable called __timeline_position during MML analysis. It is managed per track and cannot be used like inter-track references, but this variable can be used to generate dynamic SMF messages like spectrum.


----

## MML directives

This section is all about directive lines. You would find them useful when you are composing your song, but they can be skipped until you would like to learn.

### #comment : Start commenting out

:format:
- #comment

After this line, the compiler will not process any more lines until the #endcomment directive appears. This is useful if you want to skip MML compilation even more easily than conditional compilation (explained later).

### #endcomment : end of comment out

:format:
- #endcomment

The lines from #comment to this line will not be processed by the compiler.

### #basecount : Specify base count

:format:
- #basecount [number]

Specifies how many step counts a whole note length corresponds to. For more information about the step count, go back to the description on "length" type.

The argument value should be the (immediate) base count.

### #conditional : Specify conditional compilation

:format:
- #conditional block [blocks]
- #conditional track [tracks]

Indicates conditional compilation. It specifies that only the tracks or the blocks in the entire MML that match these condition will be compiled. They are useful when you want to indicate "mute" or "solo" on specific tracks, or when you want to efficiently edit and play back only from a specific part of the song.

Enter a comma-separated list of block names (identifiers) in [blocks], and a list of tracks in [tracks], following the format for specifying multiple tracks.

### #meta : Meta information description

:format:
- #meta title "(Song title)"
- #meta copyright "(Copyright information)"
- #meta text "(other text information)"

Each of these will be output as SMF meta text. (text = `FFh` `01h`, copyright = `FFh` `02h`, title = `FFh` `03h`)

### #define : string simple substitution definition

:format:
- #define [source] [replacement]

Replace all the [source] strings described here with [replacement]. The replacement is performed for the macro definition line and the track line, after all preprocessor instructions were read from the MML sources.

Note that the replacement process is applied to the entire line, not just the "body" of these lines. (This is especially true for rhythm tracks.

(This directive was added specifically for the purpose of specifying "rhythm tracks" to which rhythm track instructions should be applied, using the alias DRUMTRACKS, and defining rhythm track macros for only those tracks.)

### #macro : macro definition

:format:
- #macro [tracks] [identifier] { [...] }

Defines a macro named [identifier]. Its content will be a string enclosed between `{` and `}`.

Normally, [tracks] is omitted. By specifying it explicitly, you can limit which tracks to make this macro definition applicable. Such macros with limited scope would be useful for repeated phrases, chords, and note operations in rhythm tracks, to avoid overlaps with other tracks.

The contents of the macro will be expanded by the compiler accordingly, and circular references are prohibited.

### #include : Specify include file

:format:
- #include [filename]

The contents of the specified file will be expanded and compiled to the location where this directive is placed.

Relative file paths are interpreted as relative to the compiler executable, or relative to the "current directory". Recursive inclusions are prohibited.

----

## Primitive Operations

Primitive operations would be irrelevant to most of mugene users so this section is almost safe to skip.

Although, this section contains many basic oerations for writing advanced macro oerations such as variable assignment, reference, and conditional branching. In other words, the oerations described here are intended for those who write advanced macro oerations.

### __PRINT : Debug output

:format:
- __PRINT [identifier]

Outputs a variable that was set with the name [identifier] on the console at MML compile time. This can be used as a diagnostic (debugging) output for MML compilation.

### __LET : Variable Assignment

:format:
- __LET [identifier], [value]

Set a variable with the name of [identifier] and the value of [value]. The set variable can be referenced by the "reference vairable operator" (`$`).

### __STORE : Add buffer

:format:
- __STORE [identifier], [string], ...

Adds all string values specified in the second and subsequent arguments to the buffer variable declared with the name [identifier] (or declare a new one if there is none).

(This is a special primitive oeration provided for Vocaloid VSQ file support in the past.)

### __APPLY : macro expansion

:format:
- __APPLY [macro_string], arguments...

Parses the string specified by [macro_string] as a macro, and recursively expands it on the fly as a macro call, using the second and subsequent arguments as arguments to that macro oeration call. This `macro_string` does not have to be a string constant.

Use this in combination with the "conditional branch operator" (`?`) for flexible conditional branching of operations.

### __MIDI : MIDI output

:format:
- __MIDI arguments ...

Outputs all arguments as a string of (8-bit) **bytes**. This is basically a number, but note that all of them are only output in the range of one byte.

### __MIDI2 : MIDI 2.0 output

:format:
- __MIDI2 arguments ...

Outputs all arguments as a string of **32-bit integers**. The difference from `__MIDI` operation is the type.

### __MIDI_META : Output MIDI meta-information

:format:
- __MIDI_META arguments ...

Outputs MIDI meta events. Outputs `FFh` first, followed by the arguments as byte stream. If it is a string, it will be converted from string to byte array using UTF-8.

### __ON_MIDI_NOTE_OFF : Note off notification

:format:
- __ON_MIDI_NOTE_OFF

Not for general use - this is an oeration added as a special processing: in `default-macro.mml`, it is called when there is a note-off in a note operation, in order to treat keys with a note of zero-length as a chord construct. This makes it possible to treat e.g. `c0e0g1` as a C-major chord (without this oeration, c0 and e0 would be effectively silent).

I don't see any reason to dare to disable such chord notation, but if you want to, you can edit `default-macro.mml` so that `__ON_MIDI_NOTE_OFF` is not called (or probably easier, `#define __ON_MIDI_NOTE_OFF  // empty`).

### __LOOP_BEGIN : Loop start

:format:
- __LOOP_BEGIN

Indicates the beginning of a loop. In `default-macro.mml`, `[` is assigned as this oeration.

### __LOOP_BREAK : Loop interruption

:format:
- __LOOP_BREAK arguments ...

In `default-macro.mml`, `:` and `/` are assigned to this oeration (no difference between those two character, just two common historical operator for loop-break).

The argument specifies at which loop the context block (from this operation until next loop-break or loop-end oeration) will be performed. The argument can be either omitted, or multiple values can be specified. If the argument is omitted, it will be applied if current loop iteration count matches no other loop block within the current loop.

For example:

```
1    [ A : B :1,3 C :2 D ]5
```

the content will be `A C A D A C A B A`. `:` before `B` has no specified value, so `B` will be inserted for the 4th loop (which was not indicated by other `:`-split blocks), and `C` will be applied in the 1st. and 3rd. loops.

### __LOOP_END : End of loop

Indicates the end of the loop; in default-macro.mml, `]` is assigned as the oeration and is generally used.

### __SAVE_OPER_BEGIN : Start saving oerations temporarily

(This is an internal oeration for vocaloid support, so it is not explained here.

### __SAVE_OPER_END : End of temporary oeration saving

(This is an internal oeration for vocaloid support, so it is not explained here.)

### __SAVE_OPER_END : End of temporary saving

(This is an internal oeration for vocaloid support, so it is not explained here.)

### __RESTORE_OPER : Expand the temporary save oeration

(This is an internal oeration for vocaloid support, so it is not explained here.)

----

## Default Macro operations

(TODO)
