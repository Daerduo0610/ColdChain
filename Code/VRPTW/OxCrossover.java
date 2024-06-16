package VRPTW;

import java.util.Random;

public class OxCrossover {

    public static int[][] oxCrossover(int[] parent1, int[] parent2) {
        int[][] child = new int[2][parent1.length];
        // 随机选择两个交叉点（其中1在2前面）,交叉点1范围[0，parent1.length-2],交叉点2范围[交叉点1+1,parent1.length-1]
        int[] crossoverPoint = generateRandomNumbers(parent1.length);
        int crossoverPoint1=crossoverPoint[0];
        int crossoverPoint2=crossoverPoint[1];
        //System.out.println("c1="+crossoverPoint1);
        //System.out.println("c2="+crossoverPoint2);
        child[0]=Ox(crossoverPoint1,crossoverPoint2,parent1,parent2);
        child[1]=Ox(crossoverPoint1,crossoverPoint2,parent2,parent1);
        return child;

    }
    public static int[] generateRandomNumbers(int x) {
        int[] result = new int[2];
        Random random = new Random();
        int range = x ; // 范围是[0, x-1]，因为随机数不能等于上限x
        int firstNumber = random.nextInt(range-1); // 生成第一个随机数
        result[0] = firstNumber;
        // 生成第二个随机数，保证它大于第一个数，同时不超过范围的上限
        int secondNumber = random.nextInt(range - firstNumber-1) + firstNumber+1;
        result[1] = secondNumber;
        return result;
    }
    public static int[] Ox(int crossoverPoint1,int crossoverPoint2,int[] parent1,int[] parent2){
        int[] child = new int[parent1.length];
        int[] temp=new int[parent1.length];
        for (int i =crossoverPoint1; i <=crossoverPoint2 ; i++) {
            child[i]=parent1[i];
        }
        int j=0;
        for (int i = crossoverPoint2+1; i <parent1.length ; i++) {
            temp[j]=parent2[i];
            j++;
        }
        for (int i = 0; i < crossoverPoint2+1; i++) {
            temp[j]=parent2[i];
            j++;
        }
        int h=0;
        for (int i = crossoverPoint2+1; i < parent1.length; i++) {
            for (int k = crossoverPoint1; k <=crossoverPoint2 ; k++) {
                if(temp[h]==parent1[k]){
                    h++;
                    k=crossoverPoint1-1;//如果当前点在已复制片段中出现过，则跳过，重新遍历
                }
            }
            child[i]=temp[h];
            h++;
        }
        for (int i = 0; i < crossoverPoint1; i++) {
            for (int k = crossoverPoint1; k <=crossoverPoint2 ; k++) {

                if(temp[h]==parent1[k]){
                    h++;
                    k=crossoverPoint1-1;//如果当前点在已复制片段中出现过，则跳过，重新遍历
                }
            }
            child[i]=temp[h];
            h++;
        }

        return child;
    }
}
