package VRPTWMTMC;

import java.util.LinkedList;
import java.util.List;

import static VRPTWMTMC.Crossover.oxCrossover;

public class test {
    public static void main(String[] args) {
        // 创建第一个整数链表
        List<Integer> list1 = new LinkedList<>();
        list1.add(1);
        list1.add(2);
        list1.add(1);
        list1.add(4);

        // 创建第二个整数链表
        List<Integer> list2 = new LinkedList<>();
        list2.add(5);
        list2.add(6);
        list2.add(7);
        list2.add(8);

        // 打印两个链表内容
        System.out.println("List 1: " + list1);
        System.out.println("List 2: " + list2);

        List<Integer>[] child=oxCrossover(list1,list2);
        System.out.println(child[1]);
    }
}