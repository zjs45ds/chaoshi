package org.example.chaoshi.entity;
import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char choice;

        while (true) {
            // 输入第一个数
            System.out.print("请输入第一个数：");
            double a = scanner.nextDouble();

            // 输入第二个数
            System.out.print("请输入第二个数：");
            double b = scanner.nextDouble();

            // 输入运算符
            System.out.print("请输入运算符 (+, -, *, /): ");
            char op = scanner.next().charAt(0);

            // 计算结果
            double result = 0;
            boolean valid = true;

            if (op == '+') {
                result = a + b;
            } else if (op == '-') {
                result = a - b;
            } else if (op == '*') {
                result = a * b;
            } else if (op == '/') {
                if (b != 0) {
                    result = a / b;
                } else {
                    System.out.println("错误：除数不能为零！");
                    valid = false;
                }
            } else {
                System.out.println("错误：不支持的运算符！");
                valid = false;
            }

            // 输出结果
            if (valid) {
                // 判断是否为整数
                if (result == (int) result) {
                    System.out.println("结果是：" + (int)a + op + (int)b + "=" + (int)result);
                } else {
                    System.out.println("结果是：" + a + op + b + "=" + result);
                }
            }

            // 是否继续
            System.out.print("请问要退出吗？y/n: ");
            choice = scanner.next().charAt(0);

            // 如果输入 y 或 Y，就退出循环
            if (choice == 'y' || choice == 'Y') {
                break;  // 退出循环
            }
        }

        System.out.println("程序结束！");
        scanner.close();
    }
}