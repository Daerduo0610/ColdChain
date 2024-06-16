package VRPTW;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.Arrays;

public class vrptw {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();
        CSVReader csvReader = new CSVReader(input_filePath);
        Parameters params = new Parameters();
        Customer[] customers = csvReader.readAndSaveToCustomers(params);
        Population population = new Population(params, customers);
        System.out.println("Initialization has been completed");
        System.out.println("capacity:" + AlgorithmParameters.maximumLoad);
        System.out.println("nbcommunity:" + AlgorithmParameters.nbCommunity);
        System.out.println("running_time:" + AlgorithmParameters.running_time + "h");
        //记录
        Individual optimal_individual = population.getFeasibleIndividuals().get(0).deepClone();//全局最优
        int[] nbIter_size = new int[AlgorithmParameters.nbIter];//每次迭代的种群数量
        double[] nbIter_feasilberatio = new double[AlgorithmParameters.nbIter];//每次迭代种群的可行个体比例
        double[] nbIter_time = new double[AlgorithmParameters.nbIter];//每次迭代时间
        double[] nbIter_fitness = new double[AlgorithmParameters.nbIter];//每次迭代的最好个体的fitness
        double[] nbIter_delta_fitness = new double[AlgorithmParameters.nbIter];//每次迭代的全局最优fitness改善量
        int actual_nbIter = 0;
        for (int i = 0; i < AlgorithmParameters.nbIter; i++) {
            long nbIter_ST = System.currentTimeMillis();
            population.evolvePopulation(params, customers);
            nbIter_time[i] = (System.currentTimeMillis() - nbIter_ST) / 1000.0;
            nbIter_size[i] = population.getFeasibleIndividuals().size() + population.getInFeasibleIndividuals().size();
            nbIter_feasilberatio[i] = (population.getFeasibleIndividuals().size()) * 1.0 / nbIter_size[i];
            if (population.getFeasibleIndividuals().size() != 0) {
                Individual optimalIndividual = population.getFeasibleIndividuals().get(0);//当前迭代最优
                nbIter_fitness[i] = optimalIndividual.fitness;
                if (optimal_individual.fitness - optimalIndividual.fitness > AlgorithmParameters.eps) {
                    nbIter_delta_fitness[i] = optimal_individual.fitness - optimalIndividual.fitness;
                    optimal_individual = optimalIndividual.deepClone();
                } else {
                    nbIter_delta_fitness[i] = 0;
                }
            } else {
                System.out.println("Iteration:" + (i + 1) + "/" + AlgorithmParameters.nbIter + "infeasible");
                Individual optimalIndividual = population.getInFeasibleIndividuals().get(0);
                nbIter_fitness[i] = optimalIndividual.fitness;
                nbIter_delta_fitness[i] = 0;
                int[] routestart = Arrays.copyOfRange(optimalIndividual.routeStart, 0, optimalIndividual.getNumRoutes());
                int[] customerorder = optimalIndividual.customerOrder;
                // 逐行输出
                for (int j = 0; j < routestart.length; j++) {
                    // 截取对应的部分
                    int start = routestart[j];
                    int end;
                    if (j != routestart.length - 1) {
                        end = routestart[j + 1];
                    } else {
                        end = customerorder[customerorder.length - 1];
                    }
                    // 将对应部分写入 CSV
                    int startindex = 0;
                    for (int k = 0; k < customerorder.length; k++) {
                        if (customerorder[k] == start) {
                            startindex = k;
                            break;
                        }
                    }
                    int endindex = 0;
                    for (int k = 0; k < customerorder.length; k++) {
                        if (customerorder[k] == end) {
                            endindex = k;
                            break;
                        }
                    }
                    System.out.println("Route" + (j + 1) + ": ");
                    if (j != routestart.length - 1) {
                        for (int k = startindex; k < endindex; k++) {
                            System.out.print(customers[customerorder[k]].getCustomerIndex() + ",");
                        }
                    } else {
                        for (int k = startindex; k <= endindex; k++) {
                            System.out.print(customers[customerorder[k]].getCustomerIndex() + ",");
                        }
                    }
                }
            }


            if (i % 100 == 0) {
                System.out.println("Iteration:" + (i + 1) + "/" + AlgorithmParameters.nbIter + "feasible");
                System.out.println("Optimal_fitness: " + optimal_individual.fitness);
                double feasibleRatio = population.getFeasibleRatio();
                if (feasibleRatio > 0.25) {
                    AlgorithmParameters.penaltyCapacity *= AlgorithmParameters.penaltyDecrease;
                    AlgorithmParameters.penaltyDuration *= AlgorithmParameters.penaltyDecrease;
                } else if (feasibleRatio < 0.15) {
                    AlgorithmParameters.penaltyCapacity *= AlgorithmParameters.penaltyIncrease;
                    AlgorithmParameters.penaltyDuration *= AlgorithmParameters.penaltyIncrease;
                }
            }
            if ((((System.currentTimeMillis() - startTime) / 1000.0) / 3600) > AlgorithmParameters.running_time) {
                actual_nbIter = i;
                break;
            }
            actual_nbIter = i;
        }
        double total_time = (System.currentTimeMillis() - startTime) / 1000.0;
        CSVReader.CsvRouteOutput(optimal_individual, customers, total_time, actual_nbIter, output_filePath);
        CSVReader.CsvInfoOutput(actual_nbIter, nbIter_size, nbIter_feasilberatio, nbIter_time, nbIter_fitness, nbIter_delta_fitness, output_filePath);
    }
}



