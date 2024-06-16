package VRPTWMTMC;

import java.util.ArrayList;
import java.util.List;

public class Split {
    public static void split(Individual individual, Parameters params, Customer[] customers){
        int nbComedy=individual.getDeliveryOrder().size();
        double[] minCost=new double[nbComedy];
        List<List<Integer>> shortestPath = new ArrayList<>();
        int flag=0;
        for(int i = 0; i< nbComedy; i++){
            shortestPath.add(new ArrayList<>());
            shortestPath.get(i).add(0);
        }
        for(int i = 0; i< nbComedy; i++){
            minCost[i]= BalanceFitness.route_fitness(individual.DeliveryOrder,0,i,params,customers);//表示从0-i(包括i点)的成本
        }
        int minIndex=0;
        while(true){
            minIndex=findMinIndex(minCost,minIndex);
            minIndex++;
            shortestPath.get(minIndex-1).add(minIndex);//最后一个节点表示最后一条路径最后一个点的下一个点
            if(minIndex==nbComedy){
                break;
            }
            for(int i = minIndex; i<nbComedy; i++){
                if(minCost[i]-minCost[minIndex-1]-BalanceFitness.route_fitness(individual.DeliveryOrder,minIndex,i,params,customers)> AlgorithmParameters.eps){
                    minCost[i]=minCost[minIndex-1]+BalanceFitness.route_fitness(individual.DeliveryOrder,minIndex,i,params,customers);
                    List<Integer> iList = shortestPath.get(minIndex-1); // 获取列表
                    List<Integer> jList = new ArrayList<>(iList); // 创建一个新的列表
                    shortestPath.set(i, jList); // 将新的列表赋值给第 j 个列表
                }
            }
        }
        individual.numRoutes=shortestPath.get(nbComedy-1).size()-1;
        individual.routeStartIndex=new int[nbComedy];
        for(int i=0;i<individual.numRoutes;i++){
            individual.routeStartIndex[i]=shortestPath.get(nbComedy-1).get(i);
            individual.nodeNum[i]=shortestPath.get(nbComedy-1).get(i+1)-shortestPath.get(nbComedy-1).get(i);
        }
        //System.out.println("routeStart: "+ Arrays.toString(individual.routeStart));
        Conversion.OrderSetRoute(individual, params,customers);
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
            if (minValue-array[i] > AlgorithmParameters.eps) {
                minValue=array[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}
