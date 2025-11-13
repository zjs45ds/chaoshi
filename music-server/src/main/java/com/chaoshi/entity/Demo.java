package com.chaoshi.entity;

public class Demo {
    String id;
    String psw;
    double money;
    // 存款
    public void save_money(double x){
        System.out.println(""+x+(x+money));
    }
    // 取款
    public void withdraw_money(double y){
        money=money-y;
        System.out.println(""+y+(money-y));
    }
    // 查询余额
    public double getBalance() {
        System.out.println("" + money);
        return money;
    }
    public void change_psw(String p){
        psw=p;
        System.out.println(""+p);
    }

}