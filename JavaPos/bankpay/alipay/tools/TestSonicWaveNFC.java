package bankpay.alipay.tools;




public class TestSonicWaveNFC {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException 
	{
		System.out.println("over");
		new TestSonicWaveNFC();
	}
	
	public TestSonicWaveNFC()
	{
		SonicWave sonicWave = new SonicWave();
		sonicWave.start();
		System.out.println("SonicWave1====>"+sonicWave.flag);
		
		if(sonicWave.isAlive())
		{
			System.out.println("SonicWave3====>"+sonicWave.flag);
		}
		
		while(sonicWave.flag)
		{
			System.out.println("flag====>");
			try {
				sonicWave.sleep(1000);
				SonicWaveNFC.INSTANCE.destroy();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("temp====>"+sonicWave.tempId);

	}
}
