package com.cosven.socialim.contact.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.cosven.socialim.contact.R
import com.cosven.socialim.contact.fragment.ContactFragment
import com.dktlh.ktl.provider.router.RouterPath
import com.woniu.core.activities.BaseActivity
import com.woniu.core.bean.entity.ContactEntity
import com.woniu.core.db.DbHelper
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent
import com.woniu.core.xmpp.smack.SmackManager
import kotlinx.android.synthetic.main.activity_select_contact.*
import org.jetbrains.anko.toast
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smackx.muc.MultiUserChat
import java.lang.ref.WeakReference

/**
 * @author Anlycal<远>
 * @date 2019/5/31
 * @description 选择联系人
 */

@Route(path = RouterPath.ContactCenter.PATH_CONTACT_PERSONAL)
class SelectContactActivity : BaseActivity() {
    private var flag = ""
    private var groupId = 0
    private var groupName = ""
    private var groupAvatar = ""
    private var mFragment: ContactFragment? = null
    override fun setContentViewId(): Int {
        return R.layout.activity_select_contact
    }

    override fun initView() {
        flag = intent.getStringExtra("FLAG") ?: ""
        groupId = intent.getIntExtra("GROUP_ID", 0)
        groupName = intent.getStringExtra("GROUP_NAME") ?: ""
        groupAvatar = intent.getStringExtra("GROUP_AVATAR") ?: ""
        mFragment = ContactFragment().apply {
            var mBundle: Bundle = WeakReference<Bundle>(Bundle()).get()!!
            mBundle.putBoolean("SHOW_CHECKBOX", true)
            mBundle.putString("FLAG", flag)
            arguments = mBundle
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fl_content, this)
                .commitNow()
        }
    }

    override fun initData() {
        if (flag == "invite") {
            mActionBtn.text = "确定"
        }
        mActionBtn.setOnClickListener {
            if (flag == "invite") {
                var list = mFragment?.getSelectedList()
                list?.isNotEmpty()?.apply {
                    if (this) {
                        sendInviteMessage(list)
                    } else {
                        toast("请勾选邀请好友")
                    }
                }
            } else {

            }
        }
    }

    /**
     * 发送邀请信息
     */
    private fun sendInviteMessage(list: MutableList<ContactEntity>) {
        for (contactEntity in list) {
            contactEntity.run {
                //发送成功
                val userInfoEntity = UserInfoManage.getInstance.mUserInfoEntity
                val chatMessageEvent = ChatMessageEvent().apply {
                    var mChat = SmackManager.getInstance()
                        .createChat(SmackManager.getInstance().getChatJid(friend_user_id.toString()))
                    messageType = BaseEvent.TYPE.GROUP_INVITE.typeValue
                    messageContent = intent.getIntExtra("GROUP_ID", 0).toString()
                    avatar = userInfoEntity!!.avatar
                    nickname = userInfoEntity.nickname
                    invitationGroupAvatar = this@SelectContactActivity.groupAvatar
                    invitationGroupName = this@SelectContactActivity.groupName
                    invitationPeople = userInfoEntity.user_id.toString()
                    mChat?.sendMessage(
                        GsonUtil.getGson().toJson(
                            this
                        )
                    )
                    Log.e("yancy", "发送的消息：" + this.toString())
                    fromUserID = userInfoEntity?.user_id.toString()
                    toUserID = friend_user_id.toString()
                    nickname = groupName//改变昵称的设置，供列表好友昵称显示
                    chatUserID = friend_user_id.toString()//设置当前聊天人的好友id，
                    createTime = System.currentTimeMillis()//当前消息的时间
                    var rowId: Long = DbHelper.insertChatMessage(this)//插入数据库
                    toast("邀请信息发送成功")
                    finish()
                }
            }
        }
    }
}