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

An exception to this rule is that `\` can be used as an escape character to accept those characters above, as well as '\' itself. Note that whitespaces are still not allowed.

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

Replace all the [source] strings described here with [replacement]. The replacement is performed for the macro definition line and the track line, after all preprocessor operators were read from the MML sources.

Note that the replacement process is applied to the entire line, not just the "body" of these lines. (This is especially true for rhythm tracks.

(This directive was added specifically for the purpose of specifying "rhythm tracks" to which rhythm track operators should be applied, using the alias DRUMTRACKS, and defining rhythm track macros for only those tracks.)

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

### __LET_PN : Variable Assignment per note

:format:
- __LET_PN [identifier], [note], [value]

Set a variable with the name of [identifier] and the value of [value] only for the argument [note]. Referencing the variable is still without the note number, but `__PER_NOTE` operation has to be called beforehand, otherwise the compiler has no idea which note to reference.

Variable definitions are shared with `__LET`, but value stores are different between channel scope store and note scope store.

### __PER_NOTE : target note for per-note variable

:format:
- __PER_NOTE [note]

In combination with `__LET_PN` operation, specify which note to resolve for per-note variable. It is used to keep the enhanced syntax as closest to existing syntax as possible (otherwise variable resolution will have to take note number too).

### __PER_NOTE_RESET : reset target note

:format:
- __PER_NOTE_RESET

In combination with `__PER_NOTE` operation, it resets context note number.

It is likely unnecessary, but by cleaning up context note number it would avoid possible buggy per-note variable resolution.

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

Now, it's time to move on to the default macro operators, which are for general users. Rephrasing - the operators described here can be customized freely by editing the `default-macro.mml` file.

### CH : Specify output channel

:format:
- CH [channel]

Sets the specified channel as the target of MIDI operator output for the current track. By default, channel `0` is assigned; valid channels are `0`..`15` in MIDI 1.0, and `0`..`255` in MIDI 2.0.

### DEBUG : debug output

:format:
- DEBUG [string]

Outputs the argument as a string. This is just an alias for the primitive operator `__PRINT`.

### NOFF : Note off

:format:
- NOFF [key],[vel]

Turns off notes of the specified scale [key] and velocity [vel]. Both are specified as numbers between 0 and 127. All scale commands use this command; MIDI command `8xh` will be output.

### NON : Note on

:format:
- NON [key],[vel]

Turns on notes of the specified scale [key] and velocity [vel]. Both are specified as numbers between 0 and 127. All scale commands use this command, which outputs the MIDI command `9xh`.


### PAF : Polyphonic Key Pressure (Aftertouch)

:format:
- PAF [key],[vel]

Outputs an aftertouch with a note of the specified scale [key] and velocity [vel]. MIDI operator `Axh` is output.

### CC : Control Change

:format:
- CC [opcode],[val]

Outputs a control change with the specified operator address [opcode] and value [val]. MIDI operator `Bxh` is output.

### PROGRAM : Program Change

:format:
- PROGRAM [val]

Specifies a tone by its tone number, which is a number between 0 and 127. Normally the `@` operator is used.

### CAF : Channel Key Pressure (Aftertouch)

:format:
- CAF [val]

Outputs the aftertouch of the entire channel at the specified value. 0 to 127. MIDI operator `Dxh` is output.

### PITCH : Pitch Bend

:format:
- PITCH [val]

Outputs a pitch bend value with the specified value, which can be a number between 0 and 16383. 8192 is considered as the center.

MIDI operator is `Exh`. The PITCH operator specifies a value close to the raw SMF data as it is, which is not likely useful. On the other hand, with the `B` operator, you can specify a more intuitive value using a negative value.

### l : Specify default note length

:format:
- l [length]

The length specified in the argument will be used as the default value for the note-on and note-off operations when the length is omitted in subsequent occurrences.

### K : Transpose

:format:
- K[val]
- K[c..b]+
- K[c..b]-
- K[c..b]=

This command transposes the keys of the note operation by the value specified in the argument. Any positive/negative number can be specified. However, the valid value range in the final note operation is 0 to 127.

If a key is specified like `Kc`, `Kd`, ... , then only the value of the specific key will be +1 or -1. The values changed by `K[c..b]+` and `K[c..b]-` can be reset to 0 using the `K[c..b]=` operator (e.g. `Kg=`).

### v : Velocity absolute specification

:format:
- v [val]

Sets the specified value as a velocity value (absolute). This value is used as the default velocity value for note-on and note-off operators (individual velocities can also be specified for note operators). Valid values are 0 to 127.

### ( ) : Velocity relative specification

:format:
- ) [val=4]
- ( [val=4]

Sets the specified value by adding or subtracting it from the current velocity value (relative specification). The resulting value works the same as the `v` operator.

`)` is addition, and `(` is subtraction. If you don't like their direction i.e. you prefer `(` for additive and `)` for subtractive, then you might want to modify `default-macro.mml` to reverse the semantics.

The argument can be omitted, then the value is taken from the default velocity sense, which is `4` by default. This value `4` can be changed using the VELOCITY_SENSE directive.

### VELOCITY_SENSE : Specify Velocity relative default value

:format:
- VELOCITY_SENSE [val]

Changes the default value (of `4`) in the velocity relative specification operator `(` `)` to an arbitrary number.

### E : Expression absolute specification

:format:
- E [val]

Sets the specified value as the expression value (absolute specification). This operator outputs `CC #0B`, and therefore applies to all note operators on the target channel. Valid values are 0 to 127.

### E+ E- : Expression relative specification

:format:
- E+ [val]
- E- [val]

Sets the specified value by adding or subtracting from the current expression value (relative specification). The semantics of the value is the same as that of the `v` operator. The argument cannot be omitted, though (there is nothing like `EXPRESSION_SENSE`).

### t TEMPO: absolute tempo specification

:format:
- t [val]
- TEMPO [val]

Specifies the tempo value in terms of the number of quarter notes in a minute.

Tempo is information that is shared by the entire song across tracks. The value of a variable, on the other hand, is only stored on a track-by-track basis (the order in which MML lines are compiled is not fixed, so it cannot be specified on multiple tracks). In many DAWs, all tempo operators are placed on track 0, "master track", or "tempo track", which is a good idea to follow on MMLs too.

### t+ t- : relative tempo specification

:format:
- t+ [val]
- t- [val]

Sets the specified value by adding or subtracting it from the current tempo value (relative specification). The semntics of the value is the same as that of the `t` operator. The argument cannot be omitted.

### M : absolute modulation specification

:format:
- M [val]

Sets the specified value as a modulation value (absolute specification). This operator outputs `CC #01`. Valid values are 0 to 127.

### M+ M- : relative modulation specification

:format:
- M+ [val]
- M- [val]

Sets the specified value by adding or subtracting it from the current modulation value (relative specification). The semantics of the value is the same as that of the `M` operator. The argument cannot be omitted.

### V : absolute volume specification

:format:
- V [val]

Sets the specified value as the volume value (absolute specification). This operator outputs `CC #07`. Valid values are 0 to 127.

### V+ V- : relative volume specification

:format:
- V+ [val]
- V- [val]

Sets the specified value by adding or subtracting it from the current volume value (relative specification). The semantics of the value is the same as that of the `V` command. The argument cannot be omitted.

### P PAN : absolute panpot specification

:format:
- P [val]
- PAN [val]

Sets the specified value as the panpot value (absolute specification). This operator outputs `CC #0A`. Valid values are 0 to 127. There is no difference between `P` and `PAN`.

### P+ P- : relative panpot specification

:format:
- P+ [val]
- PAN+ [val]
- P- [val]
- PAN- [val]

Sets the specified value by adding or subtracting it from the current panpot value (relative specification). The semantics of the value is the same as that of the `P` command; `P+` and `PAN+`, and `P-` and `PAN-` are synonymous.

### H : absolute hold (damper pedal) specification

:format:
- H [val]

Sets the specified value as the hold (damper pedal) value (absolute specification). This operator outputs `CC #40`. Valid values are 0 to 127.

### @ : Program/Bank Specification

:format:
- @ [prog],[bankmsb=0],[banklsb=0]

Specifies the program change and bank select together. The values of bank select can be omitted (they will be 0). The valid values are 0 to 127 respectively. For bank select, `CC0` and `CC#20` are used.

### BEND, B : absolute pitch bend 

:format:
- B [val]
- BEND [val]

Sets the specified value as a pitch bend value (absolute). This shifts the value range of the `PITCH` operator, which can only be specified as a positive value, to `-8192` to `8191`.

### B+ B- : relative pitch bend

:format:
- B+ [val]
- B- [val]

Sets the specified value by adding or subtracting it from the current pitch bend value (relative specification). The semantics of the value is the same as that of the `B` command. The argument cannot be omitted.

### POLTATIME : portamento time specification

:format:
- POLTATIME [val]

Specifies the rate of change of the portamento pitch. This operator outputs `CC #05`.

### DTE : Data Input (Common Control)

:format:
- DTE [msb],[lsb]

DTE (Data Entry). This operator outputs `CC #06` and `CC #26`.

### PORTA : portamento switch

:format:
- PORTA [val]

Sets the portamento switch (on: >= 64, off: <= 63). This operator outputs `CC #41`.

### SOS : Sostenuto switch

:format:
- SOS [val]

Sets sostenuto switch (on: >= 64, off: <= 63). This operator outputs `CC #42`.

### SOFT : Soft Pedal switch

:format:
- SOFT [val]

Sets the soft pedal switch  (on: >= 64, off: <= 63). This operator outputs `CC #43`.

### LEGATO : Legato switch

:format:
- LEGATO [val]

Sets the legato foot switch (on: >= 64, off: <= 63). This operator outputs `CC #54`.

### RSD : Reverb Send Depth

:format:
- RSD [val]
- RSD+ [val]
- RSD- [val]

Outputs `CC #5B`. Many instruments use this to specify the reverb send depth. It can be relative with `+` or `-`.

### CSD : Chorus Send Depth

:format:
- CSD [val].
- CSD+ [val]
- CSD- [val]

Outputs `CC #5D`. Many instruments use this to specify the chorus send depth. It can be relative with `+` or `-`.

### DSD : Delay Send Depth

:format:
- DSD [val]
- DSD+ [val]
- DSD- [val]

Output `CC #5E`. Many instruments use this to specify the delay send depth. It can be relative with `+` or `-`.

### NRPN : NRRN specification

:format:
- NRPN [msb],[lsb]

Outputs an NRPN. (`CC#63`, `CC#62`)

### RPN : Specify RRN

:format:
- RPN [msb],[lsb]

Outputs an RPN. (`CC#65`, `CC#64`)

### TEXT : Text meta-event specification

:format:
- TEXT [string]

Outputs a meta text event. (`#FF`, `#01`)

### COPYRIGHT : Copyright notice meta-event specification

:format:
- COPYRIGHT [string]

Outputs a copyright notice meta event. (`#FF`, `#02`)

### TRACKNAME : Track name meta event specification

:format:
- TRACKNAME [string]

Outputs a track name meta event. (`#FF`, `#03`)

### INSTRUMENTNAME : Instrument name meta-event specification

:format:
- INSTRUMENTNAME [string]

Outputs an instrument name meta event. (`#FF`, `#04`)

### LYRIC : lyrics meta event specification

:format:
- LYRIC [string]

Outputs a lyric meta event. (`#FF`, `#05`)

### MARKER : Marker meta event specification

:format:
- MARKER [string]

Outputs a marker meta event. (`#FF`, `#06`)

### CUE : Cue meta event specification

:format:
- CUE [string]

Outputs a cue meta event. (`#FF`, `#07`)

### BEAT : beat specification

:format:
- BEAT [num],[denom]

Outputs a time signature meta event. Specify the numerator in [num] and the denominator in [denom]. (`#FF`, `#58`)

Note that notation with `/` (like `BEAT 3/4`) will not be recognized (the mugene specification does not specifically recognize such a numerical specification, and a single number cannot distinguish between `3/4` and `6/8`).

### PITCH_BEND_SENSITIVITY : Pitch bend sensitivity specification

:format:
- PITCH_BEND_SENSITIVITY [val]

Specifies the pitch bend sensitivity (sensitivity). This command outputs `RPN0,0` and `DTE`.


### GATE_DENOM : ratio-based gate time denominator

:format:
- GATE_DENOM [val]

Sets the denominator for the ratio-based gate time specification. If not specified, the value is `8`.

To calculate gate time, standard macros use `GATE_DENOM`, `Q`, and `q`. If there is a note operation with `n (steps)`, the actual gate (note) time for that note will be `n` * `Q` / `GATE_DENOM` - `q`.

The value of `Q` (ratio-based gate time specification) does not follow changes in `GATE_DENOM`, so if you change `GATE_DENOM`, set `Q` as well (otherwise `Q7` ... `GATE_DENOM16` will result in , for example, 7/16 length ratio, instead of 14/16).

### Q : ratio-based gate time

:format:
- Q [val]

Sets the numerator in the ratio-based gate time specification. It can be greater than `GATE_DENOM`. The default value is `8`.

### q : absolute gate time

:format:
- q [val]

Sets the absolute gate time value. The value set here is subtracted in steps from the calculated value from the ratio-based gate time.

### o : absolute octave

:format:
- o [val]

Specifies the octave to be used in the note operation (`c`..`b`). For each operation, o * 12 + the individual key value is passed to the NON and NOFF operations as the key value.

The key value will be 0-127, so the standard value of o will be 0-10.

### > < : octave relative specification

:format:
- > [val]
- < [val]

Moves the octave up or down one octave. `>` is up, `<` is down.

### n : Direct numerical pronunciation (note on, note off)

:format:
- n [key],[step],[gate],[vel=v],[offvel=0]

Emits a pair of Note On and Note Off operations with the specified [key] (number). A note-on for the specified key is emitted and lasts during the note length specified by [gate], and then the note-off is emitted. This operation will also wait until the next operation for the length of the note specified by [step]. The value of [vel] is used for the velocity of the note on, and [offvel] for the velocity of the note off.

The values after [key] can be omitted. If [step] is omitted, the value will be the default note length specified by the `l` operation. If [gate] is omitted, the value is calculated based on the value of [step] and the values specified by the `GATE_DENOM`, `Q`, and `q` operations. If [vel] is omitted, the value will be the default velocity specified by the `v` operation. The default value of [offvel] is `0`.

### c d e f g a b : Pronunciation (note on, note off)

:format:
- [c d e f g a b][+ - =] [step],[gate],[vel=v],[offvel=0].

This is the most common pair of note-on and note-off operations. It specifies a note of do-re-mi-fa-so-ra-si. The mapping between the keys and operations is as follows (matches the German notation):

- c : Do
- d : Re
- e : Mi
- f : Fa
- g : So
- a : La
- b : Si

These characters can be followed (without spaces) by a `+` to make them sharp, a `-` to make them flat, or an `=` to make them natural. The actual key value is added to the value of the `K` and `Kc`..`Kb` operations. However, the `Kc`..`Kb` values will not be added if Natural (`=`) is specified (which gives special meaning to the `Kc`..`Kb` based key specification).

### r : rest

:format:
- r [length]

Rest for the specified [length], then do nothing and move on i.e. NO-OP.

### [ ... : / ... ] : Loop

:format:
- [ ... ] [number]

Loops can be written as aliases for the primitive operations __LOOP_BEGIN, __LOOP_BREAK, and __LOOP_END (corresponding to `[`, `:` or `/`, and `]` respectively). See the description of the primitive operations for details of the loop behavior.

### GM_SYSTEM_ON : GM system on

:format:
- GM_SYSTEM_ON

Sends "GM System On" common system exclusive message.

### XG_RESET : XG Reset

:format:
- XG_RESET

Sends "XG reset" common system exclusive message.


## Spectra operations

There are handful of operations that repeatedly send events per short step cycles. They result in linear changes on the target parameter. We call them "spectra" (or "spectra operations")

Currently `P` (pan), `V` (volume), `E` (expression), `t` (tempo), `M` (modulation), and `B` (pitchbend) are supported.

There are two kinds of spectra:

- One-shot: it goes up or down to the start value to the end value, only one time.
- Triangle: it goes up or down to the start value to the end value, then it goes flipped, and repeats that multiple times (specified).

Note that they are not triggered by *each* note operations. There is no such binding. If you want note operations to always trigger them, define a new macro that wraps note operation prepended by the spectra (you would have to hack some macro variables to align the spectra length with the note length).

Last but not least, those spectra operations are defined in generic way so that it is possible to define custom spectra operation to any parameter that can work in similar way to those already-supported parameters. Look for `SPECTRA_ONESHOT` and `SPECTRA_TRIANGLE` in `default-macro.mml` (or `default-macro2.mml`).

### P_, V_, E_, t_, M_, B_ : One-shot Spectra

:format:
- P_ [sv:number], [ev:number], [sd:length], [len:length], [deltaLen:length = %4]
- V_ [sv:number], [ev:number], [sd:length], [len:length], [deltaLen:length = %4]
- E_ [sv:number], [ev:number], [sd:length], [len:length], [deltaLen:length = %4]
- t_ [sv:number], [ev:number], [sd:length], [len:length], [deltaLen:length = %4]
- M_ [sv:number], [ev:number], [sd:length], [len:length], [deltaLen:length = %4]
- B_ [sv:number], [ev:number], [sd:length], [len:length], [deltaLen:length = %4]

Triggers one-shot spectra explained above. It waits for `sd` (star delay) length then starts the linear value changes with `sv` (start value), towards `ev` (end value), with the whole `len` length of duration. The value change happens on every `deltaLen` optional length (4 ticks by default). The delta value for each step depends on `deltaLen` (`deltaValue = (ev - sv) / (len / deltaLen)`).


### Pt, Vt, Et, tt, Mt, Bt : Triangle Spectra

:format:
- Pt [sv:number], [ev:number], [sd:length], [ed:length], [ts:number], [es:number = %4], [delta:number], [rt:number]
- Vt [sv:number], [ev:number], [sd:length], [ed:length], [ts:number], [es:number = %4], [delta:number], [rt:number]
- Et [sv:number], [ev:number], [sd:length], [ed:length], [ts:number], [es:number = %4], [delta:number], [rt:number]
- tt [sv:number], [ev:number], [sd:length], [ed:length], [ts:number], [es:number = %4], [delta:number], [rt:number]
- Mt [sv:number], [ev:number], [sd:length], [ed:length], [ts:number], [es:number = %4], [delta:number], [rt:number]
- Bt [sv:number], [ev:number], [sd:length], [ed:length], [ts:number], [es:number = %4], [delta:number], [rt:number]

Triggers triangle spectra explained above. At first it works the same as one-shot specta, except that instead of `len` and `deltaLen` we specify `ts` (total steps), `es` (each steps), and `delta` value explicitly. And once it reaches `ev` then it flips the value change direction (to either positive or negative) and do the same loop. And once it reached back to `sv`, then it waits `ed` (end delay) until the next loop starts. The number of loops is specified as `rt`.


## Additional macro sets

There are also `nrpn-gs-xg.mml`, `gs-sysex.mml` and `drum-part.mml` additional utility macros for advanced composition using Roland GS modules or YAMAHA XG modules.

## MIDI 2.0 support

When mugene runs with MIDI 2.0 switch enabled, then `default-macro2.mml` is used instead of `default-macro.mml`. To avoid unnecessary MML difference, I leave most of the operators identical - otherwise the value ranges will be quite different e.g. there will be almost no audible notes when velocity assignments are left in MIDI1 values while the actual value range goes 0..65535, which does not make sense.

But channels are indeed expanded to 0..255 so it will make rich composition possible.

### BEND_PN, Bn, Bn+, Bn-: Per-note Pitchbend

MIDI 2.0 only.

:format:
- BEND_PN [note], [value]
- Bn [note], [value]
- Bn+ [note], [value]
- Bn- [note], [value]

Generates per-note pitchbend MIDI 2.0 messages. `Bn+` and `Bn-` indicate relative values from current memoized value. The way how it works is in general the same as `B` (channel pitchbend) operation.
