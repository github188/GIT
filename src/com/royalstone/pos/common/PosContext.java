package com.royalstone.pos.common;

import com.royalstone.pos.util.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.jdom.*;
import org.jdom.input.*;

/** POS工作的上下文信息
   @version 1.0 2004.05.14
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PosContext implements Serializable {
	/**
	 * @return	PosContext 单实例
	 */
	public static PosContext getInstance() {
		if (context == null)
			context = new PosContext();
		return context;
	}

	/**	此函数对最基本的参数进行初始化: 门店号, POS编号, 柜组号, 服务器IP.
	 * @param storeid
	 * @param posid
	 * @param placeno
	 * @param serverip
	 */
	public void init(
		String storeid,
		String posid,
		String placeno,
		String serverip) {
		store_id = storeid;
		pos_id = posid.toUpperCase();
		place_no = placeno;
		host_ip = serverip;
	}

	/**
	 * Constructor
	 */
	private PosContext() {
		context = this;
		sheet_id = 1;
	}

	/**
	 * 
	 * */
	public String getPrintType() {
		if (printtype.equals("TICKET")) {
			return printtype;
		} else {
			return "INVOICE";
		}
	}

	/**
	 * @return
	 */
	public String getStoreid() {
		return store_id;
	}

	/**
	 * @return
	 */
	public String getPosid() {
		return pos_id;
	}

	/**
	 * @return
	 */
	public String getPlaceno() {
		return place_no;
	}

	/**
	 * @return
	 */
	public String getServerip() {
		return host_ip;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return
	 */
	public String getCashierid() {
		return cashier_id;
	}

	/**
	 * @return
	 */
	public String getWaiterid() {
		return waiter_id;
	}

	/**
	 * @return
	 */
	public String getAuthorizerid() {
		return authorizer_id;
	}

	/**
	 * @return
	 */
	public Day getWorkDate() {
		//TODO  沧州富达 by fire  2005_5_11
		//return pos_turn.getWorkDate();
		//return new Day();
        return this.workDay;
	}


    public void setWorkDate(Day day) {
		//TODO  沧州富达 by fire  2005_5_11
		//return pos_turn.getWorkDate();
		//return new Day();
        this.workDay=day;
	}
	/**
	 * @return
	 */
	public String getLWorkDate() {
		GregorianCalendar todaysDate = new GregorianCalendar();
		int year = todaysDate.get(Calendar.YEAR);
		int month = todaysDate.get(Calendar.MONTH) + 1;
		int day = todaysDate.get(Calendar.DAY_OF_MONTH);
		DecimalFormat df = new DecimalFormat("00");
		String edate = "" + year + df.format(month) + df.format(day);
		return edate;
	}

	/**
	 * @return
	 */
	public String getLPosTime() {
		GregorianCalendar now = new GregorianCalendar();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		DecimalFormat df = new DecimalFormat("00");
		String etime = df.format(hour) + df.format(minute) + df.format(second);
		return etime;
	}

	/**
	 * @return	POS的工作班次号
	 */
	public int getShiftid() {
		return pos_turn.getShiftid();
	}

	/**
	 * @return
	 */
	public String getModeCode() {
		if (online) {
			return "ONLINE";
		} else {
			return "OFFLINE";
		}
	}

	/**
	 * @return
	 */
	public String getCurrenCode() {
		return curren_code;
	}

	/**
	 * @return
	 */
	public double getCurrenRate() {
		return curren_rate;
	}

	/**
	 * @return
	 */
	public Exchange getExchange() {
		return new Exchange(curren_code, curren_rate);
	}

	/**
	 * @return
	 */
	public int getSheetid() {
		return sheet_id;
	}

	/**
	 * @return
	 */
	public String getName4Journal() {
		DecimalFormat df = new DecimalFormat("00000");
		 //TODO 沧州富达 by fire  2005_5_11
		return new Day().toString()
			+ "."
			+ pos_id
			+ "."
			+ df.format(sheet_id)
			+ ".xml";
	}

	/**
	 * @return
	 */
	public String getNameLog() {
		GregorianCalendar now = new GregorianCalendar();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		DecimalFormat df = new DecimalFormat("00");
		String etime = df.format(hour) + df.format(minute) + df.format(second);

		GregorianCalendar todaysDate = new GregorianCalendar();
		int year = todaysDate.get(Calendar.YEAR);
		int month = todaysDate.get(Calendar.MONTH) + 1;
		int day = todaysDate.get(Calendar.DAY_OF_MONTH);

		String edate = "" + year + df.format(month) + df.format(day);

		return edate + etime + "." + pos_id + "." + ".xml";
	}

	/**
	 * @return
	 */
	public String getNameRePrint() {
		DecimalFormat df = new DecimalFormat("00000");
		int reprintsheetid = sheet_id - 1;
		return pos_turn.toString()
			+ "."
			+ pos_id
			+ "."
			+ df.format(reprintsheetid)
			+ ".xml";
	}

	/**
	 * @return
	 */
	public boolean isTraining() {
		return training;
	}

	/**
	 * @return
	 */
	public boolean isOnLine() {
		return online;
	}

	/**
	 * @return
	 */
	public boolean isOffLine() {
		return !online;
	}

	/**
	 * @return
	 */
	public String getModeStr() {
		String s = "";
		s += online ? "联机" : "脱机";
		s += training ? "培训" : "";
		return s;
	}

	/**
	 * @return
	 */
	public String sheetFile() {
		return sheet_file;
	}

	/**
	 * 
	 * @return	count of sheets being held up.
	 */
	/**
	 * @return
	 */
	public int getHeldCount() {
		return held_count;
	}

	/**
	 * @param count
	 */
	public void setHeldCount(int count) {
		held_count = count;
	}

	/**
	 * @param file
	 */
	public void setSheetFile(String file) {
		sheet_file = file;
	}

	/**
	 * @param storeid
	 */
	public void setStoreid(String storeid) {
		store_id = storeid;
	}

	/**
	 * @param posid
	 */
	public void setPosid(String posid) {
		pos_id = posid;
	}

	/**
	 * @param placeno
	 */
	public void setPlaceno(String placeno) {
		place_no = placeno;
	}

	/**
	 * @param cashierid
	 */
	public void setCashierid(String cashierid) {
		cashier_id = cashierid;
	}

	/**
	 * @param turn
	 */
	public void setWorkTurn(WorkTurn turn) {
		pos_turn = turn;
	}

	/**
	 * @param code
	 * @param rate
	 */
	public void setCurrency(String code, double rate) {
		curren_code = code;
		curren_rate = rate;
	}

	/**
	 * @param id
	 */
	public void setSheetid(int id) {
		sheet_id = id;
	}

	/**
	 * @param waiterid
	 */
	public void setWaiterid(String waiterid) {
		waiter_id = waiterid;
	}

	/**
	 * @return
	 */
	public String getWarning() {
		return warning;
	}

	/**
	 * @param warning
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * 
	 */
	public void incrSheetid() {
		++sheet_id;
	}

	/**
	 * 
	 */
	public void dump() {
		try {
			ObjectOutputStream out =
				new ObjectOutputStream(new FileOutputStream(file4context));
			out.writeObject(this);
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * @return
	 */
	public static PosContext load() {
		PosContext c = null;
		ObjectInputStream in = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file4context);
			in = new ObjectInputStream(fin);
			c = (PosContext) in.readObject();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fin != null) {
					fin.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			FileUtil.fileError(file4context);

			c = new PosContext();
		}

		context = c;
		return c;
	}

	/**
	   @param file xml file containing currency exchange rate list.
	 */
	/**
	 * @param file
	 */
	public void fromXML(String file) {
		try {
			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			Element elm_basic = root.getChild("basic");
			place_no = elm_basic.getChild("placeno").getTextTrim();
			training = false;
			if (elm_basic.getChild("posgroup") != null) {
				if (elm_basic
					.getChild("posgroup")
					.getTextTrim()
					.equals("TRAIN")) {
					training = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param file
	 */
	public void fromIni(String file) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			pos_id = prop.getProperty("posid").toUpperCase();
			store_id = prop.getProperty("storeid");
			host_ip = prop.getProperty("server");
			port = Integer.parseInt(prop.getProperty("port"));
			if (prop.getProperty("printtype") == null) {
				printtype = "TICKET";
			} else {
				printtype = prop.getProperty("printtype").toUpperCase();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String toString() {
		return "Storeid:	"
			+ store_id
			+ "Posid:	"
			+ pos_id
			+ "Cashierid:	"
			+ cashier_id
			+ "Sheetid:	"
			+ sheet_id
			+ "WorkTurn: "
			+ pos_turn
			+ "CurrenCode:	"
			+ curren_code
			+ "CurrenRate:	"
			+ curren_rate
			+ "Placeno:	"
			+ place_no;
	}

	/**
	 * @return
	 */
	public Element toElement() {
		Element elm = new Element("context");
		elm.addContent(new Element("store_id").addContent(store_id));
		elm.addContent(new Element("posid").addContent(pos_id));
		elm.addContent(new Element("placeno").addContent(place_no));
		elm.addContent(new Element("host_ip").addContent(host_ip));
		elm.addContent(new Element("cashier_id").addContent(cashier_id));
		elm.addContent(new Element("waiter_id").addContent(waiter_id));
		elm.addContent(new Element("authorizer_id").addContent(authorizer_id));
		elm.addContent(new Element("curren_code").addContent(curren_code));
		elm.addContent(new Element("sheet_file").addContent(sheet_file));
		elm.addContent(new Element("warning").addContent(warning));
		return elm;
	}

	/**
	 * @return
	 */
	public WorkTurn getWorkTurn() {
		return pos_turn;
	}

	/**
	 * @param id
	 */
	public void setAuthorizerid(String id) {
		authorizer_id = id;
	}

	/**
	 * @param value
	 */
	public void setOnLine(boolean value) {
		online = value;
	}

	// 商品可剂数数值
	public int drupnumber = 0;
	/**
	 * Comment for <code>context</code>
	 */
	private static PosContext context = null;
	/**
	 * Comment for <code>file4context</code>
	 */
	public static String file4context = "work" + File.separator + "context.dat";
	/**
	 * Comment for <code>store_id</code>
	 */
	private String store_id = "";
	/**
	 * Comment for <code>pos_id</code>
	 */
	private String pos_id = "";
	/**
	 * Comment for <code>place_no</code>
	 */
	private String place_no = "";
	/**
	 * Comment for <code>host_ip</code>
	 */
	private String host_ip = "";
	/**
	 * Comment for <code>port</code>
	 */
	private int port;
	/**
	 * Comment for <code>cashier_id</code>
	 */
	private String cashier_id = "";
	/**
	 * Comment for <code>waiter_id</code>
	 */
	private String waiter_id = "";
	/**
	 * Comment for <code>authorizer_id</code>
	 */
	private String authorizer_id = "";
	/**
	 * Comment for <code>curren_code</code>
	 */
	private String curren_code = "RMB";
	/**
	 * Comment for <code>curren_rate</code>
	 */
	private double curren_rate = 1;

	/**
	 * Comment for <code>pos_turn</code>
	 */
	private WorkTurn pos_turn = null;

	/**
	 * Comment for <code>sheet_id</code>
	 */
	private int sheet_id;

	/**
	 * Comment for <code>held_count</code>
	 */
	private int held_count = 0;
	/**
	 * Comment for <code>sheet_file</code>
	 */
	private String sheet_file = "work" + File.separator + "sheet#0";
	/**
	 * Comment for <code>warning</code>
	 */
	private String warning = "";
	/**
	 * Comment for <code>online</code>
	 */
	private volatile boolean online = true;
	/**
	 * Comment for <code>training</code>
	 */
	private boolean training = false;
	/**
	 * Comment for <code>locked</code>
	 */
	private boolean locked = false;
	private String printtype = "";
    private Day workDay;

    public void updateWorkDay(int listNO){
//        Day nowDay=new Day();
//        if(nowDay.getYear()>this.workDay.getYear()||
//                nowDay.getMonth()>this.workDay.getMonth()
//                ||nowDay.getDay()>this.workDay.getDay())
//          this.sheet_id=1;
//        this.workDay=nowDay;
       if(listNO>=0)
           this.sheet_id=listNO+1;

      this.workDay=new Day();


    }
        public void updateWorkDay(){
        Day nowDay=new Day();
        if(nowDay.getYear()>this.workDay.getYear()||
                nowDay.getMonth()>this.workDay.getMonth()
                ||nowDay.getDay()>this.workDay.getDay())
          this.sheet_id=1;
        this.workDay=nowDay;


        }
}
