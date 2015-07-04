/*
 * Good Day;
 */
package com.royalstone.pos.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author wubingyan
 * Created by King_net  on 2005-7-19  11:19:25
 */
public class BankCard {

	public static String invoke(String transParams) {

		String result = "001006";
		FileInputStream fin = null;

		try {

			Process p =
				Runtime.getRuntime().exec("TransWrapper.exe " + transParams);

			p.waitFor();

			File file = new File("trans.dat");
			if (file.exists()) {
				fin = new FileInputStream("trans.dat");
				BufferedReader reader =
					new BufferedReader(new InputStreamReader(fin));
				result = reader.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}

		return result;

	}

	public static void main(String[] args) throws Exception {
		System.out.println("****88now begining  to invoke the bankcard*****");
		String retValue =
			invoke("PCA1000012000000000023000034000046225885710673906=00001011113911553111996225885710673906=1561560500050000000015553111214000000000=5710673906=000000000=05000000571000000000000");
		System.out.println("the value of return is  " + retValue);
	}

}
