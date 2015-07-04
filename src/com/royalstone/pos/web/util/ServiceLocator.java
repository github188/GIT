package com.royalstone.pos.web.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJBHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

/**
 * 服务查找器，EJB的客户端透过它来查找资源
 * 它在第一次查找到相关资源后会将它缓存起来，
 * 从而加快下一次的资源查找
 * @author liangxinbiao
 */
public class ServiceLocator {
	private Context _context;
	private Map _cache;
	private static ServiceLocator _instance;
	private String url;
	private String host;

	/**
	 * 私有构造方法,单实例的要求
	 * @param host
	 * @throws Exception
	 */
	private ServiceLocator(String host) throws Exception {
		try {
			this.host = host;
			_context = this.getJBossInitialContext();
			_cache = Collections.synchronizedMap(new HashMap());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 取得ServiceLocator的单实例
	 * @param host
	 * @return
	 * @throws Exception
	 */
	static public synchronized ServiceLocator getInstance(String host)
		throws Exception {
		if (_instance == null) {
			_instance = new ServiceLocator(host);
			return _instance;
		}
		return _instance;
	}

	/**
	 * 取得远程的Home接口
	 * @param jndiHomeName
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public EJBHome getRemoteHome(String jndiHomeName, Class className)
		throws Exception {
		EJBHome home = null;
		try {
			if (_cache.containsKey(jndiHomeName)) {
				home = (EJBHome) _cache.get(jndiHomeName);
			} else {
				Object objref = _context.lookup(jndiHomeName);
				Object obj = PortableRemoteObject.narrow(objref, className);
				home = (EJBHome) obj;
				_cache.put(jndiHomeName, home);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return home;
	}

	/**
	 * 取得数据源
	 * @param dataSourceName
	 * @return
	 * @throws Exception
	 */
	public DataSource getDataSource(String dataSourceName) throws Exception {
		DataSource dataSource = null;
		try {
			if (_cache.containsKey(dataSourceName)) {
				dataSource = (DataSource) _cache.get(dataSourceName);
			} else {
				dataSource =
					(DataSource) _context.lookup(
						"java:comp/env/" + dataSourceName);
				_cache.put(dataSourceName, dataSource);
			}
		} catch (NamingException ne) {
			throw ne;
		} catch (Exception e) {
			throw e;
		}
		return dataSource;
	}

	/**
	 * 取得JBoss的初始上下文
	 * @return
	 * @throws NamingException
	 */
	private javax.naming.Context getJBossInitialContext()
		throws NamingException {
		if (host == null || host.equals("")) {
			host = "localhost";
		}

		java.util.Hashtable JNDIParm = new java.util.Hashtable();
		JNDIParm.put(Context.PROVIDER_URL, host);
		JNDIParm.put(
			Context.INITIAL_CONTEXT_FACTORY,
			"org.jnp.interfaces.NamingContextFactory");
		return new InitialContext(JNDIParm);
	}

}
