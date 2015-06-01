package bankpay.Payment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;

import bankpay.alipay.service.AliPayService;
import bankpay.alipay.tools.ParseIni;
import bankpay.alipay.tools.ParseXml;
import bankpay.alipay.tools.SonicWave;
import bankpay.alipay.tools.SonicWaveNFC;


import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.QrcodeDisplay;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Alipay_Payment extends Payment {
	private AliPayService aliPayService = new AliPayService();
	private ParseXml parseXml = new ParseXml();
	private ParseIni parseIni = new ParseIni();
	private MessageBox codeMsg = null;
	private ProgressBox soundBox = null;
	private static SonicWave sonicWave = new SonicWave();
	private String text = "请确认支付宝支付是否成功 \n" +"按数字键1查询，按数字键2退出";
	public Alipay_Payment() 
	{
	}

	public Alipay_Payment(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Alipay_Payment(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head)
	{
		super.initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{	
	try 
	{
		NetService netService = (NetService) NetService.getDefault();
		Map mapN = new HashMap();
		//获取请求地址
		mapN=parseIni.Parse();
		String url = mapN.get("aliPayUrl").toString();
		String aliConfigUrl = mapN.get("aliConfigUrl").toString();
		//Http获取Ali支付配置
		HashMap configMap = aliPayService.aliConfig(aliConfigUrl);
		
		if(null == configMap
				||null==url
				||"".equals(url)
				||null==aliConfigUrl
				||"".equals(aliConfigUrl)
				||!GlobalInfo.isOnline
				)
		{
			new MessageBox(Language.apply("请检查支付宝支付配置，并保证当前系统处于联网状态"));
			return null;
		}
		//获取商户号和商户密钥
		String partnerNo = configMap.get("partnerNo").toString().trim();
		String partnerKey = configMap.get("partnerKey").toString().trim();
		String agentId = configMap.get("agentId").toString().trim();
		JSONObject extendParams = new JSONObject();
		extendParams.put("MACHINE_ID", saleBS.saleHead.syjh);
		if(!"".equals(agentId)&&null!=agentId)
		{
			extendParams.put("AGENT_ID", agentId);
		}
		else
		{
			extendParams.put("AGENT_ID", "369698a1");
		}
		extendParams.put("STORE_ID", GlobalInfo.sysPara.mktcode);
		//0 为支付宝商户 1为现在商户
		extendParams.put("STORE_TYPE", "1");
		extendParams.put("TERMINAL_ID", saleBS.saleHead.syjh+GlobalInfo.sysPara.mktcode);
		 
		
		//System.out.println(extendParams.toString());
		if (SellType.ISSALE(saleBS.saletype))
		{
			int choice = -1;
			Vector v = new Vector();
			
			String[] line = readZfbIni();
			
			if(line == null)
			{
				//组装MutiSelectForm显示数据
				
				v.add(new String[]{"0","条码支付"});
				v.add(new String[]{"1","扫码支付"});
				v.add(new String[]{"2","声波支付"});
				v.add(new String[]{"3","查询支付"});
				

				String strmsg = Language.apply("请输入序号");
				MutiSelectForm msf = new MutiSelectForm();
				choice = msf.open(strmsg, new String[] { Language.apply("序号"), Language.apply("交易方式")}, new int[] { 80, 550}, v, true, 700, 400, 673, 285, false, false);
				
			}
			else
			{
				if(new MessageBox("本地有未成功支付宝付款交易，是否调用查询支付？\n按数字键1确认，按数字键2退出", null, true).verify() == GlobalVar.Key1)
				{
					if(line[0].equals(money))
					{
						choice = 3;
					}
					else
					{
						new MessageBox("输入金额与失败单据金额不一致！");
						return null;
					}
				}
				else
				{
					return null;
				}
			}
			
			
			if("".equals(String.valueOf(money))||null == String.valueOf(money))
			{
				new MessageBox(Language.apply("金额不能为空"));
				return null;
			}
			if(choice == -1)
			{
				return null;
			}
			//执行二维码支付
			else if(choice == 1&&SellType.ISSALE(saleBS.saletype))
			{
				
				PosLog.getLog(getClass()).info("partnerKey");
				String bearXml = aliPayService.advancePay(partnerNo,partnerKey,""
						,String.valueOf(money),url,extendParams.toString());
				//获得阿里接口返回值
				HashMap map = parseXml.domParseXml(bearXml);
				String content = map.get("qr_code").toString(); 
				String out_trade_no = map.get("out_trade_no").toString();
				String text ="请扫二维码";
				String msgString ="请确认二维码扫描是否完成?\n 按数字键1确认，按数字键2退出";
				int msg = -1;
				
				if(null !=content&&!("").equals(content))
				{

					//查询交易是否成功
					while(true)
					{
						//获取二维码
						msg = QrcodeDisplay.display(content,text,10,msgString);
						

						//本地写入支付宝数据
						writeZfbIni(money);
						
						if(msg == GlobalVar.Key1)
						{
							
							HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,out_trade_no, "",url));
							if(!"TRADE_SUCCESS".equals(queryMap.get("trade_status")))
							{
								StringBuffer msgStr = new StringBuffer();
								msgStr.append("商户单号");
								msgStr.append(out_trade_no);
								msgStr.append("\n");
								msgStr.append("并非交易成功状态");
								msgStr.append("\n");
								msgStr.append("按数字键1确认，按数字键2退出");
								msgString = msgStr.toString();
								continue;
							}
							new MessageBox(Language.apply("用户向支付宝支付完成"));
							//给小票头的商户订单号赋值
							if (!createSalePay(money)) 
							{
//								删除支付宝数据
								deleteZfbIni();
								return null;
							}
							salepay.payno = out_trade_no;
							
//							删除支付宝数据
							deleteZfbIni();
							
							return salepay;
						} 
						else if(msg == GlobalVar.Key2)
						{
							return null;
						}
						else if(msg == 999)
						{
							new MessageBox(Language.apply("无法使用客显屏显示二维码，请联系管理员或者选着其他方式付款"));
							return null;
						}
						
					}
					
				}
				else
				{
					new MessageBox(Language.apply("无法使用二维码，请联系管理员或者选着其他方式付款"));
					return null;
				}
				//二维码支付end
			}
			//条码支付begin
			else if(choice == 0  && SellType.ISSALE(saleBS.saletype))
			{
				StringBuffer req = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入条码"), Language.apply("条码"), Language.apply("请扫描客户手机条码或者输入条码号"), req, 0, 0, false,
													TextBox.IntegerInput);
				if(done)
				{
//					本地写入支付宝数据
					writeZfbIni(money);
					
					String tmCode = req.toString();
					String  bearXml= aliPayService.unifiedPay(partnerNo,partnerKey,"",tmCode,String.valueOf(money),url,extendParams.toString());
					
					
					//获得阿里接口返回值
					HashMap map = parseXml.domParseXml(bearXml);
					String out_trade_no = map.get("out_trade_no").toString();
					if(!"ORDER_SUCCESS_PAY_SUCCESS".equals(map.get("result_code")))
					{
						//查询交易是否成功
						while(true)
						{
							codeMsg = new MessageBox(text, null, true);
							
							if(codeMsg.verify() == GlobalVar.Key1)
							{
								HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,out_trade_no, "",url));
								if(!"TRADE_SUCCESS".equals(queryMap.get("trade_status")))
								{
									StringBuffer msgStr = new StringBuffer();
									if(null == out_trade_no)
									{
										msgStr.append("无效商户单号");
										msgStr.append(tmCode);
										msgStr.append("\n");
									}
									else
									{
										msgStr.append("商户单号");
										msgStr.append(out_trade_no);
										msgStr.append("\n");
										msgStr.append("并非交易成功状态");
										msgStr.append("\n");
									}
									msgStr.append("按数字键1确认，按数字键2退出");
									text = msgStr.toString();
									continue;
								}
								new MessageBox(Language.apply("用户向支付宝支付完成"));
								//给小票头的商户订单号赋值
								if (!createSalePay(money))
								{
									//删除支付宝数据
									deleteZfbIni(); 
									return null;
								}
								salepay.payno = out_trade_no;
								
//								删除支付宝数据
								deleteZfbIni();
								
								return salepay;
							}
							else if(codeMsg.verify() == GlobalVar.Key2)
							{
								return null;
							}
							
						}
					}
					else
					{
						//给小票头的商户订单号赋值
						if (!createSalePay(money)) return null;
						salepay.payno = out_trade_no;
						new MessageBox(Language.apply("用户向支付宝支付完成"));
						
//						删除支付宝数据
						deleteZfbIni();
						
						return salepay;
					}
				}
				else
				{
					return null;
				}
				//条码支付end
			}
			
			//声波支付begin
			else if(choice == 2  && SellType.ISSALE(saleBS.saletype))
			{
				try 
				{
//					本地写入支付宝数据
					writeZfbIni(money);
					
					SonicWaveNFC.INSTANCE.create();
					soundBox = new ProgressBox();
					soundBox.setText("正在接收声波，请等待...");
					if("TERMINATED".equals(sonicWave.getState().toString()))
					{
						sonicWave = new SonicWave();
						sonicWave.flag = true;
					}
					if(!"TERMINATED".equals(sonicWave.getState().toString()))
					{
						sonicWave.start();
					}
					else
					{
						soundBox.close();
						new MessageBox("启动声波控件失败，请联系管理员");
//						删除支付宝数据
						deleteZfbIni();
						return null;
					}
					
					while(sonicWave.flag)
					{
						Thread.sleep(1000);
						SonicWaveNFC.INSTANCE.destroy();
					}
					String  bearXml= aliPayService.soundWavePay(partnerNo,partnerKey,"",sonicWave.tempId,String.valueOf(money),url,extendParams.toString());
					if("".equals(sonicWave.tempId))
					{
						soundBox.close();
						new MessageBox("没有获取到客户的动态ID，请重试");
//						删除支付宝数据
						deleteZfbIni();
						return null;
					}
					//获得阿里接口返回值
					HashMap map = parseXml.domParseXml(bearXml);
					String out_trade_no = (String)map.get("out_trade_no");
					if("F".equals(map.get("is_success")))
					{
						soundBox.close();
						new MessageBox("支付宝交易失败，请重新尝试或者换其他支付方式");
//						删除支付宝数据
						deleteZfbIni();
						return null;
					}
				
					if(!"ORDER_SUCCESS_PAY_SUCCESS".equals(map.get("result_code")))
					{
						soundBox.close();
						//查询交易是否成功
						while(true)
						{
							codeMsg = new MessageBox(text, null, true);
							
							if(codeMsg.verify() == GlobalVar.Key1)
							{
								HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,out_trade_no, "",url));
								if(!"TRADE_SUCCESS".equals(queryMap.get("trade_status")))
								{
									StringBuffer msgStr = new StringBuffer();
									if(null == out_trade_no)
									{
										msgStr.append("无效商户单号");
										msgStr.append(out_trade_no);
										msgStr.append("\n");
									}
									else
									{
										msgStr.append("商户单号");
										msgStr.append(out_trade_no);
										msgStr.append("\n");
										msgStr.append("并非交易成功状态");
										msgStr.append("\n");
									}
									msgStr.append("按数字键1确认，按数字键2退出");
									text = msgStr.toString();
									continue;
								}
								new MessageBox(Language.apply("用户向支付宝支付完成"));
								//给小票头的商户订单号赋值
								if (!createSalePay(money))
								{
//									删除支付宝数据
									deleteZfbIni();
									return null;
								}
								salepay.payno = out_trade_no;
								
//								删除支付宝数据
								deleteZfbIni();
								
								return salepay;
							}
							else if(codeMsg.verify() == GlobalVar.Key2)
							{
								return null;
							}
						}
					}
					else
					{
						soundBox.close();
						//给小票头的商户订单号赋值
						if (!createSalePay(money))
						{
//							删除支付宝数据
							deleteZfbIni();
							return null;
						}
						salepay.payno = out_trade_no;
						new MessageBox("支付宝交易完成");
						
//						删除支付宝数据
						deleteZfbIni();
						
						return salepay;
					}
				} 
				catch (Exception e) 
				{
//					删除支付宝数据
					deleteZfbIni();
					
					soundBox.close();
					new MessageBox("支付宝声波支付异常");
					e.printStackTrace();
				}
				//声波支付END
			}
			
			//查询支付或补单begin
			else if(choice == 3  && SellType.ISSALE(saleBS.saletype))
			{
				StringBuffer shdnh = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("商户订单号"), Language.apply("商户订单号"), Language.apply("输入商户订单号"), shdnh, 0, 0, false,
													TextBox.IntegerInput);
													
					// 查询订单是否已存在
					
					boolean selectFlag = netService.selectOutId(shdnh.toString());

					if (done && selectFlag) 
					{
				
					//访问阿里查询接口
					HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,shdnh.toString(), "",url));
					if(!"TRADE_SUCCESS".equals(queryMap.get("trade_status")))
					{
						if(null!=queryMap.get("detail_error_des"))
						{
							new MessageBox(Language.apply("该"+shdnh+"商户订单号"+queryMap.get("detail_error_des")));
							return null;
						}
						else
						{
							new MessageBox(Language.apply("该"+shdnh+"商户订单号"+"并非交易成功状态"));
							return null;
						}
					}
					else
					{
						if (!createSalePay(money)) return null;
						salepay.payno = shdnh.toString();
						new MessageBox(Language.apply("用户向支付宝支付完成"));
						
//						删除支付宝数据
						deleteZfbIni();
						
						return salepay;
					}
				}
				else
				{
//					删除支付宝数据
					deleteZfbIni();
					return null;
				}
			}
			//查询支付或补单end*/
			
			//删除支付宝数据
			deleteZfbIni();
		}
		//支付宝退货begin
		else if(SellType.ISBACK(saleBS.saletype))
		{ 
			//退货前线先查询交易单号的状态
			String outTradeNo ="";
			StringBuffer shdnh = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入商户订单号"), Language.apply("商户订单号"), Language.apply("请输入商户订单号"), shdnh, 0, 0, false,
												TextBox.IntegerInput);
			//访问阿里查询接口
			outTradeNo = shdnh.toString();
			if(done)
			{
				HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,outTradeNo, "",url));
				//交易单号处于交易成功状态才允许退货
				if("TRADE_SUCCESS".equals(queryMap.get("trade_status")))
				{
					//退货接口
					if(!aliPayService.sellBack(partnerNo,partnerKey,outTradeNo,String.valueOf(money),url))
					{
						new MessageBox(Language.apply("该"+outTradeNo+"商户订单号"+"支付宝退款异常，请选择 其他方式退款"));
						return null;
					}
					else
					{
						new MessageBox(Language.apply("支付宝退款完成"));
						if (!createSalePay(money)) return null;
						salepay.payno = shdnh.toString();
						return salepay;
					}
				}
				else
				{
					new MessageBox(Language.apply("该"+outTradeNo+"商户订单号"+"并非交易成功状态无法退货请选择其他退货方式"));
					return null;
				}
			}
		}
		
		} 
		catch (Exception e) 
		{
			new MessageBox("支付宝交易异常");
			PosLog.getLog(getClass()).info(e);
			return null;
		}
		return null;

	}

	//撤销
	public boolean cancelPay()
	{
	try 
	{
		Map mapN = new HashMap();
		//获取请求地址
		mapN=parseIni.Parse();
		String url = mapN.get("aliPayUrl").toString();
		String aliConfigUrl = mapN.get("aliConfigUrl").toString();
		//Http获取Ali支付配置
		HashMap configMap = aliPayService.aliConfig(aliConfigUrl);
		if(null == configMap
				||null==url
				||"".equals(url)
				||null==aliConfigUrl
				||"".equals(aliConfigUrl)
				||!GlobalInfo.isOnline
				)
		{
			new MessageBox(Language.apply("请检查支付宝支付配置，并保证当前系统处于联网状态"));
			return false;
		}
		//获取商户号和商户密钥
		String partnerNo = configMap.get("partnerNo").toString().trim();
		String partnerKey = configMap.get("partnerKey").toString().trim();
		//访问阿里查询接口
		String outTradeNo = salepay.payno;
		if(true)
		{
			HashMap queryMap = parseXml.domParseXml(aliPayService.query(partnerNo,partnerKey,outTradeNo, "",url));
			//交易单号处于交易成功状态才允许退货
			if("TRADE_SUCCESS".equals(queryMap.get("trade_status")))
			{
				//撤销接口
				if(!aliPayService.sellcancel(partnerNo,partnerKey,outTradeNo,String.valueOf(salepay.je),url))
				{
					new MessageBox(Language.apply("该"+outTradeNo+"商户订单号"+"支付宝退款异常，请选择 其他方式退款"));
					return false;
				}
				new MessageBox(Language.apply("支付宝撤销完成"));
				return true;
			}
			else
			{
				new MessageBox(Language.apply("该"+outTradeNo+"商户订单号"+"并非交易成功状态无法退货请选择其他退货方式"));
				return false;
			}
		}
		return false;
		} 
		catch (Exception e) 
		{
			new MessageBox("支付宝退款异常");
			PosLog.getLog(getClass()).info(e);
			return false;
			
		}
	} 

	public boolean collectAccountPay()
	{
		
		return true;
	}
	public boolean collectAccountClear()
	{
		// 删除相应的冲正记录
		return true;
	}
	
	public String GetZfbIniFile()
	{
		return ConfigClass.LocalDBPath + "/ZfbBat.ini";
	}
	
	public void writeZfbIni(String inputstr)
	{
		PrintWriter pw = null;
		try
		{
			pw = CommonMethod.writeFile(GetZfbIniFile());
			if (pw != null)
			{
				pw.println(inputstr);
				pw.flush();
			}	
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
			}
		}				
	}
	
	public String[] readZfbIni()
	{
		BufferedReader br = null;
		try
		{

			File file = new File(GetZfbIniFile());

			if (file.exists())
			{
				if ((br = CommonMethod.readFileGB2312(GetZfbIniFile())) == null)
				{
					new MessageBox("读取支付宝数据信息失败！", null, false);
					
					return null;
				}
				
				String line = null;
				line = br.readLine();
				
				if (line == null || line.length() < 0)
				{
					new MessageBox("支付宝数据信息错误!");
					return null;
				}				
				String[] str = line.split(",");
				return str;
			}
			
			return null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
				
			new MessageBox("读取支付宝数据异常", null, false);
				
			return null;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean deleteZfbIni()
	{
		try
		{

			File file = new File(GetZfbIniFile());

			if (file.exists())
			{
				file.delete();
				if (file.exists())
				{
					new MessageBox(Language.apply("支付宝数据文件没有被删除,请检查磁盘!"));

					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			new MessageBox(Language.apply("删除支付宝数据文件失败!\n\n") + e.getMessage());

			return false;
		}
	}
}


