package com.cuneytayyildiz.githubissueexplorer.data.model

import com.google.gson.annotations.SerializedName

data class IssueComment(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("user")
    var user: UserEntity? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("body")
    var body: String? = null
) {
    data class UserEntity(
        @SerializedName("login")
        var login: String? = null,
        @SerializedName("avatar_url")
        var avatarUrl: String? = null
    )
}
