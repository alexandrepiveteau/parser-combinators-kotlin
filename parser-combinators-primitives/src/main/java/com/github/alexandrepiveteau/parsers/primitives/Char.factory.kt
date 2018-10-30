package com.github.alexandrepiveteau.parsers.primitives

import com.github.alexandrepiveteau.functional.monads.Maybe
import com.github.alexandrepiveteau.functional.monads.eitherError
import com.github.alexandrepiveteau.functional.monads.eitherValue
import com.github.alexandrepiveteau.functional.monads.toMaybe
import com.github.alexandrepiveteau.parsers.ExperimentalParser
import com.github.alexandrepiveteau.parsers.Parser

/**
 * Returns a [Parser] that takes as input a [String] and returns the first [Char] if it corresponds to the character
 * that was provided as a parameter.
 *
 * @param E The type of the errors that will be generated by this [Parser].
 *
 * @param c The character that should be at the beginning of the [String] for the [Parser] to succeed.
 * @param f The function that will generate an [E] if the [Char] was not found at the right place. Offers the [Char]
 * t        that was founs instead.
 */
@ExperimentalParser
fun <E> Char.Companion.parserOfSome(c: Char, f: (Maybe<Char>) -> E): Parser<String, Char, E> =
        Parser { s ->
            when (val xs = s.firstOrNull()) {
                c -> eitherValue(c to s.drop(1))
                else -> eitherError(f(xs.toMaybe()))
            }
        }