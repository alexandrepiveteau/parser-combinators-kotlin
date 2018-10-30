# parser-combinators

[![](https://jitpack.io/v/alexandrepiveteau/parser-combinators-kotlin.svg)](https://jitpack.io/#alexandrepiveteau/parser-combinators-kotlin)

This repository contains some utilies for parser combinators in the Kotlin programming language.
The OSS license can be found in the LICENSE.md file of the repository.

## Installation
This library is available on [JitPack.io](https://jitpack.io/#alexandrepiveteau/parser-combinators-kotlin). Make
sure to add the following Maven repository in your root **build.gradle** file :

```
allprojects {
	repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

You can now add the library modules in your application **build.gradle** file :

```
dependencies {
	implementation "com.github.alexandrepiveteau.parser-combinators-kotlin:parser-combinators:1.1.0"
    implementation "com.github.alexandrepiveteau.parser-combinators-kotlin:parser-combinators-primitives:1.1.0"
}
```

## Usage
The library contains a single module, versioned using **semantic versioning** :

- **parser-combinators** - Offers some primitives for building `Parser` instances, and combinators for manipulating `Parser` instances.
- **parser-combinators-primitives** - Offers some functions for building `Parser` on primitive data types in Kotlin.

### parser-combinators

A `Parser<Input, Output, Error>` is a structure accepting a `Input` as input, and returning an `Either<Error, Pair<Output, Input>>`, where the `Error` case of the `Either` represents a problem that occured while parsing the input, and the `Value` case of the `Either` represents a pair of the parser output, and the remaining `Input` that has not been parsed yet. A parser combinator is a function that accepts one or multiple parsers as input and outputs a different parser.

Each `Parser` instance is just an immutable wrapper around a parsing function. Therefore, it is easily possible to create your own instances of `Parser` from scratch. Nevertheless, some default `Parser` implementations are provided :

```kotlin
/*
 * This Parser will always fail, no matter what input sequence is provided to it. This can
 * be useful when combining multiple Parsers together.
 */
val l = Parser.fail<String, Int, Throwable> { IllegalArgumentException("This parser always fail.") }

/*
 * This Parser uses a lazily evaluated lambda as its argument. It can easily be used to
 * recursively call itself. The lambda has the type () -> Parser<Int, String> in this
 * example.
 */
val e = Parser.lazy<String, Int, String> { TODO("Make a recursive call, lazily evaluated.") }

/*
 * This Parser always succeeds. It will produce a Parser<Unit, T> instance, and therefore
 * has can be mapped to produce a "default" value of any type. 
 */
val x = Parser.succeed<String, String>().map { 34 } // Parser<String, Int, String>
```

Multiple combinators are provided as **extension functions** in the library. For instance, the `map { }` function (used at the end of the previous snippet) is a combinator that transforms the value produced by a `Parser`. These high-level functions can be used to build high-level `Parser` objects, which can for instance contain some custom types.

The following parser combinators are provided as **extension functions** (in each one of these, the first argument is always the current `Parser` instance) :

- `map(f: (O1) -> O2)` - Returns a `Parser` that transforms the value produced using a mapping function.
- `flatMap(f: (O1) -> Either<E, O2>)` - Returns a `Parser` that, like `map {}`, transforms the value produced using a mapping function. It can also transform the value into an `Either.Error`, which will make the `Parser` fail. This can be used when you want to validate your model with some logic that can't be easily built into parsers otherwise.
- `and(other: Parser<I, O2, E>)` - Returns a `Parser` that pairs the responses of the two combined parsers.
- `after(other: Parser<I, O2, E>)` - Same as `and`, but returns only the second value of the pair.
- `before(other: Parser<I, O2, E>)` - Same as `and`, but returns only the first value of the pair.
- `or(other: Parser<I, O2, E>)` - Tries the first `Parser` instance, and, if it fails, tries the `other` instance. Returns a `Parser` formed of an `Either` based on the result of the parsing.
- `flatOr(other: Parser<I, O, E>)` - Same as `or`, but because both `Parser` have the same type, the resulting `Either` can safely be flattened.
- `loop()` - Returns a `Parser` that applies itself repeatedly, until it fails. Returns a `List` of the results of each iteration.
