package com.cosven.message.bean

import com.woniu.core.bean.Album

data class GroupAlbumListBean (
    var current_page: Int,
    var `data`: MutableList<Album>?,
    var first_page_url: String?,
    var from: Int,
    var next_page_url: String??,
    var path: String?,
    var per_page: Int,
    var prev_page_url: String?,
    var to: Int
)