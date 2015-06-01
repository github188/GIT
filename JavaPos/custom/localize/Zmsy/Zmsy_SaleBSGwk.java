package custom.localize.Zmsy;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Zmjc.NationalityDef;
import custom.localize.Zmjc.Zmjc_SaleBS;

public class Zmsy_SaleBSGwk extends Zmjc_SaleBS
{
	//GlobalInfo.syjDef.priv第一个字符为Y 就是免税 N就是有税	
	
	/**
	 * 购物卡信息
	 */
	protected GwkDef gwk;
	protected boolean isUseGwkZkl = false;//是否使用购物卡折扣率
	protected double flightsRows = 0;//POSMGR里的最新航班行数
	
	protected Vector vecZJType = new Vector();
	protected Vector vecNational = new Vector();
	protected Vector vecTHPlace = new Vector();
	protected Vector vecFlights = new Vector();
	
	protected Zmsy_AccessBaseDB baseDB = (Zmsy_AccessBaseDB)AccessBaseDB.getDefault();
	protected Zmsy_AccessDayDB dayDB = (Zmsy_AccessDayDB)AccessDayDB.getDefault();
	protected Zmsy_AccessLocalDB localDB = (Zmsy_AccessLocalDB)AccessLocalDB.getDefault();
	protected Zmsy_NetService netservice= (Zmsy_NetService)NetService.getDefault();
	protected Zmsy_DataService dataservice = (Zmsy_DataService)DataService.getDefault();

	public GwkDef getGwk()
	{
		return gwk;
	}
	
	public void initSellData()
    {
		try
		{
			super.initSellData();
			initGWK();	
			if (checkFreeTaxSYJ())
			{
				saleHead.str8="YN";//是否免税款机
			}
			else
			{
				saleHead.str8="NN";
			}
			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
			
    }
	

	// 设置界面信息
	public void setInfoGUI()
	{
		try
		{
			super.setInfoGUI();
			saleEvent.setVIPInfo(getVipInfoLabel());
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		
	}
	
	//清除航班信息
	public void clearFlights()
	{
		if (vecFlights!=null) 
		{
			vecFlights.removeAllElements();
			PosLog.getLog(this.getClass().getSimpleName()).info("clearFlights() vecFlights.removeAllElements");
		}
		else
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("clearFlights() vecFlights=null");
		}
	}
	
	public void setFlightsRows(double rows)
	{
		flightsRows = rows;
	}
	
	public boolean getNetMemoInfo()
	{
		try
		{
			//netservice.getZJType();
			//netservice.getTourist_THPlace();
			
			if (vecZJType==null || vecZJType.size()<=0)
			{//只读一次				
				if (!getZJType(vecZJType))
				{
					new MessageBox("【证件类型】读取失败!");
				}
			}
			
			
			if (vecNational==null || vecNational.size()<=0)
			{
				if (!getNationality(vecNational))
				{
					new MessageBox("【国籍】读取失败!");
				}
			}
			
			
			if (vecTHPlace==null || vecTHPlace.size()<=0)
			{
				if (!getThplace(vecTHPlace))
				{
					new MessageBox("【提货地点】读取失败!");
				}
			}
			
			if (vecFlights==null || vecFlights.size()<=0)
			{
				if (!getFlights(vecFlights))
				{
					new MessageBox("【航班信息】读取失败!");
				}
			}
			
			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}
	
	//刷购物卡
	public void execCustomKey0(boolean keydownonsale)
	{
		try
		{
			/*GwkForm window = new GwkForm(this.saleEvent.saleBS,gwk);
	        window.open();*/
			/*ZJNOReadForm f = new ZJNOReadForm();
			f.open();*/
			/*GwkLimitInfoForm f = new GwkLimitInfoForm("title","test info","1,2,3,4,5|11,12,13,14,15");
			f.open();*/
			
			/*if (1==1)
			{//test
				RptKCForm kc = new RptKCForm(this);
				kc.open();
				
				return;
			}*/
			
			if (saleGoods.size()>0)
			{
				//录入商品之后 ，不允许刷购物卡
				new MessageBox("操作失败：请在扫商品之前录入证件号!");
				return;
			}
			initGWK();//每次弹框前,将清空购物卡信息(需要重新录入) wangyong by 2013.11.20 for 陈奕焕
			findGWK();
			saleEvent.setVIPInfo(getVipInfoLabel());//刷新界面会员控件值
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		
	}
			
	/**
	 * 是否使用购物卡折扣
	 * @param zkl 当前购物卡折扣
	 */
	public void selectUseZkl(double zkl)
	{
		try
		{
			//zkl=0.8;
			PosLog.getLog(this.getClass().getSimpleName()).info("当前购物卡折扣为【" + ManipulatePrecision.doubleToString(zkl) + "】");
			if (zkl<=0 || zkl>=1) return;
			
			int retMessage = new MessageBox("当前购物卡折扣为【" + ManipulatePrecision.doubleToString(zkl*10) + "】折\n\n 是否使用此折扣？", null, true).verify();

			//
			if (retMessage == GlobalVar.Key1)
			{
				isUseGwkZkl = true;//使用卡折扣率
				gwk.str4 = "Y";
				PosLog.getLog(this.getClass().getSimpleName()).info("是否使用购物卡折扣？收银员选择[是]");
			}
			else
			{
				isUseGwkZkl = false;//不使用卡折扣率(则使用手工券)
				gwk.str4 = "N";
				PosLog.getLog(this.getClass().getSimpleName()).info("是否使用购物卡折扣？收银员选择[否]");
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}
	
	public boolean findGWK()
	{
		try
		{
			/*//选择证件类型
			if (!chooseGwkZJType()) return;
			
			//刷卡
			StringBuffer gwkNO = new StringBuffer();
			// 输入顾客卡号
			TextBox txt = new TextBox();
			if (!txt.open("请刷会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", gwkNO, 0, 0, false, TextBox.MsrKeyInput)) { return; }

			String track2 = txt.Track2;
			
			//查找购物卡
			gwk.code = track2;
			gwk.passport = track2;
			if (!dataservice.findGwkInfo(gwk))
			{
				initGWK();
				new MessageBox("您的购物卡不存在或未生效!");
				return;
			}*/
			
			//检查航班信息是为最新
			checkFlightsRows();
			
			//读取证件相关信息
			if (!getNetMemoInfo()) return false;
			
			//修改购物卡 并刷卡
			if (!changeGWK())
			{
				initGWK();
				//new MessageBox("刷卡失败!");
				return false;
			}
			
			//当是免税款机时,判断相关
			if (checkFreeTaxSYJ())
			{
				if (gwk.ispdxe.equals("Y"))//是否控制限额
				{
					if (!gwk.status.equals("Y"))
					{
						initGWK();
						new MessageBox("您的购物卡消费次数已经用完!");
						return false;
					}
						
					String vip = "";
					if (gwk.str2!=null && gwk.str2.equalsIgnoreCase("Y"))
					{
						vip = "(超级VIP)";
					}
					String strCardInfo = "卡  号:" + gwk.zjlb + gwk.passport + vip + "  卡余额:" + ManipulatePrecision.doubleToString(gwk.xe) + "  卡面额:" + ManipulatePrecision.doubleToString(gwk.sxje) ;//+ "  可购补税商品件数:" + gwk.bsjs;
					String strTimes = "本年度已购物次数:" + String.valueOf(Convert.toInt(String.valueOf(gwk.num1))) + "  可购补税商品件数:" + String.valueOf(Convert.toInt(String.valueOf(gwk.bsjs)));
					GwkLimitInfoForm f = new GwkLimitInfoForm("购物卡相关信息", strCardInfo, strTimes, gwk.xgjs);
					f.open();
					f=null;
				}
			}
			
			//显示券余额
			if (gwk.qje>0)
			{
				new MessageBox("该券余额为：" + gwk.qje);
			}
				
			//送货提示
			if (!gwk.shts.equals("N"))
			{
				new MessageBox(gwk.shts);
			}
			
			//是否使用购物卡折扣
			selectUseZkl(gwk.zkl);
			
			//MGR里的最新航班行数
			setFlightsRows(gwk.num2);
			
			return true;
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
		}
	}
	
	protected void checkFlightsRows()
	{
		try
		{
			if (vecFlights==null) 
			{
				vecFlights = new Vector();
				return;
			}
			
			if (flightsRows>0 && vecFlights.size()>0 && flightsRows!=vecFlights.size())
			{		
				ProgressBox pb = null;
				try
				{
					pb = new ProgressBox();
					pb.setText("正在获取最新航班信息,请等待...");
				
					PosLog.getLog(this.getClass().getSimpleName()).info("开始下载最新航班信息[" + vecFlights.size() + "][ " + flightsRows + "]");
					DataService.getDefault().getNetMemoInfo();
					vecFlights.removeAllElements();
				}
				catch(Exception ex)
				{
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
				}
				finally
				{
					if (pb != null)
					{
						pb.close();
						pb = null;
					}
				}
				
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}
	

	/*//选择证件类型
	public boolean chooseGwkZJType()
	{
		try
		{
			//
			if (vecZJType == null || vecZJType.size()<=0)
			{
				new MessageBox("刷卡失败:证件类型读取失败!");
				return false;
			}
			
			Vector con = new Vector();
			ZJTypeDef zj;
			for (int i = 0; i < vecZJType.size(); i++)
			{
				zj = (ZJTypeDef) vecZJType.elementAt(i);
				if (zj==null)continue;

				con.add(new String[] { zj.zjid, zj.zjname });
			}
			String[] title = { "证件类型", "证件名称" };
			int[] width = { 100, 300 };

			int choice = new MutiSelectForm().open("请选择证件类型", title, width, con);
			if (choice == -1) return false;

			zj = ((ZJTypeDef) vecZJType.elementAt(choice));			
			gwk.ZJLB = zj.zjid;
			
			ZJNOReadForm f = new ZJNOReadForm(vecZJType, gwk);
			f.open();
			boolean blnRet = f.getIsRead();
			f=null;
			return blnRet;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("刷卡失败:证件类型读取异常!");
			return false;
		}
	}
	*/
	/**
	 * 清除购物卡信息
	 *
	 */
	public void initGWK()
	{
		gwk = new GwkDef();
		gwk.syjh = GlobalInfo.syjDef.syjh;
		gwk.syyh = GlobalInfo.posLogin.gh;
		gwk.fphm = saleHead.fphm;
		gwk.str4 = "N";
		saleHead.zmsy_gwk = gwk;
		isUseGwkZkl = false;
	}
	
	//选择即购即提
	public void selectJGJTMode()
	{
		try
		{
			//提示是否选择即购即提
			if (new MessageBox("是否即购即提？", null, true).verify() != GlobalVar.Key1)
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("未选择即购即提");
				saleHead.str8 = saleHead.str8.charAt(0) + "N";
				
			}
			else
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("选择了即购即提");
				saleHead.str8 = saleHead.str8.charAt(0) + "Y";
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public boolean changeGWK()
	{
		boolean isChangeOK=false;
		try
		{
			String strRetMsg="";
			
			/*//读取基础信息
    		getFlights(vecFlights);*/
    		
    		//读取当天最后一笔小票的航班号:客户要求默认加载上一笔的航班号
			//if (gwk.ljhb==null || gwk.ljhb.length()<=0) gwk.ljhb = getLastFlightNo();//去除此功能 for 小贺  BY 2013.9.27
			
			//选择即购即提 add by 2014.2.8
			selectJGJTMode();
			
    		//购物卡修改界面
			GwkForm f = new GwkForm(saleEvent.saleBS, gwk);
			f.setGwkBaseInfo(vecNational, vecTHPlace, vecZJType, vecFlights);
			f.open();
			isChangeOK = f.getIsChanageOK();
			strRetMsg  = f.getRetMsg();
			f.disposeForm();
			f=null;
			
			if (isChangeOK)
			{
				//修改完购物卡后,再次读取购物卡信息(获取最新信息)			
				if (!dataservice.findGwkInfo(gwk))
				{
					//购物卡查找失败
					isChangeOK = false;
					//卡信息保存成功以后，再调用刷卡接口时失败
					new MessageBox("卡信息保存成功以后，再调用刷卡接口时失败");
				}
				else
				{
					//记录购物卡信息到小票头
					recordGwkInfo();
				}
				//writeBrokenData();//写入断点保护
			}
			else
			{
				new MessageBox("刷卡失败:" + strRetMsg);
			}
				
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return isChangeOK;
	}
	
	public void recordGwkInfo()
	{
		try
		{
			//gwk.zkl=0.7;//0.9 test data
			
			String[] memo = new String[11];
			memo[0]=gwk.name;//0姓 名
			memo[1]=gwk.nation;//1国 籍(格式：中国)
			memo[2]=gwk.zjlb;//2证件类别
			memo[3]=gwk.passport;//3证件号码
			memo[4]=gwk.gklb;//4顾客类别（格式：离岛）
			memo[5]=gwk.ljrq + " " + gwk.ljsj;//5离境日期（格式：2013-08-15 10:47:27）
			memo[6]=gwk.thdd;//6提货地点（格式：三亚国内出发厅）
			memo[7]=gwk.ljhb;//7离境航班
			memo[8]=gwk.birth;//8出生日期（格式：1957-2-27）
			memo[9]=gwk.mobile;//9手机号码
			memo[10]="";//10 提货地点_简称(ThJC)
			
			//根据国籍ID 获取国籍NAME
			NationalityDef n;
			for (int i=0; i<vecNational.size(); i++)
			{
				n = (NationalityDef)vecNational.elementAt(i);
				if (n!=null && n.PCRCODE.equals(gwk.nation))
				{
					//gwk.nation = n.PCRCNAME;
					//saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SCNATIONALITY, n.PCRCNAME);
					memo[1]=n.PCRCNAME;//国 籍
					break;
				}
			}
			
			//根据ID获取离岛地点NAME
			THPlaceDef p;
			for (int i=0; i<vecTHPlace.size(); i++)
			{
				p = (THPlaceDef)vecTHPlace.elementAt(i);
				if (p!=null && p.thbillno.equals(gwk.thdd))
				{
					//gwk.thdd = p.thsp;
					//saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_THDD, p.thsp);
					memo[6]=p.thsp;//提货地点
					memo[10]=p.thjc;//提货地点简称
					break;
				}
			}
			
			//离岛类型
			if (gwk.gklb!=null)
			{
				if (gwk.gklb.equals(Zmsy_StatusType.ZMSY_GKTYPE_LJ))
				{
					//saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_GKLB, "离境");
					memo[4]="离境";
				}
				else if (gwk.gklb.equals(Zmsy_StatusType.ZMSY_GKTYPE_LD))
				{
					//saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_GKLB, "离岛");
					memo[4]="离岛";
				}
				else if (gwk.gklb.equals(Zmsy_StatusType.ZMSY_GKTYPE_BDLD))
				{
					//saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_GKLB, "本地离岛");
					memo[4]="离岛";//"本地离岛"修改为都打印"离岛" for 李会风 BY WANGYONG 2013.9.22
				}
				else
				{
					//saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_GKLB, gwk.gklb);
					memo[4]=gwk.gklb;
				}
			}
			else
			{
				memo[4]="";
			}
			
			
			/*//其它信息
			saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_BIRTHDAY, gwk.birth);//出生日期
			saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_FZJG, gwk.fzjg);//发证机关
			saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_ZJTYPE, gwk.zjlb);//证件类型
			saleEvent.saleBS.saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SY_PHONENUM, gwk.mobile);//手机号码
*/			
			//额外增加wangyong add by 2013.8.22
			StringBuffer sbInfo = new StringBuffer();
			for(int i=0; i<memo.length; i++)
			{
				sbInfo.append(memo[i]==null? "":memo[i]);
				if ( i < (memo.length-1) )
				{
					sbInfo.append("|");
				}
			}
			//用于小票打印 wangyong add by 2013.8.28
			saleHead.str9 = sbInfo.toString();
			saleHead.str6 = gwk.zjlb + gwk.passport;//购物卡号（证件类型+证件号）
			saleHead.str7 = gwk.sjcd;//税金承担(1为中免承担,2为顾客承担)
			
			saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SCNUMBER, gwk.ljhb);//存储到SALECUST表，用于取上一笔的航班号
			saleCust.custAddNode(Zmsy_CustInfoDef.CUST_SCOTHERNO, saleHead.str9);//保存顾客信息,后台需求 by 2014.01.22
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}
	
	public boolean getNationality(Vector vecNational)
	{
		try
		{
			return baseDB.getNationality(vecNational);
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}
	
	public boolean getThplace(Vector vecTHPlace)
	{
		try
		{
			return baseDB.getTourist_THPlace(vecTHPlace);
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}
	
	public boolean getZJType(Vector vecZJType)
	{
		try
		{
			return baseDB.getZJType(vecZJType);
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}
	
	public boolean getFlights(Vector vecFlights)
	{

		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText("正在读取航班信息,请等待...");
			/*if(!netservice.getFlights(vecFlights))
			 {
			 new MessageBox("实时航班信息获取失败!");
			 }
			 return true;
			 */
			return localDB.getFlights(vecFlights, "", false);

		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("航班信息获取失败：发生异常, " + ex.getMessage());
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}

		return false;
	}
	
	public int SendGWKInfoToHG(StringBuffer sbMsg)
	{
		try
		{
			if (checkFreeTaxSYJ())
			{		
				//根据GWK.STR1判断是否调用海关平台
				if (gwk!=null && gwk.str1!=null && gwk.str1.trim().equalsIgnoreCase("Y"))
				{
					PosLog.getLog(this.getClass().getSimpleName()).info("不调用海关平台 gwk.str1=【" + gwk.str1.trim() + "】");
					return -3;
				}		
			}
			else
			{
				//有税机器不调用海关接口
				PosLog.getLog(this.getClass().getSimpleName()).info("有税机器不调用海关接口syjDef.priv=【" + String.valueOf(GlobalInfo.syjDef.priv) + "】");
				return -3;
			}			
			
			//根据参数设置，是否调用海关平台
			if ((GlobalInfo.sysPara.gwkHGUrl == null ||
					GlobalInfo.sysPara.gwkHGUrl.trim().length()<=0) ||  
					(GlobalInfo.sysPara.gwkHGUrl != null && GlobalInfo.sysPara.gwkHGUrl.trim().charAt(0)=='N'))
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("参数不调用海关接口IEUrl(WW)=【" + String.valueOf(GlobalInfo.sysPara.gwkHGUrl).trim() + "】");
				return -3;
			}
			
			String strInValue = getZJLB(gwk.zjlb) + "," + gwk.passport;
			//strInValue = "01," + gwk.passport;//test data
			String strOutValue = "";
			
			PosLog.getLog(this.getClass().getSimpleName()).info("海关webservice请求 strInValue=[" + String.valueOf(strInValue) + "].");
			strOutValue = WebServiceHG.getInformation(strInValue);
			PosLog.getLog(this.getClass().getSimpleName()).info("海关webservice返回 strOutValue=[" + String.valueOf(strOutValue) + "].");
			
			if (strOutValue==null) strOutValue="";
			String[] arrResult = strOutValue.split(",");
			if (arrResult.length<2)
			{
				sbMsg.append("海关平台返回结果不规范，\n返回值为【" + strOutValue + "】");
				return -2;
			}
			
			if (!arrResult[0].toString().equalsIgnoreCase("Y"))
			{
				sbMsg.append("调用海关平台失败，不能购买！" + arrResult[1].toString());
				return 0;
			}
			
			if (arrResult[0].toString().equalsIgnoreCase("Y") && arrResult[1].toString().equalsIgnoreCase("1"))//0表示不是违规旅客，1表示是违规旅客
			{
				sbMsg.append("该顾客卡是违规旅客，不能购买！");
				return 0;
			}
			
			if (arrResult.length>=6)
			{

				//改为先发送平台,再保存卡信息 for yans by 2013.9.20
				//将从海关获取的数据，发送给后台数据库
				if(netservice.sendHGPTInfo(gwk.code, Convert.toDouble(arrResult[2].toString()), Convert.toDouble(arrResult[3].toString()), Convert.toDouble(arrResult[4].toString()), arrResult[5].toString(), sbMsg))
				{
					//开始保存购物卡信息到后台DB
					return sendGwkInfo(gwk, sbMsg);
				}
				else
				{
					PosLog.getLog(this.getClass().getSimpleName()).info("将从海关获取的数据，发送给后台数据库时失败！");
					sbMsg.append("将从海关获取的数据，发送给后台数据库时失败！");
					return 0;
				}
				
				
				/*
				//开始保存购物卡信息到后台DB
				int intRet = sendGwkInfo(gwk, sbMsg);//dataservice.sendGwkInfo(gwk, sbMsg);
				if (intRet!=1) return intRet;
				
				//将从海关获取的数据，发送给后台数据库
				if(netservice.sendHGPTInfo(gwk.code, Convert.toDouble(arrResult[2].toString()), Convert.toDouble(arrResult[3].toString()), Convert.toDouble(arrResult[4].toString()), arrResult[5].toString(), sbMsg))
				{
					return 1;
				}
				else
				{
					PosLog.getLog(this.getClass().getSimpleName()).info("将从海关获取的数据，发送给后台数据库时失败！");
					sbMsg.append("将从海关获取的数据，发送给后台数据库时失败！");
					return 0;
				}*/
				
			}
			else
			{
				sbMsg.append("海关平台返回结果不规范，返回值为【" + strOutValue + "】");
				return -2;
			}
			
			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			sbMsg.append("调用海关平台时发生异常：\n" + ex.getMessage());
			return 999;
		}
		
		
	}
	
	public int sendGwkInfo(GwkDef gwk, StringBuffer sbMsg)
	{
		return dataservice.sendGwkInfo(gwk, sbMsg);
	}
	
	/**
	 * 将POSDB中的证件类型【新】转换为海关webservice的证件类型【旧】
	 * @param zjID 新证件类型ID
	 * @return 旧证件类型ID
	 */
	protected String getZJLB(String zjID)
	{
		String strRet=zjID;
		try
		{
			if (zjID==null || zjID.trim().length()<=0)
			{
				return Zmsy_StatusType.ZMSY_ZJTYPE_QT;
			}
			
			if (zjID.equalsIgnoreCase(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
			{
				strRet = "01";
			}
			else if (zjID.equalsIgnoreCase(Zmsy_StatusType.ZMSY_ZJTYPE_HZ))
			{
				strRet = "02";
			}
			else if (zjID.equalsIgnoreCase(Zmsy_StatusType.ZMSY_ZJTYPE_TXZ))
			{
				strRet = "03";
			}
			else if (zjID.equalsIgnoreCase(Zmsy_StatusType.ZMSY_ZJTYPE_TBZ))
			{
				strRet = "04";
			}
			else if (zjID.equalsIgnoreCase(Zmsy_StatusType.ZMSY_ZJTYPE_QT))
			{
				strRet = "05";
			}
		}
		catch(Exception ex)
		{			
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return strRet;
	}
	
	/**
	 * 检查当前款机是否为免税款机
	 * @return true免税款机 false有税款机
	 */
	public boolean checkFreeTaxSYJ()
	{
		////priv第一个字段为Y 就是免税款机 N就是有税款机
		if (GlobalInfo.syjDef.priv!=null && GlobalInfo.syjDef.priv.length()>0 && GlobalInfo.syjDef.priv.charAt(0)=='Y')//priv第一个字符为Y 就是免税 N就是有税
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 检查是否已经刷购物卡
	 * @return true已经刷卡 false未刷卡
	 */
	public boolean checkGwkInput()
	{
		if (this.gwk == null || this.gwk.zjlb==null || this.gwk.zjlb.length()<=0 || this.gwk.passport==null || this.gwk.passport.length()<=0)
		{
			//new MessageBox("市内店【免税收银机】必须先刷［购物卡］才能购物!");
			return false;
		}
		return true;
	}
	
	

	//在会员显示前加上当前证件标识
	public String getVipInfoLabel()
    {
		String gwkType = "【未录入证件】";
		try
		{
	    	if (gwk != null && gwk.zjlb!=null && gwk.zjlb.trim().length()>0)
	    	{
    			gwkType = "【" + gwk.zjlb + "证件】";
    			if (vecZJType.size()<=0) getZJType(vecZJType);
	    		if (vecZJType!=null && vecZJType.size()>0)
	    		{
	    			//gwkType = "";
	    			ZJTypeDef zj;
	    			for(int i=0; i<vecZJType.size(); i++)
	    			{
	    				zj = (ZJTypeDef) vecZJType.elementAt(i);
	    				if (zj==null)continue;
	    				if (zj.zjid.trim().equals(gwk.zjlb.trim()))
	    				{
	    					gwkType = "【" + zj.zjname +"】";//对应的证件类型名称
	    					break;
	    				}    				
	    			}
	    			if (gwkType.length()<=0) 
	    			{
	    				gwkType = "【未知证件】";
	    				PosLog.getLog(this.getClass().getSimpleName()).info("gwk.ZJLB=(" + gwk.zjlb.trim() + ")" + gwkType);
	    			}
	    		}
	    		
	    	}
	    	gwkType = gwkType + super.getVipInfoLabel();
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
    	return gwkType;
    }
	
	

	/**
	 * 录入提货单
	 * 
	 */
	public void inputTHD(String strSyjh, long lngFphm, String djlb)
	{
		try
		{
			//当是免税款机时，则录入提货单号
			//退货时，不录入
			if (saleHead.str8 == null || saleHead.str8.length()<=0 || saleHead.str8.charAt(0) != 'Y' || !SellType.ISSALE(djlb)) { return; }

			TextBox textBox = null;

			int flagClose = 1;
			String fphm = String.valueOf(lngFphm);
			String syjh = strSyjh;
			String workError = Zmsy_StatusType.ZMSY_WORK_THDRESON;//"753";

			while (flagClose != 0)
			{
				flagClose++;
				StringBuffer thdh = new StringBuffer("");//请在这里输入退货单号
				String title = "请录入提货单号:";
				String help = "";
				String isPASSWORD = ""; //当值为"PASSWORD"则属于PASSWORD模式 
				int modeType = TextBox.MsrKeyInput;
				textBox = new TextBox();

				//自动弹出提货单录入框
				if (textBox.open(title, isPASSWORD, help, thdh, modeType))
				{
					//未输入提货单号返回
					if (thdh.toString().trim().length()<=0) 
					{
						new MessageBox("提货单号不能为空!");
						continue;
					}

					Zmsy_DataService data = (Zmsy_DataService) DataService.getDefault();
					if (!data.checkTHDH(fphm, syjh, thdh.toString()))
					{
						new MessageBox("检查提货单号未通过!");
						continue;
					}

					//保存到本地提货单；将当前提货单号插入到DAY.DB3库的THD表
					if (!data.saveTHDH(fphm, syjh, thdh.toString()))
					{
						continue;
					}
					//录入提货单结束正常退出
					flagClose = 0;
				}

				if (flagClose > 3)
				{
					String[] exitTitle = { "编号", "退出原因" };
					String[] reason = new String[] { "未选择强行退出信息", "其它原因", "误操作", "打印测试", "未接打印机", "打印机损坏", "打印机未打印" };

					int retMessage = new MessageBox("提货单号还没有录入或录入不正确，是否强行退出？", null, true).verify();

					//选择是否强制退出
					if (retMessage == GlobalVar.Key1)
					{
						Vector v = new Vector();
						int[] width = { 100, 450 };
						String reasonMsg = "提货单号还没有录入{" + fphm + "}: ";

						for (int i = 1; i < reason.length; i++)
						{
							v.add(new String[] { String.valueOf(i), reason[i] });
						}

						int choice = new MutiSelectForm().open("请选择退出理由：", exitTitle, width, v);

						if (choice == -1)
						{
							reasonMsg = reasonMsg + reason[0];
						}
						else
						{
							reasonMsg = reasonMsg + reason[choice + 1];
						}
						//写入本地日志表
						AccessDayDB.getDefault().writeWorkLog(reasonMsg, workError);

						//强制退出
						flagClose = 0;
					}
					if (retMessage == GlobalVar.Key2)
					{
						//非强制退出，继续while TextBox OPEN 
					}
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		
	}
	
	/**
	 * 读取当天最后一笔小票的航班号
	 * @return SCNUMBER String
	 */
	public String getLastFlightNo()
	{
		return dayDB.getLastFlightNo();
	}
	
	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef saleGoodsDef = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);

		// 对不同的行业进行属性转换
		saleGoodsDef.num13 = goodsDef.num6; //商品重量 wangyong add by 2015.3.16 五限（奶粉重量等）
		return saleGoodsDef;
	}

}
