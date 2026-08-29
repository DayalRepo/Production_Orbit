package com.orbitai.erp.core.common

/**
 * Canonical async UI state. Every list and detail screen renders from one of these, which is what
 * lets a single set of loading/empty/error components cover the whole app.
 */
sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>

    data class Success<out T>(
        val data: T,
        /** True while a pull-to-refresh or background revalidation is in flight. */
        val isRefreshing: Boolean = false,
    ) : LoadState<T>

    data class Empty(val reason: EmptyReason = EmptyReason.NoData) : LoadState<Nothing>

    data class Error(val error: OrbitError) : LoadState<Nothing>
}

/** Distinguishes "nothing exists yet" from "your filters excluded everything", which need different copy and actions. */
enum class EmptyReason { NoData, NoSearchResults, NoFilterMatches, NoPermission }

inline fun <T, R> LoadState<T>.map(transform: (T) -> R): LoadState<R> = when (this) {
    is LoadState.Success -> LoadState.Success(transform(data), isRefreshing)
    is LoadState.Loading -> this
    is LoadState.Empty -> this
    is LoadState.Error -> this
}

fun <T> LoadState<T>.dataOrNull(): T? = (this as? LoadState.Success)?.data

/** Collapses an empty collection into [LoadState.Empty] so screens do not each re-check size. */
fun <T> Outcome<List<T>>.toLoadState(
    emptyReason: EmptyReason = EmptyReason.NoData,
): LoadState<List<T>> = when (this) {
    is Outcome.Success -> if (data.isEmpty()) LoadState.Empty(emptyReason) else LoadState.Success(data)
    is Outcome.Failure -> LoadState.Error(error)
}
