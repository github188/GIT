package com.efuture.javaPos.Payment;

import java.util.ArrayList;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentSerial extends PaymentMzk
{
	   //用于保存面值卡请求和返回值
    public ArrayList arrMzkReqs = new ArrayList();
    public ArrayList arrMzkRets = new ArrayList();
    private double sumYe = 0;
    private int sumSl = 0;
    private String cardNoEnd = "";
    public PaymentMzk mzkObj = null;
    public String paycode = "";

    //连续卡号开始计算的位置(CardNoEndIndex <= 0 到卡号尾部)
    protected int CardNoStartIndex = 0;
    protected int CardNoEndIndex = -1;

    public PaymentSerial()
    {
    	initMzkObj();
    }

    public PaymentSerial(PayModeDef mode, SaleBS sale)
    {
    	initPayment(mode, sale);
    	initMzkObj();
    	mzkObj.initPayment(mode, sale);
    }

    // 该构造函数用于红冲小票时,通过小票付款明细创建对象
    public PaymentSerial(SalePayDef pay, SaleHeadDef head)
    {
    	initPayment(pay, head);
    	initMzkObj();
    	mzkObj.initPayment(pay, head);
    }
    
    public PaymentMzk initMzkObj()
    {
    	if (mzkObj == null)
    	{
	    	String rule = GlobalInfo.sysPara.serialmzkrule;
	    	String[] rules = rule.split("\\|");
	    	for (int i = 0; i < rules.length; i++)
	    	{
	    		if (rules[i].indexOf("=") <0)
	    		{
	    			mzkObj = this;
	    			break;
	    		}
	    		
	    		if (rules[i].indexOf(","+paymode.code+"=") >=0)
	    		{
	    			String payname = rules[i].substring(rules[i].indexOf("=")+1);
	    			paycode = payname.split("=")[0];
	    			payname = payname.split("=")[1];
	    			try
	    			{
	    				Class cl = CreatePayment.getDefault().payClassName(payname);
	    				if (cl != null) mzkObj = (PaymentMzk)cl.newInstance();
//	    				else new MessageBox("配置参数出错，没有找到:"+payname);
	    				else new MessageBox(Language.apply("配置参数出错，没有找到:{0}" ,new Object[]{payname}));
	    			}
	    			catch (Exception e)
	    			{
	    				e.printStackTrace();
	    			}
	        		break;
	    		}
	    	}
    	}

    	return mzkObj;
    	
    }

    public void initPayment(PayModeDef mode, SaleBS sale)
    {
        super.initPayment(mode, sale);
        
    	this.getSerialRule();
    }
    
    //获得面值卡总金额
    public String getSumYe()
    {
        return ManipulatePrecision.doubleToString(sumYe);
    }

    //获得面值卡总数量
    public String getSumSl()
    {
        return String.valueOf(sumSl);
    }

    public String getCardNoEnd()
    {
        return cardNoEnd;
    }

    public SalePayDef inputPay(String money)
    {
        try
        {
			// 退货小票不能使用,退货扣回按销售算
        	
			if (mzkObj.checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
//				new MessageBox("退货时不允许使用" + paymode.name);
				new MessageBox(Language.apply("退货时不允许使用{0}" ,new Object[]{paymode.name}));
				return null;
			}

            // 先检查是否有冲正未发送
            if (!mzkObj.sendAccountCz())
            {
                return null;
            }

            // 打开明细输入窗口
            new PaymentSerialForm().open(this, saleBS);

            // 如果付款成功,则salepay已在窗口中生成
            return salepay;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public int getCardNoStartPosition()
    {
        return 0;
    }

    public void formatCardNo(String cardno, StringBuffer cardnopart1,
                             StringBuffer cardnopartserial,
                             StringBuffer cardnopart3)
    {
        int mcardnostartindex = CardNoStartIndex;
        int mcardnoendindex = CardNoEndIndex;

        if (mcardnostartindex < 0)
        {
            mcardnostartindex = 0;
        }

        if (mcardnoendindex <= 0)
        {
            mcardnoendindex = cardno.length();
        }

        if (mcardnostartindex > 0)
        {
            cardnopart1.append(cardno.substring(0, mcardnostartindex));
        }

        if (mcardnoendindex <= cardno.length())
        {
            cardnopart3.append(cardno.substring(mcardnoendindex));
        }

        cardnopartserial.append(cardno.substring(mcardnostartindex,
                                                 mcardnoendindex));
    }

    //连续面值卡查询
    public boolean findMzk(String cardnostart, String cardnoend)
    {
        cardNoEnd = "";
        sumYe     = 0;
        sumSl     = 0;
        arrMzkReqs.clear();
        arrMzkRets.clear();

        StringBuffer cardStartPart1 = new StringBuffer();
        StringBuffer cardStartPart2 = new StringBuffer();
        StringBuffer cardStartPart3 = new StringBuffer();

        StringBuffer cardStartEnd1 = new StringBuffer();
        StringBuffer cardStartEnd2 = new StringBuffer();
        StringBuffer cardStartEnd3 = new StringBuffer();

        formatCardNo(cardnostart, cardStartPart1, cardStartPart2, cardStartPart3);
        formatCardNo(cardnoend, cardStartEnd1, cardStartEnd2, cardStartEnd3);

        int cardlen = cardStartPart2.length();

        long longcardnostart = Convert.toLong(cardStartPart2);
        long longcardnoend = Convert.toLong(cardStartEnd2);

        long num = longcardnoend - longcardnostart;

        if (num <= 0)
        {
            longcardnoend = longcardnostart;
        }

        String strcardno = "";

        for (long i = longcardnostart; i <= longcardnoend; i++)
        {
        	
            mzkObj.mzkreq = new MzkRequestDef();
            mzkObj.mzkret = new MzkResultDef();

            strcardno = cardStartPart1 +
                        ManipulateStr.PadLeft(String.valueOf(i), cardlen, '0') +
                        cardStartEnd3;

            if (!mzkObj.findMzk("", strcardno, ""))
            {
                return false;
            }
            
            // 查询是否存在相同的付款方式，如果存在，原付款方式删除
            if (saleBS.existPayment(paycode,mzkObj.mzkret.cardno) >= 0)
            {
            	boolean ret = false;
//            	if (new MessageBox("["+mzkObj.mzkret.cardno+"]已进行付款\n你要取消原付款重新输入吗？",null,true).verify() == GlobalVar.Key1)
            	if (new MessageBox(Language.apply("[{0}]已进行付款\n你要取消原付款重新输入吗？" ,new Object[]{mzkObj.mzkret.cardno}),null,true).verify() == GlobalVar.Key1)
            	{
            		ret = true;
            		int n = -1;
            		do {
            			n = saleBS.existPayment(paycode,mzkObj.mzkret.cardno);
            			if (n >= 0)
            			{
            				if (!saleBS.deleteSalePay(n))
            				{
            					ret = false;
            					break;
            				}
            			}
            		} while(n >= 0);

            		// 重新刷新付款余额及已付款列表
                    saleBS.calcPayBalance();
                   
            		saleBS.salePayEvent.refreshSalePayment();
            	}
            	
            	if (!ret)
            	{
//            		new MessageBox("账号为"+mzkObj.mzkret.cardno +"的付款方式已存在\n请先手工删除此付款方式");
            		new MessageBox(Language.apply("账号为{0}的付款方式已存在\n请先手工删除此付款方式" ,new Object[]{mzkObj.mzkret.cardno}));
            		return false;
            	}
            }

            if (mzkObj.mzkret.ye > 0)
            {
                arrMzkReqs.add(mzkObj.mzkreq);
                arrMzkRets.add(mzkObj.mzkret);

                sumSl++;
            }
            else
            {
//                new MessageBox(paymode.name + "[" + strcardno + "]\r\n的余额为零!");
                new MessageBox(Language.apply("{0}[{1}]\r\n的余额为零!" ,new Object[]{paymode.name ,strcardno}));

                return false;
            }

            //记录最后一张有效面值卡的卡号
            cardNoEnd = strcardno;

            //增加余额
            sumYe += mzkObj.mzkret.ye;
            
        }

        return true;
    }
    
    public String getDisplayStatusInfo()
	{
    	//计算最大可收金额
        return mzkObj.getDisplayStatusInfo();
	}

    public boolean createSalePay(String money)
    {
        try
        {            
            //
            if (!mzkObj.checkMoneyValid(money, saleBS.calcPayBalance()))
            {
                return false;
            }


            double douPayYe = Convert.toDouble(money);
            double douPayJe = 0;

            for (int i = 0; i < arrMzkRets.size(); i++)
            {
                if (douPayYe <= 0)
                {
                    return true;
                }

                MzkRequestDef mrq = (MzkRequestDef) arrMzkReqs.get(i);
                MzkResultDef mrs = (MzkResultDef) arrMzkRets.get(i);

                douPayJe = Math.min(douPayYe, mrs.ye);

                    //手式创建
                if (!CreateNewjPayment(mrq, mrs, douPayJe))
                {
                	return false;
                }
                
                alreadyAddSalePay = true;
                douPayYe -= douPayJe;
            }

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }

    // 手工添加付款方式
    private boolean CreateNewjPayment(MzkRequestDef req, MzkResultDef res,double money)
    {
        try
        {
            PaymentMzk pm = initMzkObj();
            PayModeDef pmd = DataService.getDefault().searchPayMode(paycode);
            
            if (pmd == null)
            {
//            	new MessageBox("付款方式"+paycode+"没有定义");
            	new MessageBox(Language.apply("付款方式{0}没有定义" ,new Object[]{paycode}));
            	return false;
            }
            
            pm.initPayment(pmd, saleBS);

            pm.paymode  = pmd;
            pm.salehead = this.salehead;
            pm.saleBS   = this.saleBS;
            pm.messDisplay = false;
            
            req.seqno = 0;
            req.je    = money;

            pm.mzkreq = req;
            pm.mzkret = res;

            /////////////////////// 创建新的付款明细对象
            // 创建付款对象,增加已付款
            
            if (pm.createSalePay(String.valueOf(money)))
            {
                saleBS.addSalePayObject(pm.salepay, pm);
                return true;
            }
            
            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    // 连号输入电子券付款方式不需要校验密码
    protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
    {
        return true;
    }
    
    // 设置连号规则
    private void getSerialRule()
    {
    	try{
	    	String rule = GlobalInfo.sysPara.serialmzkrule;
	    	String[] rules = rule.split("\\|");
	    	for (int i = 0; i < rules.length; i++)
	    	{
	    		if (rules[i].indexOf("=") <0)
	    		{
	    			String[] rule2 = rules[i].split(",");
	        		CardNoStartIndex = Integer.parseInt(rule2[0]);
	        		CardNoEndIndex = Integer.parseInt(rule2[1]);
	    			break;
	    		}
	    		
	    		if (rules[i].indexOf(","+paymode.code+"=") >=0)
	    		{
	    			String rule1 = rules[i].substring(0,rules[i].indexOf(","+paymode.code+"="));
	    			String[] rule2 = rule1.split(",");
	        		CardNoStartIndex = Integer.parseInt(rule2[0]);
	        		CardNoEndIndex = Integer.parseInt(rule2[1]);
	    		}
	    	}
    	}catch(Exception er)
    	{
    		new MessageBox(er.getMessage());
    		er.printStackTrace();
    	}
    }
}
