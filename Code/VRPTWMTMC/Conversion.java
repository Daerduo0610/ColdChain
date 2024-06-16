package VRPTWMTMC;

import java.util.ArrayList;
import java.util.List;

public class Conversion {
    public static void OrderSetRoute(Individual individual,  Parameters params,Customer[] customers) {
        int nodeIndex = 0;
        individual.clearRoutes();
        for (int i = 0; i < individual.numRoutes ; i++) {
            DeliveryRoute route = new DeliveryRoute();
            // 构建当前 Route 中的 Customer
            if(i<individual.numRoutes-1){
                while(nodeIndex!= individual.routeStartIndex[i+1]) {
                    DeliveryNode node = new DeliveryNode(individual.DeliveryOrder.get(nodeIndex).getComedy());
                    route.addDeliveryNode(node);
                    nodeIndex++;
                }
            }else{
                while(nodeIndex < individual.DeliveryOrder.size()) {
                    DeliveryNode node = new DeliveryNode(individual.DeliveryOrder.get(nodeIndex).getComedy());
                    route.addDeliveryNode(node);
                    nodeIndex++;
                }
            }
            // 将当前 Route 添加到 Individual
            route.setNodeNum();
//            route.setNodeInfo(params,customers); //改动fitness计算方法后，是不是在计算fitness时更新了参数，不用加这句？
            individual.addRoute(route);
        }
    }

    public static void RouteSetOrder(Individual individual, Parameters params,Customer[] customers){
        //int[] nodeOrder = new int[individual.DeliveryOrder.size()];
        individual.DeliveryOrder=new ArrayList<>();
        int index = 0;
        // 遍历 List<Route>，将节点添加到一维数组
        int numRoutes=0;
        for (DeliveryRoute route : individual.getRoutes()) {
            route.setNodeNum();
            int nodeNum = route.getNodeNum();
            individual.nodeNum[numRoutes]=nodeNum;
            individual.routeStartIndex[numRoutes]=index;
//            double time=params.dist[AlgorithmParameters.nbCommunity][route.getNode(0).getComedy().getIndex()];//到达第一个客户节点的时间
            for (int j = 0; j < nodeNum; j++) {
//                int node_visiting=route.getNode(j).getComedy().getIndex();//当前所访问的节点序号
//                if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getStartTW();
//                if(time>customers[node_visiting].getEndTW()){
//                    time=customers[node_visiting].getEndTW();
//                }
//                route.getNode(j).setTime(time);
//                if(j!=nodeNum-1&&node_visiting!=route.getNode(j+1).getComedy().getIndex()){
//                    time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][route.getNode(j).getComedy().getIndex()];
//                    //当当前正访问节点不是最后一个节点时，计算到达下一个节点的时间=当前修正结束的到达时间+服务时间+前往下一个节点的时间
//                }

                individual.DeliveryOrder.add(route.getNode(j));
                //nodeOrder[index++] = route.getNode(j).getCustomer().getIndex();
            }
//            route.setNodeInfo(params,customers);
            numRoutes++;


        }
//        individual.numRoutes=numRoutes;
        //individual.customerOrder=nodeOrder.clone();
    }
}
