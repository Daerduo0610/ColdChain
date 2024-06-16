package VRPTWMTMC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class vrptwmtmc {
    public static void main(String[] args) throws IOException, ClassNotFoundException, CloneNotSupportedException {
        String fileName="D:\\BUAA TRAFFIC CODE\\ColdChain\\src\\conferation\\100";
        File intputdir = new File( fileName+File.separator+"input");//输入文件夹
        File outputDir = new File(fileName+File.separator+"output");//输出文件夹
        if (!outputDir.exists()) {
            boolean wasSuccessful = outputDir.mkdirs();
            if (!wasSuccessful) {
                System.out.println("Failed to create target directory.");
                return;
            }
        }
        // 列出目录下所有文件（不包括子目录中的文件）
        File[] files = intputdir.listFiles();
        if (files != null) {
            for (File file : files) {
                // 检查是否是文件而非目录
                if (file.isFile()) {
                    // 获取原文件名
                    String input_fileName = file.getName();
                    // 生成新的文件名，原文件名前加上"o"
                    String output_fileName = "o_" + input_fileName;
                    // 定义完整路径
                    String input_filePath = fileName + File.separator + "input" + File.separator + input_fileName;
                    String output_filePath = fileName + File.separator + "output" + File.separator + output_fileName;
                    System.out.println(input_filePath);

                    try {
                        long startTime = System.currentTimeMillis();
                        InputAndOutput inputAndOutput = new InputAndOutput(input_filePath);
                        Parameters params = new Parameters();
                        Customer[] customers = inputAndOutput.readAndSaveToCustomers(params);
                        Population population = new Population(params, customers);
                        System.out.println("Initialization has been completed");
                        System.out.println("capacity:" + AlgorithmParameters.maximumLoad);
                        System.out.println("nbcommunity:" + AlgorithmParameters.nbCommunity);
                        System.out.println("running_time:" + AlgorithmParameters.running_time + "h");
                        System.out.println("nbResident:" + AlgorithmParameters.nbResident);
                        System.out.println("nbComedy:" + AlgorithmParameters.nbComedy);
                        System.out.println("commodityValue:" + Arrays.toString(AlgorithmParameters.commodityValue));
                        System.out.println("alpha:" + Arrays.toString(AlgorithmParameters.alpha));
                        System.out.println("beta:" + Arrays.toString(AlgorithmParameters.beta));
                        System.out.println("maxDelayRange:" + AlgorithmParameters.maxDelayRange);
                        //记录
                        Individual optimal_individual = new Individual();
                        optimal_individual.fitness=1e100;
                        if(population.getFeasibleIndividuals().size()!=0){
                            optimal_individual= population.getFeasibleIndividuals().get(0).Clone();//全局最优
                        }
                        int[] nbIter_size = new int[AlgorithmParameters.nbIter];//每次迭代的种群数量
                        double[] nbIter_feasilberatio = new double[AlgorithmParameters.nbIter];//每次迭代种群的可行个体比例
                        double[] nbIter_time = new double[AlgorithmParameters.nbIter];//每次迭代时间
                        double[] nbIter_fitness = new double[AlgorithmParameters.nbIter];//每次迭代的最好个体的fitness
                        double[] nbIter_delta_fitness = new double[AlgorithmParameters.nbIter];//每次迭代的全局最优fitness改善量
                        int actual_nbIter = 0;
                        List feasibilityRatio = new ArrayList<>();
                        int M10_flag = 0;
                        for (int i = 0; i < AlgorithmParameters.nbIter; i++) {
                            long nbIter_ST = System.currentTimeMillis();
                            population.evolvePopulation(params, customers, 0);
                            nbIter_time[i] = (System.currentTimeMillis() - nbIter_ST) / 1000.0;
                            nbIter_size[i] = population.getFeasibleIndividuals().size() + population.getInFeasibleIndividuals().size();
                            nbIter_feasilberatio[i] = (population.getFeasibleIndividuals().size()) * 1.0 / nbIter_size[i];
                            if (population.getFeasibleIndividuals().size() != 0) {
                                Individual optimalIndividual = population.getFeasibleIndividuals().get(0);//当前迭代最优
                                nbIter_fitness[i] = optimalIndividual.fitness;
//                                System.out.println("i= "+i+" optimal_individual.fitness= "+optimal_individual.fitness+" optimalIndividual.fitness= "+optimalIndividual.fitness);
                                if (optimal_individual.fitness - optimalIndividual.fitness > AlgorithmParameters.eps) {
                                    nbIter_delta_fitness[i] = optimal_individual.fitness - optimalIndividual.fitness;
                                    optimal_individual = optimalIndividual.Clone();
                                    M10_flag = 0;
                                } else {
                                    nbIter_delta_fitness[i] = 0;
//                                    M10_flag = 1;
                                    M10_flag++;

                                }
                            } else {
                                System.out.println("Iteration:" + (i + 1) + "/" + VRPTW.AlgorithmParameters.nbIter + "infeasible");
                                Individual optimalIndividual = population.getInFeasibleIndividuals().get(0);
                                nbIter_fitness[i] = optimalIndividual.fitness;
                                nbIter_delta_fitness[i] = 0;
                                System.out.println("fitness: " + optimalIndividual.fitness);
                                inputAndOutput.RouteOutput(optimalIndividual, params, customers);
                            }


                            if ((i + 1) % 100 == 0) {
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
                            if ((i + 1) > AlgorithmParameters.nbIter * 0.95) {
                                feasibilityRatio.add(population.getFeasibleRatio());
                            }

                            if ((((System.currentTimeMillis() - startTime) / 1000.0) / 3600) > AlgorithmParameters.running_time) {
                                actual_nbIter = i;
                                break;
                            }
                            actual_nbIter = i;
                        }
                        if(optimal_individual.fitness!=1e100){
//                            System.out.println("before allocation fitness=" + population.getFeasibleIndividuals().get(0).fitness);
//                            population.allocation(params, customers);
//                            System.out.println("*************************** this is a feasible one after allocation ******************************");
                            Individual optimalIndividual = population.getFeasibleIndividuals().get(0);
                            if (optimal_individual.fitness - optimalIndividual.fitness > AlgorithmParameters.eps) {
                                optimal_individual = optimalIndividual.Clone();
                            }
//                            System.out.println("after allocation fitness=" + optimal_individual.fitness);
                            double total_time = (System.currentTimeMillis() - startTime) / 1000.0;
                            inputAndOutput.RouteOutput(optimal_individual, params, customers);
                            inputAndOutput.CSVResultOutput(optimal_individual, total_time, actual_nbIter, output_filePath);
                            inputAndOutput.CsvInfoOutput(actual_nbIter, nbIter_size, nbIter_feasilberatio, nbIter_time, nbIter_fitness, nbIter_delta_fitness, output_filePath);
                        }else{
                            System.out.println("Feasible individuals don't exist!");
                        }

                        //Allocation


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}