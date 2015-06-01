package posserver.tools.PressTest;

public class ThreadStatus {
	private Thread thread = null;
	private int index = 0;
	private boolean issuc = true;
	private int runCount = 0;
	private int sucCount = 0;
	private int errorCount = 0;
	private String result = "";
	private String starttime = "";
	private String stoptime = "";
	
	public void setIndex(int index) {
		this.index = index;
	}
	public int getIndex() {
		return index;
	}
	public void setIssuc(boolean issuc) {
		this.issuc = issuc;
	}
	public boolean isIssuc() {
		return issuc;
	}
	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}
	public int getRunCount() {
		return runCount;
	}
	public void setSucCount(int sucCount) {
		this.sucCount = sucCount;
	}
	public int getSucCount() {
		return sucCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public int getErrorCount() {
		return errorCount;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResult() {
		return result;
	}
	
	public void increseRunCount()
	{
		this.runCount ++;
		GlobalStatus.increseCmdRoundTimes();
	}

	public void increseSucCount()
	{
		this.sucCount ++;
		GlobalStatus.increseCmdSendTimes();
	}
	
	public void increseErrorCount()
	{
		errorCount ++;
		GlobalStatus.increseCmdSendFaileTimes();
		GlobalStatus.increseCmdSendTimes();
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	public Thread getThread() {
		return thread;
	}
	public void setStoptime(String stoptime) {
		this.stoptime = stoptime;
	}
	public String getStoptime() {
		return stoptime;
	}
}
