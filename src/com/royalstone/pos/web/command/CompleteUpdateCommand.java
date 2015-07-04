package com.royalstone.pos.web.command;

import com.royalstone.pos.util.UpdateCheck;

/**
 * 当某一POS机完成程序下载后调用它,以防止它第二次启动时再下载程序
 * @author liangxinbiao
 */
public class CompleteUpdateCommand implements ICommand {

	private String posid;

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {
		if (values != null && values.length == 2) {
			posid = (String) values[1];
			updateCheck();
			Object[] result = new Object[1];
			result[0] = "";
			return result;
		}
		return null;
	}

	/**
	 * 用POS号在posupdate.properties里添加一项
	 */
	private void updateCheck() {
		UpdateCheck updateCheck = UpdateCheck.getInstance();
		updateCheck.addPOS("../download/posupdate.properties", posid);
	}

}
