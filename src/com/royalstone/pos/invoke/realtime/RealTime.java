package com.royalstone.pos.invoke.realtime;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.GoodsExt;
import com.royalstone.pos.common.GoodsCut;
import com.royalstone.pos.complex.DiscComplexList;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.favor.BulkPrice;
import com.royalstone.pos.favor.DiscCriteria;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.web.command.realtime.GetBatchnoinfoException;
import com.royalstone.pos.web.command.realtime.GetBulkPriceCommand;
import com.royalstone.pos.web.command.realtime.GetComplexListCommand;
import com.royalstone.pos.web.command.realtime.GetDiscCriteriaCommand;
import com.royalstone.pos.web.command.realtime.LookupGoodsCombCommand;
import com.royalstone.pos.web.command.realtime.LookupGoodsCommand;
import com.royalstone.pos.web.command.realtime.LookupGoodsCutCommand;
import com.royalstone.pos.web.command.realtime.LookupGoodsExtCommand;
import com.royalstone.pos.web.command.realtime.LookupPrecentageCommand;
import com.royalstone.pos.web.command.realtime.LookupNumberDurpCommand;
import com.royalstone.pos.web.command.realtime.TestOnLineCommand;

/**
 * 封装了实时查价的客户端调用
 * @author liangxinbiao
 */
public class RealTime {

	private static RealTime instance;
	private URL servlet;
	private HttpURLConnection conn;

	private LookupGoodsCommand lookupGoodsCommand = new LookupGoodsCommand();
	private LookupGoodsCombCommand lookupGoodsCombCommand =
		new LookupGoodsCombCommand();
	private LookupPrecentageCommand lookupPrecentageCommand =
		new LookupPrecentageCommand();
	private LookupGoodsCutCommand lookupGoodsCutCommand =
		new LookupGoodsCutCommand();
	private LookupGoodsExtCommand lookupGoodsExtCommand =
		new LookupGoodsExtCommand();

	private GetComplexListCommand getComplexListCommand =
		new GetComplexListCommand();

	private GetBulkPriceCommand getBulkPriceCommand = new GetBulkPriceCommand();

	private GetDiscCriteriaCommand getDiscCriteriaCommand =
		new GetDiscCriteriaCommand();
	
	private LookupNumberDurpCommand lookupNumberDurpCommand = 
		new LookupNumberDurpCommand();
		
	private TestOnLineCommand testOnLineCommand = new TestOnLineCommand();
	
	private GetBatchnoinfoException getBatchnoinfoException = new GetBatchnoinfoException();


	private RealTime() {
		try {
			servlet =
				new URL(
					"http://"
						+ pos.core.getPosContext().getServerip()
						+ ":"
						+ pos.core.getPosContext().getPort()
						+ "/pos41/DispatchServlet");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static RealTime getInstance() {
		if (instance == null) {
			instance = new RealTime();
		}
		return instance;
	}

	/**
	 * 实时查询商品信息
	 * @param code 商品编码或条码
	 * @return 商品信息
	 * @throws RealTimeException
	 */
    /*TODO 药品限购*/
	public Goods findGoods(PosPriceData code) throws RealTimeException {

		long start = System.currentTimeMillis();

		Goods result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.LookupGoodsCommand";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = lookupGoodsCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (Goods) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		System.out.println("Total = " + (System.currentTimeMillis() - start));

		return result;
	}
	
	/**
	 * 实时查询商品信息
	 * @param code 商品编码或条码
	 * @return 商品信息
	 * @throws RealTimeException
	 */
	public Goods findGoodsTwo(PosPriceData code) throws RealTimeException {

		long start = System.currentTimeMillis();

		Goods result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.LookupGoodsCommandTwo";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = lookupGoodsCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (Goods) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		System.out.println("Total = " + (System.currentTimeMillis() - start));

		return result;
	}


	public Goods findGoodsComb(String code) throws RealTimeException {

		Goods result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.LookupGoodsCombCommand";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = lookupGoodsCombCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (Goods) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		return result;
	}

	public String findPrecentage(String cardLevelID, String deptID)
		throws RealTimeException {

		String result = "0";
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[3];

			params[0] =
				"com.royalstone.pos.web.command.realtime.LookupPrecentageCommand";
			params[1] = cardLevelID;
			params[2] = deptID;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = lookupPrecentageCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (String) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		return result;
	}
	
	
	// 抵用卷验证过程
public String findNumberDrug(String cardNo, String Money)
	throws RealTimeException {

	String result = "0";
	String errorMsg1 = null;
	String errorMsg2 = null;

	try {

		Object[] params = new Object[3];

		params[0] =
			"com.royalstone.pos.web.command.realtime.LookupNumberDurpCommand";
		params[1] = cardNo;
		params[2] = Money;

		Object[] results = null;

		if (pos.hasServer) {

			conn = (HttpURLConnection) servlet.openConnection();

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO =
				HttpInvoker.invokeWithException(conn, mvI);

			if (mvO != null) {
				results = mvO.getValues();
			}

		} else {

			results = lookupNumberDurpCommand.excute(params);

		}

		if (results != null && results.length > 0) {
			result = (String) results[0];
			errorMsg1 = (String) results[1];
			errorMsg2 = (String) results[2];
		}
	} catch (IOException ex) {
		ex.printStackTrace();
		throw new RealTimeException("网络故障", true);
	}

	if (errorMsg1 != null) {
		System.out.println(errorMsg2);
		throw new RealTimeException(errorMsg1);
	}
	
	return result;
}
	

	public GoodsCut findGoodsCut(String code) throws RealTimeException {

		GoodsCut result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.LookupGoodsCutCommand";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = lookupGoodsCutCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (GoodsCut) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		return result;
	}

	/**
	 * 在一品多码里实时查询商品信息
	 * @param code 商品条码
	 * @return 商品扩展信息
	 * @throws RealTimeException
	 */
	public GoodsExt findGoodsExt(String code) throws RealTimeException {

		GoodsExt result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.LookupGoodsExtCommand";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = lookupGoodsExtCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (GoodsExt) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}
		return result;
	}

	/**
	 * 实时查询商品的普通促销信息
	 * @param code 商品编码
	 * @param ptype 促销类型
	 * @return 促销信息
	 * @throws RealTimeException
	 */
	public DiscCriteria getDiscCriteria(String code, String ptype,String level)
		throws RealTimeException {

		DiscCriteria result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[4];

			params[0] =
				"com.royalstone.pos.web.command.realtime.GetDiscCriteriaCommand";
			params[1] = code;
			params[2] = ptype;
			params[3] = level; 

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = getDiscCriteriaCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (DiscCriteria) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}
		return result;
	}

	/**
	 * 实时查询商品的量贩促销信息
	 * @param code 商品编码
	 * @return 量贩促销信息
	 * @throws RealTimeException
	 */
	public BulkPrice getBulkPrice(String code) throws RealTimeException {

		BulkPrice result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.GetBulkPriceCommand";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = getBulkPriceCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (BulkPrice) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}
		return result;
	}

	/**
	 * 实时查询挂账卡的优惠金额
	 * @param cardno 挂账卡号
	 * @return 优惠金额
	 * @throws RealTimeException
	 */
	public int getLoanCardDiscPrice(String cardno) throws RealTimeException {

		int result = 0;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.GetLoanCardDiscCountCommand";
			params[1] = cardno;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = ((Integer) results[0]).intValue();
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}
		return result;
	}

	/**
	 * 实时查询商品的组合促销信息
	 * @param code 商品编码
	 * @return 组合促销信息
	 * @throws RealTimeException
	 */
	public DiscComplexList getComplextList(String code)
		throws RealTimeException {

		DiscComplexList result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.GetComplexListCommand";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = getComplexListCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (DiscComplexList) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障!", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		return result;
	}

	/**
	 * 测试系统是否联机
	 * @return 是否联机
	 */
	public boolean testOnLine() {

		boolean result = false;

		Object[] params = new Object[1];

		params[0] = "com.royalstone.pos.web.command.realtime.TestOnLineCommand";

		Object[] results = null;

		try {

			if (pos.hasServer) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = testOnLineCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				if (((String) results[0]).equals("1")) {
					result = true;
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;

	}

	public ArrayList getGoodsUpdateList(ArrayList goodsNoList)
		throws RealTimeException {

		ArrayList result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.GetGoodsUpdateCommand";
			params[1] = goodsNoList;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = (ArrayList) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障!", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		return result;
	}
	
    /*TODO 药品限购*/
	public ArrayList findBatchnolist(PosPriceData code) throws RealTimeException {
		long start = System.currentTimeMillis();
		ArrayList result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.realtime.GetBatchnoinfoException";
			params[1] = code;

			Object[] results = null;

			if (pos.hasServer) 
			{
				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) 
				{
					results = mvO.getValues();
				}

			} else {

				results = getBatchnoinfoException.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (ArrayList) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RealTimeException("网络故障", true);
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new RealTimeException(errorMsg1);
		}

		System.out.println("Total = " + (System.currentTimeMillis() - start));

		return result;
	}

}
