package com.woniu.core.bean.entity

/**
 * @author Anlycal<远>
 * @date 2019/6/26
 * @description 上传文件返回的数据
 */


data class FileLoadEntity(
    var fid: String?,
    var name: String?,
    var size: Int?,
    var url: String?
)