package com.royalstone.pos.card;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.gui.MSRInput;
import com.royalstone.pos.gui.ShoppingCardConfirm;
import com.royalstone.pos.gui.DialogInfo;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.Value;
import com.royalstone.pos.util.PosConfig;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * 储值卡主过程，由业务方法调用。
 * 封装了包括从刷卡、查询、用户确认、支付等的动作
 * @author liangxinbiao
 */
public class SHCardProcess {
    private SimpleDateFormat sdfDateTime =
		new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private String payTotal = "0";
	private String exceptionInfo;
    private int isView=1;
    private SHCardPayVO payVO;
    private IShoppingCard shoppingCard;
    private SHCardQueryVO queryVO;
	public SHCardProcess(int isView) {
      this.isView=isView;
      File dir = new File("autorever");
	  if (!dir.exists())
			dir.mkdir();
	}

	public boolean process() {
		MSRInput msrInput = new MSRInput();
		msrInput.show();

		try {
			while (!msrInput.isFinish())
				Thread.sleep(500);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if(!msrInput.isConfirm())return false;
        //---处理是否运行手工输入卡号--------------------
         String permitManualInput=PosConfig.getInstance().getString("IF_HD_VGCD");
         long inputInterval=(long)PosConfig.getInstance().getInteger("INPUT_INTERVAL");
        System.out.println("是否允许手工输入卡号："+permitManualInput);
        System.out.println("刷卡的时间为："+msrInput.getInputInterval());
        System.out.println("有效刷卡的时间为："+inputInterval);
        if(permitManualInput.equals("OFF")&&inputInterval>0){
             long realInputInterval=msrInput.getInputInterval();
             if(realInputInterval>inputInterval){
                  exceptionInfo = "不允许手工输入卡号,请刷卡,请按清楚键继续!";
			     return false;
             }
        }

		//-----------------------
        String inputCode = msrInput.getInputcode();
		if (inputCode == null && inputCode.equals("")) {
			exceptionInfo = "卡号错误,请按清楚键继续!";
			return false;
		}

		String cardNo = null;
		String secrety = null;

		String cardValue[] = inputCode.split("=");
		if (cardValue.length != 2) {
			cardNo = cardValue[0];
			secrety = "0";
		}else{
			cardNo = cardValue[0];
			secrety = cardValue[1];
		}
        shoppingCard = SHCardFactory.createInstance();

      //------------------------------------------
//        MSRInput passInput = new MSRInput("请输入密码","loan");
//		passInput.show();
//
//		try {
//			while (!passInput.isFinish())
//				Thread.sleep(500);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		if(!msrInput.isConfirm())return false;
//
//		String passCode = passInput.getInputcode();
//        if(passCode==null||"".equals(passCode)){
//            if(!secrety.equals("0")){
//                exceptionInfo = "密码错误,请按清楚键继续!";
//			    return false;
//            }
//        }else if(!passCode.equals(secrety)){
//            exceptionInfo = "密码错误,请按清楚键继续!";
//			return false;
//        }

       String ifInputPsaa=shoppingCard.isNeedPass(cardNo);
        if(ifInputPsaa==null){
             exceptionInfo = "网络故障,按清除键继续!";
             return false;
        }
        if(!ifInputPsaa.equals("0")&&!ifInputPsaa.equals("1")){
            exceptionInfo = ifInputPsaa;
             return false;
        }

       if("1".equals(ifInputPsaa)) {

        MSRInput passInput = new MSRInput("请输入密码","loan");
		passInput.show();

		try {
			while (!passInput.isFinish())
				Thread.sleep(500);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if(!msrInput.isConfirm())return false;

		String passCode = passInput.getInputcode();
        if(passCode==null||"".equals(passCode)){
            secrety="0";
        }else {
            secrety=passCode;
        }
       }

     //---------------------------------------------


	    queryVO = shoppingCard.query(cardNo, secrety);

		if (queryVO != null && queryVO.getExceptioninfo() == null) {
			try {
			  PosConfig config=PosConfig.getInstance();
              String vgBottom=config.getString("VGBOTTOM");
              double iVg=0.0;
                try {
                    iVg=Double.parseDouble(vgBottom);
                } catch (NumberFormatException e) {}
               if(iVg>Double.parseDouble(queryVO.getDetail())){
                  DialogInfo notice = new DialogInfo();
                   notice.setMessage("此储值卡余额已低于限额，请回收！");
                   notice.show();
               }
                String tenderAmount;
				if (Double
					.parseDouble(
						(new Value(pos.core.getValue().getValueToPay())
							.toValStr()))
					> Double.parseDouble(queryVO.getDetail())) {
					tenderAmount = queryVO.getDetail();
				} else {
					tenderAmount =
						(new Value(pos.core.getValue().getValueToPay())
							.toValStr());
				}

				ShoppingCardConfirm shoppingCardConfirm =
					new ShoppingCardConfirm();
				shoppingCardConfirm.setCardNo(cardNo);
				shoppingCardConfirm.setTenderAmount(tenderAmount);
				shoppingCardConfirm.setCardAmount(
					Formatter.toMoney(queryVO.getDetail()));
				String cardBalance =
					Formatter.toMoney(
						Double.toString(
							Double.parseDouble(queryVO.getDetail())
								- Double.parseDouble(tenderAmount)));
				shoppingCardConfirm.setBalance(cardBalance);

                //-------------------------------
                queryVO.setDetail(cardBalance);
                queryVO.setCardNO(cardNo);
                //是否查询
                if(this.isView==0)
                  shoppingCardConfirm.setEnterButton(false);
				shoppingCardConfirm.show();

				try {
					while (!shoppingCardConfirm.isFinish())
						Thread.sleep(500);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (shoppingCardConfirm.confirm()) {
					
					PosContext context=PosContext.getInstance();

					payVO = new SHCardPayVO();
					payVO.setCardno(cardNo);
					payVO.setCashierid(context.getCashierid());
					payVO.setCdseq("0");
					payVO.setPassword(secrety);
					payVO.setPayvalue(String.valueOf(tenderAmount));
					payVO.setPosid(context.getPosid());
					payVO.setShopid(context.getStoreid());
					SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
					payVO.setTime(sdf.format(new Date()));

//					String result = shoppingCard.pay(payVO);
//					if (result != null && result.equals("1")) {
//
//						payTotal =
//							Integer.toString(
//								(int) Math.floor(
//									Double.parseDouble(tenderAmount) * 100));
//
//                        pos.core.getPosSheet().setShopCard(queryVO);
//                    }
                   return performPay(tenderAmount);

                }

				return shoppingCardConfirm.confirm();
			} catch (Exception ex) {
				exceptionInfo = "无效操作,按清除键继续!";
			}
		}
		if (exceptionInfo == null) {
			if (queryVO != null && queryVO.getExceptioninfo() != null) {
				exceptionInfo = queryVO.getExceptioninfo() + "!";
			} else {
				if (queryVO == null)
					exceptionInfo = "网络故障,按清除键继续!";
			}
		}
		return false;
	}

    public boolean performPay(String tenderAmount) {
        synchronized (pos.Lock) {
            try {
                String filename =
                    "autorever"
                        + File.separator
                        + sdfDateTime.format(new Date())
                        + ".xml";
                FileOutputStream fs = new FileOutputStream(filename);
                Document doc = new Document(payVO.toElement());
                XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
                outputter.output(doc, fs);
                fs.flush();
                fs.close();
                String result = shoppingCard.pay(payVO);
               // String result=null;
                if (result != null && result.equals("1")) {
                    File file = new File(filename);
                    file.delete();
                    payTotal =
							Integer.toString(
								(int) Math.floor(
									Double.parseDouble(tenderAmount) * 100));
                     pos.core.getPosSheet().setShopCard(queryVO);
                    return true;
                } else {
                    if(result==null)
                        result="储值卡使用失败";
                    exceptionInfo = result + ",按清除键继续!";
                    return false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                exceptionInfo = "无效操作,按清除键继续!";
                return false;
            }
        }
    }


	public String getPayTotal() {
		return payTotal;
	}

	/**
	 * @return
	 */
	public String getExceptionInfo() {
		return exceptionInfo;
	}

}
