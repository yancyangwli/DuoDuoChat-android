package com.zhuanxu.eclipse.aop.annotation

/**
 * User: Axl_Jacobs(Axl.Jacobs@gmail.com)
 * Date: 2018/4/10
 * Time: 17:11
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class CheckLogin