package custom.localize.Zmjc;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextEx;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.swtdesigner.SWTResourceManager;

public class CustZCDForm extends SaleBillMode{

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	private Label label_footmemo;// 底部说明
	private TextEx textEx_flight;// 回程航班
	private Text text_phoneno;// 联系方式
	private DateTime date_hcrq;// 回程日期
	private DateTime date_hcsj;// 回程时间
	private Vector VecFlights;
	private Shell shell = null;
	private boolean isChanageOK;
	private String retMsg;
	protected RetFlightDef zcd=new RetFlightDef();
	protected Zmjc_AccessLocalDB localDB = (Zmjc_AccessLocalDB) AccessLocalDB
			.getDefault();
	private String fphm;
	private String syjh;
	private SaleHeadDef saleHead;

	public void open(SaleHeadDef saleHead,Vector vecFlights) {

		this.saleHead = saleHead;
		VecFlights=vecFlights;
		fphm = String.valueOf(this.saleHead.fphm);
		syjh = this.saleHead.syjh;
		
		final Display display = Display.getDefault();
		createContents();
		CustZCDFormEvent(this);
		loadFlight();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * Create contents of the shell.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(GlobalVar.style | SWT.BORDER | SWT.CLOSE);
		shell.setLayout(new FormLayout());
		shell.setSize(438, 362);
		shell.setText(Language.apply("顾客暂存单"));
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 447, 324);

		Label lblHchb = new Label(composite, SWT.NONE);
		lblHchb.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		lblHchb.setAlignment(SWT.CENTER);
		lblHchb.setBounds(21, 44, 73, 17);
		lblHchb.setText(" " + Language.apply("回程班次"));

		Label lblHcDate = new Label(composite, SWT.NONE);
		lblHcDate.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		lblHcDate.setAlignment(SWT.CENTER);
		lblHcDate.setBounds(21, 94, 73, 17);
		lblHcDate.setText(Language.apply("回程日期"));

		Label lblHcTime = new Label(composite, SWT.NONE);
		lblHcTime.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		lblHcTime.setAlignment(SWT.CENTER);
		lblHcTime.setBounds(21, 144, 73, 17);
		lblHcTime.setText(Language.apply("回程时间"));

		Label lblPhoneNum = new Label(composite, SWT.NONE);
		lblPhoneNum.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		lblPhoneNum.setAlignment(SWT.CENTER);
		lblPhoneNum.setBounds(21, 194, 73, 17);
		lblPhoneNum.setText("*" + Language.apply("联系方式"));

		textEx_flight = new TextEx(composite, SWT.NONE);
		((GridData) textEx_flight.getSource().getLayoutData()).widthHint = 293;
		textEx_flight.getSource().setFont(
				SWTResourceManager.getFont("", 10, SWT.BOLD));
		textEx_flight.setBounds(100, 38, 300, 23);
		textEx_flight.setData("name", Language.apply("回程航班"));

		date_hcrq = new DateTime(composite, SWT.BORDER);
		date_hcrq.setBounds(100, 88, 300, 24);
		date_hcrq.setData("name", Language.apply("回程日期"));
		date_hcrq.setDay(date_hcrq.getDay()-1);

		date_hcsj = new DateTime(composite, SWT.BORDER | SWT.DROP_DOWN
				| SWT.TIME | SWT.SHORT);
		date_hcsj.setBounds(100, 138, 300, 24);
		date_hcsj.setData("name", Language.apply("回程时间"));
		date_hcsj.setHours(0);
		date_hcsj.setMinutes(0);

		text_phoneno = new Text(composite, SWT.BORDER);
		text_phoneno.setBounds(100, 188, 300, 23);
		text_phoneno.setData("name", Language.apply("联系方式"));

		label_footmemo = new Label(composite, SWT.NONE);
		label_footmemo.setBounds(0, 297, 447, 17);
		label_footmemo.setText(Language.apply("提示：按【付款键】保存，按【取消键】关闭窗口，按【回车键】移动输入框焦点"));

	}

	public void CustZCDFormEvent(CustZCDForm form) {
		shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2,
				(GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x,
				shell.getSize().y - GlobalVar.heightPL);

		shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2,
				(GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x,
				shell.getSize().y - GlobalVar.heightPL);

		// 设定键盘事件
		NewKeyEvent event = new NewKeyEvent() {
			public void keyDown(KeyEvent e, int key) {
				// keyPressed(e, key);
			}

			public void keyUp(KeyEvent e, int key) {
				keyReleased(e, key);
			}
		};

		Zmjc_NewKeyListener key = new Zmjc_NewKeyListener();
		key.event = event;
		key.inputMode = key.inputMode;
		text_phoneno.addKeyListener(key);
		textEx_flight.addKeyListener(key);
		date_hcrq.addKeyListener(key);
		date_hcsj.addKeyListener(key);
	}

	public void keyReleased(KeyEvent e, int key) {
		try {
			switch (key) {

			case GlobalVar.Exit:
				int retMessage = new MessageBox(Language.apply("是否退出窗口？"), null, true).verify();
				if (retMessage == GlobalVar.Key1) {
					this.isChanageOK = false;
					this.retMsg = Language.apply("款员取消填写暂存信息");
					this.writeLog("[款员取消]填写暂存信息");
					closeForm();
				}

				break;
			case GlobalVar.Enter:
				controlEvent(e.getSource());
				e.data = "";
				break;
			case GlobalVar.Pay:
				if (sendZCDInfo()) {
					this.isChanageOK = true;
					this.writeLog("[保存退出]刷卡窗口");
					closeForm();
				}
				e.data = "";
				break;

			}
		} catch (Exception ex) {
			this.writeLog(ex);
		}
	}

	private boolean loadFlight() {
		try {
			writeLog("loadFlight() flight_size=[" + this.VecFlights.size()
					+ "]");
			ArrayList items = new ArrayList();
			RetFlightDef f = null;
			for (int i = 0; i < this.VecFlights.size(); i++) {
				f = (RetFlightDef) VecFlights.elementAt(i);
				if (f == null)
					continue;
				items.add(f.rfname + " - " + f.rfcompany  + " - " + f.rfdepar + " - " + f.rfarrtime + " - " + f.rfid);
			}
			this.textEx_flight.setItemsSource(items.toArray());
			loadFlightValue(textEx_flight.getText());
			this.textEx_flight.forceFocus();
			this.textEx_flight.setFocus();
		} catch (Exception ex) {
			this.writeLog(ex);
		}
		return true;
	}

	private void loadFlightValue(String value) {
		try {
			if (value == null || value.length() <= 0) {
				this.textEx_flight.setText("");
				return;
			}
			this.textEx_flight.select(value);
		} catch (Exception ex) {
			this.writeLog(ex);
		}
	}

	private void writeLog(String infos) {
		try {
			PosLog.getLog(this.getClass().getSimpleName()).info(infos);
		} catch (Exception ex) {
			ex.printStackTrace();
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}

	private void writeLog(Exception ex) {
		try {
			PosLog.getLog(this.getClass().getSimpleName()).info(ex);
		} catch (Exception e) {
			e.printStackTrace();
			PosLog.getLog(this.getClass().getSimpleName()).error(e);
		}
	}

	private void closeForm() {
		try {
			shell.close();
		} catch (Exception ex) {
			this.writeLog(ex);
		}
	}

	private void controlEvent(Object ctlSource) {
		try {
			int intRet = checkInput(ctlSource);
			if (intRet == 1) {
				changeCtlFocus(ctlSource);
			} else if (intRet == 2) {
				if (this.textEx_flight.getEnabled()) {
					this.textEx_flight.setFocus();
				} else
					this.date_hcrq.setFocus();
			}
		} catch (Exception ex) {
			this.writeLog(ex);
		}
	}

	/**
	 * 切换控件焦点
	 * 
	 * @param ctlSource
	 */
	private void changeCtlFocus(Object ctlSource) {
		try {
			if (ctlSource instanceof Text) {
				Text txt = (Text) ctlSource;
				if (txt == null)
					return;
				if (txt == this.textEx_flight.getSource()) {
					if (this.date_hcrq.getEnabled())
					{
						this.date_hcrq.setFocus();
					}
					else
					{
						this.text_phoneno.setFocus();
					}
					
				} else if (txt == this.text_phoneno) {
					// TAB 9-->10
					this.textEx_flight.setFocus();
				}
			} else if (ctlSource instanceof DateTime) {
				DateTime datetime = (DateTime) ctlSource;
				if (datetime == null)
					return;
				if (datetime == this.date_hcrq) {
					this.date_hcsj.setFocus();
				} else if (datetime == this.date_hcsj) {
					this.text_phoneno.setFocus();
					text_phoneno.setSelection(text_phoneno.getText().length());
				}
			}
		} catch (Exception ex) {
			this.writeLog(ex);
		}
	}

	private boolean checkInputValue(String value) {
		if (value == null || value.trim().length() <= 0)
			return false;
		return true;
	}

	private int checkInput(Object ctlSource) {
		try {
			if (ctlSource instanceof Text) {
				Text txt = (Text) ctlSource;
				if (txt == null)
					return -1;

				// 先检查是否为空
				if (checkInputValue(this.textEx_flight.getText())==false) {
					//showMsg("[" + Language.apply("回程航班") + "]" + Language.apply("不允许为空"));
					return 1;
				} else {
					if (txt== this.textEx_flight.getSource())
        			{
        				if (checkFlightInput()!=1)
        				{
        					return 0;
        				}
        				return 1;
        			}
					else if (txt == this.text_phoneno) {
						if (!this.checkPhoneNo())
							return 0;
						else
						    return 2;
					}
				}
			} else if (ctlSource instanceof DateTime) {
				/*DateTime datetime = (DateTime) ctlSource;
				if (datetime == null)
					return -1;
				if (datetime == this.date_hcrq) {
					// 1.检查数据的合法性
					if (!ManipulateDateTime.checkDate(ManipulateDateTime
							.getFormatDate(date_hcrq.getYear(),
									date_hcrq.getMonth(), date_hcrq.getDay()))) {
						showMsg("[" + date_hcrq + "]" + Language.apply("数据不合法"));
						return 0;
					}
				}*/
				return 1;
			}
		} catch (Exception ex) {
			this.writeLog(ex);
		}
		return 1;
	}
    
	private int checkFlightInput()
    {
    	int intRet=-1;
    	try
    	{
    		/*if (checkInputValue(this.textEx_flight.getText())){
    		    getFlightId(this.textEx_flight.getText());
    		    intRet=1;
    		    if (zcd.rfarrtime.length()<1) return intRet;
    		}
    		else
    		{
    			this.setTextExFocus(this.textEx_flight);
    			showMsg(textEx_flight.getData("name") + Language.apply("不允许为空"));
    			return 1;//允许为空
    		}*/
    		getFlightId(this.textEx_flight.getText());
		    intRet=1;
		    if (zcd.rfarrtime.length()<1) return intRet;
		    
    		int hours =ManipulateDateTime.getHours(zcd.rfarrtime);;
			int minutes=ManipulateDateTime.getMinutes(zcd.rfarrtime);
			int seconds=ManipulateDateTime.getSeconds(zcd.rfarrtime);
			if (hours>-1 && minutes>-1)
			{
				this.date_hcsj.setHours(hours);
				this.date_hcsj.setMinutes(minutes);
				if (seconds<0)seconds=0;
				this.date_hcsj.setSeconds(seconds);
			}
			else
			{
				writeLog("FindHangBan() date_hcsj=[" + zcd.rfarrtime + "]格式不正确,回程时间赋值失败");
				return 0;
			}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return intRet;
    }
	
	private boolean checkPhoneNo() {
		if (this.text_phoneno.getText().getBytes().length > 11) {
			this.setTextBoxFocus(this.text_phoneno);
			showMsg(text_phoneno.getData("name") + Language.apply("内容输入超过限制") + "11");
			return false;
		}
		if (!isDigit(this.text_phoneno.getText())) {
			this.setTextBoxFocus(this.text_phoneno);
			showMsg(text_phoneno.getData("name") + Language.apply("数据不合法"));
			return false;
		}
		return true;
	}

	private void setTextBoxFocus(Text txt) {
		txt.setFocus();
		txt.selectAll();
	}

	private boolean isDigit(String value) {
		try {
			if (value == null || value.length() <= 0) {
				return true;
			}
			int sz = value.length();
			for (int i = 0; i < sz; i++) {
				if (Character.isDigit(value.charAt(i)) == false) {
					return false;
				}
			}
		} catch (Exception ex) {
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}

	private void showMsg(String msg) {
		new MessageBox(msg);
	}

	private boolean sendZCDInfo()
	{
		try
		{
			if (!getInputValue()) return false;

			ProgressBox pb = null;
			try
			{
				pb = new ProgressBox();
				pb.setText(Language.apply("正在校验回程航班信息...."));
				Zmjc_NetService znt = new Zmjc_NetService();
				String hcsj = ManipulateDateTime.getFormatTime(date_hcsj.getHours(), date_hcsj.getMinutes(), 0).substring(0, 5);
				String hcrq = ManipulateDateTime.getFormatDate(date_hcrq.getYear(), date_hcrq.getMonth(), date_hcrq.getDay());
				String phone = text_phoneno.getText();
				if (getFlightId(textEx_flight.getText()))
				{
					if (new ManipulateDateTime().compareDate(hcrq, ManipulateDateTime.getCurrentDateBySign())<0)
					{//如果日期小于今天，则视为无输入
						hcrq=" - - ";
						hcsj=" : ";
					}
					/*if (date_hcsj.getHours()<=0 && date_hcsj.getMinutes()<=0)
					{//如果时间为0，则视为未输入
						hcsj=" : ";						
					}*/
					if (znt.CheckZCDInfo(zcd, fphm, syjh, hcsj, hcrq, phone) == 1)
					{
						// 暂存单信息存到小票头的 str2 字段，格式为 回程航班编码|回程航班号|回程日期|回程时间|联系方式
						saleHead.str2 = zcd.rfid + "|" + zcd.rfname + "|" + hcrq + "|" + hcsj + "|" + phone;
						//保存暂存单
						saveZCD(saleHead.fphm, saleHead.str2);
						this.isChanageOK = true;
						return true;
					}
				}
			}
			catch (Exception ex)
			{
				this.writeLog(ex);
				new MessageBox(Language.apply("保存寄存信息时异常：") + ex.getMessage());
				return false;
			}
			finally
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}
			}
			return false;
		}

		catch (Exception ex)
		{
			this.writeLog(ex);
			return false;
		}
	}
	
	//保存暂存单到本地小票
	private boolean saveZCD(long fphm,String str2Value)
	{
		boolean isOK = false;
		try
		{
			for(int i=0; i<5; i++)
			{
				if (AccessDayDB.getDefault().updateSaleHeadStr(fphm, "str2", str2Value))
				{
					PosLog.getLog(this.getClass().getSimpleName()).info("寄存单办理成功!");
					isOK = true;
					break;
				}
				Thread.sleep(200);
			}
			if (!isOK) 
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("寄存单办理成功，但打印小票失败!");
				new MessageBox(Language.apply(Language.apply("寄存单办理成功，但打印小票失败")));
			}
			else
			{
				new MessageBox(Language.apply(Language.apply("寄存单办理成功")));
			}
				
		}
		catch(Exception ex)
		{
			this.writeLog(ex);
		}
		return isOK;
	}

	private boolean getInputValue() {
		try {
			// 回程航班
			/*if (!checkInputValue(this.textEx_flight.getText())) {
				this.setTextExFocus(this.textEx_flight);
				showMsg(textEx_flight.getData("name") + Language.apply("不允许为空"));
				return false;
			}*/

			// 手机号码
			if (checkInputValue(this.text_phoneno.getText())) {
				/*if (!checkPhoneNo())
					return false;*/
			} else {
				this.setTextBoxFocus(this.text_phoneno);
				showMsg(text_phoneno.getData("name") + Language.apply("不允许为空"));
				return false;
			}

		} catch (Exception ex) {
			this.writeLog(ex);
			return false;
		}
		return true;
	}

	private void setTextExFocus(TextEx txt) {
		txt.setFocus();
		txt.selectAll();
	}
	
	private boolean getFlightId(String value)
    {
    	try
    	{
    		String v[] = this.textEx_flight.getText().split("-");    		
    		if (v.length<5) 
    		{
    			zcd.rfname=v[0].trim();
    			zcd.rfcompany="";
        		zcd.rfdepar="";
        		zcd.rfarrtime="";
        		zcd.rfid=""; 
        		 
    		}
    		else
    		{
        		zcd.rfname=v[0].trim();
        		zcd.rfcompany=v[1].trim();
        		zcd.rfdepar=v[2].trim();
        		zcd.rfarrtime=v[3].trim();
        		zcd.rfid=v[4].trim();   
        		 	
    		}
    		return true;
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return false;
    }
}
