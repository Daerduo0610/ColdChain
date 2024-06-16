package VRPTW;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReader {
    private String fileName;

    public CSVReader(String fileName) {
        this.fileName = fileName;
    }

    public Customer[] readAndSaveToCustomers(Parameters params) {
        List<Integer> param0List = new ArrayList<>();
        List<Double> param1List = new ArrayList<>();
        List<Double> param2List = new ArrayList<>();
        List<Double> param3List = new ArrayList<>();
        List<Double> param4List = new ArrayList<>();
        List<Double> param5List = new ArrayList<>();
        List<Double> param6List = new ArrayList<>();

        List<String> lines = new ArrayList<>();

        // 读取文件到 lines 列表中
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < lines.size() - 1; i++) { // 跳过第一行(表头)和最后一行(capacity)
            String[] row = lines.get(i).split(",");
            int value0 = Integer.parseInt(row[0]);
            double value1 = Double.parseDouble(row[1]);
            double value2 = Double.parseDouble(row[2]);
            double value3 = Double.parseDouble(row[3]);
            double value4 = Double.parseDouble(row[4]);
            double value5 = Double.parseDouble(row[5]);
            double value6 = Double.parseDouble(row[6]);
            param0List.add(value0);
            param1List.add(value1);
            param2List.add(value2);
            param3List.add(value3);
            param4List.add(value4);
            param5List.add(value5);
            param6List.add(value6);
        }

        // 处理最后一行以获取capacity值
        String[] lastRow = lines.get(lines.size() - 1).split(",");
        AlgorithmParameters.maximumLoad=Integer.parseInt(lastRow[1]); // 假设capacity值在第二列
        AlgorithmParameters.nbCommunity= lines.size() - 3; // 总行数减去3（表头和最后一行、仓库）
        if (AlgorithmParameters.nbCommunity<=25){
            AlgorithmParameters.running_time=1;
        }
        else if (AlgorithmParameters.nbCommunity<=50){
            AlgorithmParameters.running_time=2;
        }else{
            AlgorithmParameters.running_time=4;
        }

        int[] param0 = param0List.stream().mapToInt(Integer::intValue).toArray();
        double[] param1 = param1List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param2 = param2List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param3 = param3List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param4 = param4List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param5 = param5List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param6 = param6List.stream().mapToDouble(Double::doubleValue).toArray();



        Customer[] customers = new Customer[AlgorithmParameters.nbCommunity];
        for (int i = 0; i < AlgorithmParameters.nbCommunity; i++) {
            customers[i] = new Customer(param1[i], param2[i], param3[i], param4[i], param5[i], param6[i],param0[i],i);
        }

        params.setParam(param1,param2);
        params.calculateParam();

        return customers;
    }
    public static void CsvRouteOutput(Individual optimalIndividual,Customer[] customers,double total_time,int actual_nbIter,String fileName){
        try {
            // 创建一个 FileWriter 对象，指定要写入的 CSV 文件路径
            FileWriter writer = new FileWriter(fileName);

            // 创建一个 BufferedWriter 对象，用于写入数据
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            int[] routestart = Arrays.copyOfRange(optimalIndividual.routeStart, 0, optimalIndividual.getNumRoutes());

            // 输出提取的数据
            System.out.println(Arrays.toString(routestart));
            int[] customerorder = optimalIndividual.customerOrder;

            // 逐行输出
            for (int j = 0; j < routestart.length; j++) {
                // 截取对应的部分
                int start = routestart[j];
                int end;
                if (j!=routestart.length-1){
                    end=routestart[j+1];
                }else{
                    end=customerorder[customerorder.length-1];
                }
                // 将对应部分写入 CSV
                int startindex=0;
                for (int k = 0; k < customerorder.length; k++) {
                    if (customerorder[k] == start) {
                        startindex = k;
                        break;
                    }
                }
                int endindex=0;
                for (int k = 0; k < customerorder.length; k++) {
                    if (customerorder[k] == end) {
                        endindex = k;
                        break;
                    }
                }
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append("Route"+(j+1)+",");
                System.out.println("Route"+(j+1)+": ");
                if (j!=routestart.length-1){
                    for (int k = startindex; k < endindex; k++) {
                        lineBuilder.append(customers[customerorder[k]].getCustomerIndex()).append(",");
                        System.out.print(customers[customerorder[k]].getCustomerIndex()+",");
                    }
                }else{
                    for (int k = startindex; k <= endindex; k++) {
                        lineBuilder.append(customers[customerorder[k]].getCustomerIndex()).append(",");
                        System.out.print(customers[customerorder[k]].getCustomerIndex()+",");
                    }
                }


                // 将拼接好的字符串写入 CSV
                bufferedWriter.write(lineBuilder.toString());
                bufferedWriter.newLine();

            }

            // 关闭 BufferedWriter
            bufferedWriter.write("方案总成本"+","+String.format("%.2f", optimalIndividual.fitness));
            System.out.println("方案总成本"+","+String.format("%.2f", optimalIndividual.fitness));
            bufferedWriter.newLine();
            bufferedWriter.write("程序时间s"+","+String.format("%.2f", total_time));
            System.out.println("程序时间s"+","+String.format("%.2f", total_time));
            bufferedWriter.newLine();
            bufferedWriter.write("总迭代次数"+","+(actual_nbIter+1));
            System.out.println("总迭代次数"+","+(actual_nbIter+1));
            bufferedWriter.newLine();
            bufferedWriter.close();
            System.out.println(fileName);
            System.out.println("Route has been appended to the CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void CsvInfoOutput(int actual_nbIter,int[] nbIter_size,double[] nbIter_feasilberatio,double[] nbIter_time,double[] nbIter_fitness,double[] nbIter_delta_fitness,String output_fileName){
        try {
            // 以追加模式打开 FileWriter
            FileWriter writer = new FileWriter(output_fileName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            // 迭代并写入新的数据行
            String line="迭代次数"+","+"种群数量"+","+"可行解比例"+","+"迭代时间s"+","+"当前种群最优"+","+"全局最优改善量";
            bufferedWriter.write(line);
            bufferedWriter.newLine(); // 换行，为下一行数据做准备
            for (int i = 0; i <=actual_nbIter; i++) {
                // 构建一行新的数据，包括迭代次数和其他数组的值
                line = (i + 1) + ", " +
                        nbIter_size[i] + ", " +
                        nbIter_feasilberatio[i] + ", " +
                        nbIter_time[i] + ", " +
                        nbIter_fitness[i] + ", " +
                        nbIter_delta_fitness[i];

                // 写入这行新数据到CSV文件
                bufferedWriter.write(line);
                bufferedWriter.newLine(); // 换行，为下一行数据做准备
            }

            // 关闭 BufferedWriter 和 FileWriter
            bufferedWriter.close();
            System.out.println("Info has been appended to the CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
