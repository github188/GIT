package custom.localize.Zmjc;

import java.io.Serializable;

public class RetFlightDef implements Serializable {

	/**
	* 回程航班信息
    * @author yuxp
	*/
	private static final long serialVersionUID = 1L;
	public static String[] ref = { "rfid", "rfname", "rfstatus", "rfarrtime","rfmemo","rfdepar","rfcompany" };
	public String rfid; // 航班编号
	public String rfname;// 航班号
	public String rfstatus;// 状态
	public String rfarrtime;// 官方航班到达机场时间
	public String rfmemo;// 备注
	public String rfdepar;//航班出发地
	public String rfcompany;//航空公司

}
