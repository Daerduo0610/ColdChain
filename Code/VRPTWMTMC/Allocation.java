package VRPTWMTMC;
import org.w3c.dom.ls.LSOutput;

import java.util.Random;

public class Allocation {
    public static DeliveryRoute  allocation(DeliveryRoute route,Parameters params, Customer[] customers){
        DeliveryRoute copyRoute=route.Clone();
        DeliveryNode markNode=route.getHead();
        double time_range=AlgorithmParameters.maximumDuration;
        for(DeliveryNode node=route.getHead();node!=null;node=node.getNext()){
            if(Math.abs(node.getCumTime()-customers[node.getComedy().getIndex()].getEndTW())<AlgorithmParameters.eps){
//                System.out.print(" warp! ");
                markNode=node.getNext();
            }else if(node.getWT()>AlgorithmParameters.eps){ //如果这个点到达后产生等待时间
//                System.out.print(" wait! ");
                double waiteTime=node.getWT();
                double delayTime=0; //总延迟时间
                for(;markNode!=node;markNode=markNode.getNext()){
                    double preTime=markNode.getCumTime();
                    double correctTime=preTime+delayTime;
                    if(waiteTime>AlgorithmParameters.eps){
                        //保证到达下一个点不会超过硬时间窗下界
                        double latestTime=customers[markNode.getNext().getComedy().getIndex()].getEndTW()-params.dist[markNode.getComedy().getIndex()][markNode.getNext().getComedy().getIndex()];
                        double endTime=Math.min(markNode.getCumTime()+waiteTime,customers[markNode.getComedy().getIndex()].getEndTW()); //限制不能超过硬时间窗下界
                        markNode.setCumTime(correctTime);
                        double arrivalTime=correctTime;
                        if(Math.min(endTime,latestTime)-correctTime>AlgorithmParameters.eps){
                            double removable_time=Math.min(Math.min(endTime,latestTime),preTime+time_range); //可移动最晚时间
                            arrivalTime=simulatedAnnealing(markNode,customers,correctTime,removable_time,1000,0.99,1e-5);
                        }
                        if(node_fitness(markNode,arrivalTime,customers)-markNode.getPenalty()>AlgorithmParameters.eps){
                            arrivalTime=correctTime;
                        }
                        markNode.setCumTime(arrivalTime);
                        delayTime=arrivalTime-preTime; //总延迟时间
                        if(waiteTime-(arrivalTime-correctTime)>AlgorithmParameters.eps){
                            waiteTime-=(arrivalTime-correctTime); //单次调整时间
                        }else{
                            waiteTime=0;
                        }
                    }else{
                        markNode.setCumTime(correctTime);
                    }
                    node_set_fitness(markNode,route.getPreNode(markNode),customers);
                }
                time_range=customers[node.getComedy().getIndex()].getEndTW()-node.getCumTime();
//                time_range=customers[node.getComedy().getIndex()].getResidents()[0].getEndTime()-node.getCumTime();
                for(int j=0;j<AlgorithmParameters.nbResident;j++){
                    if(time_range-(customers[node.getComedy().getIndex()].getResidents()[j].getEndTime()-node.getCumTime())>AlgorithmParameters.eps){
                        time_range=customers[node.getComedy().getIndex()].getResidents()[j].getEndTime()-node.getCumTime();
                    }
                }
            }
            else{
                double range=customers[node.getComedy().getIndex()].getEndTW()-node.getCumTime();
//                double range=customers[node.getComedy().getIndex()].getResidents()[0].getEndTime()-node.getCumTime();
                for(int j=0;j<AlgorithmParameters.nbResident;j++){
                    if(range-(customers[node.getComedy().getIndex()].getResidents()[j].getEndTime()-node.getCumTime())>AlgorithmParameters.eps){
                        range=customers[node.getComedy().getIndex()].getResidents()[j].getEndTime()-node.getCumTime();
                    }
                }
                if(range>AlgorithmParameters.eps&&time_range-range>AlgorithmParameters.eps){
                    time_range=range;
                }
                if(node.getNext()==null){
                    double delayTime=0; //总延迟时间
                    for(;markNode!=node;markNode=markNode.getNext()){
                        double preTime=markNode.getCumTime();
                        double correctTime=preTime+delayTime;
                        //保证到达下一个点不会超过硬时间窗下界
                        double latestTime=customers[markNode.getNext().getComedy().getIndex()].getEndTW()-params.dist[markNode.getComedy().getIndex()][markNode.getNext().getComedy().getIndex()];
                        double endTime=customers[markNode.getComedy().getIndex()].getEndTW(); //限制不能超过硬时间窗下界
                        markNode.setCumTime(correctTime);
                        double arrivalTime=correctTime;
                        if(Math.min(endTime,latestTime)-correctTime>AlgorithmParameters.eps){
                            double removable_time=Math.min(Math.min(endTime,latestTime),preTime+time_range); //可移动最晚时间
                            arrivalTime=simulatedAnnealing(markNode,customers,correctTime,removable_time,1000,0.99,1e-5);
                        }
                        if(node_fitness(markNode,arrivalTime,customers)-markNode.getPenalty()>AlgorithmParameters.eps){
                            arrivalTime=correctTime;
                        }
                        markNode.setCumTime(arrivalTime);
                        delayTime=arrivalTime-preTime; //总延迟时间
                        node_set_fitness(markNode,route.getPreNode(markNode),customers);
                    }
                }
            }
            node_set_fitness(node,route.getPreNode(node),customers);
        }
        double clear=route.getLastNode().getCumClear();
        double potential=route.getLastNode().getCumPotential();
        double delay=route.getLastNode().getCumDelay();
        double penalty=(clear+potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+delay*AlgorithmParameters.a;
//        System.out.println("fund="+route.getFundFit());
//        System.out.println("punish="+route.getPunishFit());
//        System.out.println("penalty="+route.getPenalFit());
//        System.out.println("before route="+route.getFit());
//        System.out.println("clear="+clear);
//        System.out.println("potential="+potential);
//        System.out.println("penalty="+penalty);
        route.setFitness(route.getFundFit(), route.getPunishFit(),penalty,clear,potential,delay);
        //用来测试每个点的delay是否正常
//        for(DeliveryNode node=route.getHead();node!=null;node=node.getNext()){
//            double test_delay=0;
//            double time=node.getCumTime();
//            Customer currentCustomer=customers[node.getComedy().getIndex()];
//            int type=node.getComedy().getType();
//            for(int j=0;j<AlgorithmParameters.nbResident;j++) {
//                double comedyDemand = currentCustomer.getResidents()[j].getComedyDemand()[type];
//                double comedyValue = node.getComedy().getValue();
//                if (comedyDemand > AlgorithmParameters.eps) {
//                    if (time - currentCustomer.getResidents()[j].getEndTime() > AlgorithmParameters.eps) {
//                        double test= Math.min(time - currentCustomer.getResidents()[j].getEndTime(), AlgorithmParameters.maxDelayRange) * comedyValue * 0.5 * comedyDemand;
//                        test_delay+=test;
//                        System.out.println("time="+time+" endtime="+currentCustomer.getResidents()[j].getEndTime());
//                        System.out.println("demand="+comedyDemand);
//                        System.out.println("type="+j+" delay type="+test);
//                    }
//                }
//            }
//        }
//        System.out.println("fund="+route.getFundFit());
//        System.out.println("punish="+route.getPunishFit());
//        System.out.println("penalty="+route.getPenalFit());
//        System.out.println("fund="+route.getFundFit()+" punish="+punish+" penalty="+(clear+potential+delay));
//        System.out.println("clear="+clear+" poten="+potential+" delay="+delay);
//        System.out.println("after set:"+route.getFit());
//        System.out.println("route="+route.getFit());
//        System.out.println("copyroute="+copyRoute.getFit());
        if(route.getFit()-copyRoute.getFit()>AlgorithmParameters.eps){
//            System.out.println("----in----");
//            for(int i=0;i<route.getNodeNum();i++){
//                System.out.print(" node="+(i+1)+" route node fit="+route.getNode(i).getPenalty()+" copyrote node fit="+copyRoute.getNode(i).getPenalty());
//                System.out.println();
//                System.out.println("clear="+route.getNode(i).getClear()+" potential="+route.getNode(i).getPotential()+" delay="+route.getNode(i).getDelay());
//                System.out.println();
//            }
//            System.out.println("-----into if-----");
            route=copyRoute.Clone(); //有没有改变？
//            System.out.println("-----after clone route fit="+route.getFit());
        }
        return route;
    }

    public static double node_fitness(DeliveryNode node,double time,Customer[] customers){
        double penalty_clear=0;
        double penalty_potential=0;
        double penalty_delay=0;
        Customer currentCustomer=customers[node.getComedy().getIndex()];
        int type=node.getComedy().getType();
        for(int j=0;j<AlgorithmParameters.nbResident;j++) {
            double comedyDemand = currentCustomer.getResidents()[j].getComedyDemand()[type];
            double comedyValue = node.getComedy().getValue();
            if (comedyDemand > AlgorithmParameters.eps) {
                if (currentCustomer.getResidents()[j].getStartTime() - time > AlgorithmParameters.eps) {
                    penalty_clear += (1 - Math.exp(AlgorithmParameters.beta[type] * (time - currentCustomer.getResidents()[j].getStartTime()))) * comedyDemand * comedyValue;
                } else if (time - currentCustomer.getResidents()[j].getEndTime() > AlgorithmParameters.eps) {
                    penalty_delay += Math.min(time - currentCustomer.getResidents()[j].getEndTime(), AlgorithmParameters.maxDelayRange) * comedyValue * 0.5 * comedyDemand;
                }
                penalty_potential += (1 - Math.exp(-AlgorithmParameters.alpha[type] * time)) * comedyDemand * comedyValue;
            }
        }
        return (penalty_clear+penalty_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a;
    }
    public static void node_set_fitness(DeliveryNode node,DeliveryNode lastNode,Customer[] customers){
        double penalty_clear=0;
        double penalty_potential=0;
        double penalty_delay=0;

        double time=node.getCumTime();
        Customer currentCustomer=customers[node.getComedy().getIndex()];
        int type=node.getComedy().getType();
//        char typeChar = (char) ('A' + type);
//        System.out.println((node.getComedy().getIndex() + 1) + String.valueOf(typeChar) + " ");
        for(int j=0;j<AlgorithmParameters.nbResident;j++) {
            double comedyDemand = currentCustomer.getResidents()[j].getComedyDemand()[type];
            double comedyValue = node.getComedy().getValue();
            if (comedyDemand > AlgorithmParameters.eps) {
                if (currentCustomer.getResidents()[j].getStartTime() - time > AlgorithmParameters.eps) {

                    penalty_clear += (1 - Math.exp(AlgorithmParameters.beta[type] * (time - currentCustomer.getResidents()[j].getStartTime()))) * comedyDemand * comedyValue;

                } else if (time - currentCustomer.getResidents()[j].getEndTime() > AlgorithmParameters.eps) {
                    penalty_delay += Math.min(time - currentCustomer.getResidents()[j].getEndTime(), AlgorithmParameters.maxDelayRange) * comedyValue * 0.5 * comedyDemand;

                }
                penalty_potential += (1 - Math.exp(-AlgorithmParameters.alpha[type] * time)) * comedyDemand * comedyValue;

            }
        }
//        System.out.println(penalty_clear);
//        System.out.println(node.getClear());
        node.setClear(penalty_clear);
//        System.out.println(node.getClear());
        node.setPotential(penalty_potential);
        node.setDelay(penalty_delay);
        double penalty=(penalty_clear+penalty_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a;
        node.setPenalty(penalty);
        if(lastNode!=null){
            penalty_clear+=lastNode.getCumClear();
            penalty_potential+=lastNode.getCumPotential();
            penalty_delay+=lastNode.getCumDelay();
        }
        node.setCumClear(penalty_clear);
        node.setCumPotential(penalty_potential);
        node.setCumDelay(penalty_delay);

//        if(Math.abs(test_penalty-node_fitness(node,node.getCumTime(),customers))>AlgorithmParameters.eps){
//            System.out.println("error!=,test="+test_penalty+" node_fit="+node_fitness(node,node.getCumTime(),customers));
//        }
        penalty=(penalty_clear+penalty_potential)/AlgorithmParameters.freshnessRange*AlgorithmParameters.Omega+penalty_delay*AlgorithmParameters.a;
        node.setCumPenalty(penalty);
    }
    public static double f(double a) {
        return a * a;
    }

    // 模拟退火算法
    public static double simulatedAnnealing(DeliveryNode node,Customer[] customers,double lowerBound, double upperBound, double initialTemperature, double coolingRate, double tolerance) {
        Random rand = new Random();
        double currentSolution = lowerBound + (upperBound - lowerBound) * rand.nextDouble();

        double bestSolution = currentSolution;
        double bestValue = node_fitness(node,currentSolution,customers);//f(currentSolution);

        double temperature = initialTemperature;

        while (temperature > tolerance) {
            double nextSolution = lowerBound + (upperBound - lowerBound) * rand.nextDouble();
            double nextValue = node_fitness(node,nextSolution,customers);//f(nextSolution);

            double delta = nextValue - bestValue;
            if (delta < 0 || Math.exp(-delta / temperature) > rand.nextDouble()) {
                currentSolution = nextSolution;
                bestValue = nextValue;
                if (node_fitness(node,bestSolution,customers)-nextValue>AlgorithmParameters.eps) {
                    bestSolution = nextSolution;
//                    System.out.println("have changed");
//                    System.out.println("bestsolution="+bestSolution+" bestValue="+bestValue);
                }
            }

            temperature *= coolingRate;
        }

        return bestSolution;
    }

//    public static void main(String[] args) {
//        double lowerBound = -10; // 参数a的下界
//        double upperBound = 10;  // 参数a的上界
//        double initialTemperature = 1000; // 初始温度
//        double coolingRate = 0.99; // 退火速率
//        double tolerance = 1e-5; // 容忍度
//
//        double minValue = simulatedAnnealing(lowerBound, upperBound, initialTemperature, coolingRate, tolerance);
//        System.out.println("The value of a that minimizes the function is: " + minValue);
//    }
}
