package bankpay.alipay.tools;

import com.efuture.DeBugTools.PosLog;

public class SonicWave extends Thread
{
	public static boolean flag = true;
	public static String tempId = "";
	public void run()
	{
	try 
	{
		SonicWaveNFC.INSTANCE.setDataReceivedCallback(new SonicWaveNFC.SonicWaveOnDataReceived(){

			
			public void invoke(String data) 
			{
				
					flag = false;
					tempId = data;
			
			    //PosLog.getLog(getClass()).info("SonicWaveOnDataReceived====>>"+data);
			}
			
		});
		
		SonicWaveNFC.INSTANCE.setReceiveDataInfoCallback(new SonicWaveNFC.SonicWaveOnReceiveDataInfo() {
			
			public void invoke(String info) {
				// TODO Auto-generated method stub
				//System.out.println("11111"+"SonicWaveOnDataReceived");
				//System.out.println("11111"+info);
			}
		});
		
		
		SonicWaveNFC.INSTANCE.setReceiveDataTimeoutCallback(new SonicWaveNFC.SonicWaveOnReceiveDataTimeout() {
			
			
			public void invoke() {
				// TODO Auto-generated method stub
				flag = false;
				//System.out.println("4444setReceiveDataTimeoutCallback====>"+flag);
				
			}
		});
		
		SonicWaveNFC.INSTANCE.setReceiveDataStartedCallback(new SonicWaveNFC.SonicWaveOnReceiveDataStarted() {
			
			
			public void invoke() {
				// TODO Auto-generated method stub
				//System.out.println("5555setReceiveDataStartedCallback");
			}
		});
		
		SonicWaveNFC.INSTANCE.setSendDataStartedCallback(new SonicWaveNFC.SonicWaveOnSendDataStarted() {
			
			public void invoke() {
				// TODO Auto-generated method stub
				//System.out.println("6666setSendDataStartedCallback");
			}
		});
		
		SonicWaveNFC.INSTANCE.setSendDataInfoCallback(new SonicWaveNFC.SonicWaveOnSendDataInfo() {
			
			
			public void invoke(String info) {
				// TODO Auto-generated method stub
				//System.out.println("setSendDataInfoCallback");
				//System.out.println("3333"+info);
			}
		});
		
		SonicWaveNFC.INSTANCE.startReceiveData(5, (short)20);
	
		while(flag)
		{
			Thread.sleep(1000);
		}
//		SonicWaveNFC.INSTANCE.destroy();
		
		
	}
	catch(Exception er)
	{
		er.printStackTrace();
	}
		
}
	
}
