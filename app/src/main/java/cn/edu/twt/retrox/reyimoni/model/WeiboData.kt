package cn.edu.twt.retrox.reyimoni.model

import com.google.gson.annotations.SerializedName

data class WeiboMessage(@SerializedName("mblogid")
                        val mblogid: String = "",
                        @SerializedName("gid")
                        val gid: Long = 0,
                        @SerializedName("annotations")
                        val annotations: Annotations,
                        @SerializedName("type")
                        val type: Int = 0,
                        @SerializedName("from_uid")
                        val fromUid: Long = 0,
                        @SerializedName("content")
                        val content: String = "",
                        @SerializedName("recall_status")
                        val recallStatus: Int = 0,
                        @SerializedName("media_type")
                        val mediaType: Int = 0,
                        @SerializedName("user_info")
                        val userInfo: UserInfo,
                        @SerializedName("appid")
                        val appid: Int = 0,
                        @SerializedName("id")
                        val id: Long = 0,
                        @SerializedName("time")
                        val time: String = "",
                        @SerializedName("text")
                        val text: String = "",
                        @SerializedName("mod_type")
                        val modType: String = "")


data class Annotations(@SerializedName("send_from")
                       val sendFrom: String = "",
                       @SerializedName("gdid")
                       val gdid: String = "",
                       @SerializedName("include_video")
                       val includeVideo: Int = 0)


data class UserInfo(@SerializedName("screen_name")
                    val screenName: String = "",
                    @SerializedName("verified")
                    val verified: Boolean = false,
                    @SerializedName("profile_image_url")
                    val profileImageUrl: String = "",
                    @SerializedName("remark")
                    val remark: String = "",
                    @SerializedName("id")
                    val id: String = "",
                    @SerializedName("verified_type")
                    val verifiedType: Int = 0)


