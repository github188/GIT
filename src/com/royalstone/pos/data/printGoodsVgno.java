/*
 * �������� 2006-1-18
 *
 * TODO Ҫ���Ĵ����ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package com.royalstone.pos.data;

import java.util.*;
/**
 *  ��Ʒ�ϼƴ�ӡСƱ
 *  
 */
public class printGoodsVgno {
	
	public printGoodsVgno(){
		lst = new Vector();
		}
	
	
	public boolean add(String goodsVgno){
		for (int i = 0; i < lst.size(); i ++){
			if (lst.get(i).equals(goodsVgno)){
				return false;
				}
			}
		lst.add(goodsVgno);
		return true;
		}
	
	private Vector lst;

}
