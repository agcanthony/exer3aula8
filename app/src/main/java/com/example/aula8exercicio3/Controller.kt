package com.example.aula8exercicio3

data class ApiResponse(
    val results: List<UserResponse>
)

data class UserResponse(
    val name: UserName?,
    val picture: UserPicture?
)

data class UserName(
    val title: String?,
    val first: String?,
    val last: String?
) {
    val fullName: String
        get() = listOfNotNull(title, first, last).joinToString(" ")
}

data class UserPicture(
    val large: String?,
    val medium: String?,
    val thumbnail: String?
)
