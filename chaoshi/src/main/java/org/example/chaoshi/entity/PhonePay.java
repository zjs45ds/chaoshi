package org.example.chaoshi.entity;

public class PhonePay {
    String phoneNumber;
    String password;
    double balance;
    // 验证支付密码：传入用户输入的密码
    private boolean verifyPayPassword(String inputPwd) {
        return this.password.equals(inputPwd);
    }
    // 扫码支付
    public void pay(double amount, String inputPassword) {
        if (verifyPayPassword(inputPassword)) {
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                System.out.println("支付成功，当前余额为：" + balance);
            } else {
                System.out.println("支付失败：金额无效或余额不足！");
            }
        } else {
            System.out.println("支付密码错误！");
        }
    }

    // 充值
    public void recharge(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("充值成功，当前余额为：" + balance);
        } else {
            System.out.println("充值金额错误！");
        }
    }

    // 修改支付密码
    public void updatePayPwd(String oldPwd, String newPwd1, String newPwd2) {
        if (!verifyPayPassword(oldPwd)) {
            System.out.println("原密码错误！");
            return;
        }
        if (!newPwd1.equals(newPwd2)) {
            System.out.println("两次输入的密码不一致！");
            return;
        }
        this.password = newPwd1;
        System.out.println("修改密码成功！");
    }

    public static void main(String[] args) {
        PhonePay phonePay = new PhonePay();
        phonePay.phoneNumber = "13800138000";
        phonePay.password = "654321";
        phonePay.balance = 500;

        phonePay.recharge(300); // 余额 800
        phonePay.pay(200, "654321"); // 正确密码，余额 600
        phonePay.updatePayPwd("654321", "112233", "112233"); // 改密码成功
        phonePay.pay(600, "112233"); // 用新密码支付，余额 0

        System.out.println("\n最终状态：");
        System.out.println("当前余额为：" + phonePay.balance);
        System.out.println("当前密码为：" + phonePay.password);
        System.out.println("当前手机号为：" + phonePay.phoneNumber);
    }
}