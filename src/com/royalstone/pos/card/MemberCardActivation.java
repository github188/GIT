package com.royalstone.pos.card;

import java.text.SimpleDateFormat;

import com.royalstone.pos.gui.MSRInput;
import com.royalstone.pos.util.PosConfig;

/**
 * 由业务方法调用。
 * 封装了卷号的全过程
 * @author zhouzhou
 */
public class MemberCardActivation {

	private SimpleDateFormat sdfDateTime =
		new SimpleDateFormat("yyyyMMddHHmmssSSS");
	SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmssSSS");

	private String payTotal = "0";
	private String exceptionInfo;
	private String cardNo;
	private boolean isConfirm = false;

	public MemberCardActivation() {

	}

	/**
	 * 在一单开始时刷卡
	 * @return 卡号
	 */
	public String readLoanCardNum() {
		MSRInput msrInput = new MSRInput("请刷抵用卷:","loan");

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
			
	    }else{
			inputCode=msrInput.getInputcode();
		}
		if (!msrInput.isConfirm()) {
			isConfirm = false;
			return null;
		} else {
			isConfirm = true;
		}

		//-----------------------
		if (inputCode == null && inputCode.equals("")) {
			exceptionInfo = "卷号错误,按清除键继续!";
			return null;
		}
		return inputCode;
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