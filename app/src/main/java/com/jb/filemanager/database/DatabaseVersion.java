package com.jb.filemanager.database;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseVersion {

    /**
     * 数据库老版本号
     * old(这里用一句话描述这个方法的作用)
     * (这里描述这个方法适用条件 – 可选)
     * @return int
     * @since  1.0.0
     */
    int old() default -1;

}