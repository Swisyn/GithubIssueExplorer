package com.cuneytayyildiz.githubissueexplorer.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Issue(
    @SerializedName("number") var number: Int = 0,
    @SerializedName("title") var title: String? = null,
    @SerializedName("user") var user: UserEntity? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("comments") var comments: Int = 0,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("body") var body: String? = null
) : Parcelable {

    @Parcelize
    data class UserEntity(
        @SerializedName("login") var login: String? = null,
        @SerializedName("id") var id: Int = 0,
        @SerializedName("node_id") var nodeId: String? = null,
        @SerializedName("avatar_url") var avatarUrl: String? = null
    ) : Parcelable
}
