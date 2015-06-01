package device.ICCard;

import java.util.Vector;

import mwcard.CardReader.RF_EYE_U010.MWRFJavaAPI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Global.GlobalVar;

public class Bxmx_ICCard implements Interface_ICCard
{
	public static String cardno = "";

	public static short port = 100;
	public static int baud = 9600;
	public static short sector = 3;
	public static short block1 = 12;
	public static short block2 = 13;
	public static String readPwd = "343033373936";

	public String findCard()
	{
		Bxmx_ICCard.cardno = "";
		try
		{
			// 打开等待界面，调用IC卡设备也在里面开发
			new ICcard_WaitForm().open();

			// 判断是否读到了卡
			if (Bxmx_ICCard.cardno == null || Bxmx_ICCard.cardno.equals(""))
			{
	/*			Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(100);
							NewKeyListener.sendKey(GlobalVar.Exit);
						}
						catch (Exception ex)
						{

						}
					}
				});*/
				return "error:刷卡失败";
			}

			// 读到了卡
			String tmpCard = Bxmx_ICCard.cardno.trim();

			System.out.println(tmpCard);

			// 按退出键返回值
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					try
					{
						Thread.sleep(100);
						NewKeyListener.sendKey(GlobalVar.Enter);
					}
					catch (Exception ex)
					{

					}
				}
			});

			return tmpCard;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					try
					{
						Thread.sleep(100);
						NewKeyListener.sendKey(GlobalVar.Exit);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
			return "error:刷卡异常";
		}
		finally
		{
			close();
		}
	}

	public boolean close()
	{
		return true;
	}

	public class ReadCardThread implements Runnable
	{
		ICcard_WaitForm form;

		public ReadCardThread(ICcard_WaitForm form)
		{
			this.form = form;
		}

		public void run()
		{
			int icdev = -1;
			try
			{
				System.out.println("Start of running M1 card.");

				icdev = MWRFJavaAPI.getDefault().rf_init((short) Bxmx_ICCard.port, Bxmx_ICCard.baud);
				System.out.println("init ok");

				if (icdev < 0)
				{
					// 没有接通、没有上电、设备坏了
					System.out.println("\nCann't get device id.");

					return;
				}

				// 寻卡,读出卡的ID号
				do
				{
					if (form.isExit)
						return;

					if (MWRFJavaAPI.getDefault().rf_request(icdev, (short) 0) == 0)
						break;
				} while (true);

				System.out.println("rf_request ok");

				short st = MWRFJavaAPI.getDefault().rf_anticoll(icdev, (short) 0);

				System.out.println("rf_anticoll ok");

				if (st == 0)
				{
					long _Snr = MWRFJavaAPI.getDefault().getSnr();

					st = MWRFJavaAPI.getDefault().rf_select(icdev, _Snr);

					System.out.println("rf_select ok");

					if (st == 0)
					{
						System.out.println("rf_select success");

						st = MWRFJavaAPI.getDefault().rf_load_key_hex(icdev, (short) 0, sector, Bxmx_ICCard.readPwd.toCharArray());
						System.out.println("rf_load_key_hex ok");

						if (st == 0)
						{
							System.out.println("rf_load_key_hex success");

							st = MWRFJavaAPI.getDefault().rf_authentication(icdev, (short) 0, sector);
							System.out.println("rf_authentication ok");

							if (st == 0)
							{
								System.out.println("rf_authentication success");

								st = MWRFJavaAPI.getDefault().rf_read_hex(icdev, (short) block1);
								// st = MWRFJavaAPI.getDefault().rf_read(icdev,
								// block1);
								System.out.println("block1 rf_read ok");
								if (st == 0)
								{
									System.out.println("block1 read success");

									char[] card = MWRFJavaAPI.getDefault().getHexData();

									String cardno = String.copyValueOf(card);
									int cardnoLen = Convert.toInt(cardno.substring(cardno.length() - 2, cardno.length()));
									System.out.println("block1:" + cardno);

									// 读二块
									st = MWRFJavaAPI.getDefault().rf_read_hex(icdev, (short) block2);
									System.out.println("block2 rf_read ok");
									if (st == 0)
									{
										System.out.println("block2 read success");

										char[] pass = MWRFJavaAPI.getDefault().getHexData();

										String pwd = String.copyValueOf(pass);

										int pwdLen = Convert.toInt(pwd.substring(pwd.length() - 2, pwd.length()));
										System.out.println("block2:" + pwd);

										Bxmx_ICCard.cardno = cardno.substring(0, cardnoLen) + "=" + pwd.substring(0, pwdLen);
										System.out.println("track:" + Bxmx_ICCard.cardno);
									}

									MWRFJavaAPI.getDefault().rf_beep(icdev, (short) 10);
									System.out.println(Bxmx_ICCard.cardno);
								}
							}
						}
					}
				}
				return;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (icdev > 0)
				{
					// 中止对卡操作
					MWRFJavaAPI.getDefault().rf_halt(icdev);
					// 关闭串口
					MWRFJavaAPI.getDefault().rf_exit(icdev);
					System.out.println("closed");
				}
				System.out.println("End of running M1 card.");

				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						NewKeyListener.sendKey(GlobalVar.Exit);
					}
				});
			}
		}
	}

	public class ICcard_WaitForm
	{
		public Shell shell;
		public Button btn_focus;
		public boolean isExit;
		public Thread thread;

		/**
		 * @wbp.parser.entryPoint
		 */
		public void open()
		{
			isExit = false;
			final Display display = Display.getDefault();
			createContents();

			btn_focus.forceFocus();

			thread = new Thread(new ReadCardThread(this));
			thread.start();

			if (!shell.isDisposed())
			{
				shell.open();
				shell.redraw();
			}

			while (!shell.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
		}

		protected void createContents()
		{
			shell = new Shell(SWT.APPLICATION_MODAL);
			//shell.setSize(1, 1);
			
			shell.setBounds(0,0,1,1);

			btn_focus = new Button(shell, SWT.NONE);
			btn_focus.setBounds(0, 0, 1, 1);

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
			btn_focus.addKeyListener(key);

		}

		public void keyPressed(KeyEvent e, int key)
		{

		}

		public void keyReleased(KeyEvent e, int key)
		{
			switch (key)
			{
				case GlobalVar.Exit:
					isExit = true;
					
					shell.close();
					shell.dispose();
					break;
			}
		}
	}

	public boolean open()
	{
		return true;
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { "读卡命令", " " });
		return v;
	}

	public String getDiscription()
	{
		return "一卡通设备";
	}

}
