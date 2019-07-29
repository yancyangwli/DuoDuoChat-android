package com.zhuanxu.eclipse.aop.annotation

/**
 * User: Axl_Jacobs(Axl.Jacobs@gmail.com)
 * Date: 2018/4/12
 * Time: 17:04
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class SingleClick
