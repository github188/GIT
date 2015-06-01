世贸天阶
11762 build 2012.09.26
1.此版本更改了javapos销售界面中营业员后的框内容为“购物中心”，柜组后的框为“租户”。
2.本次javapos更新内容增加多联小票打印不同抬头。
  在基类SaleBillMode.printSellBill()和Bhls_SaleBillMode.printSellBill()中增加printDifTitle()
3.在报表查询主菜单中增加四个子菜单（209、210、211、213），限制允许查询当天数据。