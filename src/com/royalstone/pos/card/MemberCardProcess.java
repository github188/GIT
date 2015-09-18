package com.royalstone.pos.card;

import java.text.SimpleDateFormat;

import com.royalstone.pos.gui.MSRInput;
import com.royalstone.pos.util.PosConfig;

/**
 * ���˿������̣���ҵ�񷽷����á�
 * ��װ�˰�����ˢ������ѯ���û�ȷ�ϡ�֧���ȵĶ���
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
	 * ��һ����ʼʱˢ��
	 * @return ����
	 */
	public MemberCard readLoanCardNum() {
		MSRInput msrInput = new MSRInput("��ˢ��:","loan");

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
				exceptionInfo = "���Ÿ�ʽ����,�����������!";
				return null;
			}
			
			if (cardLen+cardBegin-1>inputCodeOrg.length()) {
				exceptionInfo = "���Ÿ�ʽ����,�����������!";
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
       //---�����Ƿ������ֹ����뿨��--------------------
         String permitManualInput=PosConfig.getInstance().getString("IF_HD_VGCD");
         long inputInterval=(long)PosConfig.getInstance().getInteger("INPUT_INTERVAL");
        System.out.println("�Ƿ������ֹ����뿨�ţ�"+permitManualInput);
        System.out.println("ˢ����ʱ��Ϊ��"+msrInput.getInputInterval());
        System.out.println("��Чˢ����ʱ��Ϊ��"+inputInterval);
        if(permitManualInput.equals("OFF")&&inputInterval>0){
             long realInputInterval=msrInput.getInputInterval();
             if(realInputInterval>inputInterval){
                  exceptionInfo = "�������ֹ����뿨��,��ˢ��,�밴���������!";
			     return null;
             }
        }

		//-----------------------
		if (inputCode == null && inputCode.equals("")) {
			exceptionInfo = "���Ŵ���,�����������!";
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
			this.exceptionInfo="POS�������������ô���,�����������!";
		}
		MemberCard cardInfo=null;
		try {
			cardInfo = memberCardMgr.query(cardNo);
		} catch (Exception e) {
			e.printStackTrace();
			this.exceptionInfo="�������,�����������!";
		}
		
		if (exceptionInfo == null&&cardInfo == null) {
			exceptionInfo = "�˿�������,�����������!!";
	    }
        if(cardInfo!=null&&cardInfo.getExceptionInfo()!=null)
             exceptionInfo = cardInfo.getExceptionInfo();



		return cardInfo;
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
