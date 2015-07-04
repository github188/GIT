package com.royalstone.pos.card;

import java.text.SimpleDateFormat;

import com.royalstone.pos.gui.MSRInput;
import com.royalstone.pos.util.PosConfig;

/**
 * ��ҵ�񷽷����á�
 * ��װ�˾�ŵ�ȫ����
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
	 * ��һ����ʼʱˢ��
	 * @return ����
	 */
	public String readLoanCardNum() {
		MSRInput msrInput = new MSRInput("��ˢ���þ�:","loan");

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
			exceptionInfo = "��Ŵ���,�����������!";
			return null;
		}
		return inputCode;
	}

	/**
	 * @return ���ش�����ʾ
	 */
	public String getExceptionInfo() {
		return exceptionInfo;
	}
	
	/**
	 * @return ����
	 */
	public String getCardNo() {
		return cardNo;
	}
}