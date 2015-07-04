package com.royalstone.pos.workTurn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.common.PosEnv;
import com.royalstone.pos.data.PosTurn;
import com.royalstone.pos.data.PosTurnList;
import com.royalstone.pos.data.ShopClock;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.util.WorkTurn;

/**
 * @author liangxinbiao
 */

public class WorkTurnAdm {

	private static WorkTurnAdm instance;
	private URL servlet;
	private HttpURLConnection conn;

	private WorkTurnAdm() {
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

	public static WorkTurnAdm getInstance() {
		if (instance == null) {
			instance = new WorkTurnAdm();
		}
		return instance;
	}

	public PosEnv logon(
		PosContext context,
		String posid,
		String cashierid,
		String pin,
		int shiftid,
		String localip)
		throws WorkTurnException {

		PosTurnList posTurnList = PosTurnList.getInstance();
		ArrayList turnList = posTurnList.findByState(1);

		boolean isPrevTurnEndOffLine = false;
		if (turnList.size() > 0) {
			PosTurn prevTurn = (PosTurn) turnList.get(turnList.size() - 1);
			if (prevTurn.isEndOffLine()) {
				isPrevTurnEndOffLine = true;
			}
			uploadCompletePosTurn(turnList);
		}

		ShopClock newShopClock = getNewShopClock(ShopClock.getInstance());
		if (newShopClock != null) {
			ShopClock.setInstance(newShopClock);
			newShopClock.dump();
		}

		verify(posid, cashierid, pin, localip);

		ArrayList activeTurnList = PosTurnList.getInstance().findByState(0);
		PosTurn activeTurn;
		if (activeTurnList.size() > 0) {
			activeTurn = (PosTurn) activeTurnList.get(0);
			if (checkState(activeTurn)) {
				posTurnList.deletePosTurnByState(0);
				posTurnList.dump();
			}
		}

		activeTurnList = PosTurnList.getInstance().findByState(0);
		if (activeTurnList.size() == 0) {
			ShopClock shopClock = ShopClock.getInstance();
			activeTurn = new PosTurn();
			activeTurn.setPosid(posid);
			activeTurn.setStartTime(new Date());
			activeTurn.setStartOffLine(false);
			activeTurn.setStat(0);
			activeTurn.setWorkdate(shopClock.getWorkDate());
			activeTurn.setShiftID(shopClock.getShiftID());
			PosTurnList.getInstance().addPosTurn(activeTurn);
			PosTurnList.getInstance().dump();
		} else {
			activeTurn = (PosTurn) activeTurnList.get(0);
		}

		if (checkState(activeTurn)) {
			DecimalFormat df = new DecimalFormat("00");
			String warning =
				"错误：要求登录的班次已经关闭。("
					+ Formatter.getDate(activeTurn.getWorkdate())
					+ "#"
					+ df.format(activeTurn.getShiftID())
					+ ")";
			System.out.println(warning);

			PosTurn serverActiveTurn = getActivePosTurn(posid);

			if (serverActiveTurn == null) {

				reOpenPosTurn(activeTurn);

			} else {

				ShopClock shopClock = ShopClock.getInstance();

				if (serverActiveTurn
					.getWorkdate()
					.compareTo(shopClock.getWorkDate())
					> 0
					|| (serverActiveTurn
						.getWorkdate()
						.compareTo(shopClock.getWorkDate())
						== 0
						&& serverActiveTurn.getShiftID()
							> shopClock.getShiftID())) {

					activeTurn = serverActiveTurn;
					shopClock.setWorkDate(activeTurn.getWorkdate());
					shopClock.setShiftID(activeTurn.getShiftID());
					shopClock.dump();

					System.out.println(
						"Reset ShopClock from Server Active PosTurn.");

					PosTurnList.getInstance().deletePosTurnByState(0);
					PosTurnList.getInstance().addPosTurn(activeTurn);
					PosTurnList.getInstance().dump();

					System.out.println(
						"Reset Active PosTurn from Server Active PosTurn.");

				} else {
					reOpenPosTurn(activeTurn);
				}
			}
		}

		WorkTurn turn =
			new WorkTurn(
				new Day(activeTurn.getWorkdate()),
				activeTurn.getShiftID());

		if (isNewDay4Pos(posid, turn)) {
			if (!isPrevTurnEndOffLine) {
				context.setSheetid(1);
				context.dump();
			}
		}

		uploadNewPosTurn(activeTurn);

		if (shiftid != activeTurn.getShiftID()) {
			DecimalFormat df = new DecimalFormat("00");
			String warning =
				"您输入的班次号不正确，收银机"
					+ context.getPosid()
					+ "尚未作班结，当前工作班次为："
					+ Formatter.getDate(activeTurn.getWorkdate())
					+ "#"
					+ df.format(activeTurn.getShiftID())
					+ "。";

			throw new WorkTurnException(warning);
		}

		int listno = getListNo(posid);

		setCurOperator(posid, cashierid);

		PosEnv env = new PosEnv(turn, listno);

		if (turnList.size() > 0) {
			posTurnList.deletePosTurnByState(1);
			posTurnList.dump();
		}

		return env;

	}

	public void closeWorkTurn(PosContext context, String posid)
		throws WorkTurnException {

		PosTurnList posTurnList = PosTurnList.getInstance();
		ArrayList turnList = posTurnList.findByState(1);

		if (turnList.size() > 0) {
			uploadCompletePosTurn(turnList);
			posTurnList.deletePosTurnByState(1);
			posTurnList.dump();
		}

		ArrayList activeTurnList = PosTurnList.getInstance().findByState(0);
		PosTurn activeTurn;
		if (activeTurnList.size() == 0) {
			ShopClock shopClock = ShopClock.getInstance();
			activeTurn = new PosTurn();
			activeTurn.setPosid(posid);
			activeTurn.setStartTime(new Date());
			activeTurn.setStartOffLine(false);
			activeTurn.setStat(0);
			activeTurn.setWorkdate(shopClock.getWorkDate());
			activeTurn.setShiftID(shopClock.getShiftID());
			PosTurnList.getInstance().addPosTurn(activeTurn);
			PosTurnList.getInstance().dump();
		} else {
			activeTurn = (PosTurn) activeTurnList.get(0);
		}

		uploadNewPosTurn(activeTurn);

		com.royalstone.pos.managment.WorkTurnAdm w =
			new com.royalstone.pos.managment.WorkTurnAdm(
				context.getServerip(),
				context.getPort());
		Response rep = (Response) w.closeTurn();
		if (!rep.succeed()) {
			throw new WorkTurnException(rep.getNote());
		}

		setCurOperator(posid, null);

		ShopClock newShopClock = getNewShopClock(ShopClock.getInstance());
		if (newShopClock != null) {
			ShopClock.setInstance(newShopClock);
			newShopClock.dump();
		}

		posTurnList.deletePosTurnByState(0);
		posTurnList.dump();

	}

	public void uploadCompletePosTurn(ArrayList turnList)
		throws WorkTurnException {

		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.UploadCompletePosTurnCommand";
			params[1] = turnList;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				errorMsg1 = (String) results[0];
				errorMsg2 = (String) results[1];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}
	}

	private int getListNo(String posid) throws WorkTurnException {

		int result = 0;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.GetListNoCommand";
			params[1] = posid;

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
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}

		return result;
	}

	private void setCurOperator(String posid, String cashierid)
		throws WorkTurnException {

		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[3];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.SetCurOperatorCommand";
			params[1] = posid;
			params[2] = cashierid;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				errorMsg1 = (String) results[0];
				errorMsg2 = (String) results[1];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}

	}

	private boolean isNewDay4Pos(String posid, WorkTurn turn)
		throws WorkTurnException {

		boolean result = false;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[3];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.IsNewDay4PosCommand";
			params[1] = posid;
			params[2] = turn;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = ((Boolean) results[0]).booleanValue();
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}

		return result;
	}

	private void verify(
		String posid,
		String cashierid,
		String plainpin,
		String localip)
		throws WorkTurnException {

		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[5];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.LogonVerifyCommand";
			params[1] = posid;
			params[2] = cashierid;
			params[3] = plainpin;
			params[4] = localip;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				errorMsg1 = (String) results[0];
				errorMsg2 = (String) results[1];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}
	}

	private ShopClock getNewShopClock(ShopClock shopClock)
		throws WorkTurnException {

		ShopClock result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.GetNewShopClockCommand";
			params[1] = shopClock;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = (ShopClock) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}

		return result;
	}

	public void uploadNewPosTurn(PosTurn posTurn) throws WorkTurnException {

		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.UploadNewPosTurnCommand";
			params[1] = posTurn;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				errorMsg1 = (String) results[0];
				errorMsg2 = (String) results[1];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}
	}

	private boolean checkState(PosTurn turn) throws WorkTurnException {

		boolean result = false;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.CheckStatCommand";
			params[1] = turn;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = ((Boolean) results[0]).booleanValue();
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}

		return result;
	}

	private PosTurn getActivePosTurn(String posid) throws WorkTurnException {

		PosTurn result = null;
		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.GetActivePosTurnCommand";
			params[1] = posid;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = (PosTurn) results[0];
				errorMsg1 = (String) results[1];
				errorMsg2 = (String) results[2];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}

		return result;
	}

	private void reOpenPosTurn(PosTurn turn) throws WorkTurnException {

		String errorMsg1 = null;
		String errorMsg2 = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.workTurn.ReOpenPosTurnCommand";
			params[1] = turn;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invokeWithException(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				errorMsg1 = (String) results[0];
				errorMsg2 = (String) results[1];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new WorkTurnException("网络故障,按清除键继续!");
		}

		if (errorMsg1 != null) {
			System.out.println(errorMsg2);
			throw new WorkTurnException(errorMsg1);
		}

	}

}
