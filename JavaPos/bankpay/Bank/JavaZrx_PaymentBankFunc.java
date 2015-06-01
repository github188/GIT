package bankpay.Bank;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import pos.trans.base.impl.DrmServiceImpl;

public class JavaZrx_PaymentBankFunc extends PaymentBankFunc
{
	protected SaleBS saleBS = null;
	protected DrmServiceImpl dsi = null;
	
	public String[] getFuncItem()
	{
	    String[] func = new String[3];

	    func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	    func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	    func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";
	    
	    return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
        //0-4对应FORM中的5个输入框
        //null表示该不用输入
        switch (type)
        {
            case PaymentBank.XYKXF: //消费
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "优惠券号";
                grpLabelStr[4] = "执行操作";

            break;
            case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "执行操作";

            break;
            case PaymentBank.XYKTH: //隔日退货   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "优惠券号";
                grpLabelStr[4] = "执行操作";
            break;
        }

        return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
        //0-4对应FORM中的5个输入框
        //null表示该需要用户输入,不为null用户不输入
        switch (type)
        {
            case PaymentBank.XYKXF: //消费
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "回车消费";

            break;
            case PaymentBank.XYKCX: //消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "回车撤销";

            break;
            case PaymentBank.XYKTH: //退货
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "回车退货";
            break;
        }

        return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH))
        {
            errmsg = "知而行接口不支持该交易";
            new MessageBox(errmsg);

            return false;
        }
		
		try
		{
			// 根据不同的类型生成文本结构
            switch (type)
            {	
            	case PaymentBank.XYKXF:	
            		saleBS = (SaleBS)memo.elementAt(2);
            		
            		if (saleBS.saleHead.str2 == null || !saleBS.saleHead.str2.equals("F_XSXP_ADD"))
                	{
            			if (!this.sendSaleGoods(saleBS.saleHead,saleBS.saleGoods)) return false; 
                	}
            		
            		if (!this.SendValidateCoupon(saleBS.saleHead,track2)) 
            		{
            			SalePayDef salepay  = (SalePayDef)memo.elementAt(3);
            			
            			if (!isZrxPayMode(salepay)) 
            			{
            				if (!this.SendCancel(saleBS.saleHead)) return false;
            				
            				bld.retcode	= "0";
            				
            				bld.retbz  = 'N';
            			}
            			
            			return false;
            		}
            		
            		saleBS.saleHead.str2 = "F_XSXP_ADD";	
          
            	break;	
            	case PaymentBank.XYKTH:
            		saleBS = (SaleBS)memo.elementAt(2);
            		
            		if (saleBS.saleHead.str2 == null || !saleBS.saleHead.str2.equals("F_XSXP_ADD"))
                	{
            			if (!this.sendSaleGoods(saleBS.saleHead,saleBS.saleGoods)) return false;
                	}
            		
            		if (!this.RetCoupon(saleBS.saleHead,track2))
            		{
            			SalePayDef salepay  = (SalePayDef)memo.elementAt(3);
            			
            			if (!isZrxPayMode(salepay)) 
            			{
            				if (!this.SendCancel(saleBS.saleHead)) return false;
            				
            				bld.retcode	= "0";
            				
            				bld.retbz  = 'N';
            			}
            			
            			
            			return false;
            		}
            		
            		saleBS.saleHead.str2 = "F_XSXP_ADD";	
            	break;	
            	case PaymentBank.XYKCX:
            		saleBS = (SaleBS)memo.elementAt(2);
            		
            		if (saleBS == null)
                	{
                		saleBS = GlobalInfo.saleform.getSaleEvent().saleBS;
                	}
            		
            		if (memo != null && memo.elementAt(3) != null)
            		{
            			SalePayDef salepay  = (SalePayDef)memo.elementAt(3);
            			
            			if (isZrxPayMode(salepay))
            			{
            				if (!this.SendCancelCoupon(saleBS.saleHead,salepay.payno)) return false;
            			}
            			else
            			{
            				if (!this.SendCancelCoupon(saleBS.saleHead,salepay.payno)) return false;
            				
            				if (!this.SendCancel(saleBS.saleHead)) return false;
            			}
            		}
            		
            	break;	
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
			if (dsi != null) dsi = null;
		}
	}
	
	public boolean sendSaleGoods(SaleHeadDef saleHead,Vector saleGoods)
	{
		dsi = getDrmServiceImpl();
		
		for (int i = 0;i < saleGoods.size();i++)
		{
			SaleGoodsDef salegoods = (SaleGoodsDef)saleGoods.get(i);
			
			if (SellType.ISSALE(saleHead.djlb))
			{
				if (dsi.sendFlow(saleHead.syjh,String.valueOf(saleHead.fphm),salegoods.barcode,salegoods.sl,salegoods.lsj,salegoods.hjje,"RMB","自营",salegoods.code,salegoods.catid) != 1) 
				{
					if (bld != null)
					{
						bld.retbz  = 'N';
						bld.retcode = "0";
						bld.retmsg  = "消费传送失败";
						errmsg = bld.retmsg;
					}
					
					return false;
				}
			}
			else if (SellType.ISBACK(saleHead.djlb))
			{
				if (dsi.sendFlow(saleHead.syjh,String.valueOf(saleHead.fphm),salegoods.barcode,salegoods.sl * -1,salegoods.lsj * -1,salegoods.hjje * -1,"RMB","自营",salegoods.code,salegoods.catid) != 1)
				{
					if (bld != null)
					{
						bld.retbz  = 'N';
						bld.retcode = "0";
						bld.retmsg  = "退货传送失败";
						errmsg = bld.retmsg;
					}
					
					return false;
				}
			}
			else
			{
				if (bld != null)
				{
					bld.retbz  = 'N';
					bld.retcode = "0";
					bld.retmsg  = "未知消费";
					errmsg = bld.retmsg;
				}
			
				return false;
			}
		}
		
		if (bld != null) bld.retbz  = 'Y';
		
		return true;
	}
	
	protected boolean SendValidateCoupon(SaleHeadDef saleHead,String couponcode)
	{
		dsi = getDrmServiceImpl();
		
		if (dsi.sendValidateCoupon(saleHead.syjh,String.valueOf(saleHead.fphm),couponcode) == 0)
		{
			bld.retbz  = 'N';
			bld.retcode = "0";
			bld.retmsg = "通讯失败";
			bld.cardno  = couponcode;
			errmsg = bld.retmsg;
			
			return false;
		}
		
		if (dsi.sendValidateCoupon.getIType() != 1)
		{
			bld.retbz  = 'N';
			bld.retcode = String.valueOf(dsi.sendValidateCoupon.getIType());
			bld.retmsg = dsi.sendValidateCoupon.getCError();
			bld.cardno  = couponcode;
			errmsg = bld.retmsg;
			
			return false;
		}
		else
		{
			bld.retbz  = 'Y';
			bld.retcode = String.valueOf(dsi.sendValidateCoupon.getIType());
			bld.retmsg  = "优惠券合法";
			bld.cardno  = couponcode;
			bld.je		= dsi.sendValidateCoupon.getCCouponMoney();
			
			// 优惠券项目号,优惠券模板号
			bld.memo	= dsi.sendValidateCoupon.getCProjectID()+ "," + dsi.sendValidateCoupon.getCTemplateID();
			errmsg = bld.retmsg;
			
			return true;
		}
		
		
	}
	
	private boolean SendCancelCoupon(SaleHeadDef saleHead,String couponcode)
	{
		dsi = getDrmServiceImpl();
		
		if (dsi.sendCancelCoupon(saleHead.syjh,String.valueOf(saleHead.fphm),couponcode) == 0)
		{
			bld.retbz  = 'N';
			bld.retcode = "0";
			bld.retmsg = "通讯失败";
			bld.cardno  = couponcode;
			errmsg = bld.retmsg;
			
			return false;
		}
		
		if (dsi.sendCancelCoupon.getIType() != 1)
		{
			bld.retbz  = 'N';
			bld.retcode	= String.valueOf(dsi.sendCancelCoupon.getIType());
			bld.retmsg	= dsi.sendCancelCoupon.getCError();
			bld.cardno  = couponcode;
			errmsg = bld.retmsg;
			
			return false;
		}
		else
		{
			bld.retbz  = 'Y';
			bld.retcode = String.valueOf(dsi.sendCancelCoupon.getIType());
			bld.retmsg  = "取消成功";
			bld.cardno  = couponcode;
			bld.je		= dsi.sendCancelCoupon.getCCouponMoney();
			
			//	优惠券项目号,优惠券模板号
			bld.memo	= dsi.sendCancelCoupon.getCProjectID()+ "," + dsi.sendCancelCoupon.getCTemplateID();
			errmsg = bld.retmsg;
			
			return true;
		}
	}
	
	public boolean SendFinish(SaleHeadDef saleHead)
	{
		dsi = getDrmServiceImpl();
		
		// 本单已放发知而行优惠券
		dsi.sendFinish(saleHead.syjh,String.valueOf(saleHead.fphm),saleHead.hykh);
		
		return true;
	}
	
	private boolean SendCancel(SaleHeadDef saleHead)
	{
		dsi = getDrmServiceImpl();
		
		// 清除整单,无论是返回失败或成功都返回true
		if (dsi.sendCancel(saleHead.syjh,String.valueOf(saleHead.fphm)) != 1) 
		{
			bld.retcode	= "0";
			bld.retmsg	= "传送失败";
			errmsg = bld.retmsg;
		}
		else
		{
			bld.retcode	= "1";
			bld.retmsg	= "整单清除成功";
			errmsg = bld.retmsg;
			
			saleHead.str2 = "";
		}
		
		bld.retbz  = 'Y';
		
		return true;
	}
	
	private boolean RetCoupon(SaleHeadDef saleHead,String couponcode)
	{
		dsi = getDrmServiceImpl();
		
		if (dsi.retCoupon(saleHead.syjh,String.valueOf(saleHead.fphm),couponcode) == 0)
		{
			bld.retcode = "0";
			bld.retmsg = "通讯失败";
			bld.cardno  = couponcode;
			bld.retbz  = 'N';
			errmsg = bld.retmsg;
			
			return false;
		}
		
		if (dsi.retCoupon.getIType() != 1)
		{
			bld.retcode = String.valueOf(dsi.retCoupon.getIType());
			bld.retmsg = dsi.retCoupon.getCError();
			bld.cardno  = couponcode;
			bld.retbz  = 'N';
			errmsg = bld.retmsg;
			
			return false;
		}
		else
		{
			bld.retcode = String.valueOf(dsi.retCoupon.getIType());
			bld.retmsg  = "优惠券合法";
			bld.cardno  = couponcode;
			bld.je		= dsi.retCoupon.getCCouponMoney();
			bld.retbz  = 'Y';
			errmsg = bld.retmsg;
			
			return true;
		}
	}
	
	private boolean isZrxPayMode(SalePayDef salepay)
	{
		int count = 0;
		
		for (int i = 0;i < saleBS.salePayment.size();i++)
		{
			SalePayDef salepay1 = (SalePayDef)saleBS.salePayment.elementAt(i);
			
			if (!salepay.paycode.equals(salepay1.paycode)) continue;
			
			count = count + 1;
		}
		
		if (count > 0) return true;
		
		return false;
	}
	
	protected DrmServiceImpl getDrmServiceImpl()
	{
		if (dsi == null) dsi = new DrmServiceImpl();
		
		return dsi;
	}
}
