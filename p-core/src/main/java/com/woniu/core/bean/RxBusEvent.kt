package com.woniu.core.bean

/**
 * RxBus通知实体
 */
class RxBusEvent {
    var updateGroupMember: Boolean = false
    var updateFriendList: Boolean = false
    var deleteFriendWithId = 0//删除好友
}