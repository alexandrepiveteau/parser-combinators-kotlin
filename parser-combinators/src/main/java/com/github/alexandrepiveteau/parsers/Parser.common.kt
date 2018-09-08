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

fun <O1, O2, E> Parser<O1, E>.map(f: (O1) -> O2): Parser<O2, E> =
        Parser { text -> parse(text).toValue().map { (o, r) -> f(o) to r }.either }

fun <O1, O2, E> Parser<O1, E>.flatMap(f: (O1) -> Either<E, O2>): Parser<O2, E> =
        Parser { text ->
            val result = this.parse(text)
            return@Parser when (result) {
                is Either.Error -> eitherError<E, Pair<O2, String>>(result.error)
                is Either.Value -> {
                    val mappedResult = f(result.value.first)
                    return@Parser when (mappedResult) {
                        is Either.Error -> eitherError<E, Pair<O2, String>>(mappedResult.error)
                        is Either.Value -> eitherValue(mappedResult.value to result.value.second)
                    }
                }
            }
        }

fun <O1, O2, E> Parser<O1, E>.and(other: Parser<O2, E>): Parser<Pair<O1, O2>, E> =
        Parser { text ->
            val result1 = this.parse(text)
            return@Parser when (result1) {
                is Either.Error -> eitherError<E, Pair<Pair<O1, O2>, String>>(result1.error)
                is Either.Value -> {
                    val result2 = other.parse(result1.value.second)
                    return@Parser when (result2) {
                        is Either.Error -> eitherError<E, Pair<Pair<O1, O2>, String>>(result2.error)
                        is Either.Value -> eitherValue(result1.value.first to result2.value.first to result2.value.second)
                    }
                }
            }
        }

fun <O1, O2, E> Parser<O1, E>.after(other: Parser<O2, E>): Parser<O2, E> =
        and(other).map { (_, b) -> b }

fun <O1, O2, E> Parser<O1, E>.before(other: Parser<O2, E>): Parser<O1, E> =
        and(other).map { (a, _) -> a }

fun <O, E> Parser<O, E>.flatOr(other: Parser<O, E>): Parser<O, E> =
        or(other).map { either ->
            return@map when (either) {
                is Either.Error -> either.error
                is Either.Value -> either.value
            }
        }

fun <O1, O2, E> Parser<O1, E>.or(other: Parser<O2, E>): Parser<Either<O2, O1>, E> =
        Parser { text ->
            val result1 = this.parse(text)
            return@Parser when (result1) {
                is Either.Error -> {
                    val result2 = other.parse(text)
                    return@Parser when (result2) {
                        is Either.Error -> eitherError<E, Pair<Either<O2, O1>, String>>(result2.error)
                        is Either.Value -> eitherValue(eitherError<O2, O1>(result2.value.first) to result2.value.second)
                    }
                }
                is Either.Value -> eitherValue<E, Pair<Either<O2, O1>, String>>(eitherValue<O2, O1>(result1.value.first) to result1.value.second)
            }
        }