package com.efuture.javaPos.Global;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Struct.BuyerInfoDef;
import com.efuture.javaPos.Struct.CallInfoDef;
import com.efuture.javaPos.Struct.CustomerTypeDef;
import com.efuture.javaPos.Struct.DzcModeDef;
import com.efuture.javaPos.Struct.GlobalParaDef;
import com.efuture.javaPos.Struct.ManaFrameDef;
import com.efuture.javaPos.Struct.MemoInfoDef;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.NewsDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.PayinModeDef;
import com.efuture.javaPos.Struct.PaymentLimitDef;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.Struct.PrepareMoneyDef;
import com.efuture.javaPos.Struct.SyjGrangeDef;
import com.efuture.javaPos.Struct.SyjMainDef;
import com.efuture.javaPos.Struct.SyjStatusDef;
import com.efuture.javaPos.Struct.TasksDef;

//这个类主要用于,从网上下载数据,放入到本地数据库中
public class AccessLocalDB
{
	public static AccessLocalDB currentAccessLocalDB = null;

	public static AccessLocalDB getDefault()
	{
		if (AccessLocalDB.currentAccessLocalDB == null)
		{
			AccessLocalDB.currentAccessLocalDB = CustomLocalize.getDefault().createAccessLocalDB();
		}

		return AccessLocalDB.currentAccessLocalDB;
	}

	// 插入收银机信息
	public boolean writeSyjDef(String[] arg)
	{
		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地收银机表,请等待......"));

			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From SYJMAIN")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("SYJMAIN");
			if (ref == null || ref.length <= 0)
				ref = SyjMainDef.ref;

			String line = CommonMethod.getInsertSql("SYJMAIN", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			SyjMainDef syj = new SyjMainDef();

			if (!Transition.ConvertToObject(syj, arg)) { return false; }

			if (!GlobalInfo.localDB.setObjectToParam(syj, ref)) { return false; }

			if (!GlobalInfo.localDB.executeSql()) { return false; }

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地收银机表耗时: "));
		}
	}

	// 获得本地收银机信息
	public boolean readSyjDef(SyjMainDef SyjDef)
	{
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地收银机表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from SYJMAIN");

			if (rs == null)
				return false;

			if (rs.next() && GlobalInfo.localDB.getResultSetToObject(SyjDef))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地收银机表耗时: "));
		}
	}

	// 插入收银机参数
	public boolean writeSysPara(Vector v, boolean done)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地参数表,请等待......"));

			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (done)
			{
				if (!GlobalInfo.localDB.executeSql("Delete From SYSPARA")) { return false; }
			}

			if (!GlobalInfo.localDB.setSql("Insert into SYSPARA(code,name,value) values(?,?,?)")) { return false; }

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				GlobalInfo.localDB.paramSetString(1, row[0]);
				GlobalInfo.localDB.paramSetString(2, row[1]);
				GlobalInfo.localDB.paramSetString(3, row[2]);

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (GlobalInfo.localDB.commitTrans())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地参数表耗时: "));
		}
	}

	public boolean readPreMoneyDef(Vector v)
	{
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地备用金表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from PREPAREMONEY");

			if (rs == null) { return false; }

			while (rs.next())
			{
				PrepareMoneyDef pre = new PrepareMoneyDef();

				if (!GlobalInfo.localDB.getResultSetToObject(pre)) { return false; }

				v.add(pre);
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地备用金表耗时: "));
		}
	}

	public boolean writePreMoneyDef(Vector v)
	{
		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地备用金表,请等待......"));

			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From PREPAREMONEY")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("PREPAREMONEY");
			if (ref == null || ref.length <= 0)
				ref = PrepareMoneyDef.ref;

			String line = CommonMethod.getInsertSql("PREPAREMONEY", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			for (int i = 0; i < v.size(); i++)
			{
				PrepareMoneyDef preMoneyDef = (PrepareMoneyDef) v.elementAt(i);

				if (!GlobalInfo.localDB.setObjectToParam(preMoneyDef, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (GlobalInfo.localDB.commitTrans())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地备用金表耗时: "));
		}
	}

	// 读取系统参数
	public boolean readSysPara()
	{
		return readSysPara(true);
	}

	public boolean readSysPara(boolean dofinish)
	{
		ResultSet rs = null;

		// 赋缺省值
		GlobalInfo.sysPara = new GlobalParaDef();
		paraInitDefault();
		GlobalInfo.sysPara.paraInitDefault();

		try
		{
			PublicMethod.timeStart(Language.apply("正在读取本地参数表,请等待......"));

			// 将#开头的成员映射参数放在老参数之后
			rs = GlobalInfo.localDB.selectData("select code,value,name from SysPara order by length(code),code desc");

			if (rs == null) { return false; }

			while (rs.next())
			{
				paraConvertByCode(rs.getString(1), rs.getString(2));
				GlobalInfo.sysPara.paraConvertByCode(rs.getString(1), rs.getString(2), rs.getString(3));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地参数表耗时: "));
		}

		// 参数转换完毕处理
		return (dofinish ? paraInitFinish() : true);
	}

	public void paraInitDefault()
	{
		GlobalInfo.sysPara.isJavaPosManager = false;
		GlobalInfo.sysPara.mktname = "";
		GlobalInfo.sysPara.mktcode = "";
		GlobalInfo.sysPara.shopname = "";
		GlobalInfo.sysPara.mystorekey = "";
		GlobalInfo.sysPara.mystorecallid = "";
		GlobalInfo.sysPara.mystoreurl = "";
		GlobalInfo.sysPara.mystoreusestimes = 20;
		GlobalInfo.sysPara.textFilterChar = "";
		GlobalInfo.sysPara.quantityChange = "Y";
		GlobalInfo.sysPara.yyygz = 'Y';
		GlobalInfo.sysPara.isxh = 'Y';
		GlobalInfo.sysPara.xhisshowsl = 'Y';
		GlobalInfo.sysPara.customvsgoods = 'Y';
		GlobalInfo.sysPara.bdjyh = 'Y';
		GlobalInfo.sysPara.inputydoc = 'Y';
		GlobalInfo.sysPara.isconnect = 'Y';
		GlobalInfo.sysPara.querytime = 180;
		GlobalInfo.sysPara.maxxj = 0;
		GlobalInfo.sysPara.custinfo = "GNNN";
		GlobalInfo.sysPara.noshowcustinfogroup = "";
		GlobalInfo.sysPara.custinfocyclesel = "N";
		GlobalInfo.sysPara.rulepop = 'N';
		GlobalInfo.sysPara.localfind = 'N';
		GlobalInfo.sysPara.lxprint = 'N';
		GlobalInfo.sysPara.chglimit = 99;
		GlobalInfo.sysPara.zljd = 'Y';
		GlobalInfo.sysPara.cutzl = 'N';
		GlobalInfo.sysPara.autoopendrawer = 'Y';
		GlobalInfo.sysPara.isshowname = 'N';
		GlobalInfo.sysPara.codesale = 'Y';
		GlobalInfo.sysPara.dzccodesale = 'N';
		GlobalInfo.sysPara.isCalcAsPfj = 'Y';
		GlobalInfo.sysPara.forcebybarcode = 'N';
		GlobalInfo.sysPara.isnumandcode = 'Y';
		GlobalInfo.sysPara.cashsale = 'N';
		GlobalInfo.sysPara.downloadtime = 600;
		GlobalInfo.sysPara.downloadsaleupdate = 'Y';
		GlobalInfo.sysPara.fdprintyyy = 'N';
		GlobalInfo.sysPara.printyyygrouptype = '1';
		GlobalInfo.sysPara.fdprintyyytrack = '3';
		GlobalInfo.sysPara.saprintyyytrack = '3';
		GlobalInfo.sysPara.printyyhsequence = 'A';
		GlobalInfo.sysPara.isinputjkdate = 'N';
		GlobalInfo.sysPara.printjknum = 2;
		GlobalInfo.sysPara.isGetNetMaxJkdNo = 'N';
		GlobalInfo.sysPara.closedrawer = 'Y';
		GlobalInfo.sysPara.isthgh = "";
		GlobalInfo.sysPara.ismzkgh = "";
		GlobalInfo.sysPara.grantpwd = 'N';
		GlobalInfo.sysPara.grtpwdshow = 'N';
		GlobalInfo.sysPara.grantpasswordmsr = 'N';
		GlobalInfo.sysPara.payover = 'N';
		GlobalInfo.sysPara.payex = "";
		GlobalInfo.sysPara.fpjepayex = "";
		GlobalInfo.sysPara.paychgmore = 'N';
		GlobalInfo.sysPara.paychgyy = "";
		GlobalInfo.sysPara.msrspeed = 0;
		GlobalInfo.sysPara.icCardIsEnter = "Y";
		GlobalInfo.sysPara.cardsvrurl = "";
		GlobalInfo.sysPara.cardrealpay = 'N';
		GlobalInfo.sysPara.havebroken = 'Y';
		GlobalInfo.sysPara.checknetlogin = 'Y';
		GlobalInfo.sysPara.rebatepriacemode = 'Y';
		GlobalInfo.sysPara.paysummarymode = 'Y';
		GlobalInfo.sysPara.autojfexchange = 'N';
		GlobalInfo.sysPara.quitpwd = "9999";
		GlobalInfo.sysPara.thmzk = 'N';
		GlobalInfo.sysPara.num_down = 10;
		GlobalInfo.sysPara.validver = "";
		GlobalInfo.sysPara.printdelayline = 0;
		GlobalInfo.sysPara.printdelaysec = 0;
		GlobalInfo.sysPara.secMonitorPlayer = 'Y';
		GlobalInfo.sysPara.memcardsvrurl = "";
		GlobalInfo.sysPara.showerrorcmd = "";
		GlobalInfo.sysPara.cardpasswd = "N";
		GlobalInfo.sysPara.onlyUseBReturn = 'N';
		GlobalInfo.sysPara.exceptBReturnType = "";
		GlobalInfo.sysPara.mjPaymentRule = "";
		GlobalInfo.sysPara.refundByPos = 'N';
		GlobalInfo.sysPara.refundPayMode = "";
		GlobalInfo.sysPara.refundCouponPaymode = "";
		GlobalInfo.sysPara.acceptfjkrule = 'N';
		GlobalInfo.sysPara.serialmzkrule = "";
		GlobalInfo.sysPara.feechargelimit = 0;
		GlobalInfo.sysPara.havePayRule = 'N';
		GlobalInfo.sysPara.fjkyetype = "";
		GlobalInfo.sysPara.refundAllowBack = 'Y';
		GlobalInfo.sysPara.refundScale = 'Y';
		GlobalInfo.sysPara.customerbyconnect = 'N';
		GlobalInfo.sysPara.customerbysale = 'N';
		GlobalInfo.sysPara.custDisconnetNoPeriod = 'N';
		GlobalInfo.sysPara.apportMode = 'A';
		GlobalInfo.sysPara.loopInputPay = "Y";
		GlobalInfo.sysPara.printpaysummary = "";
		GlobalInfo.sysPara.isinputpremoney = "Y";
		GlobalInfo.sysPara.calcjfbyconnect = 'N';
		GlobalInfo.sysPara.sendhyjf = 'N';
		GlobalInfo.sysPara.sendsaletocrm = 'N';
		GlobalInfo.sysPara.mzkbillnum = 1;
		GlobalInfo.sysPara.salebillnum = 1;
		GlobalInfo.sysPara.isGoodsSryPrn = 'N';
		GlobalInfo.sysPara.allowbankselfsale = 'N';
		GlobalInfo.sysPara.isCheckReJe ='N';
		GlobalInfo.sysPara.calcfqbyreal = 'N';
		GlobalInfo.sysPara.calcmystorecouponbyreal = 'N';
		GlobalInfo.sysPara.issendmystorecouponwithhyk = 'Y';
		GlobalInfo.sysPara.printInfo1 = "";
		GlobalInfo.sysPara.printInfo2 = "";
		GlobalInfo.sysPara.setPriceBackStatus = 'Y';
		GlobalInfo.sysPara.isbackpricestatus = 'Y';
		GlobalInfo.sysPara.inputyyyfph = 'N';
		GlobalInfo.sysPara.batchtotalrebate = 'N';
		GlobalInfo.sysPara.CloseShowZkCompetence = 'N';
		GlobalInfo.sysPara.FirstClearLsZk = 'Y';
		GlobalInfo.sysPara.ispregetmsinfo = 'Y';
		GlobalInfo.sysPara.bankprint = 2;
		GlobalInfo.sysPara.displaybanktype = 'N';
		GlobalInfo.sysPara.paycodebanktype = "";
		GlobalInfo.sysPara.paycodebankform = "";
		GlobalInfo.sysPara.custompayobj = "";
		GlobalInfo.sysPara.findcustfjk = 'N';
		GlobalInfo.sysPara.payprecision = 'N';
		GlobalInfo.sysPara.goodsPrecision = 0;
		GlobalInfo.sysPara.ischoiceExit = 'N';
		GlobalInfo.sysPara.backRefundMSR = "N";
		GlobalInfo.sysPara.backgoodscodestyle = "B";
		GlobalInfo.sysPara.dzcbarcodestyle = 'Y';
		GlobalInfo.sysPara.saleTicketBarcodeStyle = "A";
		GlobalInfo.sysPara.validservicedate = "";
		GlobalInfo.sysPara.HCcontrol = "";
		GlobalInfo.sysPara.quickinputsku = 'Y';
		GlobalInfo.sysPara.goodsAmountInteger = 'N';
		GlobalInfo.sysPara.barcodeshowcode = 'N';
		GlobalInfo.sysPara.isinputnextgoods = 'N';
		GlobalInfo.sysPara.showgoodscode = 'Y';
		GlobalInfo.sysPara.isblankcheckgoods = 'Y';
		GlobalInfo.sysPara.checklength = 0;
		GlobalInfo.sysPara.checkgrouplen = 0;
		GlobalInfo.sysPara.ischeckadditive = 'N';
		GlobalInfo.sysPara.ischecksaveprint = 'N';
		GlobalInfo.sysPara.ischeckgz = 'N';
		GlobalInfo.sysPara.ischeckcode = 'N';
		GlobalInfo.sysPara.isaddwithzeroquantity = 'N';
		GlobalInfo.sysPara.ischeckquantity = 'N';
		GlobalInfo.sysPara.printpaymode = "";
		GlobalInfo.sysPara.debugtracelog = false;
		GlobalInfo.sysPara.issetprinter = 'N';
		GlobalInfo.sysPara.maxSaleGoodsCount = 200;
		GlobalInfo.sysPara.maxSalePayCount = 200;
		GlobalInfo.sysPara.maxSaleGoodsQuantity = 9999.99;
		GlobalInfo.sysPara.maxSaleGoodsMoney = 9999999.99;
		GlobalInfo.sysPara.maxSaleMoney = 9999999.99;
		GlobalInfo.sysPara.cmdCustList = null;
		GlobalInfo.sysPara.unionVIPMode = 'A';
		GlobalInfo.sysPara.fjkkhhl = "";
		GlobalInfo.sysPara.printInBill = 'N';
		GlobalInfo.sysPara.isHcPrintBill = 'Y';
		GlobalInfo.sysPara.removeGoodsModel = 'N';
		GlobalInfo.sysPara.removeGoodsMsg = 'N';
		GlobalInfo.sysPara.mzkStatistics = "";
		GlobalInfo.sysPara.couponRuleType = 'Y';
		GlobalInfo.sysPara.isMoneyInputDefault = 'Y';
		GlobalInfo.sysPara.MoneyInputDefaultPay = "";
		GlobalInfo.sysPara.overNightBegin = "";
		GlobalInfo.sysPara.overNightEnd = "";
		GlobalInfo.sysPara.overNightTime = "";
		GlobalInfo.sysPara.onlineGd = "N";
		GlobalInfo.sysPara.isHandVIPDiscount = 'N';
		GlobalInfo.sysPara.handVIPDiscount = 0;
		GlobalInfo.sysPara.searchPosAndCUST = "N";
		GlobalInfo.sysPara.jygs = "";
		GlobalInfo.sysPara.isMoreSelectQuerygoods = 'Y';
		GlobalInfo.sysPara.setTextLimit = "13";
		GlobalInfo.sysPara.useGoodsFrameMode = 'N';
		GlobalInfo.sysPara.allowGoodsFrameSale = 'Y';
		GlobalInfo.sysPara.checkGoodsDate = "";
		GlobalInfo.sysPara.checkGoodsCw = "";
		GlobalInfo.sysPara.isInputSaleAppend = 'N';
		GlobalInfo.sysPara.saleAppendSaleType = "";
		GlobalInfo.sysPara.saleAppendStatus = 'Y';
		GlobalInfo.sysPara.issaleby0 = 'N';
		GlobalInfo.sysPara.isGoodsMoney0 = 'N';
		GlobalInfo.sysPara.iscloseJkUI = 'Y';
		GlobalInfo.sysPara.isshowAllBcData = 'Y';
		GlobalInfo.sysPara.isusepaySelect = 'Y';
		GlobalInfo.sysPara.ClawBackCalcModel = 'A';
		GlobalInfo.sysPara.jdxxfkflag = 'N';
		GlobalInfo.sysPara.isInputPayMoney = 'N';
		GlobalInfo.sysPara.isinputjdfhdd = 'N';
		GlobalInfo.sysPara.lczcmaxmoney = 0;
		GlobalInfo.sysPara.isAutoLczc = 'N';
		GlobalInfo.sysPara.isAutoPayByLczc = 'N';
		GlobalInfo.sysPara.isReMSR = 'N';
		GlobalInfo.sysPara.iscardcode = 'N';
		GlobalInfo.sysPara.iscfgtable = 'N';
		GlobalInfo.sysPara.custommust = 'N';
		GlobalInfo.sysPara.isPrintGd = "Y";
		GlobalInfo.sysPara.gdTimes = 100;
		GlobalInfo.sysPara.exitsyswhenexistgd = 'Y';
		GlobalInfo.sysPara.nodeletepaycode = "";
		GlobalInfo.sysPara.noinputpaycode = "";
		GlobalInfo.sysPara.jdfhdd = "";
		GlobalInfo.sysPara.verifyDzcmname = "";
		GlobalInfo.sysPara.backgoodsminmoney = 0;
		GlobalInfo.sysPara.backgoodsmaxmoney = 0;

		GlobalInfo.sysPara.isEARNESTZT = 'N';
		GlobalInfo.sysPara.salepayDisplayRate = 'Y';
		GlobalInfo.sysPara.isRealPrintPOP = 'N';

		GlobalInfo.sysPara.withdrawauotbsmoney = "";
		GlobalInfo.sysPara.isForceRound = 'N';

		GlobalInfo.sysPara.lackpayfee = 0.01;
		GlobalInfo.sysPara.oldqpaydet = 'N';
		GlobalInfo.sysPara.printYXQ = 'N';
		GlobalInfo.sysPara.issendcrmnohyk = 'N';
		GlobalInfo.sysPara.mzkChkLength = 4;
		GlobalInfo.sysPara.prebarcode = "";
		GlobalInfo.sysPara.crmswitch = 'N';
		GlobalInfo.sysPara.vipzklimit = 0;

		GlobalInfo.sysPara.uploadOldInfo = '0';
		GlobalInfo.sysPara.isPreSale = 'Y';
		GlobalInfo.sysPara.cancelBankGrant = 'Y';
		GlobalInfo.sysPara.isVipMaxSlMsg = 'N';
		GlobalInfo.sysPara.isModifySaleFP = 'N';
		GlobalInfo.sysPara.elcScaleMode = 'N';
		GlobalInfo.sysPara.elcScalecycletime = 100;
		GlobalInfo.sysPara.isHbGoods = 'N';
		GlobalInfo.sysPara.localNotCheckMultiGz = "N";
		GlobalInfo.sysPara.isenablefq = 'N';
		GlobalInfo.sysPara.istsfq = 'N';
		GlobalInfo.sysPara.isBackPaymentCover = 'Y';
		GlobalInfo.sysPara.custConfig = 'Y';
		GlobalInfo.sysPara.isprinticandmzk = 'Y';
		GlobalInfo.sysPara.mdcode = "";
		GlobalInfo.sysPara.couponSaleType = "A";
		GlobalInfo.sysPara.defaultmzkpass = "";
		GlobalInfo.sysPara.backyyyh = 'N';
		GlobalInfo.sysPara.backinputmode = ' ';
		GlobalInfo.sysPara.backisinput = 'Y';
		GlobalInfo.sysPara.hyMaxdateMsg = "0";
		GlobalInfo.sysPara.yePrintPayCode = "";
		GlobalInfo.sysPara.bankCXMsg = "Y";
		GlobalInfo.sysPara.commMerchantId = "";
		GlobalInfo.sysPara.dosPosSvrAddress = "";
		GlobalInfo.sysPara.dosPosSvrCmdList = "";
		GlobalInfo.sysPara.bacthfdisvisible = "N";
		GlobalInfo.sysPara.batchfdrate = 100;
		GlobalInfo.sysPara.stampprefix = "";
		GlobalInfo.sysPara.localgoodsisdelgoods = 'N';
		GlobalInfo.sysPara.isUseNewMzkRange = 'N';
		GlobalInfo.sysPara.isNull = "N";
		GlobalInfo.sysPara.sendsaleissuccess = 'Y';
		GlobalInfo.sysPara.isDouble = 'N';
	}

	public void paraConvertByCode(String code, String value)
	{
		try
		{
			if (code.equals("20"))
			{
				GlobalInfo.sysPara.isJavaPosManager = true;

				return;
			}

			if (code.equals("13"))
			{
				GlobalInfo.sysPara.mktname = value.trim();

				return;
			}

			if (code.equals("14"))
			{
				GlobalInfo.sysPara.mktcode = value.trim();

				return;
			}

			if (code.equals("16"))
			{
				GlobalInfo.sysPara.shopname = value.trim();

				return;
			}
			if (code.equals("17"))
			{
				GlobalInfo.sysPara.mystorekey = value.trim();

				return;
			}
			if (code.equals("18"))
			{
				GlobalInfo.sysPara.mystorecallid = value.trim();

				return;
			}

			if (code.equals("19"))
			{
				GlobalInfo.sysPara.mystoreurl = value.trim();

				return;
			}

			if (code.equals("22") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.mystoreusestimes = Convert.toInt(values[0].trim());
				}
				if (values.length > 1)
				{
					GlobalInfo.sysPara.timeoutpresendcoupon = Convert.toInt(values[1].trim());

					if (GlobalInfo.sysPara.timeoutpresendcoupon == 0)
						GlobalInfo.sysPara.timeoutpresendcoupon = ConfigClass.ConnectTimeout;
				}
				if (values.length > 2)
				{
					GlobalInfo.sysPara.timeoutnocoupon = Convert.toInt(values[2].trim());
					if (GlobalInfo.sysPara.timeoutnocoupon == 0)
						GlobalInfo.sysPara.timeoutnocoupon = ConfigClass.ConnectTimeout;
				}
				if (values.length > 3)
				{
					GlobalInfo.sysPara.timeouthavecoupon = Convert.toInt(values[3].trim());
					if (GlobalInfo.sysPara.timeouthavecoupon == 0)
						GlobalInfo.sysPara.timeouthavecoupon = ConfigClass.ConnectTimeout;
				}
				return;
			}

			if (code.equals("23"))
			{
				GlobalInfo.sysPara.textFilterChar = value.trim();

				return;
			}
			if (code.equals("24"))
			{
				GlobalInfo.sysPara.quantityChange = value.trim();

				return;
			}
			if (code.equals("40") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.yyygz = value.charAt(0);

				return;
			}

			if (code.equals("41") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.isxh = values[0].charAt(0);
				}
				if (values.length > 1)
				{
					GlobalInfo.sysPara.xhisshowsl = values[1].charAt(0);
				}
				return;
			}

			if (code.equals("42") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.customvsgoods = value.charAt(0);
				if (value.length() > 1)
					GlobalInfo.sysPara.custommust = value.charAt(1);

				return;
			}

			if (code.equals("43") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.bdjyh = value.charAt(0);

				return;
			}

			if (code.equals("44"))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.inputydoc = values[0].charAt(0);
				}
				if (values.length > 1)
				{
					GlobalInfo.sysPara.backyyyh = values[1].charAt(0);
				}
				if (values.length > 2)
				{
					GlobalInfo.sysPara.backinputmode = values[2].charAt(0);
				}
				if (values.length > 3)
				{
					GlobalInfo.sysPara.backisinput = values[3].charAt(0);
				}
				return;
			}

			if (code.equals("45") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isconnect = value.charAt(0);

				return;
			}

			if (code.equals("48") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.querytime = Integer.parseInt(value);

				return;
			}

			if (code.equals("4C") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.maxxj = Double.parseDouble(value);

				return;
			}

			if (code.equals("4E"))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					// T:树的模式,G:普通模式
					if (values[0].indexOf("T") == 0)
					{
						GlobalInfo.sysPara.custinfo = values[0].trim();
					}
					else
					{
						GlobalInfo.sysPara.custinfo = "G" + values[0].trim();
					}
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.custinfocyclesel = values[1].trim();
				}

				if (values.length > 2)
				{
					GlobalInfo.sysPara.noshowcustinfogroup = values[2].trim();
				}

				return;
			}

			if (code.equals("4F") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.rulepop = value.charAt(0);

				return;
			}

			if (code.equals("4H") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.localfind = value.charAt(0);

				return;
			}

			if (code.equals("4I") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.lxprint = value.charAt(0);

				return;
			}

			if (code.equals("4J") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
					GlobalInfo.sysPara.chglimit = Double.parseDouble(values[0]);

				if (values.length > 1)
					GlobalInfo.sysPara.zljd = values[1].trim().charAt(0);

				if (values.length > 2)
					GlobalInfo.sysPara.cutzl = values[2].trim().charAt(0);
				return;
			}

			if (code.equals("4L") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.autoopendrawer = values[0].charAt(0);
				}
				if (value.length() > 1)
				{
					GlobalInfo.sysPara.isshowname = values[1].charAt(0);
				}

				return;
			}

			if (code.equals("4M") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.codesale = values[0].charAt(0);
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.dzccodesale = values[1].charAt(0);
				}

				if (values.length > 2)
				{
					GlobalInfo.sysPara.isCalcAsPfj = values[2].charAt(0);
				}

				if (values.length > 3)
				{
					GlobalInfo.sysPara.forcebybarcode = values[3].charAt(0);
				}

				if (values.length > 4)
				{
					GlobalInfo.sysPara.isnumandcode = values[4].charAt(0);
				}
				return;
			}

			if (code.equals("4N") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cashsale = value.charAt(0);

				return;
			}

			if (code.equals("4P") && CommonMethod.noEmpty(value))
			{
				String[] str = value.trim().split(",");

				if (str.length > 0)
					GlobalInfo.sysPara.downloadtime = Long.parseLong(str[0].trim());

				if (str.length > 1)
					GlobalInfo.sysPara.downloadsaleupdate = str[1].trim().charAt(0);

				return;
			}

			if (code.equals("4Q") && CommonMethod.noEmpty(value))
			{
				String[] print = value.trim().split(",");

				GlobalInfo.sysPara.fdprintyyy = print[0].trim().charAt(0);

				if (print.length > 1)
				{
					GlobalInfo.sysPara.printyyygrouptype = print[1].trim().charAt(0);
				}
				else
				{
					if (value.trim().length() > 1)
					{
						GlobalInfo.sysPara.printyyygrouptype = value.trim().charAt(1);
					}
				}

				if (print.length > 2)
				{
					GlobalInfo.sysPara.fdprintyyytrack = print[2].trim().charAt(0);
				}
				else
				{
					if (value.trim().length() > 2)
					{
						GlobalInfo.sysPara.fdprintyyytrack = value.trim().charAt(2);
					}
				}

				if (print.length > 3)
				{
					GlobalInfo.sysPara.printyyhsequence = print[3].trim().charAt(0);
				}
				else
				{
					if (value.trim().length() > 3)
					{
						GlobalInfo.sysPara.printyyhsequence = value.trim().charAt(3);
					}
				}

				return;
			}

			if (code.equals("4R") && CommonMethod.noEmpty(value))
			{
				String[] jkInfo = value.split(",");

				if (jkInfo.length > 0)
				{
					GlobalInfo.sysPara.isinputjkdate = jkInfo[0].charAt(0);
				}
				if (jkInfo.length > 1)
				{
					GlobalInfo.sysPara.printjknum = Integer.parseInt(jkInfo[1]);
				}
				if (jkInfo.length > 2)
				{
					GlobalInfo.sysPara.isGetNetMaxJkdNo = jkInfo[2].charAt(0);
				}
				if (jkInfo.length > 3)
				{
					GlobalInfo.sysPara.printJKList = jkInfo[3].charAt(0);
				}
				return;
			}

			if (code.equals("4T") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.closedrawer = value.charAt(0);

				return;
			}

			if (code.equals("MX"))
			{
				GlobalInfo.sysPara.isthgh = value.trim();

				return;
			}

			if (code.equals("MZ"))
			{
				GlobalInfo.sysPara.ismzkgh = value.trim();

				return;
			}

			if (code.equals("J1") && CommonMethod.noEmpty(value))
			{
				String[] pwdPara = value.split(",");

				if (pwdPara.length > 0)
				{
					GlobalInfo.sysPara.grantpwd = pwdPara[0].charAt(0);
				}

				if (pwdPara.length > 1)
				{
					GlobalInfo.sysPara.grtpwdshow = pwdPara[1].charAt(0);
				}

				if (pwdPara.length > 2)
				{
					GlobalInfo.sysPara.grantpasswordmsr = pwdPara[2].charAt(0);
				}

				return;
			}

			if (code.equals("J2") && CommonMethod.noEmpty(value))
			{
				String[] s = value.split("\\|");
				if (s.length > 0)
					GlobalInfo.sysPara.payover = s[0].charAt(0);
				if (s.length > 1)
					GlobalInfo.sysPara.payex = s[1];
				if (s.length > 2)
					GlobalInfo.sysPara.fpjepayex = s[2];
				return;
			}

			if (code.equals("J3") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.paychgmore = value.charAt(0);
				int pos = value.indexOf(",");
				if (pos >= 0 && pos + 1 < value.length())
					GlobalInfo.sysPara.paychgyy = value.substring(pos + 1);

				return;
			}

			if (code.equals("J4") && CommonMethod.noEmpty(value))
			{
				String[] values = value.split(",");
				if (values.length > 0)
					GlobalInfo.sysPara.msrspeed = Convert.toInt(values[0].trim());
				if (values.length > 1)
					GlobalInfo.sysPara.icCardIsEnter = values[1].trim();
				return;
			}

			if (code.equals("J5"))
			{
				GlobalInfo.sysPara.cardsvrurl = value.trim();

				return;
			}

			if (code.equals("J6") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cardrealpay = value.charAt(0);

				return;
			}

			if (code.equals("J7") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.havebroken = value.charAt(0);

				return;
			}

			if (code.equals("J8") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.checknetlogin = value.charAt(0);

				return;
			}

			if (code.equals("J9") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.rebatepriacemode = value.charAt(0);

				return;
			}

			if (code.equals("JA") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.paysummarymode = value.charAt(0);

				return;
			}

			if (code.equals("JB") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.autojfexchange = value.charAt(0);
			}

			if (code.equals("JC"))
			{
				GlobalInfo.sysPara.quitpwd = value.trim();

				return;
			}

			if (code.equals("JD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.thmzk = value.charAt(0);
				return;
			}

			if (code.equals("JE") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.num_down = Long.parseLong(value.trim());
				return;
			}

			if (code.equals("JF"))
			{
				GlobalInfo.sysPara.validver = value.trim();

				return;
			}

			if (code.equals("JG") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.iscardmsg = value.trim().charAt(0);
				return;
			}

			if (code.equals("JH") && CommonMethod.noEmpty(value))
			{
				String[] s = value.split(",");
				if (s.length > 0)
					GlobalInfo.sysPara.printdelayline = Integer.parseInt(s[0]);
				if (s.length > 1)
					GlobalInfo.sysPara.printdelaysec = Integer.parseInt(s[1]);
				return;
			}

			if (code.equals("JI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.secMonitorPlayer = value.trim().charAt(0);
				return;
			}

			if (code.equals("JJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.memcardsvrurl = value.trim();
				return;
			}

			if (code.equals("JK") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cardpasswd = value.trim();
				return;
			}

			if (code.equals("JL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.onlyUseBReturn = value.charAt(0);
				if (value.length() > 2)
					GlobalInfo.sysPara.exceptBReturnType = value.substring(2).trim();
				return;
			}

			if (code.equals("JM") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.acceptfjkrule = value.charAt(0);
				return;
			}

			if (code.equals("JN") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.mjPaymentRule = value.trim();
				return;
			}

			if (code.equals("JO") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.refundByPos = value.charAt(0);
				String row[] = value.split("\\|");
				if (value.length() > 1 && value.split("\\|").length > 1)
				{
					GlobalInfo.sysPara.refundPayMode = row[0].substring(1).trim();
					GlobalInfo.sysPara.refundCouponPaymode = row[1];
				}
				else if (value.length() > 1)
				{
					GlobalInfo.sysPara.refundCouponPaymode = GlobalInfo.sysPara.refundPayMode = value.substring(1).trim();
				}
				return;
			}

			if (code.equals("JP") && CommonMethod.noEmpty(value))
			{
				String[] lczc = value.split(",");
				if (lczc.length > 0)
					GlobalInfo.sysPara.lczcmaxmoney = Double.parseDouble(lczc[0]);
				if (lczc.length > 1)
					GlobalInfo.sysPara.isAutoLczc = lczc[1].charAt(0);
				if (lczc.length > 2)
					GlobalInfo.sysPara.isAutoPayByLczc = lczc[2].charAt(0);
				if (lczc.length > 3)
					GlobalInfo.sysPara.isReMSR = lczc[3].charAt(0);
				return;
			}

			if (code.equals("JQ") && CommonMethod.noEmpty(value))
			{
				String[] gz = value.split(",");
				if (gz.length > 0)
					GlobalInfo.sysPara.serialmzkrule = gz[0];
				if (gz.length > 1)
					GlobalInfo.sysPara.iscardcode = gz[1].charAt(0);

				return;
			}

			if (code.equals("JR") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.feechargelimit = Double.parseDouble(value);
				return;
			}

			if (code.equals("JS") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.havePayRule = value.charAt(0);
				return;
			}

			if (code.equals("JT") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.fjkyetype = value.trim();
				return;
			}

			if (code.equals("JU") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.refundAllowBack = value.charAt(0);

				if (value.split(",").length > 1)
				{
					GlobalInfo.sysPara.refundScale = value.split(",")[1].charAt(0);
				}
				return;
			}

			if (code.equals("JV") && CommonMethod.noEmpty(value))
			{
				String[] s = value.split(",");
				if (s.length > 0)
					GlobalInfo.sysPara.customerbyconnect = s[0].charAt(0);
				if (s.length > 1)
					GlobalInfo.sysPara.customerbysale = s[1].charAt(0);
				if (s.length > 2)
					GlobalInfo.sysPara.custDisconnetNoPeriod = s[2].charAt(0);
				return;
			}

			if (code.equals("JW") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.apportMode = value.charAt(0);
				return;
			}

			if (code.equals("JX") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.loopInputPay = value.trim();
				return;
			}

			if (code.equals("JY") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.printpaysummary = value.trim();
				return;
			}

			if (code.equals("JZ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isinputpremoney = value.trim();

				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.isinputpremoney = values[0].trim();
				}
				if (values.length > 1)
				{
					GlobalInfo.sysPara.isNull = values[1].trim();
				}
				return;
			}

			if (code.equals("O1") && CommonMethod.noEmpty(value))
			{
				String[] values = value.split(",");

				if (values.length >= 1)
				{
					GlobalInfo.sysPara.calcjfbyconnect = values[0].charAt(0);
				}

				if (values.length >= 2)
				{
					GlobalInfo.sysPara.sendhyjf = values[1].charAt(0);
				}

				return;
			}

			if (code.equals("O2") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.sendsaletocrm = value.charAt(0);
				return;
			}

			if (code.equals("O3") && CommonMethod.noEmpty(value))
			{
				String[] billnums = value.split(",");

				if (billnums.length > 0)
				{
					GlobalInfo.sysPara.mzkbillnum = Convert.toInt(billnums[0]);
				}

				if (billnums.length > 1)
				{
					GlobalInfo.sysPara.salebillnum = Convert.toInt(billnums[1]);
				}

				if (billnums.length > 2)
				{
					GlobalInfo.sysPara.isGoodsSryPrn = billnums[2].charAt(0);
				}

				return;
			}

			if (code.equals("O4") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");
				if (val.length > 0)	GlobalInfo.sysPara.allowbankselfsale = val[0].charAt(0);
				if (val.length > 1) GlobalInfo.sysPara.isCheckReJe = val[1].charAt(0);
				return;
			}

			if (code.equals("O5") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");

				if (val.length > 0)
					GlobalInfo.sysPara.calcfqbyreal = val[0].charAt(0);

				if (val.length > 1)
					GlobalInfo.sysPara.calcmystorecouponbyreal = val[1].charAt(0);

				if (val.length > 2)
					GlobalInfo.sysPara.issendmystorecouponwithhyk = val[2].charAt(0);

				return;

			}

			if (code.equals("O6") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.printInfo1 = value.trim();
				return;
			}

			if (code.equals("O7") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.printInfo2 = value.trim();
				return;
			}

			if (code.equals("O8") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");

				if (val.length > 0)
					GlobalInfo.sysPara.setPriceBackStatus = val[0].charAt(0);

				if (val.length > 1)
					GlobalInfo.sysPara.isbackpricestatus = val[1].charAt(0);

				return;
			}

			if (code.equals("O9") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.inputyyyfph = value.charAt(0);
				return;
			}

			if (code.equals("OA") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");

				if (val.length > 0)
					GlobalInfo.sysPara.batchtotalrebate = val[0].charAt(0);

				if (val.length > 1)
					GlobalInfo.sysPara.CloseShowZkCompetence = val[1].charAt(0);

				if (val.length > 2)
					GlobalInfo.sysPara.FirstClearLsZk = val[2].charAt(0);

				return;
			}

			if (code.equals("OB") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");

				if (val.length > 0)
					GlobalInfo.sysPara.bankprint = Integer.parseInt(val[0]);

				if (val.length > 1)
					GlobalInfo.sysPara.displaybanktype = val[1].charAt(0);

				if (val.length > 2)
					GlobalInfo.sysPara.paycodebanktype = val[2].trim();

				if (val.length > 3)
					GlobalInfo.sysPara.paycodebankform = val[3].trim();

				return;
			}

			if (code.equals("OC") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.custompayobj = value;
				return;
			}

			if (code.equals("OD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.findcustfjk = value.charAt(0);

				return;
			}

			if (code.equals("OE") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");

				if (val.length > 0)
					GlobalInfo.sysPara.payprecision = val[0].charAt(0);
				if (val.length > 1)
					GlobalInfo.sysPara.goodsPrecision = Convert.toDouble(val[1]);

				return;
			}

			if (code.equals("OF") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.ischoiceExit = value.charAt(0);

				return;
			}

			if (code.equals("OG") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");

				if (val.length > 0)
					GlobalInfo.sysPara.backRefundMSR = val[0].trim();
				if (val.length > 1)
					GlobalInfo.sysPara.backgoodscodestyle = val[1].trim();
				if (val.length > 2)
					GlobalInfo.sysPara.dzcbarcodestyle = val[2].trim().charAt(0);
				if (val.length > 3)
					GlobalInfo.sysPara.saleTicketBarcodeStyle = val[3].trim();
				return;
			}

			if (code.equals("OH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.validservicedate = value;

				return;
			}

			if (code.equals("OI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.HCcontrol = value;

				return;
			}

			if (code.equals("OJ") && CommonMethod.noEmpty(value))
			{
				String[] val = value.trim().split(",");

				if (val.length > 0)
					GlobalInfo.sysPara.quickinputsku = val[0].charAt(0);
				if (val.length > 1)
					GlobalInfo.sysPara.goodsAmountInteger = val[1].charAt(0);
				if (val.length > 2)
					GlobalInfo.sysPara.barcodeshowcode = val[2].charAt(0);
				if (val.length > 3)
					GlobalInfo.sysPara.isinputnextgoods = val[3].charAt(0);
				if (val.length > 4)
					GlobalInfo.sysPara.showgoodscode = val[4].charAt(0);
				return;
			}

			if (code.equals("OK") && CommonMethod.noEmpty(value))
			{
				String[] billnums = value.split(",");

				if (billnums.length > 0)
				{
					GlobalInfo.sysPara.isblankcheckgoods = billnums[0].trim().charAt(0);
				}

				if (billnums.length > 1)
				{
					GlobalInfo.sysPara.checklength = Convert.toInt(billnums[1]);
				}

				if (billnums.length > 2)
				{
					GlobalInfo.sysPara.checkgrouplen = Convert.toInt(billnums[2]);
				}

				if (billnums.length > 3)
				{
					GlobalInfo.sysPara.ischeckadditive = billnums[3].trim().charAt(0);
				}

				if (billnums.length > 4)
				{
					GlobalInfo.sysPara.ischecksaveprint = billnums[4].trim().charAt(0);
				}

				if (billnums.length > 5)
				{
					GlobalInfo.sysPara.ischeckgz = billnums[5].trim().charAt(0);
				}

				if (billnums.length > 6)
				{
					GlobalInfo.sysPara.ischeckcode = billnums[6].trim().charAt(0);
				}

				if (billnums.length > 7)
				{
					GlobalInfo.sysPara.ischeckquantity = billnums[7].trim().charAt(0);
				}

				if (billnums.length > 8)
				{
					GlobalInfo.sysPara.isaddwithzeroquantity = billnums[8].trim().charAt(0);
				}

				return;
			}

			if (code.equals("OL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.printpaymode = value;

				return;
			}

			if (code.equals("OM") && CommonMethod.noEmpty(value))
			{
				if (value.charAt(0) == 'Y')
					GlobalInfo.sysPara.debugtracelog = true;
				else
					GlobalInfo.sysPara.debugtracelog = false;

				return;
			}

			if (code.equals("ON") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.mzkStatistics = value;

				return;
			}

			if (code.equals("OO") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cmdCustList = value.trim();

				return;
			}

			if (code.equals("OP") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.unionVIPMode = value.trim().charAt(0);

				return;
			}

			if (code.equals("OY") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.issetprinter = value.charAt(0);

				return;
			}

			if (code.equals("OZ") && CommonMethod.noEmpty(value))
			{
				String[] maxSaleInfo = value.split(",");

				if (maxSaleInfo.length > 0)
				{
					GlobalInfo.sysPara.maxSaleGoodsCount = Convert.toInt(maxSaleInfo[0]);
				}

				if (maxSaleInfo.length > 1)
				{
					GlobalInfo.sysPara.maxSalePayCount = Convert.toInt(maxSaleInfo[1]);
				}

				if (maxSaleInfo.length > 2)
				{
					GlobalInfo.sysPara.maxSaleGoodsQuantity = Convert.toDouble(maxSaleInfo[2]);
				}

				if (maxSaleInfo.length > 3)
				{
					GlobalInfo.sysPara.maxSaleGoodsMoney = Convert.toDouble(maxSaleInfo[3]);
				}

				if (maxSaleInfo.length > 4)
				{
					GlobalInfo.sysPara.maxSaleMoney = Convert.toDouble(maxSaleInfo[4]);
				}

				return;
			}

			if (code.equals("P1") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.printInBill = values[0].charAt(0);
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.isHcPrintBill = values[1].charAt(0);
				}

				return;
			}

			if (code.equals("P2") && CommonMethod.noEmpty(value))
			{
				String[] s = value.split(",");

				if (s.length > 0)
					GlobalInfo.sysPara.removeGoodsModel = s[0].charAt(0);
				if (s.length > 1)
					GlobalInfo.sysPara.removeGoodsMsg = s[1].charAt(0);

				return;
			}

			if (code.equals("P3") && CommonMethod.noEmpty(value))
			{

				String[] moneyInputDefault = value.split("\\|");

				if (moneyInputDefault.length > 0)
				{
					GlobalInfo.sysPara.isMoneyInputDefault = moneyInputDefault[0].charAt(0);
				}

				if (moneyInputDefault.length > 1)
				{
					GlobalInfo.sysPara.MoneyInputDefaultPay = moneyInputDefault[1];
				}

				return;
			}

			if (code.equals("P4") && CommonMethod.noEmpty(value))
			{
				String[] gdPara = value.split(",");

				if (gdPara.length > 0)
				{
					GlobalInfo.sysPara.onlineGd = gdPara[0];
				}

				if (gdPara.length > 1)
				{
					GlobalInfo.sysPara.isPrintGd = gdPara[1];
				}

				if (gdPara.length > 2)
				{
					GlobalInfo.sysPara.gdTimes = Integer.parseInt(gdPara[2]);
				}

				if (gdPara.length > 3)
				{
					GlobalInfo.sysPara.exitsyswhenexistgd = gdPara[3].charAt(0);
				}

				return;
			}

			if (code.equals("P6") && CommonMethod.noEmpty(value))
			{
				String[] handVIP = value.split("\\|");

				if (handVIP.length > 0)
				{
					GlobalInfo.sysPara.isHandVIPDiscount = handVIP[0].charAt(0);
				}

				if (handVIP.length > 1)
				{
					GlobalInfo.sysPara.handVIPDiscount = Convert.toDouble(handVIP[1].toString().trim());
				}

				return;
			}

			if (code.equals("P7") && CommonMethod.noEmpty(value))
			{

				GlobalInfo.sysPara.searchPosAndCUST = value.trim();
				return;
			}

			if (code.equals("E7") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.fjkkhhl = value.trim();

				return;
			}

			if (code.equals("EA") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.couponRuleType = value.trim().charAt(0);

				return;
			}

			if (code.equals("P5") && CommonMethod.noEmpty(value))
			{
				if (value.trim().indexOf(",") >= 0)
				{
					String[] s = value.trim().split(",");
					GlobalInfo.sysPara.overNightBegin = s[0];
					GlobalInfo.sysPara.overNightEnd = s[1];
					GlobalInfo.sysPara.overNightTime = "";
				}
				else
				{
					GlobalInfo.sysPara.overNightBegin = "";
					GlobalInfo.sysPara.overNightEnd = "";
					GlobalInfo.sysPara.overNightTime = value.trim();
				}
				return;
			}
			if (code.equals("1M") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.jygs = value.trim();

				return;
			}

			if (code.equals("P8") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");
				if (values.length > 0)
				{
					GlobalInfo.sysPara.isMoreSelectQuerygoods = value.trim().charAt(0);
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.setTextLimit = values[1].trim();
				}
				return;
			}

			if (code.equals("P9") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.useGoodsFrameMode = values[0].charAt(0);
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.allowGoodsFrameSale = values[1].charAt(0);
				}
				return;
			}

			if (code.equals("PA") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.checkGoodsDate = value.trim();

				return;
			}

			if (code.equals("PB") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.checkGoodsCw = value.trim();

				return;
			}

			if (code.equals("PB") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.checkGoodsCw = value.trim();

				return;
			}

			if (code.equals("PC") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isInputSaleAppend = value.trim().charAt(0);

				if (value.trim().length() > 1)
				{
					GlobalInfo.sysPara.saprintyyytrack = value.trim().charAt(1);
				}

				if (value.split("\\|").length > 1)
				{
					GlobalInfo.sysPara.saleAppendSaleType = value.split("\\|")[1];
				}

				if (value.split("\\|").length > 2)
				{
					GlobalInfo.sysPara.saleAppendStatus = value.split("\\|")[2].charAt(0);
				}
				return;
			}

			if (code.equals("PD") && CommonMethod.noEmpty(value))
			{
				if (value.trim().indexOf(",") >= 0)
				{
					GlobalInfo.sysPara.issaleby0 = value.split(",")[0].trim().charAt(0);
					if (value.trim().split(",").length > 1)
						GlobalInfo.sysPara.isGoodsMoney0 = value.split(",")[1].trim().charAt(0);
				}
				else
				{
					GlobalInfo.sysPara.issaleby0 = value.trim().charAt(0);
				}
				return;
			}

			if (code.equals("PE") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.iscloseJkUI = value.trim().charAt(0);

				return;
			}

			if (code.equals("PF") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isshowAllBcData = value.trim().charAt(0);

				return;
			}

			if (code.equals("PG") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isusepaySelect = value.trim().charAt(0);

				return;
			}

			if (code.equals("PH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.ClawBackCalcModel = value.trim().charAt(0);

				return;
			}

			if (code.equals("PI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.jdxxfkflag = value.trim().charAt(0);

				return;
			}

			if (code.equals("PJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isInputPayMoney = value.trim().charAt(0);

				return;
			}

			if (code.equals("PK") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isinputjdfhdd = value.trim().charAt(0);

				return;
			}

			if (code.equals("PL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.iscfgtable = value.trim().charAt(0);

				return;
			}

			if (code.equals("PM") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.jdfhdd = value.trim();

				return;
			}

			if (code.equals("PN") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split("\\|");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.nodeletepaycode = values[0];
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.noinputpaycode = values[1];
				}

				return;
			}

			if (code.equals("PO") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.verifyDzcmname = value.trim();

				return;
			}

			if (code.equals("PR") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					GlobalInfo.sysPara.backgoodsminmoney = Double.parseDouble(values[0]);
				}

				if (values.length > 1)
				{
					GlobalInfo.sysPara.backgoodsmaxmoney = Double.parseDouble(values[1]);
				}

				return;
			}

			if (code.equals("PS") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isEARNESTZT = value.charAt(0);
				if (value.trim().length() > 1)
					GlobalInfo.sysPara.salepayDisplayRate = value.trim().charAt(1);
				if (value.trim().length() > 2)
					GlobalInfo.sysPara.isRealPrintPOP = value.trim().charAt(2);
				return;
			}

			if (code.equals("PT") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.withdrawauotbsmoney = value.trim();
				return;
			}

			if (code.equals("PU") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isForceRound = value.trim().charAt(0);
				return;
			}

			if (code.equals("PV") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.lackpayfee = Convert.toDouble(value.trim());
				return;
			}

			if (code.equals("PW") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.oldqpaydet = value.trim().charAt(0);
				return;
			}

			if (code.equals("PX") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.vipzklimit = Convert.toDouble(value);
				return;
			}

			if (code.equals("PZ") && CommonMethod.noEmpty(value))
			{
				if (value.trim().indexOf(",") >= 0)
				{
					GlobalInfo.sysPara.uploadOldInfo = value.split(",")[0].trim().charAt(0);
					if (value.trim().split(",").length > 1)
						GlobalInfo.sysPara.isPreSale = value.split(",")[1].trim().charAt(0);
					if (value.trim().split(",").length > 2)
						GlobalInfo.sysPara.sendsaleissuccess = value.split(",")[2].trim().charAt(0);
				}
				else
				{
					GlobalInfo.sysPara.uploadOldInfo = value.charAt(0);
				}
				return;
			}

			if (code.equals("S1"))
			{
				GlobalInfo.sysPara.usemzklog = value.charAt(0);

				return;
			}

			if (code.equals("S2") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.disableCmd = value.trim();
				return;
			}

			if (code.equals("S3") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cancelBankGrant = value.charAt(0);
				return;
			}

			if (code.equals("S4") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isVipMaxSlMsg = value.charAt(0);
				return;
			}

			if (code.equals("S5") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isModifySaleFP = value.charAt(0);
				return;
			}

			if (code.equals("S6") && CommonMethod.noEmpty(value))
			{
				String[] values = value.split(",");
				if (values.length > 0)
					GlobalInfo.sysPara.elcScaleMode = values[0].charAt(0);
				if (values.length > 1)
					GlobalInfo.sysPara.elcScalecycletime = Integer.parseInt(values[1].trim());
				return;
			}

			if (code.equals("S7") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isHbGoods = value.trim().charAt(0);
				return;
			}

			if (code.equals("S8") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.limitGH = value.trim();
				return;
			}

			if (code.equals("S9") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.localNotCheckMultiGz = value.trim();
				return;
			}

			if (code.equals("SE") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.custConfig = value.trim().charAt(0);
				return;
			}

			if (code.equals("HC") && CommonMethod.noEmpty(value))
			{
				String[] values = value.split(",");
				if (values.length > 0)
					GlobalInfo.sysPara.isenablefq = values[0].charAt(0);
				if (values.length > 1)
					GlobalInfo.sysPara.istsfq = values[1].charAt(0);
				return;
			}

			if (code.equals("BH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.printYXQ = value.trim().charAt(0);
				return;
			}

			if (code.equals("ZB") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.mzkChkLength = Integer.parseInt(value.trim());
				return;
			}

			if (code.equals("ZH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.showerrorcmd = value.trim();
				return;
			}

			if (code.equals("ZP") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.issendcrmnohyk = value.trim().charAt(0);
				return;
			}

			if (code.equals("ZJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.prebarcode = value.trim();
				return;
			}

			if (code.equals("ZI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.crmswitch = value.trim().charAt(0);
				return;
			}

			if (code.equals("WI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isprinticandmzk = value.trim().charAt(0);
				return;
			}

			if (code.equals("SB") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isBackPaymentCover = value.trim().charAt(0);

				return;
			}
			if (code.equals("SD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.disablePrinterCounter = value.trim();

				return;
			}
			if (code.equals("SG") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.couponSaleType = value.trim();

				return;
			}
			if (code.equals("WL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isSuperMarketPop = value.trim().charAt(0);

				return;
			}
			if (code.equals("MD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.mdcode = value.trim();
				return;
			}
			if (code.equals("SH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.defaultmzkpass = value.trim();
				return;
			}
			if (code.equals("SI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.hyMaxdateMsg = value.trim();
				return;
			}
			if (code.equals("SJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.bankCXMsg = value.trim();
				return;
			}
			if (code.equals("SK") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.dosPosSvrAddress = value.trim();
				return;
			}
			if (code.equals("SL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.dosPosSvrCmdList = value.trim();
				return;
			}
			if (code.equals("SM") && CommonMethod.noEmpty(value))
			{
				String[] values = value.split(",");
				if (values.length > 0)
					GlobalInfo.sysPara.bacthfdisvisible = values[0];
				if (values.length > 1)
					GlobalInfo.sysPara.batchfdrate = Integer.parseInt(values[1].trim());
				return;
			}

			if (code.equals("SN") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.stampprefix = value.trim();
				return;
			}

			if (code.equals("SO") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.localgoodsisdelgoods = value.trim().charAt(0);
				return;
			}

			if (code.equals("SP") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isUseNewMzkRange = value.trim().charAt(0);
				return;
			}

			if (code.equals("W2") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.yePrintPayCode = value.trim();
				return;
			}

			if (code.equals("HI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.commMerchantId = value.trim();
				return;
			}
			if (code.equals("HP") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isDouble = value.trim().charAt(0);
				return;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return;
		}
	}

	public boolean paraInitFinish()
	{
		// 检查配置的门店号是否与数据库定义门店号匹配
		if (ConfigClass.Market != null && ConfigClass.Market.trim().length() > 0)
		{
			String mktcode = null;
			if (GlobalInfo.sysPara.mktcode.indexOf(",") >= 0)
				mktcode = GlobalInfo.sysPara.mktcode.split(",")[0];
			else
				mktcode = GlobalInfo.sysPara.mktcode;
			if (!ConfigClass.Market.equals(mktcode) && GlobalInfo.sysPara.isShowMktWarm == 'Y')
			{
				if (new MessageBox(Language.apply("款机配置的门店号与数据库定义的门店号不匹配\n\n请检查款机门店设置以免发生数据错误\n\n你要确定要继续进入系统吗？"), null, true).verify() != GlobalVar.Key1) { return false; }
			}
		}

		// 查询时间最少3分钟,以避免过于频繁
		if (GlobalInfo.sysPara.querytime <= 0)
			GlobalInfo.sysPara.querytime = 180;

		// 如果本地优先模式改变联网提示颜色以作警示
		if (GlobalInfo.sysPara.localfind != 'N')
			GlobalInfo.statusBar.setNetStatus();

		// 自定义付款对象
		if (GlobalInfo.sysPara.custompayobj != null && GlobalInfo.sysPara.custompayobj.trim().length() > 0)
		{
			if (ConfigClass.CustomPayment == null)
				ConfigClass.CustomPayment = new Vector();

			String s[] = null;
			if (GlobalInfo.sysPara.custompayobj.indexOf(';') >= 0)
				s = GlobalInfo.sysPara.custompayobj.split(";");
			else
				s = GlobalInfo.sysPara.custompayobj.split("\\|");
			for (int i = 0; i < s.length; i++)
			{
				if (s[i].trim().length() <= 0)
				{
					continue;
				}

				boolean append = true;
				for (int j = 0; j < ConfigClass.CustomPayment.size(); j++)
				{
					String el = (String) ConfigClass.CustomPayment.elementAt(j);

					if (el.trim().equals(s[i]))
					{
						append = false;
						break;
					}
				}

				if (append)
					ConfigClass.CustomPayment.add(s[i].trim());
			}
		}

		// 由于连接每日库时，参数还没有得到,因此讲本次获得的参数设置设置写入文件,以供重新开机连接数据库时进行读取
		LoadSysInfo.getDefault().getOverNight(false);

		return true;
	}

	public boolean readSyjGrange(Vector v)
	{
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地收银范围表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from SYJGRANGE");

			if (rs == null) { return false; }

			while (rs.next())
			{
				SyjGrangeDef grangeDef = new SyjGrangeDef();

				if (!GlobalInfo.localDB.getResultSetToObject(grangeDef)) { return false; }

				v.add(grangeDef);
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地收银范围表耗时: "));
		}
	}

	public boolean readPayMode(Vector v)
	{
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地付款方式表,请等待......"));

			// 在服务器端进行排序，此地不需要order by code,num2记录排序号
			if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
			{
				rs = GlobalInfo.localDB.selectData("select * from PAYMODE order by num2,code");
			}
			else
			{
				rs = GlobalInfo.localDB.selectData("select * from PAYMODE order by code");
			}

			if (rs == null) { return false; }

			while (rs.next())
			{
				PayModeDef mode = new PayModeDef();

				if (!GlobalInfo.localDB.getResultSetToObject(mode)) { return false; }
				/*
				 * // yinliang test if (mode.code.equals("06")) { mode.name = "美
				 * 元"; mode.hl = 7.7190; mode.zlhl = 7.7190; mode.sswrjd = 0.01;
				 * mode.iszl = 'Y'; mode.isyy = 'Y'; }
				 * 
				 * if (mode.code.equals("07")) { mode.name = "日 元"; mode.hl =
				 * 0.0650; mode.zlhl = 0.0650; mode.sswrjd = 1; mode.iszl = 'Y';
				 * mode.isyy = 'Y'; }
				 * 
				 * //qujuj测试 if (mode.code.trim().equals("0300")) { mode.isbank
				 * = 'Y'; }
				 */
				if (mode.hl <= 0)
					mode.hl = 1;
				if (mode.maxval <= mode.minval)
					mode.maxval = mode.minval + 1;
				v.add(mode);
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地付款方式表耗时: "));
		}
	}

	public boolean writePayMode(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地付款方式表,请等待......"));

			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From PAYMODE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("PAYMODE");
			if (ref == null || ref.length <= 0)
				ref = PayModeDef.ref;

			String line = CommonMethod.getInsertSql("PAYMODE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			PayModeDef pay = new PayModeDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(pay, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(pay, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (GlobalInfo.localDB.commitTrans())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地付款方式表耗时: "));
		}
	}

	// 插入收银班次
	public boolean writePosTime(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地班次表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From POSTIME")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("POSTIME");
			if (ref == null || ref.length <= 0)
				ref = PosTimeDef.ref;

			String line = CommonMethod.getInsertSql("POSTIME", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			PosTimeDef pt = new PosTimeDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(pt, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(pt, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (GlobalInfo.localDB.commitTrans())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地班次表耗时: "));
		}
	}

	// 获得收银班次
	public boolean readPosTime()
	{
		PosTimeDef timeDef = null;
		Vector v = new Vector();
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地班次表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from POSTIME order by code");

			if (rs == null) { return false; }

			while (rs.next())
			{
				timeDef = new PosTimeDef();

				if (!GlobalInfo.localDB.getResultSetToObject(timeDef)) { return false; }

				v.add(timeDef);
			}

			GlobalInfo.posTime = v;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地班次表耗时: "));
		}

		return true;
	}

	// 插入主功能菜单
	public boolean writeMenuFunc(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地菜单表,请等待......"));

			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From MENUFUNC")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("MENUFUNC");
			if (ref == null || ref.length <= 0)
				ref = MenuFuncDef.ref;

			String line = CommonMethod.getInsertSql("MENUFUNC", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			MenuFuncDef mn = new MenuFuncDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(mn, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(mn, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (GlobalInfo.localDB.commitTrans())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地菜单表耗时: "));
		}
	}

	// 获得主功能菜单
	public boolean readMenuFunc()
	{
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地菜单表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from MENUFUNC order by code");

			if (rs == null) { return false; }

			ArrayList menuFunArray = new ArrayList();

			while (rs.next())
			{
				MenuFuncDef mfd = new MenuFuncDef();

				if (!GlobalInfo.localDB.getResultSetToObject(mfd)) { return false; }

				menuFunArray.add(mfd);
			}

			GlobalInfo.menuFunArray = menuFunArray;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地菜单表耗时: "));
		}
	}

	// 读取收银机状态
	public boolean readSyjStatus()
	{
		ResultSet rs = null;

		try
		{
			if (GlobalInfo.syjStatus == null)
			{
				GlobalInfo.syjStatus = new SyjStatusDef();
			}

			//
			rs = GlobalInfo.localDB.selectData("select * from SYJSTATUS where syjh = '" + ConfigClass.CashRegisterCode + "'");

			if (rs == null) { return false; }

			if (rs.next())
			{
				if (!GlobalInfo.localDB.getResultSetToObject(GlobalInfo.syjStatus)) { return false; }

				GlobalInfo.syjStatus.syjh = ConfigClass.CashRegisterCode;
				// GlobalInfo.syjStatus.status = StatusType.STATUS_START; //
				// 保持上次状态
				GlobalInfo.syjStatus.netstatus = 'Y';
				GlobalInfo.syjStatus.syyh = "";
				GlobalInfo.syjStatus.bs = 0;
				GlobalInfo.syjStatus.je = 0;
				GlobalInfo.syjStatus.xjje = 0;
			}
			else
			{
				GlobalInfo.syjStatus.syjh = ConfigClass.CashRegisterCode;
				GlobalInfo.syjStatus.fphm = 1;
				GlobalInfo.syjStatus.status = StatusType.STATUS_START;
				GlobalInfo.syjStatus.netstatus = 'Y';
				GlobalInfo.syjStatus.bc = '1';
				GlobalInfo.syjStatus.syyh = "";
				GlobalInfo.syjStatus.bs = 0;
				GlobalInfo.syjStatus.je = 0;
				GlobalInfo.syjStatus.xjje = 0;
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}

	// 写入收银机状态
	public boolean writeSyjStatus()
	{
		try
		{
			GlobalInfo.syjStatus.je = ManipulatePrecision.doubleConvert(GlobalInfo.syjStatus.je, 2, 1);
			GlobalInfo.syjStatus.xjje = ManipulatePrecision.doubleConvert(GlobalInfo.syjStatus.xjje, 2, 1);

			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From SYJSTATUS")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("SYJSTATUS");
			if (ref == null || ref.length <= 0)
				ref = SyjStatusDef.ref;

			String line = CommonMethod.getInsertSql("SYJSTATUS", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			if (!GlobalInfo.localDB.setObjectToParam(GlobalInfo.syjStatus, ref)) { return false; }

			if (!GlobalInfo.localDB.executeSql()) { return false; }

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	// 写入收银机收银范围
	public boolean writeSyjGrange(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地收银范围表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From SYJGRANGE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("SYJGRANGE");
			if (ref == null || ref.length <= 0)
				ref = SyjGrangeDef.ref;

			String line = CommonMethod.getInsertSql("SYJGRANGE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			SyjGrangeDef gz = new SyjGrangeDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(gz, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(gz, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地收银范围表耗时: "));
		}
	}

	// 写入用户角色
	public boolean writeOperRole(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地人员角色表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From OPERROLE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("OPERROLE");
			if (ref == null || ref.length <= 0)
				ref = OperRoleDef.ref;

			String line = CommonMethod.getInsertSql("OPERROLE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			OperRoleDef role = new OperRoleDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(role, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(role, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地人员角色表耗时: "));
		}
	}

	// 写入顾客卡类型
	public boolean writeCustomerType(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地会员类型表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From CUSTOMERTYPE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("CUSTOMERTYPE");
			if (ref == null || ref.length <= 0)
				ref = CustomerTypeDef.ref;

			String line = CommonMethod.getInsertSql("CUSTOMERTYPE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			CustomerTypeDef type = new CustomerTypeDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(type, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(type, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地会员类型表耗时: "));
		}
	}

	// 写入顾客采集信息
	public boolean writeBuyerInfo(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地顾客采集信息表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From BUYERINFO")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("BUYERINFO");
			if (ref == null || ref.length <= 0)
				ref = BuyerInfoDef.ref;

			String line = CommonMethod.getInsertSql("BUYERINFO", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			BuyerInfoDef info = new BuyerInfoDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(info, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(info, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			PublicMethod.timeEnd(Language.apply("写入本地顾客采集信息表耗时: "));
		}
	}

	// 写入呼叫信息
	public boolean writeCallInfo(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地呼叫信息表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From CALLINFO")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("CALLINFO");
			if (ref == null || ref.length <= 0)
				ref = CallInfoDef.ref;

			String line = CommonMethod.getInsertSql("CALLINFO", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			CallInfoDef info = new CallInfoDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(info, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(info, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			PublicMethod.timeEnd(Language.apply("写入本地呼叫信息表耗时: "));
		}
	}

	// 获得呼叫信息
	public boolean readCallInfo()
	{
		CallInfoDef info = null;
		Vector v = new Vector();
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地呼叫信息,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from CALLINFO");

			if (rs == null) { return false; }

			while (rs.next())
			{
				info = new CallInfoDef();

				if (!GlobalInfo.localDB.getResultSetToObject(info)) { return false; }

				v.add(info);
			}

			GlobalInfo.callInfo = v;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地呼叫信息耗时: "));
		}

		return true;
	}

	// 写入备用信息
	public boolean writeMemoInfo(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地备用信息表,请等待......"));
			if (!GlobalInfo.localDB.isTableExist("MEMOINFO"))
				return true;

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From MEMOINFO")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("MEMOINFO");
			if (ref == null || ref.length <= 0)
				ref = MemoInfoDef.ref;

			String line = CommonMethod.getInsertSql("MEMOINFO", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			MemoInfoDef info = new MemoInfoDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(info, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(info, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			PublicMethod.timeEnd(Language.apply("写入本地备用信息表耗时: "));
		}
	}

	// 获得备用信息
	public boolean readMemoInfo()
	{
		Object obj = null;

		try
		{
			PublicMethod.timeStart(Language.apply("正在读取本地备用信息,请等待......"));
			if (!GlobalInfo.localDB.isTableExist("MEMOINFO"))
				return true;

			// 移动充值商品数据
			obj = GlobalInfo.localDB.selectOneData("select count(*) from MEMOINFO where type = 'YDCZ'");
			if (obj != null && Convert.toInt(obj) > 0)
				GlobalInfo.useMobileCharge = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			PublicMethod.timeEnd(Language.apply("读取本地备用信息耗时: "));
		}

		return true;
	}

	// 获得付款上限信息
	public boolean readPaymentLimit()
	{
		PaymentLimitDef limitDef = null;
		Vector v = new Vector();
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地付款上限表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from PAYMENTLIMIT");

			if (rs == null) { return false; }

			while (rs.next())
			{
				limitDef = new PaymentLimitDef();

				if (!GlobalInfo.localDB.getResultSetToObject(limitDef)) { return false; }

				v.add(limitDef);
			}

			GlobalInfo.payLimit = v;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地付款上限表耗时: "));
		}

		return true;
	}

	// 检查是否移动充值商品
	public MemoInfoDef checkMobileCharge(String code)
	{
		ResultSet rs = null;
		MemoInfoDef info = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在检查移动充值商品,请等待......"));

			// memo = 1-在线充值商品/2-离线充值商品/3-找零充值商品
			if (code != null && code.trim().length() > 0)
				rs = GlobalInfo.localDB.selectData("select * from MEMOINFO where code ='" + code + "' and type = 'YDCZ'");
			else
				rs = GlobalInfo.localDB.selectData("select * from MEMOINFO where type = 'YDCZ' and memo = '3'");
			if (rs != null && rs.next())
			{
				info = new MemoInfoDef();
				if (GlobalInfo.localDB.getResultSetToObject(info)) { return info; }
			}

			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return null;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("检查移动充值商品耗时: "));
		}
	}

	// 写入电子秤付款模版
	public boolean writeDzcMode(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地电子秤表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From DZCMODE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("DZCMODE");
			if (ref == null || ref.length <= 0)
				ref = DzcModeDef.ref;

			String line = CommonMethod.getInsertSql("DZCMODE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			DzcModeDef dzc = new DzcModeDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(dzc, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(dzc, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			PublicMethod.timeEnd(Language.apply("写入本地电子秤表耗时: "));
		}
	}

	// 获得电子秤模版
	public boolean readDzcMode()
	{
		DzcModeDef dzcDef = null;
		Vector v = new Vector();
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地电子秤表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select * from DZCMODE");

			if (rs == null) { return false; }

			while (rs.next())
			{
				dzcDef = new DzcModeDef();

				if (!GlobalInfo.localDB.getResultSetToObject(dzcDef)) { return false; }

				v.add(dzcDef);
			}

			GlobalInfo.dzcMode = v;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地电子秤表耗时: "));
		}

		return true;
	}

	// 写入缴款模版
	public boolean writePayinMode(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地缴款模版表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From PAYINMODE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("PAYINMODE");
			if (ref == null || ref.length <= 0)
				ref = PayinModeDef.ref;

			String line = CommonMethod.getInsertSql("PAYINMODE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			PayinModeDef payin = new PayinModeDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(payin, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(payin, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地缴款模版表耗时: "));
		}
	}

	// 写入系统管理架构
	public boolean writeManaFrame(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地管理架构表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From MANAFRAME")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("MANAFRAME");
			if (ref == null || ref.length <= 0)
				ref = ManaFrameDef.ref;

			String line = CommonMethod.getInsertSql("MANAFRAME", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			ManaFrameDef m = new ManaFrameDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(m, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(m, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地管理架构表耗时: "));
		}
	}

	// 写入付款上限
	public boolean writePaymentLimit(Vector v)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入付款上限表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From PAYMENTLIMIT")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("PAYMENTLIMIT");
			if (ref == null || ref.length <= 0)
				ref = PaymentLimitDef.ref;

			String line = CommonMethod.getInsertSql("PAYMENTLIMIT", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			PaymentLimitDef p = new PaymentLimitDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(p, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(p, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入付款上限表耗时: "));
		}
	}

	// 写入历史通知
	public boolean writeNews(NewsDef news)
	{
		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地通知表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			// 计算历史通知个数
			long seqno;
			Object obj = GlobalInfo.localDB.selectOneData("select max(seqno) from NEWS");

			if (obj == null)
			{
				seqno = 1;
			}
			else
			{
				seqno = Long.parseLong(String.valueOf(obj)) + 1;
			}

			// 只保留最近的100条通知
			if ((seqno > 100) && !GlobalInfo.localDB.executeSql("delete from NEWS where seqno <= " + String.valueOf((seqno - 100)))) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("NEWS");
			if (ref == null || ref.length <= 0)
				ref = NewsDef.ref;

			String line = CommonMethod.getInsertSql("NEWS", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			news.seqno = seqno;
			news.rqsj = ManipulateDateTime.getCurrentDateTime();
			news.syyh = ((GlobalInfo.posLogin == null) ? "" : GlobalInfo.posLogin.gh);

			if (!GlobalInfo.localDB.setObjectToParam(news, ref)) { return false; }

			if (!GlobalInfo.localDB.executeSql()) { return false; }

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地通知表耗时: "));
		}
	}

	// 写入任务表
	public long writeTask(TasksDef task)
	{
		try
		{
			// 同一个任务不重复写入
			String line = "select seqno from TASKS where type = '" + String.valueOf(task.type) + "' and keytext = '" + task.keytext + "'";
			Object obj = GlobalInfo.localDB.selectOneData(line);

			if (obj != null)
			{
				return Long.parseLong(String.valueOf(obj));
			}
			else
			{
				//
				if (!GlobalInfo.localDB.beginTrans()) { return 0; }

				// 计算任务个数
				long seqno;
				obj = GlobalInfo.localDB.selectOneData("select max(seqno) from TASKS");

				if (obj == null)
				{
					seqno = 1;
				}
				else
				{
					seqno = Long.parseLong(String.valueOf(obj)) + 1;
				}

				// 按表的字段确定对象的数据,表结构不存在的数据不保存
				String[] ref = GlobalInfo.localDB.getTableColumns("TASKS");
				if (ref == null || ref.length <= 0)
					ref = TasksDef.ref;

				// 新增任务
				line = CommonMethod.getInsertSql("TASKS", ref);

				if (!GlobalInfo.localDB.setSql(line)) { return 0; }

				task.seqno = seqno;

				if (!GlobalInfo.localDB.setObjectToParam(task, ref)) { return 0; }

				if (!GlobalInfo.localDB.executeSql()) { return 0; }

				if (!GlobalInfo.localDB.commitTrans()) { return 0; }

				return seqno;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return 0;
		}
	}

	//
	public long writeTask(char type, String keytext)
	{
		TasksDef task = new TasksDef();

		task.type = type;
		task.keytext = keytext;

		return writeTask(task);
	}

	// 删除任务表
	public boolean deleteTask(long seqno)
	{
		try
		{
			String line = "delete from TASKS where seqno = " + String.valueOf(seqno);

			if (!GlobalInfo.localDB.executeSql(line)) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	// 读取一个任务
	public TasksDef readTask(long seqno)
	{
		TasksDef task = null;
		ResultSet rs = null;

		try
		{
			rs = GlobalInfo.localDB.selectData("select * from TASKS where seqno > " + String.valueOf(seqno) + " order by seqno");

			if (rs == null) { return null; }

			if (rs.next())
			{
				task = new TasksDef();

				if (!GlobalInfo.localDB.getResultSetToObject(task)) { return null; }
			}

			return task;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return null;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}

	public String getGzName(String gzCode)
	{
		try
		{
			Object o = GlobalInfo.localDB.selectOneData("select name from MANAFRAME where gz = '" + gzCode + "'");

			if (o != null)
			{
				return String.valueOf(o);
			}
			else
			{
				return "";
			}
		}
		catch (Exception er)
		{
			return "";
		}
	}

	public boolean checkSyjGrange(String gz)
	{
		try
		{
			Object obj = GlobalInfo.localDB.selectOneData("select count(gz) from SYJGRANGE");

			if (obj != null && ((Integer) obj).intValue() == 0) { return true; }

			obj = GlobalInfo.localDB.selectOneData("select count(gz) from SYJGRANGE where gz = '" + gz + "'");

			if (obj != null && ((Integer) obj).intValue() > 0) { return true; }

			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}

	public Vector getAllManaframe()
	{
		ResultSet rs = null;

		try
		{
			Vector list = new Vector();
			if ((rs = GlobalInfo.localDB.selectData("select gz,name from MANAFRAME")) != null)
			{
				while (rs.next())
				{
					String[] mana = new String[] { rs.getString("gz"), rs.getString("name") };
					list.add(mana);
				}
			}

			if (list.size() <= 0)
			{
				// 在Base库的MANAFRAME库检查
				if (GlobalInfo.baseDB.isTableExist("MANAFRAME"))
				{
					if ((rs = GlobalInfo.baseDB.selectData("select gz,name from MANAFRAME")) != null)
					{
						while (rs.next())
						{
							String[] mana = new String[] { rs.getString("gz"), rs.getString("name") };
							list.add(mana);
						}
					}
				}
			}

			return list;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return null;
		}
		finally
		{
			if (rs != null)
			{
				GlobalInfo.localDB.resultSetClose();
			}
		}
	}

	public boolean checkManaframe(String gz, char iscs)
	{
		try
		{
			if (GlobalInfo.psManaframe == null)
				return true;

			GlobalInfo.localDB.setSql(GlobalInfo.psManaframe);
			GlobalInfo.localDB.paramSetString(1, gz);
			GlobalInfo.localDB.paramSetChar(2, iscs);
			Object obj = GlobalInfo.localDB.selectOneData();

			if (obj != null && ((Integer) obj).intValue() > 0) { return true; }

			// 在Base库的MANAFRAME库检查
			if (GlobalInfo.baseDB.isTableExist("MANAFRAME"))
			{
				obj = GlobalInfo.baseDB.selectOneData("select count(*) from MANAFRAME where gz = '" + gz + "' and iscs = '" + iscs + "'");
				if (obj != null && ((Integer) obj).intValue() > 0) { return true; }
			}

			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}

	// 写入POS积分规则
	public boolean writePosJfRule(Vector v)
	{
		return true;
	}
}
