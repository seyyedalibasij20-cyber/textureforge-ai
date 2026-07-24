package com.textureforge.ai.core.domain.model

/**
 * Sealed result type used across repositories and use cases so that every
 * Room/network/camera operation resolves to an explicit, user-presentable
 * state rather than an unhandled exception (Section 10, "Zero-crash target").
 */
sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error(val failure: DomainFailure) : DomainResult<Nothing>()

    inline fun <R> map(transform: (T) -> R): DomainResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    inline fun onSuccess(block: (T) -> Unit): DomainResult<T> {
        if (this is Success) block(data)
        return this
    }

    inline fun onError(block: (DomainFailure) -> Unit): DomainResult<T> {
        if (this is Error) block(failure)
        return this
    }
}

sealed class DomainFailure(val userMessage: String, val cause: Throwable? = null) {
    data object NoNetwork : DomainFailure("You're offline. Showing cached data where available.")
    data object NotFound : DomainFailure("That item couldn't be found.")
    data object Storage : DomainFailure("There was a problem reading or writing local data.")
    data class AiFailure(val reason: String) : DomainFailure(reason)
    data class Unexpected(val throwable: Throwable) : DomainFailure(
        throwable.message ?: "Something went wrong.", throwable
    )
}
