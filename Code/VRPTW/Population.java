package VRPTW;
import java.io.IOException;
import java.util.*;

public class Population {
    private int populationSize;         // 种群中个体总数量
    private double feasibleRatio;
    private List<Individual> feasibleIndividuals;    // 可行个体数组
    private List<Individual> infeasibleIndividuals;  // 不可行个体数组
    public List<Individual> getFeasibleIndividuals() {return feasibleIndividuals;}
    public List<Individual> getInFeasibleIndividuals() {return infeasibleIndividuals;}
    public double getFeasibleRatio(){return feasibleRatio;}
    // 构造函数，用于初始化种群
    public Population(Parameters params,Customer[] customers) {

        //this.populationSize = 4 * minPopulation; // 第一轮生成4倍min_population数量的个体
        //this.feasiblePopulationSize = minPopulation;

        this.feasibleIndividuals = new ArrayList<Individual>();
        this.infeasibleIndividuals = new ArrayList<Individual>();
        initializePopulationFirstRound(params,customers);
        Selection(params,customers);
    }
    //public List<Individual> getAllInfeasible() {
    //    return infeasibleIndividuals;
    //}

    // 初始化第一轮种群的方法
    private void initializePopulationFirstRound(Parameters params,Customer[] customers) {
        for (int i = 0; i < 4*AlgorithmParameters.min_population; i++) {
            Individual individual = generateRandomIndividual(params,customers);
            if (testFeasible(individual,params,customers)) {
                feasibleIndividuals.add(individual);
            } else {
                infeasibleIndividuals.add(individual);
            }
        }
    }

    private void Repair(Parameters params,Customer[] customers){
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

    private void Selection(Parameters params,Customer[] customers){
        //Repair(params,customers);
        for(Individual feasibleIndividual:feasibleIndividuals){
            feasibleIndividual.fitness=BalanceFitness.fitness(feasibleIndividual,params,customers);
        }
        for(Individual infeasibleIndividual:infeasibleIndividuals){
            infeasibleIndividual.fitness=BalanceFitness.fitness(infeasibleIndividual,params,customers);
        }
        if(feasibleIndividuals.size()>AlgorithmParameters.min_population+AlgorithmParameters.population_range){
            // 根据适应度对可行的个体进行排序（升序）
            feasibleIndividuals.sort(Comparator.comparingDouble(a -> a.fitness));
            // Retain only the top 25 individuals
            feasibleIndividuals = feasibleIndividuals.subList(0, AlgorithmParameters.min_population);
        }
        if(infeasibleIndividuals.size()>AlgorithmParameters.min_population+AlgorithmParameters.population_range){
            // 根据适应度对不可行的个体进行排序（升序）
            infeasibleIndividuals.sort(Comparator.comparingDouble(a -> a.fitness));
            // Retain only the top 25 individuals
            infeasibleIndividuals = infeasibleIndividuals.subList(0, AlgorithmParameters.min_population);
        }
        // 可行个体数量
        double feasiblePopulationSize = feasibleIndividuals.size();
        this.feasibleRatio = feasiblePopulationSize /(feasiblePopulationSize + infeasibleIndividuals.size());
    }


    // 种群迭代的方法
    public void evolvePopulation(Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        // Combine feasible and infeasible individuals into a single list
        List<Individual> combinedList = new ArrayList<>();
        combinedList.addAll(feasibleIndividuals);
        combinedList.addAll(infeasibleIndividuals);

        // Sort the combined list based on fitness (ascending order)
        combinedList.sort(Comparator.comparingDouble(a -> a.fitness));

        // Retain the overall smallest individuals
        List<Individual> subCombinedList = combinedList.subList(0, AlgorithmParameters.nbElite);

        // Separate the retained individuals back into feasible and infeasible lists
        feasibleIndividuals.clear();
        infeasibleIndividuals.clear();

        // 随机对任意两个个体进行cross函数操作，产生46个新个体作为下一代
        Random random = new Random();
        for (int i = 0; i < AlgorithmParameters.crossNum; i++) {
            Individual child1 = new Individual();
            Individual child2 = new Individual();
            int parentIndex1=random.nextInt(combinedList.size());
            int parentIndex2=random.nextInt(combinedList.size());
            int[][] child = OxCrossover.oxCrossover(combinedList.get(parentIndex1).customerOrder,combinedList.get(parentIndex2).customerOrder);
            child1.customerOrder=child[0].clone();
            child2.customerOrder=child[1].clone();
            Split.split(child1,params,customers);
            Split.split(child2,params,customers);
            subCombinedList.add(child1);
            subCombinedList.add(child2);
        }
        for (Individual individual : subCombinedList) {
            Education.education(individual,params,customers);
            if (testFeasible(individual,params,customers)) {
                feasibleIndividuals.add(individual);
            } else {
                infeasibleIndividuals.add(individual);
            }
        }
        Selection(params,customers);
        /*if(feasibleRatio<0.2){
            AlgorithmParameters.penaltyCapacity*=AlgorithmParameters.penaltyIncrease;
            AlgorithmParameters.penaltyDuration*=AlgorithmParameters.penaltyIncrease;
        }else{
            AlgorithmParameters.penaltyCapacity*=AlgorithmParameters.penaltyDecrease;
            AlgorithmParameters.penaltyDuration*=AlgorithmParameters.penaltyDecrease;
        }*/
    }

    // 以下是辅助方法，根据需要实现
    private Individual generateRandomIndividual(Parameters params,Customer[] customers) {
        // 随机产生0-49这50个数的乱序排列
        Random random = new Random();
        Individual[] individuals = new Individual[AlgorithmParameters.nbInitial];
        for(int i=0;i<AlgorithmParameters.nbInitial;i++){
            individuals[i]=new Individual();
            individuals[i].InitializeOrder();
            Split.split(individuals[i],params,customers);
        }
        int optimalIndex=0;
        double optimalFitness=BalanceFitness.fitness(individuals[0],params,customers);
        for(int i=0;i<AlgorithmParameters.nbInitial;i++){
            if(BalanceFitness.fitness(individuals[i],params,customers)<optimalFitness){
                optimalIndex=i;
                optimalFitness=BalanceFitness.fitness(individuals[i],params,customers);
            }
        }
        return individuals[optimalIndex];
    }

    private boolean testFeasible(Individual individual,Parameters params,Customer[] customers) {
        // 在这里实现testFeasible函数，判断个体是否为可行个体
        double punishment_fitness=BalanceFitness.total_punishment_fitness(individual,params,customers);
        // 返回 true 表示可行，false 表示不可行
        if(Math.abs(punishment_fitness-0)<AlgorithmParameters.eps){
            return true;
        }else{
            return false;
        }
    }
}
