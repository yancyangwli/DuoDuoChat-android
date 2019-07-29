package com.cosven.message.impl;

/**
 * 功能按钮
 * 1.复制
 * 2.分享
 * 3.收藏
 * 4.转发
 */
public interface OnActionPopClickedListener {
    /**
     * 分享
     */
    void onShareListener();

    /**
     * 复制
     */
    void onCopyListener();

    /**
     * 收藏
     */
    void onCollectionListener();

    /**
     * 转发
     */
    void onForwardListener();
}
