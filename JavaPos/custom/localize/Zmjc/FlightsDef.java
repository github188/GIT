package custom.localize.Zmjc;

import java.io.Serializable;

/**
 * 航班信息
 * @author wy
 *
 */
public class FlightsDef implements Serializable
{

	private static final long serialVersionUID = 0L;
	public static String[] ref = { "fseqno", "frowno", "fhblb", "ffjlb", "fnumber", "fairlines", "ftime", "frealtime", "fport1", "fport2", "ftype", "fmemo", "data0", "data1", "data2", "data3", "data4", "data5", "data6", "fjt", "fbaojdate" };
    
    
	 public String fseqno;	//序列号
	    public int frowno;//行号
	    public String fhblb;//航班类别（01离境  02离岛）
	    public String ffjlb;//飞机类别（01包机  02班机）
	    public String fnumber;//航班号
	    public String fairlines;//航空公司
	    public String ftime;//起飞默认时间
	    public String frealtime;//离境时间（起飞真正时间）
	    public String fport1;//出发地（提货地点）
	    public String fport2;//目的地
	    public String ftype;//机型
	    public String fmemo;//备注
	    public char data0;//星期日
	    public char data1;//星期一
	    public char data2;//星期二
	    public char data3;//星期三
	    public char data4;//星期四
	    public char data5;//星期五
	    public char data6;//星期六
	    public String fjt;//经停
	    public String fbaojdate;    //包机日期
    
}
