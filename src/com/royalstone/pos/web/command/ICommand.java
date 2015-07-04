package com.royalstone.pos.web.command;

/**
 * 服务端命令接口,透过DispatchServlet中转的服务端类一定要实现此接口
 * @author liangxinbiao
 */
public interface ICommand {
	
	/**
	 * 服务端程序的业务方法
	 * @param values
	 * @return
	 */
	public Object[] excute(Object[] values);

}
