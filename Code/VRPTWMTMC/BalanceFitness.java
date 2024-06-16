package VRPTWMTMC;

import java.util.HashSet;
import java.util.List;

public class BalanceFitness {
    public static void route_fitness_update(DeliveryRoute route, DeliveryNode unchangedNode, Parameters params, Customer[] customers){
        double demand=unchangedNode.getCumDem();
        double distance=unchangedNode.getCumDis();
        double time = unchangedNode.getCumTime(); //到达第一个客户节点的时间
        double route_punishment_fitness=unchangedNode.getCumPunish();
        double route_penalty_fitness=unchangedNode.getCumPenalty();
        double penalty_clear=unchangedNode.getCumClear();
        double penalty_potential=unchangedNode.getCumPotential();
        double penalty_delay=unchangedNode.getCumDelay();
        HashSet<Integer> typeSet=unchangedNode.getTypeSet();

        if(unchangedNode.getNext()!=null&&unchangedNode.getComedy().getIndex()!=unchangedNode.getNext().getComedy().getIndex()){
            double dist=params.dist[unchangedNode.getComedy().getIndex()][unchangedNode.getNext().getComedy().getIndex()];
            time+=customers[unchangedNode.getComedy().getIndex()].getServiceTime()+dist;
            distance+=dist;
            //当当前正访问节点不是最后一个节点时，计算到达下一个节点的时间=当前修正结束的到达时间+服务时间+前往下一个节点的时间
        }

        for(DeliveryNode node=unchangedNode.getNext();node!=null;node=node.getNext()){
            //违反硬时间窗
            Customer currentCustomer=customers[node.getComedy().getIndex()];
            if(time<currentCustomer.getStartTW()){
                node.setWT(currentCustomer.getStartTW()-time);
                time=currentCustomer.getStartTW();
            }
            if(time>currentCustomer.getEndTW()){
                route_punishment_fitness+=(time-currentCustomer.getEndTW())*AlgorithmParameters.penaltyDuration;///
                route.setIsFeasible(false);
                node.setTimeWarp(time-currentCustomer.getEndTW());
                time=currentCustomer.getEndTW();
            }
            //违反软时间窗
            int type=node.getComedy().getType();
            double node_clear=0;
            double node_potential=0;
            double node_delay=0;
            for(int j=0;j<AlgorithmParameters.nbResident;j++){
                double comedyDemand=currentCustomer.getResidents()[j].getComedyDemand()[type];
                double comedyValue=node.getComedy().getValue();
                if(comedyDemand>AlgorithmParameters.eps){
                    if(currentCustomer.getResidents()[j].getStartTime()-time>AlgorithmParameters.eps){
                        node_clear+=(1-Math.exp(AlgorithmParameters.beta[type]*(time-currentCustomer.getResidents()[j].getStartTime())))*comedyDemand*comedyValue;

                    }else if(time-currentCustomer.getResidents()[j].getEndTime()>AlgorithmParameters.eps){
                        node_delay+=Math.min(time-currentCustomer.getResidents()[j].getEndTime(),AlgorithmParameters.maxDelayRange)*comedyValue*0.5*comedyDemand;
                    }
                    node_potential+=(1-Math.exp(-AlgorithmParameters.alpha[type]*time))*comedyDemand*comedyValue;
                }
            }

            penalty_clear+=node_clear;
            penalty_delay+=node_delay;
            penalty_potential+=node_potential;
            route_penalty_fitness+=((node_clear+node_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+node_delay*AlgorithmParameters.a);

            demand+=node.getComedy().getDemand();
            typeSet.add(node.getComedy().getType());

            node.setClear(node_clear);
            node.setDelay(node_delay);
            node.setPotential(node_potential);
            node.setPenalty(((node_clear+node_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+node_delay*AlgorithmParameters.a));
            node.setTypeSet(typeSet);
            node.setCumDem(demand);
            node.setCumTime(time);
            node.setCumDis(distance);
            node.setCumPunish(route_punishment_fitness);
            node.setCumPenalty(route_penalty_fitness);
            node.setCumClear(penalty_clear);
            node.setCumPotential(penalty_potential);
            node.setCumDelay(penalty_delay);


            if(node.getNext()!=null&&node.getComedy().getIndex()!=node.getNext().getComedy().getIndex()){
                double dist=params.dist[node.getComedy().getIndex()][node.getNext().getComedy().getIndex()];
                time+=currentCustomer.getServiceTime()+dist;
                distance+=dist;
                //当当前正访问节点不是最后一个节点时，计算到达下一个节点的时间=当前修正结束的到达时间+服务时间+前往下一个节点的时间
            }
        }
        if(demand-AlgorithmParameters.maximumLoad>AlgorithmParameters.eps){
            route_punishment_fitness+=(demand-AlgorithmParameters.maximumLoad)*AlgorithmParameters.penaltyCapacity;
            route.setIsFeasible(false);
        }
        distance+=params.dist[route.getLastNode().getComedy().getIndex()][AlgorithmParameters.nbCommunity];

        if(typeSet.size()>AlgorithmParameters.maxComedy){
//            System.out.println("commodityType>nbComedy, commodityType="+typeSet.size());
            distance+=AlgorithmParameters.M; //如果商品种类超出最大种类，则视为距离成本无限大
        }
        route.setFitness(distance,route_punishment_fitness,route_penalty_fitness,penalty_clear,penalty_potential,penalty_delay);
    }

    public static void route_fitness_computation(DeliveryRoute route, Parameters params, Customer[] customers){
        if (route.getHead() == null) {
            route.setFitness(0,0,0,0,0,0);
            route.setfitness(0);
        }else{
            DeliveryNode node=route.getHead();
            node.setCumResource();
            double route_punishment_fitness=0;
            double route_penalty_fitness=0;
            double penalty_clear=0;
            double penalty_potential=0;
            double penalty_delay=0;

            double dist=params.dist[AlgorithmParameters.nbCommunity][node.getComedy().getIndex()];
            double time=dist;

            Customer currentCustomer=customers[node.getComedy().getIndex()];
            if(time<currentCustomer.getStartTW()){
                node.setWT(currentCustomer.getStartTW()-time);
                time=currentCustomer.getStartTW();
            }
            if(time>currentCustomer.getEndTW()){
                route_punishment_fitness+=(time-currentCustomer.getEndTW())*AlgorithmParameters.penaltyDuration;///
                route.setIsFeasible(false);
                node.setTimeWarp(time-currentCustomer.getEndTW());
                time=currentCustomer.getEndTW();
            }
            //违反软时间窗
            int type=node.getComedy().getType();
            for(int j=0;j<AlgorithmParameters.nbResident;j++){
                double comedyDemand=currentCustomer.getResidents()[j].getComedyDemand()[type];
                double comedyValue=node.getComedy().getValue();
                if(comedyDemand>AlgorithmParameters.eps){
                    if(currentCustomer.getResidents()[j].getStartTime()-time>AlgorithmParameters.eps){
                        penalty_clear+=(1-Math.exp(AlgorithmParameters.beta[type]*(time-currentCustomer.getResidents()[j].getStartTime())))*comedyDemand*comedyValue;
                    }else if(time-currentCustomer.getResidents()[j].getEndTime()>AlgorithmParameters.eps){
                        penalty_delay+=Math.min(time-currentCustomer.getResidents()[j].getEndTime(),AlgorithmParameters.maxDelayRange)*comedyValue*0.5*comedyDemand;
                    }
                    penalty_potential+=(1-Math.exp(-AlgorithmParameters.alpha[type]*time))*comedyDemand*comedyValue;
                }
            }
            route_penalty_fitness+=((penalty_clear+penalty_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a);

            node.setCumDem(node.getComedy().getDemand());
            node.setCumTime(time);
            node.setCumDis(dist);
            node.setCumPunish(route_punishment_fitness);
            node.setCumPenalty(route_penalty_fitness);
            node.setCumClear(penalty_clear);
            node.setCumPotential(penalty_potential);
            node.setCumDelay(penalty_delay);
            node.setClear(penalty_clear);
            node.setDelay(penalty_delay);
            node.setPotential(penalty_potential);
            node.setPenalty(((penalty_clear+penalty_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a));

            route_fitness_update(route,route.getHead(),params,customers);
        }

    }

    public static void fitness_computation(Individual individual,Parameters params,Customer[] customers){
        double fitness=0;
        for(DeliveryRoute route:individual.getRoutes()){
            route_fitness_computation(route,params,customers);
            fitness+=route.getFit();
        }
        individual.fitness=fitness;
    }

    public static void replace_route_fitness(Individual individual,int routeIndex,int nodeIndex, Parameters params,Customer[] customers){
        individual.fitness-=individual.getRoute(routeIndex).getFit();

        if(individual.getRoute(routeIndex).getHead()==null){
            individual.DeliveryRoutes.remove(routeIndex);
            individual.numRoutes--;
        }else{
            if(nodeIndex<0){
                BalanceFitness.route_fitness_computation(individual.getRoute(routeIndex),params,customers);
            }else{
                BalanceFitness.route_fitness_update(individual.getRoute(routeIndex),individual.getRoute(routeIndex).getNode(nodeIndex),params,customers);
            }
            individual.fitness+=individual.getRoute(routeIndex).getFit();
            individual.getRoute(routeIndex).setNodeNum();
        }
    }

    public static double fitness(Individual individual, Parameters params, Customer[] customers){
        //传入一个个体，要求个体中包含一级信息+所用车辆数目k+第i+1条路径所访问的客户节点数node[i]+第i+1条路径所访问的第一个客户节点序号route_start[i]
        int node_visited=0;
        double fundamental_fitness=0;
        double punishment_fitness=0;
        double penalty_fitness=0;
        //fitness=fundamental_fitness+punishment_fitness,即基础运输成本和违反时间窗的惩罚成本
        for (int i = 0; i <individual.numRoutes ; i++) {
            //于该循环下计算每条路径的fitness
            double route_fundamental_fitness=fundamental_fitness(individual.getRoute(i),params);  //该函数可计算第i+1条路径的基础运输成本
            route_fitness_computation(individual.getRoute(i),params,customers);
            if(Math.abs(individual.getRoute(i).getFundFit()-route_fundamental_fitness)>AlgorithmParameters.eps){
                System.out.println("error!route fund fit!=");
            }
            double route_punishment_fitness=punishment_fitness(individual.getRoute(i),params,customers);  //该函数可计算第i+1条路径的违反时间窗的惩罚成本
            if(Math.abs(individual.getRoute(i).getPunishFit()-route_punishment_fitness)>AlgorithmParameters.eps){
                System.out.println("error!route punish fit!=");
            }
//            if((route_punishment_fitness-punishment_fitness(individual.getRoute(i),params,customers))>AlgorithmParameters.eps){
//                System.out.println("error,fitness!=");
//            }
            double route_penalty_fitness=penalty_fitness(individual.getRoute(i),customers);
            if(Math.abs(individual.getRoute(i).getPenalFit()-route_penalty_fitness)>AlgorithmParameters.eps){
                System.out.println("error!route penal fit!=");
            }
            fundamental_fitness+=route_fundamental_fitness;  //该函数可计算第i+1条路径的基础运输成本
            punishment_fitness+=route_punishment_fitness;  //该函数可计算第i+1条路径的违反时间窗的惩罚成本
            penalty_fitness+=route_penalty_fitness;
//            individual.getRoute(i).setfitness(route_fundamental_fitness+route_punishment_fitness+route_penalty_fitness);
            node_visited+= individual.DeliveryRoutes.get(i).getNodeNum();

        }
        return fundamental_fitness+punishment_fitness+penalty_fitness;

    }

    public static double total_punishment_fitness(Individual individual, Parameters params, Customer[] customers){
        int node_visited=0;
        double punishment_fitness=0;
        for (int i = 0; i <individual.numRoutes ; i++) {
            punishment_fitness+=punishment_fitness(individual,i,node_visited,params,customers);  //该函数可计算第i+1条路径的违反时间窗的惩罚成本
            node_visited+= individual.DeliveryRoutes.get(i).getNodeNum();

        }
        return punishment_fitness;
    }

    public static double fundamental_fitness(DeliveryRoute route,Parameters params){
        double route_fundamental_fitness=0;//一条路径的基础运输成本
        route_fundamental_fitness+=params.dist[AlgorithmParameters.nbCommunity][route.getHead().getComedy().getIndex()];
        for (int i = 0; i < route.getNodeNum()-1; i++) {
//            if(Math.abs(route_fundamental_fitness-route.getNode(i).getCumDis())>AlgorithmParameters.eps){
//                System.out.println("node cumulative is error");
//            }
            route_fundamental_fitness+=params.dist[route.getNode(i).getComedy().getIndex()][route.getNode(i+1).getComedy().getIndex()];//计算两点间的基础运输成本
        }
        route_fundamental_fitness+=params.dist[route.getLastNode().getComedy().getIndex()][AlgorithmParameters.nbCommunity];
        return route_fundamental_fitness;
    }

    public static double fundamental_fitness(Individual individual, int i, int node_visited, Parameters params){
        double route_fundamental_fitness=params.dist[AlgorithmParameters.nbCommunity][individual.DeliveryOrder.get(node_visited).getComedy().getIndex()]+params.dist[individual.DeliveryOrder.get(node_visited+ individual.DeliveryRoutes.get(i).getNodeNum() -1).getComedy().getIndex()][AlgorithmParameters.nbCommunity];
        //route_fundamental_fitness代表每条路径的基础运输成本,初始值为仓库至第一个节点+路径的最后一个节点到仓库的基础运输成本
        for (int j = 0; j< individual.DeliveryRoutes.get(i).getNodeNum() -1 ; j++) {
            route_fundamental_fitness+=params.dist[individual.DeliveryOrder.get(j+node_visited).getComedy().getIndex()][individual.DeliveryOrder.get(j+node_visited+1).getComedy().getIndex()];//计算两点间的基础运输成本
        }
        return route_fundamental_fitness;
    }
    public static double punishment_fitness(DeliveryRoute route,Parameters params,Customer[] customers){
        double route_punishment_fitness=0;
        double demand=0;
        double time=params.dist[AlgorithmParameters.nbCommunity][route.getHead().getComedy().getIndex()];//到达第一个客户节点的时间
        for (int j = 0; j <route.getNodeNum() ; j++) {
            int node_visiting=route.getNode(j).getComedy().getIndex();//当前所访问的节点序号
            if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getStartTW();
            if(time>customers[node_visiting].getEndTW()){
                route_punishment_fitness+=(time-customers[node_visiting].getEndTW())*AlgorithmParameters.penaltyDuration;///
                time=customers[node_visiting].getEndTW();
            }
//            individual.DeliveryOrder.get(j+node_visited).setTime(time);
            if(j!=route.getNodeNum()-1&&node_visiting!=route.getNode(j+1).getComedy().getIndex()){
                time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][route.getNode(j+1).getComedy().getIndex()];
                //当当前正访问节点不是最后一个节点时，计算到达下一个节点的时间=当前修正结束的到达时间+服务时间+前往下一个节点的时间
            }
            demand=demand+route.getNode(j).getComedy().getDemand();
        }
        if(demand>AlgorithmParameters.maximumLoad) route_punishment_fitness+=(demand-AlgorithmParameters.maximumLoad)*AlgorithmParameters.penaltyCapacity;
        return route_punishment_fitness;
    }
    public static double punishment_fitness(Individual individual, int i, int node_visited, Parameters params, Customer[] customers){
        double route_punishment_fitness=0;
        double demand=0;
        double time=params.dist[AlgorithmParameters.nbCommunity][individual.DeliveryOrder.get(i).getComedy().getIndex()];//到达第一个客户节点的时间
        for (int j = 0; j <individual.DeliveryRoutes.get(i).getNodeNum() ; j++) {
            int node_visiting=individual.DeliveryOrder.get(j+node_visited).getComedy().getIndex();//当前所访问的节点序号
            if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getStartTW();
            if(time>customers[node_visiting].getEndTW()){
                route_punishment_fitness+=(time-customers[node_visiting].getEndTW())*AlgorithmParameters.penaltyDuration;///
                time=customers[node_visiting].getEndTW();
            }
//            individual.DeliveryOrder.get(j+node_visited).setTime(time);
            if(j!=individual.DeliveryRoutes.get(i).getNodeNum()-1&&node_visiting!=individual.DeliveryOrder.get(j+node_visited+1).getComedy().getIndex()){
                time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][individual.DeliveryOrder.get(j+node_visited+1).getComedy().getIndex()];
                //当当前正访问节点不是最后一个节点时，计算到达下一个节点的时间=当前修正结束的到达时间+服务时间+前往下一个节点的时间
            }
            demand=demand+individual.DeliveryOrder.get(j+node_visited).getComedy().getDemand();
        }
        if(demand>AlgorithmParameters.maximumLoad) route_punishment_fitness+=(demand-AlgorithmParameters.maximumLoad)*AlgorithmParameters.penaltyCapacity;
        return route_punishment_fitness;
    }

    public static double route_fitness(List<DeliveryNode> DeliveryOrder, int route_start, int route_end, Parameters params, Customer[] customers){//传入完整的访问信息，以及要访问路径的头尾节点的序号
        int depot=AlgorithmParameters.nbCommunity;
        int startCom=DeliveryOrder.get(route_start).getComedy().getIndex();
        int endCom=DeliveryOrder.get(route_end).getComedy().getIndex();//注意传入的route_start和route_end都是route下标，不是社区节点的序号
        double route_fitness=params.dist[depot][startCom]+params.dist[endCom][depot];
        double penalty_clear=0;
        double penalty_potential=0;
        double penalty_delay=0;
        double load=0; //载货量
        HashSet<Integer> typeSet = new HashSet<>(); //货物集合
        double time=params.dist[depot][startCom];//到达第一个客户节点的时间
        for (int i = route_start; i<=route_end; i++) {
            int node_visiting=DeliveryOrder.get(i).getComedy().getIndex();
            int type=DeliveryOrder.get(i).getComedy().getType();
            Customer currentCustomer=customers[node_visiting];

            if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getStartTW();//提早到达需等待，修正时间
            if(time>customers[node_visiting].getEndTW()){
                route_fitness+=(time-customers[node_visiting].getEndTW())*AlgorithmParameters.penaltyDuration;//计算时间惩罚成本 时间惩罚成本需要系数
                time=customers[node_visiting].getEndTW();//修正时间
            }

            for(int j=0;j<AlgorithmParameters.nbResident;j++){
                double comedyDemand=currentCustomer.getResidents()[j].getComedyDemand()[type];
                double comedyValue=DeliveryOrder.get(i).getComedy().getValue();
                if(comedyDemand>AlgorithmParameters.eps){
                    if(currentCustomer.getResidents()[j].getStartTime()-time>AlgorithmParameters.eps){
                        penalty_clear+=(1-Math.exp(AlgorithmParameters.beta[type]*(time-currentCustomer.getResidents()[j].getStartTime())))*comedyValue*comedyDemand;
                    }else if(time-currentCustomer.getResidents()[j].getEndTime()>AlgorithmParameters.eps){
                        penalty_delay+=Math.min(time-currentCustomer.getResidents()[j].getEndTime(),AlgorithmParameters.maxDelayRange)*comedyValue*0.5*comedyDemand;
                    }
                    penalty_potential+=(1-Math.exp(-AlgorithmParameters.alpha[type]*time))*comedyValue*comedyDemand;
                    load+=comedyDemand;
                }
            }
            if(i!=route_end&&node_visiting!=DeliveryOrder.get(i+1).getComedy().getIndex()){//如果正在访问的节点不是最后一个节点，计算它前往下一个节点所需的基础运输成本以及到达下一个节点的时间
//                System.out.println("True?   dis penalty is add(start), current index="+node_visiting+" next index="+DeliveryOrder.get(i+1).getComedy().getIndex());
//                System.out.println("route_fitness="+route_fitness+" + "+params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()]+" = "+(route_fitness+(params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()])));
//                System.out.println("%%%%%%%% end %%%%%%%%%%%");
                route_fitness+=params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()];
                time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()];
            }
            typeSet.add(DeliveryOrder.get(i).getComedy().getType());
        }
        if((load-AlgorithmParameters.maximumLoad)>AlgorithmParameters.eps){
            route_fitness+=(load-AlgorithmParameters.maximumLoad)*AlgorithmParameters.penaltyCapacity;
        }
        if(typeSet.size()>AlgorithmParameters.maxComedy){
//            System.out.println("commodityType>nbComedy, commodityType="+typeSet.size());
            route_fitness+=AlgorithmParameters.M;
        }
//        System.out.println("route fit"+penalty_delay);
        return route_fitness+(penalty_clear+penalty_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a;
    }
//    public static double route_test_fitness(List<DeliveryNode> DeliveryOrder, int route_start, int route_end, Parameters params, Customer[] customers){//传入完整的访问信息，以及要访问路径的头尾节点的序号
//        int depot=AlgorithmParameters.nbCommunity;
//        int startCom=DeliveryOrder.get(route_start).getComedy().getIndex();
//        int endCom=DeliveryOrder.get(route_end).getComedy().getIndex();//注意传入的route_start和route_end都是route下标，不是社区节点的序号
//        double route_fitness=params.dist[depot][startCom]+params.dist[endCom][depot];
//        double penalty_clear=0;
//        double penalty_potential=0;
//        double penalty_delay=0;
//        double time=params.dist[depot][startCom]/AlgorithmParameters.velocity;//到达第一个客户节点的时间
//        for (int i = route_start; i<=route_end; i++) {
//            int node_visiting=DeliveryOrder.get(i).getComedy().getIndex();
//            int type=DeliveryOrder.get(i).getComedy().getType();
//            Customer currentCustomer=customers[node_visiting];
//            for(int j=0;j<AlgorithmParameters.nbResident;j++){
//                if(currentCustomer.getResidents()[j].getComedyDemand()[type]>AlgorithmParameters.eps){
//                    if(currentCustomer.getResidents()[j].getStartTime()-time>AlgorithmParameters.eps){
//                        System.out.println("penalty_clear="+penalty_clear+" + "+(1-Math.exp(time-currentCustomer.getResidents()[j].getStartTime()))*currentCustomer.getResidents()[j].getComedyDemand()[type]+" = "+(penalty_clear+(1-Math.exp(time-currentCustomer.getResidents()[j].getStartTime()))*currentCustomer.getResidents()[j].getComedyDemand()[type]));
//                        penalty_clear+=(1-Math.exp(time-currentCustomer.getResidents()[j].getStartTime()))*currentCustomer.getResidents()[j].getComedyDemand()[type];
//                    }else if(time-currentCustomer.getResidents()[j].getEndTime()>AlgorithmParameters.eps){
//                        System.out.println("penalty_delay="+penalty_delay+" + "+(time-currentCustomer.getResidents()[j].getEndTime())*currentCustomer.getResidents()[j].getComedyDemand()[type]+" = "+(penalty_delay+(time-currentCustomer.getResidents()[j].getEndTime())*currentCustomer.getResidents()[j].getComedyDemand()[type]));
//                        penalty_delay+=(time-currentCustomer.getResidents()[j].getEndTime())*currentCustomer.getResidents()[j].getComedyDemand()[type];
//                    }
//                    System.out.println("penalty_potential="+penalty_potential+" + "+(1-Math.exp(-time))*currentCustomer.getResidents()[j].getComedyDemand()[type]+" = "+(penalty_potential+((1-Math.exp(-time))*currentCustomer.getResidents()[j].getComedyDemand()[type])));
//                    penalty_potential+=(1-Math.exp(-time))*currentCustomer.getResidents()[j].getComedyDemand()[type];
//                }
//            }
//            if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getStartTW();//提早到达需等待，修正时间
//            if(time>customers[node_visiting].getEndTW()){
//                System.out.println("False!!!!!!!!!time penalty is add(start), time="+time+" endTW="+customers[node_visiting].getEndTW());
//                System.out.println("route_fitness="+route_fitness+" + "+(time-customers[node_visiting].getEndTW())+" = "+(route_fitness+(time-customers[node_visiting].getEndTW())));
//                System.out.println("%%%%%%%% end %%%%%%%%%%%");
//                route_fitness+=(time-customers[node_visiting].getEndTW());//计算时间惩罚成本
//                time=customers[node_visiting].getEndTW();//修正时间
//            }
//            if(i!=route_end&&node_visiting!=DeliveryOrder.get(i+1).getComedy().getIndex()){//如果正在访问的节点不是最后一个节点，计算它前往下一个节点所需的基础运输成本以及到达下一个节点的时间
//                System.out.println("True?   dis penalty is add(start), current index="+node_visiting+" next index="+DeliveryOrder.get(i+1).getComedy().getIndex());
//                System.out.println("route_fitness="+route_fitness+" + "+params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()]+" = "+(route_fitness+(params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()])));
//                System.out.println("%%%%%%%% end %%%%%%%%%%%");
//                route_fitness+=params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()];
//                time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][DeliveryOrder.get(i+1).getComedy().getIndex()]/ AlgorithmParameters.velocity;
//            }
//        }
//        if(startCom!=endCom){
//            for (int i = 0; i <AlgorithmParameters.nbCommunity; i++) {
//                //循环找到所检索路径的第一个点
//                if(route[i]==route_start){
//                    double time=params.dist[50][route_start]/ VRPTW.AlgorithmParameters.velocity;//到达第一个客户节点的时间
//                    for (int j = i; ; j++) {
//                        int node_visiting=route[j];
//                        if(time<customers[node_visiting].getStartTW()) time=customers[node_visiting].getEndTW();//提早到达需等待，修正时间
//                        if(time>customers[node_visiting].getEndTW()){
//                            route_fitness+=(time-customers[node_visiting].getEndTW());//计算时间惩罚成本
//                            time=customers[node_visiting].getEndTW();//修正时间
//                        }
//                        if(node_visiting!=route_end){//如果正在访问的节点不是最后一个节点，计算它前往下一个节点所需的基础运输成本以及到达下一个节点的时间
//                            route_fitness+=params.dist[node_visiting][route[j+1]];
//                            time+=customers[node_visiting].getServiceTime()+params.dist[node_visiting][route[j+1]]/ AlgorithmParameters.velocity;
//                        }else{
//                            break;//如若是最后一个节点，则所有成本计算结束，跳出循环
//                        }
//                    }
//                    break;
//                }
//            }
//            //如若路径只有一个点，则成本为基础运输成本（不可能存在时间窗违反成本）
//        }
//        System.out.println("route from "+route_start+" to "+route_end+"clear="+penalty_clear);
//        System.out.println("route from "+route_start+" to "+route_end+"poten="+penalty_potential);
//        System.out.println("route from "+route_start+" to "+route_end+"delay="+penalty_delay);
//        return route_fitness+penalty_clear*AlgorithmParameters.Omega+penalty_potential*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a;
//    }
    public static double penalty_fitness(DeliveryRoute route, Customer[] customers){
        double penalty_clear=0;
        double penalty_potential=0;
        double penalty_delay=0;
        if(route.getHead()!=null){
            for(int i=0;i<route.getNodeNum();i++){
                double time=route.getNode(i).getCumTime();
                Customer currentCustomer=customers[route.getNode(i).getComedy().getIndex()];
                int type=route.getNode(i).getComedy().getType();
                for(int j=0;j<AlgorithmParameters.nbResident;j++){
                    double comedyDemand=currentCustomer.getResidents()[j].getComedyDemand()[type];
                    double comedyValue=route.getNode(i).getComedy().getValue();
                    if(comedyDemand>AlgorithmParameters.eps){
                        if(currentCustomer.getResidents()[j].getStartTime()-time>AlgorithmParameters.eps){
                            penalty_clear+=(1-Math.exp(AlgorithmParameters.beta[type]*(time-currentCustomer.getResidents()[j].getStartTime())))*comedyDemand*comedyValue;
                        }else if(time-currentCustomer.getResidents()[j].getEndTime()>AlgorithmParameters.eps){
                            penalty_delay+=Math.min(time-currentCustomer.getResidents()[j].getEndTime(),AlgorithmParameters.maxDelayRange)*comedyValue*0.5*comedyDemand;
                        }
                        penalty_potential+=(1-Math.exp(-AlgorithmParameters.alpha[type]*time))*comedyDemand*comedyValue;
                    }
                }
//                if(route.getNode(i).route.getNode(i).getCumulativeArrivalTime())
//                if(route.getNode(i).getCustomer().getStartTW()-route.getNode(i).getCumulativeArrivalTime()> VRPTW.AlgorithmParameters.eps){
//                    penalty_clear+=1-Math.exp(route.getNode(i).getCumulativeArrivalTime()-route.getNode(i).getCustomer().getStartTW());
//                }else if(route.getNode(i).getCumulativeArrivalTime()-route.getNode(i).getCustomer().getEndTW()> VRPTW.AlgorithmParameters.eps){
//                    penalty_delay+=route.getNode(i).getCumulativeArrivalTime()-route.getNode(i).getCustomer().getEndTW();
//                }
//                penalty_potential+=1-Math.exp(-route.getNode(i).getCumulativeArrivalTime());
            }
        }
//        System.out.println(penalty_clear);
//        System.out.println(route.getClearFit());
//        System.out.println(penalty_potential);
//        System.out.println(route.getPotentialFit());
//        System.out.println(penalty_delay);
//        System.out.println(route.getDelayFit());
//        if(Math.abs(v -route.getPenalFit())>AlgorithmParameters.eps){
//            System.out.println("total!=");
//        }
//        System.out.println("fun in return="+ v);
//        System.out.println("fun in Fit="+route.getPenalFit());
        return (penalty_clear+penalty_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a;
    }
}
