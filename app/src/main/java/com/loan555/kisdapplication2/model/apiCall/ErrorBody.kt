package com.loan555.kisdapplication2.model.apiCall

data class ErrorBody(
    val status: String,
    val message: String
)

data class ErrorBodyRegisterKids(
    val status: String,
    val msg: String
)