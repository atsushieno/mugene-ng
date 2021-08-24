# Technical choices that I made into mugene

## Why MML?

MML is an encient way to compose music that was popular in 20th century. In 21st. century we typically use DAWs, but some people (e.g. @atsushieno) still prefer using MML.

It is mostly because text files are handy, but in 21st. century we have other choices like Live Coding languages, so probably I should put more rationale on the language.

Also, MML is not a specific syntax with solid definition. Every MML compiler has its own language with own principles (historically it is due to output music binary target). While mugene's target format is very common Standard Midi File, it is not exceptional here.

## MML syntax characteristics, or "peculiarity"

MML looks like some sort of programming language, or maybe more like a DSL, but it is unique in certain aspect. For example:

- Macro operators are not tokenized per whitespace. `cde` must be interpreted like `c d e`. It is more like East-Asian languages rather than Indo-European languages.
- Various symbols are used as Macro operators. `(` and `)` for relative velocity changes, `.` for time dots (as in musical notation), `=` for [accidental natural](https://en.wikipedia.org/wiki/Accidental_(music)).
- Number are typically attached to Macro operators as length without any connecting token i.e. `c4` is `c` with length `4`. Therefore you cannot use numbers as name characters.

These characteristics are quite common in MML, but at the same time, make the syntax quite different from, for example, Live Coding languages. They parse the text more like programming languages.

MML is more music authoring oriented, and typically generate serialized music. A lot of boilerplate melodic operators are not very welcomed.

On the other hand, those language characteristics impose less programmability on the syntax. Also, there is no predefined functions like `sin` or `cos`, therefore some features that we appreciate on DAWs do not exist...*yet*. I personally find it a serious problem and there may be some remedy to utilize standard functions in the end.

## Partially flexible syntax

mugene MML syntax is designed to be flexible enough to customize syntax if you prefer, like exchanging "<" and ">" for relative octave specification (it is like a religious difference from where people grew up, like N88-BASIC, Z-MUSIC, MXDRV, MUAP 98, PMD, FMP, etc.). You can simply `#define` macros for them.

It is, however, still not *that* flexible to fully customize the syntax. For example, we use `{` and `}` in macro definition and they cannot be altered. `#` is used to identify hexadecimal numbers from decimal numbers. `[` and `]` are used as loop initiator and terminator and cannot be altered.

Ultimately, all mugene Macros are expanded as "primitive operations" such as `__MIDI {...}`, `__MIDI2 {...}` or `__MIDI_META {...}`. Note-On macro `n` is expanded as `__MIDI #9n, ...`. `c` ~ `b` are expanded like `n60`.

Any user-level macros can be flexibly defined. In mugene syntax, phrase pattern definitions can be achieved by this macro definition feature.

## Match longest

Regarding macro definition flexibility, mugene's tokenization strategy is "match longest". Some MML syntaxes have different strategy e.g. their macros must be "more than one character" "begin with uppercase" etc. mugene has no such limitation.

Historically, preprocessing MML should not be very costful, but in 21st. century it is almost non-issue.

## Chords

There are handful of ways to express chords in MML, but historically it was not very popular because an MML "track" used to be tied to an output "channel" (there aren't even such concepts in traditional MML compilers) and each channel is monophonic (like an FM channel on SoundBlaster or YAMAHA YM-2203).

In later generations that for either target MIDI or PCM, there are couple of MML compilers that support chord notation. For example, [sakuramml](http://sakuramml.com/) uses `'ceg'` kind of notation. mugene uses zero-length special notation e.g. `c0e0g4` inspired by [MUC](https://www.vector.co.jp/soft/dos/art/se028130.html) MML compiler.

I'm not sure if I choose the best syntax, but it does not require special symbol character. On the other hand, I had to introduce a special primitive operator `__ON_MIDI_NOTE_OFF` to support this chord functionality to proess any pending note operators, which is kind of defect in the syntax.
