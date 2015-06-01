package device.ICCard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import org.eclipse.swt.widgets.Button;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.javaPos.Device.DeviceName;

import mwcard.CardReader.RF_EYE_U010.MWRFJavaAPI;
/**明华KRF-35
 * 若测试失败，请检查org.javapos.lib/windows下的nsvcr100.dll,nwrf32.dll,nwrfjavaapi.dll
 * 以上三个文件是否存在。
 * 
 * */
public class LNBE_ICCard extends LZY_ICCard
{
	private static char[] code = { '0', '3', '1', '5', '8', '9', '2', '4', '6', '7' };

	public static String cardno = "";

	public static short port;
	public static int baud;
	public static short sector;
	public static short block;
	public static String readPwd;
	public static String writePwd;

	private static int getIndex(char c)
	{
		for (int i = 0; i < code.length; i++)
		{
			if (code[i] == c)
				return i;
		}
		return 0;
	}

	public static String encrypt(String cardno)
	{
		String desStr = "";

		int crc = (int) (Math.random() * 10000);
		cardno = String.valueOf(crc) + cardno;

		for (int i = 0; i < cardno.length(); i++)
		{
			switch (cardno.charAt(i))
			{
				case '0':
					desStr += code[0];
					break;
				case '1':
					desStr += code[1];
					break;
				case '2':
					desStr += code[2];
					break;
				case '3':
					desStr += code[3];
					break;
				case '4':
					desStr += code[4];
					break;
				case '5':
					desStr += code[5];
					break;
				case '6':
					desStr += code[6];
					break;
				case '7':
					desStr += code[7];
					break;
				case '8':
					desStr += code[8];
					break;
				case '9':
					desStr += code[9];
					break;
			}
		}
		return desStr;
	}

	public static String decrypt(String key)
	{
		String desStr = "";

		key = key.substring(4, key.length());

		for (int i = 0; i < key.length(); i++)
			desStr += getIndex(key.charAt(i));

		return desStr;
	}

	public String findCard()
	{
		cardno = "";

		String[] arg = DeviceName.deviceICCard.split("\\|");

		try
		{
			// 读取配置信息
			if (arg.length > 1)
			{
				String para[] = arg[1].split(",");

				if (para.length > 0)
					port = (short) Convert.toInt(para[0]);
				if (para.length > 1)
					baud = Convert.toInt(para[1]);
				if (para.length > 2)
					sector = (short) Convert.toInt(para[2]);
				if (para.length > 3)
					block = (short) Convert.toInt(para[3]);
				if (para.length > 4)
					readPwd = para[4];
				if (para.length > 5)
					writePwd = para[5];
			}

			//打开等待界面，调用IC卡设备也在里面开发
			new ICcard_WaitForm().open();

			//判断是否读到了卡
			if (LNBE_ICCard.cardno == null || cardno.equals(""))
			{
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

						}
					}
				});
				return "error:刷卡失败";
			}

			//读到了卡
			String tmpCard = LNBE_ICCard.cardno.trim();

			//对卡信息进行操作
			if (tmpCard.startsWith(";") && tmpCard.endsWith("="))
			{
				tmpCard = decrypt((tmpCard.substring(1, tmpCard.length() - 1)));
			}
			else
			{
				tmpCard = tmpCard.substring(0, tmpCard.length() - 1);
				tmpCard = tmpCard.substring(4, tmpCard.length());
			}

			System.out.println(tmpCard);

			//按退出键返回值
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

	public String updateCardMoney(String card, String operator, double je)
	{
		int icdev = -1;

		try
		{
			String[] arg = DeviceName.deviceICCard.split("\\|");

			if (arg.length > 1)
			{
				String para[] = arg[1].split(",");

				if (para.length > 0)
					port = (short) Convert.toInt(para[0]);
				if (para.length > 1)
					baud = Convert.toInt(para[1]);
				if (para.length > 2)
					sector = (short) Convert.toInt(para[2]);
				if (para.length > 3)
					block = (short) Convert.toInt(para[3]);
				if (para.length > 4)
					readPwd = para[4];
				if (para.length > 5)
					writePwd = para[5];
			}

			// 用于写卡
			card = ";" + encrypt(card) + "=";
			System.out.println("Start of running M1 card.");

//			 若此地报错，请加载 org.javapos.jar/Windows下的nwrf_IC.jar
			icdev = MWRFJavaAPI.getDefault().rf_init((short) LNBE_ICCard.port, LNBE_ICCard.baud);
			System.out.println("init ok");

			if (icdev < 0)
			{
				// 没有接通、没有上电、设备坏了
				System.out.println("\nCann't get device id.");

				return "error:串口打开失败!";
			}

			short st = MWRFJavaAPI.getDefault().rf_request(icdev, (short) 0);
			System.out.println("rf_request ok");

			if (st == 0)
			{
				st = MWRFJavaAPI.getDefault().rf_anticoll(icdev, (short) 0);
				System.out.println("rf_anticoll ok");

				if (st == 0)
				{
					long _Snr = MWRFJavaAPI.getDefault().getSnr();

					st = MWRFJavaAPI.getDefault().rf_select(icdev, _Snr);
					System.out.println("rf_select ok");

					if (st == 0)
					{

						st = MWRFJavaAPI.getDefault().rf_load_key_hex(icdev, (short) 4, sector, LNBE_ICCard.writePwd.toCharArray());
						System.out.println("rf_load_key_hex ok");

						if (st == 0)
						{
							st = MWRFJavaAPI.getDefault().rf_authentication(icdev, (short) 5, sector);
							System.out.println("rf_authentication ok");

							if (st == 0)
							{
								card = ManipulateStr.PadRight(card, 16, ' ');
								st = MWRFJavaAPI.getDefault().rf_write(icdev, block, card.toCharArray());
								System.out.println("rf_write ok"); 
								if (st == 0)
								{
									MWRFJavaAPI.getDefault().rf_beep(icdev, (short) 10);
									new MessageBox("写卡成功");
									
									System.out.println("write ok");
									return "写卡成功";
								}
							}
						}
					}
				}
			}
			return "error:写卡失败";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "error:写卡异常!";
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

				icdev = MWRFJavaAPI.getDefault().rf_init((short) LNBE_ICCard.port, LNBE_ICCard.baud);
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
						st = MWRFJavaAPI.getDefault().rf_load_key_hex(icdev, (short) 0, sector, LNBE_ICCard.readPwd.toCharArray());
						System.out.println("rf_load_key_hex ok");

						if (st == 0)
						{
							st = MWRFJavaAPI.getDefault().rf_authentication(icdev, (short) 0, sector);
							System.out.println("rf_authentication ok");

							if (st == 0)
							{
								st = MWRFJavaAPI.getDefault().rf_read(icdev, block);
								System.out.println("rf_read ok");
								if (st == 0)
								{
									MWRFJavaAPI.getDefault().rf_beep(icdev, (short) 10);

									LNBE_ICCard.cardno = String.copyValueOf(MWRFJavaAPI.getDefault().getData());

									System.out.println(LNBE_ICCard.cardno);
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
						NewKeyListener.sendKey(GlobalVar.Validation);
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
			shell.setSize(2, 2);
			shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);

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
					break;
				case GlobalVar.Validation:
					shell.close();
					shell.dispose();
					break;
			}
		}
	}
}
