/*
 * Copyright 2019 Louis Cognault Ayeva Derman. Use of this source code is governed by the Apache 2.0 license.
 */

package splitties.experimental

@RequiresOptIn(
    message = "This is an experimental splitties API.",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalSplittiesApi

@RequiresOptIn(
    message = "This is an internal splitties API. It should not be used.",
    level = RequiresOptIn.Level.ERROR
)
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class InternalSplittiesApi
