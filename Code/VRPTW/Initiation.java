package VRPTW;

import java.util.List;

public class Initiation {
    // 根据节点下标顺序、Route总数和每个Route的开始节点对应下标顺序构建多个Route类作为初始解
    public static void OrderSetRoute(Individual individual, Customer[] customers, Parameters params) {
        int nodeIndex = 0;
        individual.clearRoutes();
        for (int i = 0; i < individual.numRoutes ; i++) {
            Route route = new Route();
            // 构建当前 Route 中的 Customer
            if(i<individual.numRoutes-1){
                while(individual.customerOrder[nodeIndex]!= individual.routeStart[i+1]) {
                    Node node = new Node(customers[individual.customerOrder[nodeIndex]]);
                    route.addNode(node);
                    nodeIndex++;
                }
            }else{
                while(nodeIndex < AlgorithmParameters.nbCommunity) {
                    Node node = new Node(customers[individual.customerOrder[nodeIndex]]);
                    route.addNode(node);
                    nodeIndex++;
                }
            }
            // 将当前 Route 添加到 Individual
            route.setNodeNum();
            route.setNodeInfo(params);
            individual.addRoute(route);
        }
    }

    public static void RouteSetOrder(Individual individual, Parameters params){
        int[] nodeOrder = new int[AlgorithmParameters.nbCommunity];
        int index = 0;
        // 遍历 List<Route>，将节点添加到一维数组
        int numRoutes=0;
        for (Route route : individual.getRoutes()) {
            route.setNodeNum();
            int nodeNum = route.getNodeNum();
            individual.nodeNum[numRoutes]=nodeNum;
            individual.routeStart[numRoutes]=route.getNode(0).getCustomer().getIndex();
            for (int j = 0; j < nodeNum; j++) {
                nodeOrder[index++] = route.getNode(j).getCustomer().getIndex();
            }
            route.setNodeInfo(params);
            numRoutes++;
        }
        individual.numRoutes=numRoutes;
        individual.customerOrder=nodeOrder.clone();
    }
}
