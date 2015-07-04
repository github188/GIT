package com.royalstone.pos.hardware.iccard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import com.royalstone.pos.hardware.iccard.util.FromSigned;

/**
 * 用JavaComm读写RC500类型的IC卡的底层类
 * @author marco
 */
public class RC500 implements SerialPortEventListener {
	
	private static final int BAUD_RATE = 9600;
	private static byte[] keyOfSector;
	
	static interface Instruction {
		byte F_REQUEST = 0x41;
		byte F_ANTICOLL = 0x42;
		byte F_SELECT = 0x43;
		byte F_AUTHENTICATION = 0x44;
		byte F_HALT = 0x45;
		byte F_READ = 0x46;
		byte F_WRITE = 0x47;
		byte F_INCREMENT = 0x48;
		byte F_DECREMENT = 0x49;
		byte F_RESTORE = 0x4A;
		byte F_TRANSFER = 0x4B;
		byte F_LOADKEY = 0x4C;
		byte F_RESET = 0x4E;
		byte F_GETINFO = 0x4F;
		byte F_CONFIGRC = 0x52;
		byte F_CLOSERC = 0x3F;
		byte F_BEEP = 0x60;
		byte F_READE2 = 0x61;
		byte F_WRITEE2 = 0x62;
	}
	public static interface KeyOfSector {
		byte[] A0A1A2A3A4A5 = FromSigned.toUnsignedByteArray(new int[]{0xa0,
				0xa1, 0xa2, 0xa3, 0xa4, 0xa5});
		byte[] FFFFFFFFFFFF = FromSigned.toUnsignedByteArray(new int[]{0xff,
				0xff, 0xff, 0xff, 0xff, 0xff});
	}
	private static final byte MODE = 0;
	private static final byte BCNT = 0;
	private final byte[] snr = new byte[4];
	private final byte STB_VAL = FromSigned.toUnsignedByte(0x0da);
	private final InputStream input;
	private final OutputStream output;
	private byte[] commResult;
	private static final int DATA_LEN = 20;
	private final SerialPort serialPort;
	public RC500(String port) throws NoSuchPortException, PortInUseException,
			IOException, TooManyListenersException,
			UnsupportedCommOperationException {
		CommPortIdentifier commPort = CommPortIdentifier
				.getPortIdentifier(port);
		this.serialPort = (SerialPort) commPort.open("Mifair", 3000);
		this.output = new BufferedOutputStream(this.serialPort
				.getOutputStream(), DATA_LEN);
		this.input = new BufferedInputStream(this.serialPort.getInputStream(),
				DATA_LEN);
		this.serialPort.setInputBufferSize(DATA_LEN);
		this.serialPort.addEventListener(this);
		this.serialPort.notifyOnDataAvailable(true);
		this.serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	}
	public void close() throws IOException {
		try {
			this.input.close();
		} finally {
			try {
				this.output.close();
			} finally {
				this.serialPort.close();
			}
		}
	}
	private byte[] send(byte instruction) throws IOException {
		final byte[] bytes = this.createByteArray();
		bytes[1] = instruction;
		return this.sendInstruction(bytes);
	}
	private synchronized byte[] sendInstruction(byte[] aBytes)
			throws IOException {
		byte[] bytes = (byte[]) aBytes.clone();
		bytes[0] = this.STB_VAL;
		byte checkSum = bytes[1];
		for (int i = 2; i < bytes.length - 1; i++) {
			checkSum = FromSigned.toUnsignedByte(checkSum ^ bytes[i]);
		}
		bytes[bytes.length - 1] = checkSum;
		output.write(bytes);
		output.flush();
		return this.waitForResult(1000);
	}
	private synchronized byte[] waitForResult(long timeout) throws IOException {
		this.commResult = null;
		try {
			this.wait(timeout);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (this.commResult == null) {
			throw new IOException("time out");
		}
		return this.commResult;
	}
	private byte[] createByteArray() {
		return new byte[DATA_LEN];
	}
	public byte loadKeyOfSector(int sectorIndex, byte[] key) throws IOException {
		System.out.println("load key");
		final byte[] bytes = this.createByteArray();
		bytes[1] = Instruction.F_LOADKEY;
		bytes[2] = MODE;
		bytes[3] = (byte) sectorIndex;
		System.arraycopy(key, 0, bytes, 4, key.length);
		return this.sendInstruction(bytes)[1];
	}
	public byte configRC() throws IOException {
		System.out.println("config rc");
		return this.send(Instruction.F_CONFIGRC)[1];
	}
	public byte request() throws IOException {
		System.out.println("request");
		return this.send(Instruction.F_REQUEST)[1];
	}
	public byte beep() throws IOException {
		System.out.println("beep");
		final byte[] bytes = this.createByteArray();
		final byte howLong = 10;
		bytes[1] = Instruction.F_BEEP;
		bytes[2] = howLong;
		return this.sendInstruction(bytes)[1];
	}
	public byte anticoll() throws IOException {
		System.out.println("anticoll");
		final byte[] bytes = this.createByteArray();
		bytes[1] = Instruction.F_ANTICOLL;
		bytes[2] = BCNT;
		byte[] result = this.sendInstruction(bytes);
		System.arraycopy(result, 2, this.snr, 0, this.snr.length);
		return result[1];
	}
	public byte selectCard() throws IOException {
		System.out.println("Select Card");
		final byte[] bytes = this.createByteArray();
		bytes[1] = Instruction.F_SELECT;
		System.arraycopy(this.snr, 0, bytes, 2, this.snr.length);
		return this.sendInstruction(bytes)[1];
	}
	public byte authenticateSector(int sectorIndex) throws IOException {
		System.out.println("authenticate");
		final byte[] bytes = this.createByteArray();
		bytes[1] = Instruction.F_AUTHENTICATION;
		bytes[2] = MODE;
		bytes[3] = (byte) sectorIndex;
		return this.sendInstruction(bytes)[1];
	}
	public byte[] readBlock(int blockIndex) throws IOException {
		System.out.println("read");
		final byte[] bytes = this.createByteArray();
		bytes[1] = Instruction.F_READ;
		bytes[2] = (byte) blockIndex;
		return this.sendInstruction(bytes);
	}
	public byte writeBlock(int blockIndex, byte[] aBytes) throws IOException {
		System.out.println("write");
		final byte[] bytes = this.createByteArray();
		bytes[1] = Instruction.F_WRITE;
		bytes[2] = (byte) blockIndex;
		System.arraycopy(aBytes, 0, bytes, 3, aBytes.length);
		return this.sendInstruction(bytes)[1];
	}
	public byte halt() throws IOException {
		System.out.println("halt");
		return this.send(Instruction.F_HALT)[1];
	}
	public byte closeRC() throws IOException {
		System.out.println("close RC");
		return this.send(Instruction.F_CLOSERC)[1];
	}
	private synchronized void readCommResult() {
		try {
			if (this.input.available() == DATA_LEN) {
				this.commResult = new byte[this.input.available()];
				this.input.read(this.commResult);
				if (this.commResult[0] != this.STB_VAL) {
					this.commResult = null;
				}
				this.notify();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			this.readCommResult();
		}
	}
	public static byte[] extractData(byte[] commResult) {
		byte[] result = new byte[16];
		System.arraycopy(commResult, 2, result, 0, result.length);
		return result;
	}


	public static byte[] getKeyOfSector() {
		return keyOfSector;
	}

	public static void setKeyOfSector(byte[] bs) {
		keyOfSector = bs;
	}

}