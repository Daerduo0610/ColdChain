package VRPTW;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Split {
    public static void split(Individual individual, Parameters params, Customer[] customers){
        double[] minCost=new double[AlgorithmParameters.nbCommunity];
        List<List<Integer>> shortestPath = new ArrayList<>();
        for(int i=0;i<AlgorithmParameters.nbCommunity;i++){
            shortestPath.add(new ArrayList<>());
            shortestPath.get(i).add(0);
        }
        for(int i=0;i<AlgorithmParameters.nbCommunity;i++){
            minCost[i]=BalanceFitness.route_fitness(individual.customerOrder,individual.customerOrder[0],individual.customerOrder[i],params,customers);
        }
        int minIndex=0;
        while(true){
            minIndex=findMinIndex(minCost,minIndex);
            minIndex++;
            shortestPath.get(minIndex-1).add(minIndex);
            if(minIndex==AlgorithmParameters.nbCommunity){
                break;
            }
            for(int i=minIndex;i<AlgorithmParameters.nbCommunity;i++){
                if(minCost[i]-minCost[minIndex-1]-BalanceFitness.route_fitness(individual.customerOrder,individual.customerOrder[minIndex],individual.customerOrder[i],params,customers)>AlgorithmParameters.eps){
                    minCost[i]=minCost[minIndex-1]+BalanceFitness.route_fitness(individual.customerOrder,individual.customerOrder[minIndex],individual.customerOrder[i],params,customers);
                    List<Integer> iList = shortestPath.get(minIndex-1); // 获取列表
                    List<Integer> jList = new ArrayList<>(iList); // 创建一个新的列表
                    shortestPath.set(i, jList); // 将新的列表赋值给第 j 个列表
                }
            }
        }
        individual.numRoutes=shortestPath.get(AlgorithmParameters.nbCommunity-1).size()-1;
        individual.routeStart=new int[AlgorithmParameters.nbCommunity];
        for(int i=0;i<individual.numRoutes;i++){
            individual.routeStart[i]=individual.customerOrder[shortestPath.get(AlgorithmParameters.nbCommunity-1).get(i)];
            individual.nodeNum[i]=shortestPath.get(AlgorithmParameters.nbCommunity-1).get(i+1)-shortestPath.get(AlgorithmParameters.nbCommunity-1).get(i);
        }
        //System.out.println("routeStart: "+ Arrays.toString(individual.routeStart));
        Initiation.OrderSetRoute(individual, customers, params);
        /*for(int i=0;i<individual.numRoutes;i++){
            System.out.println("routeStart: "+individual.routeStart[i]);
            System.out.println("this route num: "+individual.getRoute(i).getNodeNum());
            System.out.print("this route: ");
            individual.getRoute(i).printNode();
            System.out.println();
        }*/
    }
    public static int findMinIndex(double[] array,int startIndex){
        double minValue = array[startIndex]; // 计算最小值
        int minIndex = startIndex;
        for (int i = startIndex+1; i < array.length; i++) {
            if (minValue-array[i] >AlgorithmParameters.eps) {
                minValue=array[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}
