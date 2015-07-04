/*
 * Created on 2005-5-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.card;

/**
 * @author yaopoqing
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MemberCardMgrFactory {
	private static MemberCardMgr instance = null;

	public static void setInstance(MemberCardMgr value) {
		instance = value;
	}

	public static MemberCardMgr createInstance()throws Exception {
		if (instance == null) {
			instance = new MemberCardMgrImpl();
		}
		return instance;
	}
}
