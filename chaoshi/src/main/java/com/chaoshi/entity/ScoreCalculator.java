package com.chaoshi.entity;

public class ScoreCalculator {
    // 方法1：没有参数，返回一个成绩数组
    public static int[] getDefaultScores() {
        int[] scores = {85, 92, 78, 90, 88};
        return scores;
    }
    // 方法2：有参数，不返回值
    public static void printScoreDetails(int[] scores) {
        for (int i = 0; i < scores.length; i++) {
            System.out.println("第" + (i + 1) + "名学生成绩：" + scores[i] + "分");
        }
    }
    // 方法3：有参数，返回平均分
    public static double calculateAverage(int[] scores) {
        int sum = 0;
        for (int i = 0; i < scores.length; i++) {
            sum = sum + scores[i];
        }
        double average = sum * 1.0 / scores.length;
        return average;
    }
    public static void main(String[] args) {
        // 调用方法1，拿到成绩数组
        int[] myScores = getDefaultScores();
        System.out.println("学生成绩详情：");
        // 调用方法2，打印每个学生的成绩
        printScoreDetails(myScores);
        // 调用方法3，计算平均分
        double avg = calculateAverage(myScores);
        // 打印平均分，保留1位小数
        System.out.printf("平均分：%.1f分\n", avg);
        System.out.println("姓名：张记文");
        System.out.println("学号：233817311718");
    }
}