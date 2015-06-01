package bankpay.alipay.tools;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


/**
 * 声波控件的Java封装。<BR>
 * @author wuxie.wl
 *
 */
public class SonicWaveNFC {

	/**
	 * 以下是Com组件的OnDataRecived事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnDataReceived extends Callback {
        void invoke(String data);
    }
    
	/**
	 * 以下是Com组件的OnReceiveDataInfo事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnReceiveDataInfo extends Callback {
        void invoke(String info);
    }
    
	/**
	 * 以下是Com组件的OnReceiveDataTimeout事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnReceiveDataTimeout extends Callback {
        void invoke();
    }
    
	/**
	 * 以下是Com组件的OnSendDataTimeout事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnSendDataTimeout extends Callback {
        void invoke();
    }
    
	/**
	 * 以下是Com组件的OnSendDataInfo事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public  interface SonicWaveOnSendDataInfo extends Callback {
        void invoke(String info);
    }
    
	/**
	 * 以下是Com组件的OnSendDataStarted事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnSendDataStarted extends Callback {
        void invoke();
    }
    
	/**
	 * 以下是Com组件的OnSendDataFailed事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnSendDataFailed extends Callback {
        void invoke(short reason);
    }
    
	/**
	 * 以下是Com组件的OnReceiveDataStarted事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnReceiveDataStarted extends Callback {
        void invoke();
    }
    
	/**
	 * 以下是Com组件的OnReceiveDataFailed事件的接收。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveOnReceiveDataFailed extends Callback {
        void invoke(short reason);
    }
    
	/**
	 * DLL库映映射。<BR>
	 * @author wuxie.wl
	 *
	 */
	public interface SonicWaveNFCLib extends Library {
        
		/**
		 * 映射DLL中的SonicWaveCreate方法。<BR>
		 */
        void SonicWaveCreate();
        
        /**
         * 映射DLL中的SonicWaveDestroy方法。<BR>
         */
    	void SonicWaveDestroy();
    	
    	/**
    	 * 映射DLL中的SonicWaveStartSendData方法。<BR>
    	 * @param str
    	 * @param iTimeoutSeconds
    	 * @param iSoundType
    	 * @param iVolume
    	 * @return
    	 */
    	Boolean SonicWaveStartSendData(String str, long iTimeoutSeconds, short iSoundType, short iVolume);
    	
    	/**
    	 * 映射DLL中的SonicWaveStopSendData方法。<BR>
    	 */
    	void SonicWaveStopSendData();
    	
    	/**
    	 * 映射DLL中的SonicWaveStartReceiveData方法。<BR>
    	 * @param iTimeoutSeconds
    	 * @param iMinAmplitude
    	 * @return
    	 */
    	Boolean SonicWaveStartReceiveData(long iTimeoutSeconds, short iMinAmplitude);
    	
    	/**
    	 * 映射DLL中的SonicWaveStopReceiveData方法。<BR>
    	 */
    	void SonicWaveStopReceiveData();
    	
    	/**
    	 * 映射DLL中的SonicWaveGetMicrophoneName方法。<BR>
    	 * @return
    	 */
    	Pointer SonicWaveGetMicrophoneName();
    	
    	/**
    	 * 映射DLL中的SonicWaveGetMicrophoneStatus方法。<BR>
    	 * @return
    	 */
    	long SonicWaveGetMicrophoneStatus();
    	
    	/**
    	 * 映射DLL中的SonicWaveSetDefaultBkSoundWave4Mix方法。<BR>
    	 * @return
    	 */
    	Boolean SonicWaveSetDefaultBkSoundWave4Mix();
        
    	/**
    	 * OnDataReceived事件的回调。<BR>
    	 * 
    	 * @param callback
    	 */
        void SonicWaveSetDataReceivedCallback(SonicWaveOnDataReceived callback);
        
        /**
         * OnReceiveDataInfo事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetReceiveDataInfoCallback(SonicWaveOnReceiveDataInfo callback);
        
        /**
         * OnReceiveDataTimeout事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetReceiveDataTimeoutCallback(SonicWaveOnReceiveDataTimeout callback);
        
        /**
         * WaveOnSendDataTimeout事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetSendDataTimeoutCallback(SonicWaveOnSendDataTimeout callback);
        
        /**
         * OnSendDataInfo事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetSendDataInfoCallback(SonicWaveOnSendDataInfo callback);
        
        /**
         * SendDataStarted事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetSendDataStartedCallback(SonicWaveOnSendDataStarted callback);
        
        /**
         * OnSendDataFailed事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetSendDataFailedCallback(SonicWaveOnSendDataFailed callback);
        
        /**
         * OnReceiveDataStarted事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetReceiveDataStartedCallback(SonicWaveOnReceiveDataStarted callback);
        
        /**
         * OnReceiveDataFailed事件的回调。<BR>
         * 
         * @param callback
         */
        void SonicWaveSetReceiveDataFailedCallback(SonicWaveOnReceiveDataFailed callback);
    }
	
	public static SonicWaveNFC INSTANCE = new SonicWaveNFC();
	
	private SonicWaveNFCLib sonicWaveNFCLib = (SonicWaveNFCLib)Native.loadLibrary("SonicWaveDllWrapper", SonicWaveNFCLib.class);

	private SonicWaveOnDataReceived dataReceivedCallback;
	
	private SonicWaveOnReceiveDataInfo receiveDataInfoCallback;
	
	private SonicWaveOnReceiveDataTimeout receiveDataTimeoutCallback;
	
	private SonicWaveOnSendDataTimeout sendDataTimeoutCallback;
	
	private SonicWaveOnSendDataInfo sendDataInfoCallback;
	
	private SonicWaveOnSendDataStarted sendDataStartedCallback;
	
	private SonicWaveOnSendDataFailed sendDataFailedCallback;
	
	private SonicWaveOnReceiveDataStarted receiveDataStartedCallback;
	
	private SonicWaveOnReceiveDataFailed receiveDataFailedCallback;
	
	/**
	 * 不允许外部构造对象。<BR>
	 */
	private SonicWaveNFC(){
		
	}
	
	/**
	 * 创建声波控件。<BR>
	 */
	public void create(){		
		sonicWaveNFCLib.SonicWaveCreate();				
	}
	
	/**
	 * 销毁声波控件。<BR>
	 */
	public void destroy(){
		sonicWaveNFCLib.SonicWaveDestroy();
	}
	

//	/**
//	 * 开始发送数据。<BR>
//	 * 
//	 * @param str
//	 * @param iTimeoutSeconds
//	 * @param iSoundType
//	 * @param iVolume
//	 * @return
//	 */
//	public Boolean startSendData(String str, long iTimeoutSeconds, short iSoundType, short iVolume){
//		return sonicWaveNFCLib.SonicWaveStartSendData(str, iTimeoutSeconds, iSoundType, iVolume);
//	}
//	
//	/**
//	 * 停止发送数据。<BR>
//	 */
//	public void stopSendData(){
//		sonicWaveNFCLib.SonicWaveStopSendData();
//	}
//	
	/**
	 * 开始接收数据。<BR>
	 * 
	 * @param iTimeoutSeconds
	 * @param iMinAmplitude
	 */
	public void startReceiveData(int iTimeoutSeconds, short iMinAmplitude){		
		sonicWaveNFCLib.SonicWaveStartReceiveData(iTimeoutSeconds, iMinAmplitude);	
	}
	
	/**
	 * 停止接收数据。<BR>
	 */
	public void stopReceiveData(){
		sonicWaveNFCLib.SonicWaveStopReceiveData();
	}
	
	/**
	 * 取得麦克风的名称。<BR>
	 * 
	 * @return
	 */
	public String getMicrophoneName(){			
		return sonicWaveNFCLib.SonicWaveGetMicrophoneName().getString(0);
	}
	
	/**
	 * 取得麦克风的状态.<BR>
	 * 
	 * @return
	 */
	public long getMicrophoneStatus(){
		return sonicWaveNFCLib.SonicWaveGetMicrophoneStatus();
	}
	
//	/**
//	 * 
//	 * 
//	 * @return
//	 */
//	public Boolean setDefaultBkSoundWave4Mix(){
//		return sonicWaveNFCLib.SonicWaveSetDefaultBkSoundWave4Mix();
//	}	
//	
	/**
	 * 设置声波接收的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setDataReceivedCallback(SonicWaveOnDataReceived callback){
		dataReceivedCallback = callback;
		sonicWaveNFCLib.SonicWaveSetDataReceivedCallback(dataReceivedCallback);
	}
	
	/**
	 * 设置声波接收信息的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setReceiveDataInfoCallback(SonicWaveOnReceiveDataInfo callback){
		receiveDataInfoCallback = callback;
		sonicWaveNFCLib.SonicWaveSetReceiveDataInfoCallback(receiveDataInfoCallback);
	}
	
	/**
	 * 设置声波接收超时的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setReceiveDataTimeoutCallback(SonicWaveOnReceiveDataTimeout callback){
		receiveDataTimeoutCallback = callback;		
		sonicWaveNFCLib.SonicWaveSetReceiveDataTimeoutCallback(receiveDataTimeoutCallback);        
	}
	
	/**
	 * 设置声波发送超时的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setSendDataTimeoutCallback(SonicWaveOnSendDataTimeout callback){
		sendDataTimeoutCallback = callback;
		sonicWaveNFCLib.SonicWaveSetSendDataTimeoutCallback(sendDataTimeoutCallback);
		
	}
	
	/**
	 * 设置声波发送超时的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setSendDataInfoCallback(SonicWaveOnSendDataInfo callback){
    	sendDataInfoCallback = callback;
    	sonicWaveNFCLib.SonicWaveSetSendDataInfoCallback(sendDataInfoCallback);
    }
    
	/**
	 * 设置声波发送开始的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setSendDataStartedCallback(SonicWaveOnSendDataStarted callback){
    	sendDataStartedCallback = callback;
    	sonicWaveNFCLib.SonicWaveSetSendDataStartedCallback(sendDataStartedCallback);
    }
    
	/**
	 * 设置声波发送失败的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setSendDataFailedCallback(SonicWaveOnSendDataFailed callback){
    	sendDataFailedCallback = callback;
    	sonicWaveNFCLib.SonicWaveSetSendDataFailedCallback(sendDataFailedCallback);
    }
    
	/**
	 * 设置声波接收开始的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setReceiveDataStartedCallback(SonicWaveOnReceiveDataStarted callback){
    	receiveDataStartedCallback = callback;
    	sonicWaveNFCLib.SonicWaveSetReceiveDataStartedCallback(receiveDataStartedCallback);
    }
    
	/**
	 * 设置声波接收失败的回调器。<BR>
	 * 
	 * @param callback
	 */
	public void setReceiveDataFailedCallback(SonicWaveOnReceiveDataFailed callback){
    	receiveDataFailedCallback = callback;
    	sonicWaveNFCLib.SonicWaveSetReceiveDataFailedCallback(receiveDataFailedCallback);
    }
    
	/**
	 * 验证NFC数据是否合法。<BR>
	 * 
	 * @param nfcData
	 * @return
	 */
	public Boolean validateNfc(String nfcData )
    {
    	if((nfcData.length()<16) || (nfcData.length()>24))    	{
    		return false;
    	}

    	String lowerNfcData=nfcData.toLowerCase();
    
    	if(lowerNfcData.length()>16)
    	{      
    		if(lowerNfcData.charAt(0) !='k')
    		{
    			return false;
    		}

    		if(lowerNfcData.charAt(1) !='f')
    		{
    			return false;
    		}

    		return true;
    	}


    	for (int i = 0 ; i <lowerNfcData.length();i++)  
    	{  
    		char charValue = lowerNfcData.charAt(i);  
    		if (charValue <= '9' && charValue >= '0')  
    		{  
    			continue;

    		}
    		if (charValue <= 'f' && charValue >= 'a')  
    		{  
    			continue;
    		}      

    		return false;
    	}

    	return true;
    }

}
