package com.jb.filemanager.abtest;

/**
 * 类名称：TestUser<br>
 * 类描述：测试用户集合<br>
 * 创建人：makai<br>
 * 修改人：makai<br>
 * 修改时间：2014年11月24日 下午2:29:07<br>
 * 修改备注：<br>
 * 
 * @version 1.0.0<br>
 * 
 */
public class TestUser {

    // TODO 增加说明
    @TestUserModel(isTestUser = true, isUpGradeUser = false, odds = 50)
    public static final String USER_A = "a";

    // TODO 增加说明
    @TestUserModel(isTestUser = true, isUpGradeUser = false, odds = 50)
    public static final String USER_B = "b";

    /*@TestUserModel(isTestUser = true, isUpGradeUser = false, odds = 33)
    public static final String USER_C = "c";*/

    @TestUserModel(isTestUser = false, isDefaultUser = true)
    public static final String USER_Z = "z";

}
