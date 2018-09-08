/*
 * MIT License
 *
 * Copyright (c) 2018 Alexandre Piveteau
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.alexandrepiveteau.parsers

import com.github.alexandrepiveteau.functional.monads.*

class Parser<O, E>(private val f: (String) -> Either<E, Pair<O, String>>) {

    fun parse(text: String): Either<E, Pair<O, String>> = f(text)

    companion object Factory {

        fun <E> char(char: Char, f: (Char) -> E): Parser<Char, E> =
                Parser { text ->
                    return@Parser if (text.firstOrNull() == char)
                        eitherValue<E, Pair<Char, String>>(char to text.drop(1))
                    else
                        eitherError(f(char))
                }

        fun <O, E> fail(f: () -> E): Parser<O, E> = Parser { eitherError<E, Pair<O, String>>(f()) }
        fun <O, E> lazy(f: () -> Parser<O, E>): Parser<O, E> = Parser { text -> f().parse(text) }
        fun <E> succeed(): Parser<Unit, E> = Parser { eitherValue<E, Pair<Unit, String>>(Unit to it) }
    }
}