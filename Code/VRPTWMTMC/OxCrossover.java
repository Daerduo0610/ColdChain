package VRPTWMTMC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OxCrossover {

    public static List<DeliveryNode>[] oxCrossover(List<DeliveryNode> parent1, List<DeliveryNode> parent2) {
        List<DeliveryNode>[] child = new List[2];
        child[0] = new ArrayList<>(parent1.size());
        child[1] = new ArrayList<>(parent1.size());

        // 随机选择两个交叉点（其中1在2前面）
        int[] crossoverPoint = generateRandomNumbers(parent1.size());
        int crossoverPoint1 = crossoverPoint[0];
        int crossoverPoint2 = crossoverPoint[1];

        // 执行OX交叉
        child[0] = ox(crossoverPoint1, crossoverPoint2, parent1, parent2);
        child[1] = ox(crossoverPoint1, crossoverPoint2, parent2, parent1);

        return child;
    }

    public static List<DeliveryNode> ox(int crossoverPoint1, int crossoverPoint2, List<DeliveryNode> parent1, List<DeliveryNode> parent2) {
        List<DeliveryNode> child = new ArrayList<>(parent1.size());
        List<DeliveryNode> temp = new ArrayList<>(parent1.size());
        List<DeliveryNode> tempchild = new ArrayList<>(parent1.size());
        //把复制片段复制
        for (int i = crossoverPoint1; i <= crossoverPoint2; i++) {
            tempchild.add(parent1.get(i));
        }
        //将剩余节点排序拷贝到temp里
        for (int i = crossoverPoint2 + 1; i < parent1.size(); i++) {
            temp.add(parent2.get(i));
        }

        for (int i = 0; i < crossoverPoint2 + 1; i++) {
            temp.add(parent2.get(i));
        }
        //遍历交叉点后的点
        int h = 0;
        for (int i = crossoverPoint2 + 1; i < parent1.size(); i++) {
            for (int k = crossoverPoint1; k <= crossoverPoint2; k++) {
                if (temp.get(h).getComedy().getIndex()==parent1.get(k).getComedy().getIndex() && temp.get(h).getComedy().getType()==parent1.get(k).getComedy().getType()) {
                    h++;
                    k = crossoverPoint1 - 1;
                }
            }
            tempchild.add(temp.get(h));
            h++;
        }

        //遍历交叉点前的点
        for (int i = 0; i < crossoverPoint1; i++) {
            for (int k = crossoverPoint1; k <= crossoverPoint2; k++) {
                if (temp.get(h).getComedy().getIndex()==parent1.get(k).getComedy().getIndex() && temp.get(h).getComedy().getType()==parent1.get(k).getComedy().getType()) {
                    h++;
                    k = crossoverPoint1 - 1;
                }
            }
            child.add(temp.get(h));
            h++;
        }
        child.addAll(tempchild);
//        if(child.size()!=88){
//            System.out.println("tempchild"+tempchild.size());
//            System.out.println("child.size"+child.size());
//            System.out.println('h'+h);
//            System.out.println(crossoverPoint2-crossoverPoint1+1);
//        }
//        for (DeliveryNode node : child) {
//            System.out.print(node + " ");
//        }

        return child;
    }

    public static int[] generateRandomNumbers(int x) {
        int[] result = new int[2];
        Random random = new Random();
        int range = x;

        int firstNumber = random.nextInt(range - 1);
        result[0] = firstNumber;

        int secondNumber = random.nextInt(range - firstNumber - 1) + firstNumber + 1;
        result[1] = secondNumber;

        return result;
    }
}