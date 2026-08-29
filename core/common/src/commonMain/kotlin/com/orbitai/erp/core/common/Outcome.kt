package com.orbitai.erp.core.common

/**
 * Result type for operations that can fail in a way the UI must render. Named [Outcome] to avoid
 * colliding with `kotlin.Result`, which cannot carry a domain-specific error type.
 */
sealed interface Outcome<out T> {
    data class Success<out T>(val data: T) : Outcome<T>
    data class Failure(val error: OrbitError) : Outcome<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): OrbitError? = (this as? Failure)?.error
}

inline fun <T, R> Outcome<T>.map(transform: (T) -> R): Outcome<R> = when (this) {
    is Outcome.Success -> Outcome.Success(transform(data))
    is Outcome.Failure -> this
}

inline fun <T> Outcome<T>.onSuccess(action: (T) -> Unit): Outcome<T> {
    if (this is Outcome.Success) action(data)
    return this
}

inline fun <T> Outcome<T>.onFailure(action: (OrbitError) -> Unit): Outcome<T> {
    if (this is Outcome.Failure) action(error)
    return this
}

fun <T> T.asSuccess(): Outcome<T> = Outcome.Success(this)

fun OrbitError.asFailure(): Outcome<Nothing> = Outcome.Failure(this)

/**
 * Domain errors, deliberately coarse. Each case maps to a distinct UI treatment, which is the
 * only reason for a case to exist.
 */
sealed interface OrbitError {
    /** Message safe to show to a user; null means the UI should pick a generic string. */
    val message: String?

    data class Network(override val message: String? = null) : OrbitError
    data class Timeout(override val message: String? = null) : OrbitError
    data class Unauthorized(override val message: String? = null) : OrbitError
    data class Forbidden(override val message: String? = null) : OrbitError
    data class NotFound(override val message: String? = null) : OrbitError
    data class Validation(
        val fieldErrors: Map<String, String> = emptyMap(),
        override val message: String? = null,
    ) : OrbitError

    data class Conflict(override val message: String? = null) : OrbitError
    data class Server(val code: Int? = null, override val message: String? = null) : OrbitError
    data class Unknown(val cause: Throwable? = null, override val message: String? = null) : OrbitError
}
