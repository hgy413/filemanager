package com.jb.filemanager.abtest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ABTestBean
 * 
 * @author laojiale
 *
 */
public class ABTestBean {
	public final List<TestUserBean> mUsers = new ArrayList<TestUserBean>();
	private String mRebuildFlag = "";

	public String getRebuildFlag() {
		return mRebuildFlag;
	}

	public void setRebuildFlag(String rebuildFlag) {
		mRebuildFlag = rebuildFlag;
	}

	private final static String KEY_REBUILD_FLAG = "rebuild_flag";
	private final static String KEY_USERS = "users";

	/**
	 * 转成JSONObject, 出错则返回null<br>
	 * 
	 * @return
	 */
	public JSONObject toJSONObject() {
		JSONObject o;
		try {
			o = new JSONObject();
			// 是否需要重新生成用户的标识，当客户端的标识与服务器的不一样的则表示需要重新生成ABTest用户
			o.put(KEY_REBUILD_FLAG, mRebuildFlag);
			// 测试用户
			o.put(KEY_USERS, TestUserBean.usersToJSONArray(mUsers));
		} catch (JSONException e) {
			o = null;
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 解释jsonString成为Bean, 出错时返回null<br>
	 * 
	 * @param jsonString
	 * @return
	 */
	public static ABTestBean parseJsonString(String jsonString) {
		ABTestBean ab;
		try {
			JSONObject o = new JSONObject(jsonString);
			final String rebuildFlag = o.getString(KEY_REBUILD_FLAG);
			JSONArray jsonArray = o.getJSONArray(KEY_USERS);
			final List<TestUserBean> users = TestUserBean
					.parseJSONArray(jsonArray);
			ab = new ABTestBean();
			ab.setRebuildFlag(rebuildFlag);
			if (null != users) {
				ab.mUsers.addAll(users);
			}
		} catch (JSONException e) {
			ab = null;
			e.printStackTrace();
		}
		return ab;
	}

}
