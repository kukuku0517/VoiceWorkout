package com.example.domain

interface UseCase<in Params, Results> {
    fun invoke(params: Params? = null): Results
}

interface UseCaseVoid<in Params> {
    fun invoke(params: Params? = null)
}


interface UseCaseFuture<in Params, Results> {
    suspend fun invoke(params: Params? = null): Results
}

interface UseCaseFutureVoid<in Params> {
    suspend fun invoke(params: Params? = null)
}

class UseCaseParameterNullPointerException : Exception() {
    override val message: String?
        get() = "Required non-null params is null."
}