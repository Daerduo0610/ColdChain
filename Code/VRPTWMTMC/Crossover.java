package VRPTWMTMC;



import java.util.*;

public class Crossover {

    public static List<Integer>[] oxCrossover(List<Integer> parent1, List<Integer> parent2) {
        List<Integer>[] child = new List[2];
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

    public static List<Integer> ox(int crossoverPoint1, int crossoverPoint2, List<Integer> parent1, List<Integer> parent2) {
        System.out.println("parent");
        checkForDuplicateNodes( parent1);
        checkForDuplicateNodes( parent2);
        List<Integer> child = new ArrayList<>(parent1.size());
        List<Integer> temp = new ArrayList<>(parent1.size());
        List<Integer> tempchild = new ArrayList<>(parent1.size());

        //把复制片段复制
        for (int i = crossoverPoint1; i <= crossoverPoint2; i++) {
            tempchild.add(parent1.get(i));
        }
        System.out.println("tempchild"+tempchild);
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
                if (temp.get(h).equals(parent1.get(k))) {
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
                if (temp.get(h).equals(parent1.get(k))) {
                    h++;
                    k = crossoverPoint1 - 1;
                }
            }
            child.add(temp.get(h));
            h++;
        }
        child.addAll(tempchild);
        if(child.size()!=88){
            System.out.println("tempchild"+tempchild.size());
            System.out.println("child.size"+child.size());
            System.out.println("h"+h);
            System.out.println(crossoverPoint1);
            System.out.println(crossoverPoint2);
            System.out.println(crossoverPoint2-crossoverPoint1+1);
        }
        System.out.println("child");


        checkForDuplicateNodes(child);
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

    public static void checkForDuplicateNodes(List<Integer> child) {
        Set<Integer> uniqueNodes = new HashSet<>();

        for (Integer node : child) {
            if (!uniqueNodes.add(node)) {
                System.err.println("Error: Duplicate nodes found in the child.");
                return; // Exit the function when duplicates are found
            }
        }
    }
}