package com.efuture.javaPos.Payment;

import java.util.ArrayList;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class PaymentSerialMzk extends PaymentMzk
{
    //用于保存面值卡请求和返回值
    public ArrayList arrMzkReqs = new ArrayList();
    public ArrayList arrMzkRets = new ArrayList();
    private double sumYe = 0;
    private int sumSl = 0;
    private String cardNoEnd = "";

    //连续卡号开始计算的位置(CardNoEndIndex <= 0 到卡号尾部)
    protected int CardNoStartIndex = 0;
    protected int CardNoEndIndex = -1;

    public PaymentSerialMzk()
    {
    }

    public PaymentSerialMzk(PayModeDef mode, SaleBS sale)
    {
    	initPayment(mode, sale);
    }

    // 该构造函数用于红冲小票时,通过小票付款明细创建对象
    public PaymentSerialMzk(SalePayDef pay, SaleHeadDef head)
    {
    	initPayment(pay, head);
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
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
//				new MessageBox("退货时不允许使" + paymode.name);
				new MessageBox(Language.apply("退货时不允许使{0}" ,new Object[]{paymode.name}));
				return null;
			}

            // 先检查是否有冲正未发送
            if (!sendAccountCz())
            {
                return null;
            }

            // 打开明细输入窗口
            new PaymentSerialMzkForm().open(this, saleBS);

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
            mzkreq = new MzkRequestDef();
            mzkret = new MzkResultDef();

            strcardno = cardStartPart1 +
                        ManipulateStr.PadLeft(String.valueOf(i), cardlen, '0') +
                        cardStartEnd3;

            if (!findMzk("", strcardno, ""))
            {
                return false;
            }

            if (mzkret.ye > 0)
            {
                arrMzkReqs.add(mzkreq);
                arrMzkRets.add(mzkret);

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
            sumYe += mzkret.ye;
        }

        return true;
    }

    public boolean createSalePay(String money)
    {
        try
        {
            if (!checkMoneyValid(money, saleBS.calcPayBalance()))
            {
                return false;
            }

            double douPayYe = Convert.toDouble(money);
            double douPayJe = 0;
            boolean iscreate = false;

            for (int i = 0; i < arrMzkRets.size(); i++)
            {
                if (douPayYe <= 0)
                {
                    return true;
                }

                MzkRequestDef mrq = (MzkRequestDef) arrMzkReqs.get(i);
                MzkResultDef mrs = (MzkResultDef) arrMzkRets.get(i);

                douPayJe = Math.min(douPayYe, mrs.ye);

                if (!iscreate)
                {
                    mzkreq = mrq;
                    mzkret = mrs;

                    if (!super.createSalePay(ManipulatePrecision.doubleToString(douPayJe)))
                    {
                        return false;
                    }

                    iscreate = true;
                }
                else
                {
                    //手式创建
                    CreateNewjPayment(mrq, mrs, douPayJe);
                }

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
    private void CreateNewjPayment(MzkRequestDef req, MzkResultDef res,double money)
    {
        try
        {
            PaymentSerialMzk psm = new PaymentSerialMzk(paymode, saleBS);

            psm.paymode  = this.paymode;
            psm.salehead = this.salehead;
            psm.saleBS   = this.saleBS;

            req.seqno = 0;
            req.je    = money;

            psm.mzkreq = req;
            psm.mzkret = res;

            /////////////////////// 创建新的付款明细对象
            // 创建付款对象,增加已付款
            if (psm.superCreateSalePay(money))
            {
                saleBS.addSalePayObject(psm.salepay, psm);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // 调用超类方法
    private boolean superCreateSalePay(double money)
    {
        if (super.createSalePay(String.valueOf(money)))
        {
            return true;
        }

        return false;
    }

    // 连号输入电子券付款方式不需要校验密码
    protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
    {
        return true;
    }
    
    // 设置连号规则
    private void getSerialRule()
    {
    	String rule = GlobalInfo.sysPara.serialmzkrule;
    	if(null != rule && 3 == rule.length() && 1 == rule.indexOf(','))
    	{
    		CardNoStartIndex = Integer.parseInt(rule.substring(0,1));
    		CardNoEndIndex = Integer.parseInt(rule.substring(2,3));
    	}
    }
}
