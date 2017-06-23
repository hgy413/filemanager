package com.jb.filemanager.abtest;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ab test 用户数据模型<br>
 * 
 * @author laojiale
 * 
 */
public class TestUserBean {

	private String mUser = TestUser.USER_Z;
	/**
	 * 概率权重<br>
	 */
	private int mOdds = 50;

	public TestUserBean() {

	}

	public TestUserBean(String user, int odds) {
		super();
		mUser = user;
		mOdds = odds;
	}

	public String getUser() {
		return mUser;
	}

	public void setUser(String user) {
		mUser = user;
	}

	public int getOdds() {
		return mOdds;
	}

	public void setOdds(int odds) {
		mOdds = odds;
	}

	private final static String KEY_USER = "user";
	private final static String KEY_ODDS = "odds";

	/**
	 * 若转换失败返回null<br>
	 * 
	 * @return
	 */
	public JSONObject toJSONObject() {
		JSONObject o;
		try {
			o = new JSONObject();
			o.put(KEY_USER, mUser);
			o.put(KEY_ODDS, mOdds);
		} catch (JSONException e) {
			o = null;
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 将josn字符串还原成一个bean,或失败返回null<br>
	 * 
	 * @param jsonString
	 * @return
	 */
	public static TestUserBean parseJSONObject(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		TestUserBean u;
		try {
			JSONObject o = new JSONObject(jsonString);
			u = parseJSONObject(o);
		} catch (JSONException e) {
			u = null;
			e.printStackTrace();
		}
		return u;
	}

	/**
	 * 将JSONObject还原成一个bean,或失败返回null<br>
	 * 
	 * @param jsonString
	 * @return
	 */
	public static TestUserBean parseJSONObject(JSONObject jsonObject) {
		if (null == jsonObject) {
			return null;
		}
		TestUserBean u;
		try {
			u = new TestUserBean(jsonObject.getString(KEY_USER),
					jsonObject.getInt(KEY_ODDS));
		} catch (JSONException e) {
			u = null;
			e.printStackTrace();
		}
		return u;
	}

	public static List<TestUserBean> parseJSONArray(String arrayString) {
		if (TextUtils.isEmpty(arrayString)) {
			return null;
		}
		List<TestUserBean> users = new ArrayList<TestUserBean>();
		JSONArray array;
		try {
			array = new JSONArray(arrayString);
			users = parseJSONArray(array);
		} catch (JSONException e) {
			users = null;
			e.printStackTrace();
		}
		return users;
	}

	public static List<TestUserBean> parseJSONArray(JSONArray array) {
		if (null == array) {
			return null;
		}
		List<TestUserBean> users = new ArrayList<TestUserBean>();
		try {
			final int length = array.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				TestUserBean user = parseJSONObject(jsonObject);
				if (user != null) {
					users.add(user);
				}
			}
		} catch (JSONException e) {
			users = null;
			e.printStackTrace();
		}
		return users;
	}

	public static String usersToJsonString(List<TestUserBean> users) {
		String result = "";
		JSONArray usersToJSONArray = usersToJSONArray(users);
		if (null != usersToJSONArray) {
			result = usersToJSONArray.toString();
		}
		return result;
	}

	public static JSONArray usersToJSONArray(List<TestUserBean> users) {
		if (null == users) {
			return null;
		}
		JSONArray array = new JSONArray();
		for (TestUserBean user : users) {
			JSONObject jsonObject = user.toJSONObject();
			if (null != jsonObject) {
				array.put(jsonObject);
			}
		}
		return array;
	}

	/**
	 * 用于生成所需的测试用户, 根据需要调整代码<br>
	 * <ul>
	 * <li>
	 * v1.23版本测试如下: a用户(默认)完成页旧形式，b用户完成页信息流形式</li>
	 * </ul>
	 * 
	 * @return
	 */
	public static List<TestUserBean> getTestUsers() {
		List<TestUserBean> users = new ArrayList<TestUserBean>();
//		users.add(new TestUserBean(TestUser.USER_A, 50));
//		users.add(new TestUserBean(TestUser.USER_B, 50));
//		users.add(new TestUserBean(TestUser.USER_C, 50));
//		users.add(new TestUserBean(TestUser.USER_D, 50));
		return users;
	}

}
