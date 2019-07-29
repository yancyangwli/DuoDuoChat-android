package com.dktlh.ktl.provider.router

object RouterPath {

    class MainCenter {
        companion object {
            const val PATH_MAIN: String = "/mainCenter/main"
        }
    }

    //消息模块
    class MessageCenter {
        companion object {
            const val PATH_MESSAGE_RED_ENVELOPE: String = "/messageCenter/red_envelope"
        }
    }

    class ContactCenter {
        companion object {
            const val PATH_CONTACT_SEARCH: String = "/contactCenter/search"
            const val PATH_CONTACT_PERSONAL: String = "/contactCenter/personal"
            const val PATH_ADD_FRIEND_GROUP: String = "/contactCenter/addFriend"
            const val PATH_ADD_FRIEND_REQUESTS: String = "/contactCenter/friendRequests"
        }
    }

    //动态模块
    class DynamicCenter {
        companion object {
            const val PATH_DYNAMIC_DETAIL: String = "/dynamicCenter/dynamic_detail"
            const val PATH_DYNAMIC_LIST = "/dynamicCenter/dynamic_list"
        }
    }

    class MesageCenter {
        companion object {
            const val PATH_CHAT_LIST: String = "/messageCenter/chat_list"//单聊
            const val PATH_CHAT_GROUP: String = "/messageCenter/chat_group"//群聊
            const val PATH_CHAT_PAGE: String = "/messageCenter/chat_page"
            const val PATH_GROUP_INFO: String = "/messageCenter/group_info"//群聊信息界面
            const val PATH_CREATE_CHAT_GROUP: String = "/messageCenter/create_chat_group"//创建群聊
        }
    }

    //我的模块
    class MineCenter {
        companion object {
            const val PATH_QR_CODE: String = "/mineCenter/qr_code"
            const val PATH_USER_INFO: String = "/mineCenter/user_info"
            const val PATH_LOGIN: String = "/mineCenter/login"
            const val PATH_MINE_FEED_BACK: String = "/mineCenter/feedback"
        }
    }


}
