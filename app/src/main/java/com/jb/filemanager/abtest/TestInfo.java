package com.jb.filemanager.abtest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类名称：TestInfo<br>
 * 类描述：测试信息<br>
 * 创建人：makai<br>
 * 修改人：makai<br>
 * 修改时间：2014年11月24日 下午3:04:11<br>
 * 修改备注：<br>
 * 
 * @version 1.0.0<br>
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestInfo {

	/**
	 * 发布时间 releaseTime(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)<br>
	 * 
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String startTime();

	/**
	 * 过期时间expireTime(这里用一句话描述这个方法的作用)(这里描述这个方法适用条件 – 可选)<br>
	 * 
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String endTime();

	/**
	 * 是否需要重新生产测试用例rebuild(这里用一句话描述这个方法的作用)(这里描述这个方法适用条件 – 可选)<br>
	 * 
	 * @return boolean
	 * @exception
	 * @since 1.0.0
	 */
	boolean rebuild() default false;

	int testVersion();
}
