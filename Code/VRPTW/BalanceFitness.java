package VRPTW;

import java.lang.reflect.Parameter;
import java.util.List;

public class BalanceFitness {
    public static double fitness(Individual individual,Parameters params,Customer[] customers){
        //传入一个个体，要求个体中包含一级信息+所用车辆数目k+第i+1条路径所访问的客户节点数node[i]+第i+1条路径所访问的第一个客户节点序号route_start[i]
        int node_visited=0;
        double fundamental_fitness=0;
        double punishment_fitness=0;
        //fitness=fundamental_fitness+punishment_fitness,即基础运输成本和违反时间窗的惩罚成本
        for (int i = 0; i <individual.numRoutes ; i++) {
            //于该循环下计算每条路径的fitness
            fundamental_fitness+=fundamental_fitness(individual,i,node_visited,params);  //该函数可计算第i+1条路径的基础运输成本
            punishment_fitness+=punishment_fitness(individual,i,node_visited,params,customers);  //该函数可计算第i+1条路径的违反时间窗的惩罚成本
            node_visited+= individual.routes.get(i).getNodeNum();

        }
        return fundamental_fitness+punishment_fitness;

    }
    public static double total_punishment_fitness(Individual individual,Parameters params,Customer[] customers){
        int node_visited=0;
        double punishment_fitness=0;
        for (int i = 0; i <individual.numRoutes ; i++) {
            punishment_fitness+=punishment_fitness(individual,i,node_visited,params,customers);  //该函数可计算第i+1条路径的违反时间窗的惩罚成本
            node_visited+= individual.routes.get(i).getNodeNum();

        }
        return punishment_fitness;
    }
    public static double fundamental_fitness(Individual individual,int i,int node_visited,Parameters params){
        double route_fundamental_fitness=params.dist[AlgorithmParameters.nbCommunity][individual.routeStart[i]]+params.dist[individual.customerOrder[node_visited+ individual.routes.get(i).getNodeNum() -1]][AlgorithmParameters.nbCommunity];
        //route_fundamental_fitness代表每条路径的基础运输成本,初始值为仓库至第一个节点+路径的最后一个节点到仓库的基础运输成本
        for (int j = 0; j< individual.routes.get(i).getNodeNum() -1 ; j++) {
            route_fundamental_fitness+=params.dist[individual.customerOrder[j+node_visited]][individual.customerOrder[j+node_visited+1]];//计算两点间的基础运输成本

        }
        return route_fundamental_fitness;
    }

    public static double punishment_fitness(Individual individual,int i,int node_visited,Parameters params,Customer[] customers){
        double route_punishment_fitness=0;
        double demand=0;
        double time=params.dist[AlgorithmParameters.nbCommunity][individual.routeStart[i]]/AlgorithmParameters.velocity;//到达第一个客户节点的时间
        for (int j = 0; j <individual.routes.get(i).getNodeNum() ; j++) {
            int node_visiting=individual.customerOrder[j+node_visited];//当前所访问的节点序号
            if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getStartTW();
            if(time>customers[node_visiting].getEndTW()){
                route_punishment_fitness+=(time-customers[node_visiting].getEndTW())*AlgorithmParameters.penaltyDuration;///
                time=customers[node_visiting].getEndTW();
            }
            if(j!=individual.routes.get(i).getNodeNum()-1){
                time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][individual.customerOrder[j+node_visited+1]]/AlgorithmParameters.velocity;
                //当当前正访问节点不是最后一个节点时，计算到达下一个节点的时间=当前修正结束的到达时间+服务时间+前往下一个节点的时间
            }
            demand=demand+customers[node_visiting].getDemand();
        }
        if(demand>AlgorithmParameters.maximumLoad) route_punishment_fitness+=(demand-AlgorithmParameters.maximumLoad)*AlgorithmParameters.penaltyCapacity;
        return route_punishment_fitness;
    }

    public static double route_fitness(int[] route,int route_start,int route_end,Parameters params,Customer[] customers){//传入完整的访问信息，以及要访问路径的头尾节点的序号
        double route_fitness=params.dist[AlgorithmParameters.nbCommunity][route_start]+params.dist[route_end][AlgorithmParameters.nbCommunity];
        if(route_start!=route_end){
            for (int i = 0; i < AlgorithmParameters.nbCommunity; i++) {
                //循环找到所检索路径的第一个点
                if(route[i]==route_start){
                    double time=params.dist[AlgorithmParameters.nbCommunity][route_start]/AlgorithmParameters.velocity;//到达第一个客户节点的时间
                    for (int j = i; ; j++) {
                        int node_visiting=route[j];
                        if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getEndTW();//提早到达需等待，修正时间
                        if(time>customers[node_visiting].getEndTW()){
                            route_fitness+=(time-customers[node_visiting].getEndTW());//计算时间惩罚成本
                            time=customers[node_visiting].getEndTW();//修正时间
                        }
                        if(node_visiting!=route_end){//如果正在访问的节点不是最后一个节点，计算它前往下一个节点所需的基础运输成本以及到达下一个节点的时间
                            route_fitness+=params.dist[node_visiting][route[j+1]];
                            time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][route[j+1]]/AlgorithmParameters.velocity;
                        }else{
                            break;//如若是最后一个节点，则所有成本计算结束，跳出循环
                        }
                    }
                    break;
                }
            }
            //如若路径只有一个点，则成本为基础运输成本（不可能存在时间窗违反成本）
        }
        return route_fitness;
    }
    public static double penalty_fitness(Individual individual,Parameters params,Customer[] customers){
        double penalty_clear=0;
        double penalty_potential=0;
        double penalty_delay=0;
        for(Route route:individual.getRoutes()){
            if(route.getHead()!=null){
                for(int i=0;i<route.getNodeNum();i++){
                    if(route.getNode(i).getCustomer().getStartTW()-route.getNode(i).getCumulativeArrivalTime()>AlgorithmParameters.eps){
                        penalty_clear+=1-Math.exp(route.getNode(i).getCumulativeArrivalTime()-route.getNode(i).getCustomer().getStartTW());
                    }else if(route.getNode(i).getCumulativeArrivalTime()-route.getNode(i).getCustomer().getEndTW()>AlgorithmParameters.eps){
                        penalty_delay+=route.getNode(i).getCumulativeArrivalTime()-route.getNode(i).getCustomer().getEndTW();
                    }
                    penalty_potential+=1-Math.exp(-route.getNode(i).getCumulativeArrivalTime());
                }
            }
        }
        return penalty_clear*10+penalty_potential*10+penalty_delay*20;
    }
}
