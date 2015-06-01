package posserver.tools.PressTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;

public class GlobalStatus {
	// 纯种运行是否停止
	public static boolean isstoping = false;
	private static boolean isstopcompleted = true;

	private static Timer timer;
	
	private static Timer timer1;

	// 活动线程记数
	private static int activeThreadNum = 0;
	// 总共的运行的次数
	private static int cmdRoundTimes = 0;
	// 发送总次数
	private static int cmdSendTimes = 0;
	// 发送总的失败次数
	private static int cmdSendFaileTimes = 0;

	// 纯程状态列表
	public static ThreadStatus[] arrThreadStatus = null;

	// 正在测试的命令列表
	public static Vector cmdlisting = new Vector();

	public static void init() {
		setActiveThreadNum(0);
		isstoping = false;
		setIsstopcompleted(false);
		cmdRoundTimes = 0;
		cmdSendTimes = 0;
		cmdSendFaileTimes = 0;
		cmdlisting.clear();
		arrThreadStatus = null;
	}

	public static void setActiveThreadNum(int allCount) {
		GlobalStatus.activeThreadNum = allCount;
	}

	public static int getActiveThreadNum() {
		return activeThreadNum;
	}

	public static int getCmdSendTimes() {
		return cmdSendTimes;
	}

	public static int getCmdSendFaileTimes() {
		return cmdSendFaileTimes;
	}

	public synchronized static void increseActiveThreadNum() {
		GlobalStatus.activeThreadNum++;

		if (PosSrvPressureEvent.getDefault() != null) {
			PosSrvPressureEvent.getDefault().updateStatus();
		}
	}

	public synchronized static void decreseActiveThreadNum() {
		GlobalStatus.activeThreadNum--;
		if (GlobalStatus.activeThreadNum <= 0) {
			GlobalStatus.activeThreadNum = 0;
			GlobalStatus.setIsstopcompleted(true);
		}

		if (PosSrvPressureEvent.getDefault() != null) {
			PosSrvPressureEvent.getDefault().updateStatus();
		}
	}

	public synchronized static void increseCmdSendTimes() {
		GlobalStatus.cmdSendTimes++;

		if (PosSrvPressureEvent.getDefault() != null) {
			PosSrvPressureEvent.getDefault().updateStatus();
		}
	}

	public synchronized static void decreseCmdSendTimes() {
		GlobalStatus.cmdSendTimes--;

		if (PosSrvPressureEvent.getDefault() != null) {
			PosSrvPressureEvent.getDefault().updateStatus();
		}
	}

	public synchronized static void increseCmdSendFaileTimes() {
		GlobalStatus.cmdSendFaileTimes++;

		if (PosSrvPressureEvent.getDefault() != null) {
			PosSrvPressureEvent.getDefault().updateStatus();
		}
	}

	public synchronized static void decreseCmdSendFaileTimes() {
		GlobalStatus.cmdSendFaileTimes--;

		if (PosSrvPressureEvent.getDefault() != null) {
			PosSrvPressureEvent.getDefault().updateStatus();
		}
	}

	public synchronized static void increseCmdRoundTimes() {
		GlobalStatus.cmdRoundTimes++;

		if (PosSrvPressureEvent.getDefault() != null) {
			PosSrvPressureEvent.getDefault().updateStatus();
		}
	}

	public static int getCmdRoundTimes() {
		return cmdRoundTimes;
	}
	
	public static void startUpdateStatus() {
		stopUpdateStatus();

		timer = new Timer();
		timer.schedule(new MyTask(), 2000, 2000);
	}

	public static void stopUpdateStatus() {
		if (timer != null) {
			timer.cancel();
		}

		timer = null;
	}
	
	public static void startTimer1() {
		if (GlobalConfig.testTimer <=0 )	return;
		
		stopTimer1();

		timer1 = new Timer();
		timer1.schedule(new End(), GlobalConfig.testTimer*60*1000);
	}

	public static void stopTimer1() {
		if (timer1 != null) {
			timer1.cancel();
		}

		timer1 = null;
	}

	public static void setIsstopcompleted(boolean isstopcompleted) {
		if (isstopcompleted) {
			GlobalStatus.stopUpdateStatus();
			
			new MyTask().run();
			
			GlobalStatus.stopTimer1();
		}

		GlobalStatus.isstopcompleted = isstopcompleted;
	}

	public static boolean isIsstopcompleted() {
		return isstopcompleted;
	}

	public static boolean clearHisData() {
		String strdelete = "DELETE FROM PRESSSTATUS WHERE IDENTIFY = '" + GlobalConfig.identify + "'";

		if (!GlobalVar.getSqldb1().executeSql(strdelete)) {
			GlobalStatus.timer.cancel();
			new MessageBox("删除数据库状态失败!\n" + strdelete);
			return false;
		}
		
		new MessageBox("清除历史数据成功!");
		
		return true;
	}

	static class MyTask extends java.util.TimerTask {
		public void run() {
			// 记录状态
			String strupdate = "UPDATE PRESSSTATUS SET ACTIVETHREADNUM="
					+ GlobalStatus.getActiveThreadNum() + ",CMDROUNDTIMES="
					+ GlobalStatus.getCmdRoundTimes() + ",CMDSENDTIMES="
					+ GlobalStatus.getCmdSendTimes() + ",CMDSENDFAILETIMES="
					+ GlobalStatus.getCmdSendFaileTimes()
					+ " Where IDENTIFY = '" + GlobalConfig.identify + "'"
					+ " AND THREADCOUNT = " + GlobalConfig.threadCount;

			if (!GlobalVar.getSqldb1().executeSql(strupdate)) {
				GlobalStatus.timer.cancel();
				new MessageBox("更新数据库状态失败!\n" + strupdate);
				return;
			}

			if (GlobalVar.getSqldb1().getAffectRow() <= 0) {
				String strinsert = "INSERT INTO PRESSSTATUS(IDENTIFY,THREADCOUNT,ACTIVETHREADNUM,CMDROUNDTIMES,CMDSENDTIMES,CMDSENDFAILETIMES) "
						+ "VALUES('"
						+ GlobalConfig.identify
						+ "',"
						+ GlobalConfig.threadCount
						+ ","
						+ GlobalStatus.getActiveThreadNum()
						+ ","
						+ GlobalStatus.getCmdRoundTimes()
						+ ","
						+ GlobalStatus.getCmdSendTimes()
						+ ","
						+ GlobalStatus.getCmdSendFaileTimes() + ")";

				if (!GlobalVar.getSqldb1().executeSql(strinsert)) {
					GlobalStatus.timer.cancel();
					new MessageBox("更新数据库状态失败!\n" + strinsert);
					return;
				}
			}
		}
	}
	
	static class End extends java.util.TimerTask {
		public void run() {
			if (GlobalStatus.isIsstopcompleted())
				return;

			GlobalStatus.isstoping = true;

			if (GlobalStatus.arrThreadStatus != null
					&& GlobalStatus.arrThreadStatus.length > 1)
				try {
					Thread.sleep(GlobalStatus.arrThreadStatus.length * 15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			new MessageBox("测试完成!测试时长:"+ GlobalConfig.testTimer +"分钟");
		}
	}
}
