package com.francescobottino.thehubproject

inline fun <reified T, reified R> Result<T>.mapSuccess(transform: (T) -> Result<R>): Result<R> {
    return fold(
        onSuccess = { transform(it) },
        onFailure = { Result.failure(it) }
    )
}

inline fun <reified T, reified R> Result<T>.mapSuccessCatching(transform: (T) -> R): Result<R> {
    return fold(
        onSuccess = { runCatching { transform(it) } },
        onFailure = { Result.failure(it) }
    )
}