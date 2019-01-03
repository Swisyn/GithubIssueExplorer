package com.cuneytayyildiz.githubissueexplorer.data.model

import com.google.gson.annotations.SerializedName

data class Repo(@SerializedName("name") val name: String? = null)