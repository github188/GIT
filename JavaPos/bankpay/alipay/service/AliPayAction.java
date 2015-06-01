package bankpay.alipay.service;

import java.util.HashMap;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SalePayDef;

public class AliPayAction 
{
	//public SalePayDef salepay = null;
//	public SalePayDef unifiedPay(SalePayDef salepay,String partnerNo,String partnerKey,double money,String url)
//	{
//		StringBuffer req = new StringBuffer();
//		boolean done = new TextBox().open(Language.apply("请输入条码"), Language.apply("条码"), Language.apply("请扫描客户手机条码或者输入条码号"), req, 0, 0, false,
//											TextBox.IntegerInput);
//		if(done)
//		{
//			String tmCode = req.toString();
//			String  bearXml= aliPayService.unifiedPay(partnerNo,partnerKey,"",tmCode,String.valueOf(money),url);
//			
//			
//			//获得阿里接口返回值
//			HashMap map = parseXml.domParseXml(bearXml);
//			//String resultCode = map.get("result_code").toString();
//			//String isSuccess = map.get("is_success").toString();
//			String out_trade_no = (String)map.get("out_trade_no");
//			if(!"ORDER_SUCCESS_PAY_SUCCESS ".equals(map.get("result_code")))
//			{
//				//查询交易是否成功
//				while(true)
//				{
//					codeMsg = new MessageBox(text, null, true);
//					
//					if(codeMsg.verify() == GlobalVar.Key1)
//					{
//						HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,out_trade_no, "",url));
//						if(!"TRADE_SUCCESS".equals(queryMap.get("trade_status")))
//						{
//							StringBuffer msgStr = new StringBuffer();
//							if(null == out_trade_no)
//							{
//								msgStr.append("无效商户单号");
//								msgStr.append(tmCode);
//								msgStr.append("\n");
//							}
//							else
//							{
//								msgStr.append("商户单号");
//								msgStr.append(out_trade_no);
//								msgStr.append("\n");
//								msgStr.append("并非交易成功状态");
//								msgStr.append("\n");
//							}
//							msgStr.append("按数字键1确认，按数字键2退出");
//							text = msgStr.toString();
//							continue;
//						}
//						new MessageBox(Language.apply("用户向支付宝支付完成"));
//						//给小票头的商户订单号赋值
//						if (!createSalePay(money)) return null;
//						salepay.payno = out_trade_no;
//						//saleBS.saleHead.str5 = out_trade_no;
//						return salepay;
//					}
//					else if(codeMsg.verify() == GlobalVar.Key2)
//					{
//						return null;
//					}
//					
//				}
//			}
//			else
//			{
//				//给小票头的商户订单号赋值
//				if (!createSalePay(money)) return null;
//				salepay.payno = out_trade_no;
//				//saleBS.saleHead.str5 = out_trade_no;
//				return salepay;
//			}
//		}
//		else
//		{
//			return null;
//		}
//	}
}
