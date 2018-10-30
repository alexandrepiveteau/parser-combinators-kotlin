package com.github.alexandrepiveteau.parsers

/**
 * An annotation indicating that a certain API for parser handling is still experimental. Experimental APIs that are
 * released are not guaranteed to be maintained over time, and therefore an explicit opt-in from the client will be
 * required when using the functionality.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@Experimental(level = Experimental.Level.WARNING)
annotation class ExperimentalParser