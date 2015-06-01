package custom.localize.Zmsy;

public class ZhxFlightApiDef
{
	public String type;//标志位——0表示获取成功，1表示获取失败，-1表示IP禁止,-2表示无法获取ip
	public String pflightno;//航班号——旅客所乘航班的航班号；（10个字符 ）
	public String deptdate;//起飞日期——具体的航班日期；格式：yyyy：mm:dd
	public String depttime;//起飞时间——航班的计划起飞时间；格式：hh:MM
	public String depairport;//——出发机场 （最多20个汉字）
	public String desairport;//——目的地机场（最多20个汉字）
	public String intertype;//——国际航班标志位,0表示国际航班，1表示国内航班
	
}
