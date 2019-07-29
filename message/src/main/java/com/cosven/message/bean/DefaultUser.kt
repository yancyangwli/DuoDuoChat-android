package com.cosven.message.bean

import cn.jiguang.imui.commons.models.IUser

class DefaultUser : IUser {
    private val id: String
    private val displayName: String
    private val avatar: String

    constructor(id: String?, displayName: String?, avatar: String?) {
        this.id = id.toString()
        this.displayName = displayName.toString()
        this.avatar = avatar.toString()
    }

    override fun getId(): String {
        return id
    }

    override fun getDisplayName(): String {
        return displayName
    }

    override fun getAvatarFilePath(): String {
        return avatar
    }
}