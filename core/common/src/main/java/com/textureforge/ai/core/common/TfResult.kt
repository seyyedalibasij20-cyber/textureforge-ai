package com.textureforge.ai.core.common

sealed class TfResult<out T> {
    data class Success<T>(val data: T) : TfResult<T>()
    data class Error(val throwable: Throwable, val message: String? = throwable.message) : TfResult<Nothing>()
    data object Loading : TfResult<Nothing>()

    inline fun <R> map(transform: (T) -> R): TfResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    fun getOrNull(): T? = (this as? Success)?.data
}

inline fun <T> runCatchingTf(block: () -> T): TfResult<T> = try {
    TfResult.Success(block())
} catch (t: Throwable) {
    TfResult.Error(t)
}
