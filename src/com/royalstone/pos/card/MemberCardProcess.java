package com.royalstone.pos.card;

import java.text.SimpleDateFormat;

import com.royalstone.pos.gui.MSRInput;
import com.royalstone.pos.util.PosConfig;

/**
 * 挂账卡主过程，由业务方法调用。
 * 封装了包括从刷卡、查询、用户确认、支付等的动作
 * @author liangxinbiao
 */
public class MemberCardProcess {

	private SimpleDateFormat sdfDateTime =
		new SimpleDateFormat("yyyyMMddHHmmssSSS");
	SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmssSSS");

	private String payTotal = "0";
	private String exceptionInfo;
	private String cardNo;
    private MemberCardMgr memberCardMgr;
    private MemberCard  memberCard;
	private boolean isConfirm = false;

	public MemberCardProcess() {

	}

	/**
	 * 在一单开始时刷卡
	 * @return 卡号
	 */
	public MemberCard readLoanCardNum() {
		MSRInput msrInput = new MSRInput("请刷卡:","loan");

		msrInput.show();

		try {
			while (!msrInput.isFinish())
				Thread.sleep(500);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		PosConfig config=PosConfig.getInstance();
		String inputCode="";
		String memStyle=config.getString("MEMCD_STYLE");
		if(!memStyle.equals("")){
			String inputCodeOrg = msrInput.getInputcode();
	
			int cardBegin=0;
			int cardLen=0;
			int len=memStyle.length();
			int index=memStyle.indexOf(';');
			
			try{
				if(index==0){
					cardBegin=0;
				}else{
					cardBegin=Integer.parseInt(memStyle.substring(0,index));
				}
				
				cardLen=Integer.parseInt(memStyle.substring(index+1));
				
			}catch(NumberFormatException ex){
				ex.printStackTrace();
				exceptionInfo = "卡号格式错误,按清除键继续!";
				return null;
			}
			
			if (cardLen+cardBegin-1>inputCodeOrg.length()) {
				exceptionInfo = "卡号格式错误,按清除键继续!";
				return null;
			}

			inputCode=inputCodeOrg.substring(cardBegin-1,cardBegin+cardLen-1);
			
			
	    }else{
			inputCode=msrInput.getInputcode();
		}
		if (!msrInput.isConfirm()) {
			isConfirm = false;
			return null;
		} else {
			isConfirm = true;
		}
       //---处理是否运行手工输入卡号--------------------
         String permitManualInput=PosConfig.getInstance().getString("IF_HD_VGCD");
         long inputInterval=(long)PosConfig.getInstance().getInteger("INPUT_INTERVAL");
        System.out.println("是否允许手工输入卡号："+permitManualInput);
        System.out.println("刷卡的时间为："+msrInput.getInputInterval());
        System.out.println("有效刷卡的时间为："+inputInterval);
        if(permitManualInput.equals("OFF")&&inputInterval>0){
             long realInputInterval=msrInput.getInputInterval();
             if(realInputInterval>inputInterval){
                  exceptionInfo = "不允许手工输入卡号,请刷卡,请按清除键继续!";
			     return null;
             }
        }

		//-----------------------
		if (inputCode == null && inputCode.equals("")) {
			exceptionInfo = "卡号错误,按清除键继续!";
			return null;
		}

		String cardNo = null;
		String secrety = null;

		String cardValue[] = inputCode.split("=");
		if (cardValue.length != 2) {
			cardNo = cardValue[0];
			secrety = "0";
		} else {
			cardNo = cardValue[0];
			secrety = cardValue[1];
		}

		try {
			memberCardMgr = MemberCardMgrFactory.createInstance();
		} catch (Exception e) {
			e.printStackTrace();
			this.exceptionInfo="POS服务器参数配置错误,按清除键继续!";
		}
		MemberCard cardInfo=null;
		try {
			cardInfo = memberCardMgr.query(cardNo);
		} catch (Exception e) {
			e.printStackTrace();
			this.exceptionInfo="网络故障,按清除键继续!";
		}
		
		if (exceptionInfo == null&&cardInfo == null) {
			exceptionInfo = "此卡不存在,按清除键继续!!";
	    }
        if(cardInfo!=null&&cardInfo.getExceptionInfo()!=null)
             exceptionInfo = cardInfo.getExceptionInfo();



		return cardInfo;
	}



	/**
	 * @return 返回错误提示
	 */
	public String getExceptionInfo() {
		return exceptionInfo;
	}



	/**
	 * @return 卡号
	 */
	public String getCardNo() {
		return cardNo;
	}


}
