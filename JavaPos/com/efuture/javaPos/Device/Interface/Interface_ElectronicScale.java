package com.efuture.javaPos.Device.Interface;

import java.util.Vector;

public interface Interface_ElectronicScale
{
	public boolean open();

	public void close();

	public void setEnable(boolean enable);

	public Vector getPara();

	public String getDiscription();

	public boolean sendData1(String data);

	public boolean sendData2(String data);

	public boolean sendData3(String data);

	public boolean sendData4(String data);

	public boolean sendData5(String data);

	public void setData1(double memo1);

	public void setData2(double memo2);

	public void setData3(double memo3);

	public void setData4(double memo4);

	public void setData5(double memo5);

	public boolean recvData1();

	public boolean recvData2();

	public boolean recvData3();

	public boolean recvData4();

	public boolean recvData5();

	public double getData1();

	public double getData2();

	public double getData3();

	public double getData4();

	public double getData5();

	public void setWeight(double weight);

	public void setMoney(double money);

	public double getWeight();

	public double getMoney();

}
