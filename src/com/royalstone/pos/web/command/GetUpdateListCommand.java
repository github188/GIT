package com.royalstone.pos.web.command;

import java.io.FileNotFoundException;

import org.jdom.JDOMException;

import com.royalstone.pos.util.UpdateCheck;
import com.royalstone.pos.util.UpdateList;

/**
 * 服务端代码，取得程序更新的模块列表
 * @author liangxinbiao
 */
public class GetUpdateListCommand implements ICommand {

	private String posid;

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {

		if (values != null && values.length == 2) {
			posid = (String) values[1];
			Object[] result = new Object[1];
			result[0] = getUpdateList();
			return result;
		}
		return null;
	}

	/**
	 * 获得要更新的文件信息的列表
	 * 其中posupdate.properties里记录了已经下载过的POS机的POS机号
	 * posupdate.xml里记录了要更新的文件的信息
	 * @return 要更新的文件信息的列表
	 */
	private UpdateList getUpdateList() {
		try {

			UpdateCheck updateCheck = UpdateCheck.getInstance();
			if (posid != null
				&& !updateCheck.hadUpdate("../download/posupdate.properties", posid)) {
				UpdateList updateList = new UpdateList("../download/posupdate.xml");
				return updateList;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return null;
	}

}
