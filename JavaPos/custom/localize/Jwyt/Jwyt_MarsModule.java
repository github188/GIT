package custom.localize.Jwyt;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;

public class Jwyt_MarsModule
{
	private static Jwyt_MarsModule mars;
	private static Jwyt_IdleTask query;
	private MarsSaleRet marsSaleRet;

	public static Jwyt_MarsModule getDefault()
	{
		if (mars == null)
		{
			mars = new Jwyt_MarsModule();
		}

		return mars;
	}

	public void startQueryTimer()
	{
		// 启线程
		query = mars.new Jwyt_IdleTask();
		new Timer().schedule(query, 1000, 1800 * 1000);
	}

	public void stopQueryTimer()
	{
		try
		{
			if (query != null)
				query.cancel();

			query = null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public MarsSaleRet getMarSaleRet()
	{
		return marsSaleRet;
	}

	public void clear()
	{
		marsSaleRet = null;
	}

	// 接收二维码扫描数据
	public String getDataFromVideo(SaleBS saleBS)
	{
		try
		{
			if (saleBS == null)
			{
				return new ReadDataWaitForm().open();
			}
			else
			{
				new ReadDataWaitForm().open(saleBS);
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// 获取终端标识
	public boolean getDeviceSecKey()
	{
		String transSeq = System.currentTimeMillis() + "";
		MarsDeviceClient client = null;
		try
		{
			if (!readConfig())
				return false;

			if (MarsConfig.UptDate != null && !MarsConfig.UptDate.equals(""))
			{
				if (new ManipulateDateTime().compareDate(ManipulateDateTime.getCurrentDate(), MarsConfig.UptDate) <= 0)
					return false;
			}

			client = new MarsDeviceClient();
			String reqJson = client.buildReqParam(transSeq, false);

			if (reqJson == null)
			{
				new MessageBox("生成请求参数失败!");
				return false;
			}

			MarsResponseEntity answerEntity = client.requestForHTTP(GlobalInfo.sysPara.marsurl, reqJson, GlobalInfo.sysPara.marsKey, GlobalInfo.sysPara.marsmerchantId);
			if (answerEntity == null || answerEntity.getContent() == null || answerEntity.getContent().trim().equals(""))
			{
				new MessageBox("返回响应数据失败!");
				return false;
			}

			System.out.println(answerEntity.getContent());

			MarsDeviceRet marsRet = client.parseResParam(transSeq, answerEntity.getContent());

			if (marsRet == null)
				return false;

			if (writeConfig(marsRet))
				readConfig();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("返回响应数据失败\n" + ex.getMessage());
			return false;
		}
	}

	// 验码
	public boolean sendValidateCode(String cryptogram, String assistCode, boolean encrypt)
	{
		String transSeq = System.currentTimeMillis() + "";
		MarsValidateCodeClient client = null;
		try
		{
			// 验码时，直接清Null
			marsSaleRet = null;
			client = new MarsValidateCodeClient();
			String reqJson = client.buildReqParam(transSeq, cryptogram, assistCode, encrypt);

			if (reqJson == null)
			{
				new MessageBox("生成请求参数失败!");
				return false;
			}

			MarsResponseEntity answerEntity = client.requestForHTTP(GlobalInfo.sysPara.marsurl, reqJson, GlobalInfo.sysPara.marsKey, GlobalInfo.sysPara.marsmerchantId);
			if (answerEntity == null || answerEntity.getContent() == null || answerEntity.getContent().trim().equals(""))
			{
				new MessageBox("返回响应数据失败!");
				return false;
			}

			System.out.println(answerEntity.getContent());

			marsSaleRet = client.parseResParam(transSeq, answerEntity.getContent());

			if (marsSaleRet == null)
				return false;
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("返回响应数据失败\n" + ex.getMessage());
			return false;
		}

	}

	// 用码
	public boolean sendUseCode(String assistCode, double money)
	{
		String transSeq = System.currentTimeMillis() + "";
		MarsUseCodeClient client = null;
		try
		{
			client = new MarsUseCodeClient();
			String reqJson = client.buildReqParam(transSeq, assistCode, money);

			if (reqJson == null)
			{
				new MessageBox("生成请求参数失败!");
				return false;
			}

			MarsResponseEntity answerEntity = client.requestForHTTP(GlobalInfo.sysPara.marsurl, reqJson, GlobalInfo.sysPara.marsKey, GlobalInfo.sysPara.marsmerchantId);
			if (answerEntity == null || answerEntity.getContent() == null || answerEntity.getContent().trim().equals(""))
			{
				new MessageBox("返回响应数据失败!");
				return false;
			}

			System.out.println(answerEntity.getContent());

			if (marsSaleRet == null)
				marsSaleRet = new MarsSaleRet();

			return client.parseResParam(marsSaleRet, transSeq, answerEntity.getContent());

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("返回响应数据失败\n" + ex.getMessage());
			return false;
		}
	}

	// 代金凭证退费接口
	public boolean sendReturnMarCoupon(String assistCode, String tcode, double money)
	{
		String transSeq = System.currentTimeMillis() + "";
		MarsReturnMarcouponClient client = null;
		try
		{
			client = new MarsReturnMarcouponClient();
			String reqJson = client.buildReqParam(transSeq, assistCode, tcode, money);

			if (reqJson == null)
			{
				new MessageBox("生成请求参数失败!");
				return false;
			}

			MarsResponseEntity answerEntity = client.requestForHTTP(GlobalInfo.sysPara.marsurl, reqJson, GlobalInfo.sysPara.marsKey, GlobalInfo.sysPara.marsmerchantId);
			if (answerEntity == null || answerEntity.getContent() == null || answerEntity.getContent().trim().equals(""))
			{
				new MessageBox("返回响应数据失败!");
				return false;
			}

			System.out.println(answerEntity.getContent());

			if (marsSaleRet == null)
				marsSaleRet = new MarsSaleRet();

			return client.parseResParam(marsSaleRet, transSeq, answerEntity.getContent());

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("返回响应数据失败\n" + ex.getMessage());
			return false;
		}

	}

	private boolean readConfig()
	{
		BufferedReader br = null;
		try
		{
			br = CommonMethod.readFile("tcode.ini");

			if (br == null)
			{
				new MessageBox("读取配置文件失败!");
				return false;
			}

			String line = null;

			while ((line = br.readLine()) != null)
			{
				if (line == null || line.trim().equals(""))
					continue;

				if (line.indexOf("=") < 0 || line.startsWith("["))
					continue;

				String[] item = line.split("=");

				if (item == null || item.length < 2)
					continue;

				if (item[0].trim().equalsIgnoreCase("deviceId"))
					MarsConfig.DeviceID = item[1].trim();
				else if (item[0].trim().equalsIgnoreCase("sn"))
					MarsConfig.EquipSn = item[1].trim();
				else if (item[0].trim().equalsIgnoreCase("code"))
					MarsConfig.CheckCode = item[1].trim();
				else if (item[0].trim().equalsIgnoreCase("seckey"))
					MarsConfig.SecKey = item[1].trim();
				else if (item[0].trim().equalsIgnoreCase("port"))
					MarsConfig.Port = Convert.toInt(item[1].trim());
				else if (item[0].trim().equalsIgnoreCase("CameraID"))
					MarsConfig.CameraID = Convert.toInt(item[1].trim());
				else if (item[0].trim().equalsIgnoreCase("uptdate"))
					MarsConfig.UptDate = item[1].trim();
			}

			br.close();
			br = null;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("返回响应数据失败\n" + ex.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
			}
			catch (Exception ex)
			{
				br = null;
			}
			br = null;
		}
	}

	private boolean writeConfig(MarsDeviceRet ret)
	{
		PrintWriter pw = null;
		try
		{// c:\\Camera\\
			if (PathFile.fileExist("tcode.ini"))
				PathFile.deletePath("tcode.ini");

			pw = CommonMethod.writeFileUTF("tcode.ini");

			pw.write("[tcode]" + "\r\n");
			pw.write("deviceId  =" + (ret.deviceId == null ? "" : ret.deviceId) + "\r\n");
			pw.write("sn=" + (ret.equipmentSn == null ? "" : ret.equipmentSn) + "\r\n");
			pw.write("code=" + (ret.validCode == null ? "" : ret.validCode) + "\r\n");
			pw.write("seckey=" + (ret.secKey == null ? "" : ret.secKey) + "\r\n");
			pw.write("port=" + (MarsConfig.Port == 0 ? 9991 : MarsConfig.Port) + "\r\n");
			pw.write("cameraid=" + (MarsConfig.CameraID + "" == null ? "" : MarsConfig.CameraID + "") + "" + "\r\n");
			pw.write("uptdate=" + ManipulateDateTime.getCurrentDate() + "\r\n");

			pw.flush();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("返回响应数据失败\n" + ex.getMessage());
			return false;
		}
		finally
		{
			if (pw != null)
				pw.close();
			pw = null;
		}
	}

	public String getError(String code)
	{

		if (code.equals("00"))
			return "成功";
		else if (code.equals("100"))
			return "默认校验通过";
		else if (code.equals("999"))
			return "默认校验不通过";
		else if (code.equals("501"))
			return "终端不存在";
		else if (code.equals("502"))
			return "终端不可用";
		else if (code.equals("503"))
			return "终端已停用";
		else if (code.equals("504"))
			return "终端未分配";
		else if (code.equals("711"))
			return "凭证已回收--凭证代金金额为0（需要其他解决方式）";
		else if (code.equals("712"))
			return "凭证ID不存在";
		else if (code.equals("713"))
			return "凭证作废";
		else if (code.equals("714"))
			return "凭证已使用";
		else if (code.equals("715"))
			return "未在使用期内";
		else if (code.equals("716"))
			return "输入金额或次数错误";
		else if (code.equals("718"))
			return "凭证类型不正确--凭证与接口请求参数的凭证类型不符";
		else if (code.equals("719"))
			return "凭证过期";
		else if (code.equals("720"))
			return "凭证不可用--凭证审核过程中或未通过";
		else if (code.equals("725"))
			return "退还金额过多";
		else if (code.equals("541"))
			return "商户不存在";
		else if (code.equals("542"))
			return "商户不可使用";
		else if (code.equals("543"))
			return "商户不能在此店铺使用";
		else if (code.equals("544"))
			return "密码错误,不可以使用";
		else if (code.equals("545"))
			return "代金--账户余额不足";
		else
			return " 未知错误";
	}

	class Jwyt_IdleTask extends TimerTask
	{
		public void run()
		{
			if (Jwyt_MarsModule.getDefault().getMarSaleRet() != null && !Jwyt_MarsModule.getDefault().getMarSaleRet().isusing)
				Jwyt_MarsModule.getDefault().clear();
		}
	}

	public class ReadDataWaitForm
	{
		public DatagramSocket udpServ = null;
		public Shell shell;
		public Text txt;
		public String data;
		private Process p = null;

		Label label = null;
		Control focus = null;
		Image bkimg = null;

		public void open(final SaleBS saleBS)
		{
			final Display display = Display.getDefault();
			try
			{
				createForm();
				Thread comm = new Thread()
				{
					public void run()
					{
						try
						{
							udpServ = new DatagramSocket(9991);
							udpServ.setSoTimeout(0); // 永不超时

							do
							{
								byte[] recvBuf = new byte[1024];
								DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
								udpServ.receive(recvPacket);

								data = new String(recvPacket.getData(), 0, recvPacket.getLength());
								System.out.println("recvPacket:" + data);

								display.syncExec(new Runnable()
								{
									public void run()
									{
										label.setText("正在查找商品,请稍候......");
										// new MessageBox("code:" + data);

										String[] code = data.split(":");
										if (code[0].equalsIgnoreCase("ISBN-10") || code[0].equalsIgnoreCase("EAN-13"))
										{
											saleBS.saleEvent.saleform.code.setText(code[1]);
											saleBS.saleEvent.saleform.code.setFocus();
											saleBS.enterInput();
										}

										label.setText("请继续扫描商品条码......");
									}
								});
							} while (true);
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				};
				comm.start();

				if (PathFile.fileExist("TCodeScan.exe"))
				{
					p = Runtime.getRuntime().exec("TCodeScan.exe");
				}
				else
				{
					new MessageBox("找不到视频扫描模块 TCodeScan.exe");
					if (!shell.isDisposed())
					{
						shell.close();
					}
				}

				Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

				if (!shell.isDisposed())
				{
					shell.open();
					shell.setFocus();
				}

				while (!shell.isDisposed())
				{
					if (!display.readAndDispatch())
					{
						display.sleep();
					}
				}
				ConfigClass.disposeBackgroundImage(bkimg);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (udpServ != null)
				{
					// udpServ.disconnect();
					udpServ.close();
					udpServ = null;
				}

				if (p != null)
					p.destroy();

			}

		}

		public String open()
		{
			final Display display = Display.getDefault();
			try
			{
				createForm();
				Thread comm = new Thread()
				{
					public void run()
					{
						try
						{
							udpServ = new DatagramSocket(9991);
							udpServ.setSoTimeout(0); // 永不超时

							byte[] recvBuf = new byte[1024];
							DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

							udpServ.receive(recvPacket);

							data = new String(recvPacket.getData(), 0, recvPacket.getLength());
							System.out.println("recvPacket:" + data);

							udpServ.disconnect();
							udpServ.close();
							udpServ = null;

							display.asyncExec(new Runnable()
							{
								public void run()
								{
									shell.close();
									shell.dispose();
								}
							});
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				};
				comm.start();

				if (PathFile.fileExist("TCodeScan.exe"))
				{
					p = Runtime.getRuntime().exec("TCodeScan.exe");
				}
				else
				{
					new MessageBox("找不到视频扫描模块 TCodeScan.exe");
					if (!shell.isDisposed())
					{
						shell.close();
						return null;
					}
				}

				Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

				if (!shell.isDisposed())
				{
					shell.open();
					shell.setFocus();
				}

				while (!shell.isDisposed())
				{
					if (!display.readAndDispatch())
					{
						display.sleep();
					}
				}
				ConfigClass.disposeBackgroundImage(bkimg);
				return data;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			finally
			{
				if (udpServ != null)
				{
					// udpServ.disconnect();
					udpServ.close();
					udpServ = null;
				}

				if (p != null)
					p.destroy();

			}
		}

		protected void createForm()
		{
			focus = Display.getDefault().getFocusControl();
			shell = new Shell(GlobalVar.style_linux);
			shell.setLayout(new FormLayout());

			shell.setSize(500, 105);
			shell.setLocation((GlobalVar.rec.x / 2) - (shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (shell.getSize().y / 2));

			label = new Label(shell, SWT.NONE);
			final FormData formData = new FormData();
			formData.bottom = new FormAttachment(0, 65);
			formData.top = new FormAttachment(0, 35);
			formData.right = new FormAttachment(0, 475);
			formData.left = new FormAttachment(0, 20);
			label.setLayoutData(formData);

			label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
			label.setText("请将操作转向扫码设备...");

			NewKeyEvent event = new NewKeyEvent()
			{
				public void keyDown(KeyEvent e, int key)
				{
					keyPressed(e, key);
				}

				public void keyUp(KeyEvent e, int key)
				{
					keyReleased(e, key);
				}
			};

			NewKeyListener key = new NewKeyListener();
			key.event = event;
			shell.addKeyListener(key);

		}

		public void keyPressed(KeyEvent e, int key)
		{

		}

		public void keyReleased(KeyEvent e, int key)
		{
			switch (key)
			{
				case GlobalVar.Exit:
					shell.close();
					shell.dispose();
					break;
			}
		}
	}
}
