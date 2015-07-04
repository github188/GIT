/*
 * Good Day;
 */
package com.royalstone.pos.common;


/**
 * @author wubingyan
 * Created by King_net  on 2005-7-19  15:27:54
 */
public class BankCardTransResults {
	public static void main(String[] args) {
	}
	
	public String getBankTransResults(String key){
		if(key.equals("000000")) 	return "交易成功";
		if(key.equals("001111")) 	return "配置文件不存在配置文件不存在";
		if(key.equals("001001")) 	return "非法交易,接口不支持此交易RESERVE";
		if(key.equals("001002")) 	return "非法交易金额";
		if(key.equals("001003")) 	return "非法流水号";
		if(key.equals("001004")) 	return "非法批次号RESERVE";
		if(key.equals("001005")) 	return "取刷卡方式错误,INI配置错误";
		if(key.equals("001006")) 	return "刷卡错误";
		if(key.equals("001007")) 	return "磁道信息错误";
		if(key.equals("001008")) 	return "取密码错误";
		if(key.equals("001009")) 	return "取配置错误";
		if(key.equals("002000")) 	return "系统错";
		if(key.equals("002001")) 	return "发送交易包错误";
		if(key.equals("002002")) 	return "接收响应包超时";
		if(key.equals("010000")) 	return "查发卡方";
		if(key.equals("020000")) 	return "查发卡方的特殊条件";
		if(key.equals("030000")) 	return "无效商户";
		if(key.equals("040000")) 	return "没收卡";
		if(key.equals("050000")) 	return "不予承兑";
		if(key.equals("060000")) 	return "出错";
		if(key.equals("070000")) 	return "特殊条件下没收卡";
		if(key.equals("090000")) 	return "请求正在处理中";
		if(key.equals("120000")) 	return "无效交易";
		if(key.equals("130000")) 	return "无效金额";
		if(key.equals("140000")) 	return "无效卡号";
		if(key.equals("150000")) 	return "无此发卡方";
		if(key.equals("190000")) 	return "重新送入交易";
		if(key.equals("200000")) 	return "无效应答";
		if(key.equals("210000")) 	return "不作任何处理";
		if(key.equals("220000")) 	return "怀疑操作有误";
		if(key.equals("230000")) 	return "不可接受的交易费";
		if(key.equals("250000")) 	return "未能找到文件上记录";
		if(key.equals("300000")) 	return "格式错误";
		if(key.equals("310000")) 	return "交换中心不支持的银行";
		if(key.equals("330000")) 	return "过期的卡（没收卡）";
		if(key.equals("340000")) 	return "有作弊嫌疑（没收卡）";
		if(key.equals("350000")) 	return "受卡方呼受理方安全保密部门（没收卡）";
		if(key.equals("360000")) 	return "受限制的卡（没收卡）";
		if(key.equals("370000")) 	return "受卡方呼受理方安全保密部门（没收卡）";
		if(key.equals("380000")) 	return "超过允许的密码试输入（没收卡）";
		if(key.equals("390000")) 	return "无此信用卡帐户";
		if(key.equals("400000")) 	return "请求的功能尚不支持";
		if(key.equals("410000")) 	return "挂失卡（没收卡）";
		if(key.equals("420000")) 	return "无此帐户";
		if(key.equals("430000")) 	return "被窃卡（没收卡）";
		if(key.equals("440000")) 	return "无此投资帐户";
		if(key.equals("510000")) 	return "无足够的存款";
		if(key.equals("520000")) 	return "无此支票帐户";
		if(key.equals("530000")) 	return "无此储蓄卡帐户";
		if(key.equals("540000")) 	return "过期的卡";
		if(key.equals("550000")) 	return "不正确的密码";
		if(key.equals("560000")) 	return "无此卡记录";
		if(key.equals("570000")) 	return "不允许持卡人进行的交易";
		if(key.equals("580000")) 	return "不允许终端进行的交易";
		if(key.equals("590000")) 	return "有作弊嫌疑";
		if(key.equals("600000")) 	return "受卡方与安全保密部门联系";
		if(key.equals("610000")) 	return "超出取款金额限制";
		if(key.equals("620000")) 	return "受限制的卡";
		if(key.equals("630000")) 	return "违反安全保密规定";
		if(key.equals("640000")) 	return "原始金额不正确";
		if(key.equals("650000")) 	return "超出取款次数限制";
		if(key.equals("660000")) 	return "受卡方呼受理方安全保密部门";
		if(key.equals("670000")) 	return "捕捉（没收卡）";
		if(key.equals("680000")) 	return "收到的回答太迟";
		if(key.equals("750000")) 	return "允许的输入密码次数超限";
		if(key.equals("770000")) 	return "POS批次与网络中心不一致";
		if(key.equals("780000")) 	return "网络中心需要向POS终端下载数据";
		if(key.equals("790000")) 	return "POS终端上传的脱机数据对帐不平";
		if(key.equals("900000")) 	return "日期切换正在处理";
		if(key.equals("910000")) 	return "发卡方或交换中心不能操作";
		if(key.equals("920000")) 	return "金融机构无法达到";
		if(key.equals("930000")) 	return "交易违法、不能完成";
		if(key.equals("940000")) 	return "重复交易";
		if(key.equals("950000")) 	return "调节控制错";
		if(key.equals("960000")) 	return "系统异常、失效";
		if(key.equals("970000")) 	return "ATM/POS终端号找不到";
		if(key.equals("980000")) 	return "交换中心收不到发卡方应答";
		if(key.equals("990000")) 	return "PIN 格式错";
		if(key.equals("A00000")) 	return "MAC鉴别失败";
		if(key.equals("Z00000")) 	return "迟到的批准应答";
		if(key.equals("Z10000")) 	return "MAC鉴别失败引发的冲正";
		if(key.equals("Z20000")) 	return "MIS系统不能把批准应答送交终端";
		if(key.equals("Z30000")) 	return "MIS系统不能把批准应答送交终端";
		if(key.equals("Z40000")) 	return "中心代为引发的冲正";
		if(key.equals("Z50000")) 	return "收不到交换中心的应答";
		if(key.equals("Z60000")) 	return "持卡人本人正常结算";
		if(key.equals("Z70000")) 	return "非持卡人本人正常结算";
		if(key.equals("UM0000")) 	return "前后台MACKEY不一致";
		if(key.equals("B00000")) 	return "接收前台重发交易成功";
		if(key.equals("B10000")) 	return "后台系统错";
		if(key.equals("B20000")) 	return "接收银行超时";
		if(key.equals("B30000")) 	return "包文格式错";
		if(key.equals("B40000")) 	return "前台包文MAC错";
		if(key.equals("B50000")) 	return "卡号不能识别";
		if(key.equals("B60000")) 	return "主机未开通";
		if(key.equals("B70000")) 	return "无此原交易";
		if(key.equals("B80000")) 	return "此卡无此功能";
		if(key.equals("B90000")) 	return "后台正在轧帐";
		if(key.equals("BA0000")) 	return "前台上送商户号错";
		if(key.equals("BB0000")) 	return "前台上送终端号错";
		if(key.equals("BC0000")) 	return "收银员密码错";
		if(key.equals("BD0000")) 	return "商户未开通";
		if(key.equals("BE0000")) 	return "无此收银员定义";
		if(key.equals("BF0000")) 	return "收银员密码错";
		if(key.equals("C00000")) 	return "收银员状态错";
		if(key.equals("C10000")) 	return "商户号不符合";
		if(key.equals("C20000")) 	return "非收银主管";
		if(key.equals("C30000")) 	return "非法交易金额";
		if(key.equals("S10000")) 	return "无效交易代码";
		if(key.equals("S20000")) 	return "无效的8583域";
		if(key.equals("S30000")) 	return "调用加密进程错";
		if(key.equals("S40000")) 	return "返回包处理码不一致";
		if(key.equals("S50000")) 	return "返回包交易金额不符";
		if(key.equals("S60000")) 	return "返回包终端号不符";
		if(key.equals("S70000")) 	return "更新KEY信息错误";
		if(key.equals("S70000")) 	return "更新KEY信息错误";
		if(key.equals("S80000")) 	return "无此原交易";
		if(key.equals("S90000")) 	return "商户未开通";
		if(key.equals("SA0000")) 	return "商户已结算,请重新签到";
		if(key.equals("SB0000")) 	return "非法交易";
		if(key.equals("SC0000")) 	return "流水号重复";
		if(key.equals("SD0000")) 	return "无效交易金额";
		if(key.equals("SE0000")) 	return "系统错";
		if(key.equals("SF0000")) 	return "无效响应码";
		if(key.equals("R00000")) 	return "冲正成功";
		return key+"--未定义错误!";
	}
	
	
	
	
}
