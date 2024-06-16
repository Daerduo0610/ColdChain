package VRPTWMTMC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Population {
    private int populationSize;         // 种群中个体总数量
    private double feasibleRatio;
    private List<Individual> feasibleIndividuals;    // 可行个体数组
    private List<Individual> infeasibleIndividuals;  // 不可行个体数组
    private List<Individual> allIndividuals; //所有个体
    public List<Individual> getFeasibleIndividuals() {return feasibleIndividuals;}
    public List<Individual> getInFeasibleIndividuals() {return infeasibleIndividuals;}
    public List<Individual> getAllIndividuals() {return allIndividuals;}
    public double getFeasibleRatio(){return feasibleRatio;}
    // 构造函数，用于初始化种群
    public Population(Parameters params, Customer[] customers) {

        //this.populationSize = 4 * minPopulation; // 第一轮生成4倍min_population数量的个体
        //this.feasiblePopulationSize = minPopulation;

        this.feasibleIndividuals = new ArrayList<Individual>();
        this.infeasibleIndividuals = new ArrayList<Individual>();
        this.allIndividuals=new ArrayList<Individual>();
        initializePopulationFirstRound(params,customers);
//        for(DeliveryRoute route:allIndividuals.get(0).getRoutes()){
//            System.out.println("node num="+route.getNodeNum());
//            for(DeliveryNode node=route.getHead();node!=null;node=node.getNext()){
//                System.out.println("test"+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//            }
//        }
        Selection();
    }
    //public List<Individual> getAllInfeasible() {
    //    return infeasibleIndividuals;
    //}

    // 初始化第一轮种群的方法
    private void initializePopulationFirstRound(Parameters params, Customer[] customers) {
        for (int i = 0; i < 4*AlgorithmParameters.min_population; i++) {
            Individual individual = generateRandomIndividual(params,customers);
            if (testFeasible(individual,params,customers)) {
                feasibleIndividuals.add(individual);
            } else {
                infeasibleIndividuals.add(individual);
            }
            allIndividuals.add(individual);
        }
    }
    // 以下是辅助方法，根据需要实现
    private Individual generateRandomIndividual(Parameters params,Customer[] customers) {
        // 随机产生0-49这50个数的乱序排列
        Random random = new Random();
        Individual[] individuals = new Individual[AlgorithmParameters.nbInitial];
        for(int i=0;i<AlgorithmParameters.nbInitial;i++){
            individuals[i]=new Individual();
            individuals[i].InitializeOrder(customers);
            Split.split(individuals[i],params,customers);

//            System.out.println("DeliveryOrder size of individual"+(i+1)+": "+individuals[i].DeliveryOrder.size());
//            for(int j=0;j<individuals[i].DeliveryOrder.size();j++){
//                if(individuals[i].DeliveryOrder.get(j).getComedy().getType()==0) System.out.print((individuals[i].DeliveryOrder.get(j).getComedy().getIndex()+1)+"A ");
//                else if(individuals[i].DeliveryOrder.get(j).getComedy().getType()==1) System.out.print((individuals[i].DeliveryOrder.get(j).getComedy().getIndex()+1)+"B ");
//            }
//            System.out.print("\n");
//            InputAndOutput inputAndOutput=new InputAndOutput("");
//            System.out.println("************************individual "+(i+1)+"*****************************");
//            inputAndOutput.RouteOutput(individuals[i]);
        }
        int optimalIndex=0;
//        individuals[0].fitness=BalanceFitness.fitness(individuals[0],params,customers);
        BalanceFitness.fitness_computation(individuals[0],params,customers);

        double optimalFitness=individuals[0].fitness;
        for(int i=1;i<AlgorithmParameters.nbInitial;i++){
//            individuals[i].fitness=BalanceFitness.fitness(individuals[i],params,customers);
            BalanceFitness.fitness_computation(individuals[i],params,customers);
            if(individuals[i].fitness<optimalFitness){
                optimalIndex=i;
                optimalFitness=individuals[i].fitness;
            }
        }
//            InputAndOutput inputAndOutput=new InputAndOutput("");
//            System.out.println("************************individual optimal*****************************");
//            inputAndOutput.RouteOutput(individuals[optimalIndex]);
        return individuals[optimalIndex];
    }

    private void Repair(Parameters params, Customer[] customers){
        // 对不可行个体进行repair操作
        int i=0;
        for (Individual infeasibleIndividual : infeasibleIndividuals) {
            //infeasibleIndividual = repair(infeasibleIndividuals.get(i));

            // 判断repair后的个体是否为可行个体
            if (testFeasible(infeasibleIndividual,params,customers)) {
                feasibleIndividuals.add(infeasibleIndividual);
            }
            i++;
        }
    }

    private void Selection(){
        //Repair(params,customers);
//        for(Individual feasibleIndividual:this.feasibleIndividuals){
//            feasibleIndividual.fitness= BalanceFitness.fitness(feasibleIndividual,params,customers);
//        }
//        for(Individual infeasibleIndividual:this.infeasibleIndividuals){
//            infeasibleIndividual.fitness= BalanceFitness.fitness(infeasibleIndividual,params,customers);
//        }
        int flag=0;
        for(Individual individual:this.feasibleIndividuals){
            if(individual.fitness<AlgorithmParameters.eps){
                System.out.println("fesible fitness=0!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
        for(Individual individual:this.infeasibleIndividuals){
            if(individual.fitness<AlgorithmParameters.eps){
                System.out.println("infesible fitness=0!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
//        System.out.println("begin rank");
        rank(this.allIndividuals);
//        System.out.println("finish rank");
        this.feasibleIndividuals.sort(Comparator.comparingDouble(a -> a.rank));
        this.infeasibleIndividuals.sort(Comparator.comparingDouble(a -> a.rank));
        if(this.feasibleIndividuals.size()> AlgorithmParameters.min_population+ AlgorithmParameters.population_range){
            // 根据适应度对可行的个体进行排序（升序）
            // Retain only the top 25 individuals
            this.feasibleIndividuals = this.feasibleIndividuals.subList(0, AlgorithmParameters.min_population);
            flag=1;
        }
        if(this.infeasibleIndividuals.size()> AlgorithmParameters.min_population+ AlgorithmParameters.population_range){
            // 根据适应度对不可行的个体进行排序（升序）
            // Retain only the top 25 individuals
            this.infeasibleIndividuals = this.infeasibleIndividuals.subList(0, AlgorithmParameters.min_population);
            flag=1;
        }
        if (flag==1){
            this.allIndividuals.clear();
            this.allIndividuals.addAll(this.feasibleIndividuals);
            this.allIndividuals.addAll(this.infeasibleIndividuals);
//            rank(this.allIndividuals);
        }
//        this.allIndividuals.sort(Comparator.comparingDouble(a -> a.fitness));
        this.feasibleIndividuals.sort(Comparator.comparingDouble(a -> a.fitness));
        this.infeasibleIndividuals.sort(Comparator.comparingDouble(a -> a.fitness));

        // 可行个体数量
        double feasiblePopulationSize = this.feasibleIndividuals.size();
        this.feasibleRatio = feasiblePopulationSize /(feasiblePopulationSize + this.infeasibleIndividuals.size());
    }


    // 种群迭代的方法
    public void evolvePopulation(Parameters params, Customer[] customers,int M10_flag) throws IOException, ClassNotFoundException {
        // Combine feasible and infeasible individuals into a single list
        List<Individual> combinedList = new ArrayList<>();
        combinedList.addAll(this.feasibleIndividuals);
        combinedList.addAll(this.infeasibleIndividuals);

        // Sort the combined list based on fitness (ascending order)
        combinedList.sort(Comparator.comparingDouble(a -> a.fitness));

        // Retain the overall smallest individuals
        List<Individual> subCombinedList = combinedList.subList(0, AlgorithmParameters.nbElite);

        // Separate the retained individuals back into feasible and infeasible lists
        this.feasibleIndividuals.clear();
        this.infeasibleIndividuals.clear();
        this.allIndividuals.clear();

        // 随机对任意两个个体进行cross函数操作，产生50个新个体作为下一代
//        System.out.println("begin crossover");
        Random random = new Random();
        for (int i = 0; i < AlgorithmParameters.crossNum; i++) {
            Individual child1 = new Individual();
            Individual child2 = new Individual();
            int parentIndex1=random.nextInt(combinedList.size());
            int parentIndex2=random.nextInt(combinedList.size());
            List<DeliveryNode>[] child=OxCrossover.oxCrossover(combinedList.get(parentIndex1).DeliveryOrder,combinedList.get(parentIndex2).DeliveryOrder);
            //int[][] child = OxCrossover.oxCrossover(combinedList.get(parentIndex1).DeliveryOrder,combinedList.get(parentIndex2).DeliveryOrder);
            child1.DeliveryOrder.addAll(child[0]);
            child2.DeliveryOrder.addAll(child[1]);
            Split.split(child1,params,customers);
            Split.split(child2,params,customers);
//            child1.fitness=BalanceFitness.fitness(child1,params,customers);
//            child2.fitness=BalanceFitness.fitness(child1,params,customers);
            BalanceFitness.fitness_computation(child1,params,customers);
            BalanceFitness.fitness_computation(child2,params,customers);
            subCombinedList.add(child1);
            subCombinedList.add(child2);
        }
//        System.out.println("finish crossover");
//        for (Individual individual : combinedList) {
//        int count=1;
        for (Individual individual : subCombinedList) {
            BalanceFitness.fitness_computation(individual,params,customers);
            Education.education(individual,params,customers,M10_flag);
//            System.out.println("individual "+count+" is completed");
//            count++;
            Conversion.RouteSetOrder(individual,params,customers);
            if (testFeasible(individual,params,customers)) {
                this.feasibleIndividuals.add(individual);
            } else {
                this.infeasibleIndividuals.add(individual);
            }
            this.allIndividuals.add(individual);
        }
        Selection();

        /*if(feasibleRatio<0.2){
            AlgorithmParameters.penaltyCapacity*=AlgorithmParameters.penaltyIncrease;
            AlgorithmParameters.penaltyDuration*=AlgorithmParameters.penaltyIncrease;
        }else{
            AlgorithmParameters.penaltyCapacity*=AlgorithmParameters.penaltyDecrease;
            AlgorithmParameters.penaltyDuration*=AlgorithmParameters.penaltyDecrease;
        }*/
    }

//    public void allocation(Parameters params,Customer[] customers){
//        for(Individual individual:this.getFeasibleIndividuals()){
//            double fitness=0;
//            for (int i = 0; i < individual.numRoutes; i++) {
//                individual.DeliveryRoutes.set(i, Allocation.allocation(individual.getRoute(i),params,customers));
////                System.out.println("out allocation route fit="+individual.getRoute(i).getFit());
//                fitness+=individual.getRoute(i).getFit();
//            }
//            individual.fitness=fitness;
//        }
//        for(Individual individual:this.getInFeasibleIndividuals()){
//            double fitness=0;
//            for (int i = 0; i < individual.numRoutes; i++) {
//                individual.DeliveryRoutes.set(i, Allocation.allocation(individual.getRoute(i),params,customers));
//                fitness+=individual.getRoute(i).getFit();
//            }
//            individual.fitness=fitness;
//        }
//        this.feasibleIndividuals.sort(Comparator.comparingDouble(a -> a.fitness));
//        this.allIndividuals.clear();
//        this.allIndividuals.addAll(this.feasibleIndividuals);
//        this.allIndividuals.addAll(this.infeasibleIndividuals);
//    }



    public boolean testFeasible(Individual individual, Parameters params, Customer[] customers) {
        // 在这里实现testFeasible函数，判断个体是否为可行个体
//        double punishment_fitness= BalanceFitness.total_punishment_fitness(individual,params,customers); //这是之前的写法
        double punishment_fitness = 0;
        for(DeliveryRoute route:individual.getRoutes()){
            punishment_fitness+=route.getPunishFit();
        }
        // 返回 true 表示可行，false 表示不可行
        if(Math.abs(punishment_fitness-0)< AlgorithmParameters.eps){
            return true;
        }else{
            return false;
        }
    }

    public int Hamming(Individual A,Individual B){
        List<DeliveryNode> order1=A.getDeliveryOrder();
        List<DeliveryNode> order2=B.getDeliveryOrder();
        int distance=0;
        for (int i = 0; i <order1.size() ; i++) {
            int a=((order1.get(i).getComedy().getIndex()<<1)| (order1.get(i).getComedy().getType()));
            int b=((order2.get(i).getComedy().getIndex()<<1)| (order2.get(i).getComedy().getType()));
            if ((a^b)!=0){
                distance+=1;
            }
        }
        return distance;
    }
    public void rank(List<Individual> allIndividuals){
        allIndividuals.sort(Comparator.comparingDouble(a -> a.fitness));
        for (int i = 0; i < allIndividuals.size(); i++) {
            allIndividuals.get(i).fitness_rank=i+1;//得到fitness排名
            allIndividuals.get(i).closedistance.clear();//调用此函数说明种群发生变化，则所有distance需重算
        }
        //计算两两方案间的汉明距离
        for (int i = 0; i < allIndividuals.size(); i++) {
            for (int j = i+1; j < allIndividuals.size(); j++) {
                int distance=Hamming(allIndividuals.get(i),allIndividuals.get(j));
                allIndividuals.get(i).closedistance.add(distance);
                allIndividuals.get(j).closedistance.add(distance);
            }
        }
        //计算汉明距离得到的dc
        for (int i = 0; i < allIndividuals.size(); i++) {
            if(allIndividuals.get(i).closedistance.size()!=allIndividuals.size()-1){
                System.out.println("error!closedistance.size()");
            }
            allIndividuals.get(i).distance= allIndividuals.get(i).closedistance.stream()
                    .sorted()
                    .limit(AlgorithmParameters.nbClose)  // 取前五个最小的数
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);  // 如果链表为空，则返回0.0
        }
        allIndividuals.sort(Comparator.comparingDouble(a -> a.distance));

        for (int i = 0; i < allIndividuals.size(); i++) {
            allIndividuals.get(i).distance_rank=allIndividuals.size()-i;//得到fitness排名
            allIndividuals.get(i).rank=allIndividuals.get(i).fitness_rank+(1-AlgorithmParameters.nbElite/allIndividuals.size())*allIndividuals.get(i).distance_rank;
//            System.out.println(" rank:"+allIndividuals.get(i).distance_rank+"distance:"+allIndividuals.get(i).distance);
        }
        allIndividuals.sort(Comparator.comparingDouble(a -> a.rank));
    }
}
