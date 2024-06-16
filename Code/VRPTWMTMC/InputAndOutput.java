package VRPTWMTMC;

import VRPTW.Route;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputAndOutput {
    private String fileName;

    public InputAndOutput(String fileName) {
        this.fileName = fileName;
    }

    public Customer[] readAndSaveToCustomers(Parameters params) {

        List<Double> param1List = new ArrayList<>();
        List<Double> param2List = new ArrayList<>();
        List<Double> param3List = new ArrayList<>();
        //List<Double> param4List = new ArrayList<>();
        List<Double> param5List = new ArrayList<>();
        List<Double> param6List = new ArrayList<>();
        AlgorithmParameters.nbResident=Character.getNumericValue(fileName.charAt(fileName.length()-8));
        AlgorithmParameters.nbComedy=Character.getNumericValue(fileName.charAt(fileName.length()-5));
        double[] commodityValue=new double []{4,6,7,10};//货物种类对应的价值
        double[] alpha={0.00022,0.00035,0.00043,0.00011};
        double[] beta={0.00044,0.00070,0.00086,0.00022};
        if(AlgorithmParameters.nbComedy==4){
            for (int i = 0; i < 4; i++) {
                AlgorithmParameters.alpha[i]=alpha[i];
                AlgorithmParameters.beta[i]=beta[i];
                AlgorithmParameters.commodityValue[i]=commodityValue[i];
            }
        }else if(AlgorithmParameters.nbComedy==2) {
            AlgorithmParameters.alpha[0] = alpha[0];
            AlgorithmParameters.beta[0] = beta[0];
            AlgorithmParameters.commodityValue[0] = commodityValue[1];
            AlgorithmParameters.alpha[1] = alpha[2];
            AlgorithmParameters.beta[1] = beta[2];
            AlgorithmParameters.commodityValue[1] = commodityValue[3];
        }else{
            AlgorithmParameters.alpha[0] = alpha[1];
            AlgorithmParameters.beta[0] = beta[1];
            AlgorithmParameters.commodityValue[0] = commodityValue[1];
        }
        ArrayList[][] comedyList = new ArrayList[AlgorithmParameters.nbResident][AlgorithmParameters.nbComedy];
        ArrayList[][] timeList = new ArrayList[AlgorithmParameters.nbResident][2];
        for(int i=0;i<AlgorithmParameters.nbResident;i++){
            for(int j=0;j<AlgorithmParameters.nbComedy;j++){
                comedyList[i][j]=new ArrayList<>();
            }
            timeList[i][0]=new ArrayList<>();
            timeList[i][1]=new ArrayList<>();
        }

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
        for (int h = 2; h < lines.size() - 1; h++) { // 跳过第一行(表头)、第二行（仓库）和最后一行(capacity)
            String[] row = lines.get(h).split(",");
            double value1 = Double.parseDouble(row[1]);
            double value2 = Double.parseDouble(row[2]);
            double value3 = Double.parseDouble(row[3]);
            //double value4 = Double.parseDouble(row[4]);
            double value5 = Double.parseDouble(row[5]);
            double value6 = Double.parseDouble(row[6]);
            for(int i=0;i<AlgorithmParameters.nbResident;i++){
                for(int j=0;j<AlgorithmParameters.nbComedy;j++){
                    comedyList[i][j].add(Double.parseDouble(row[9+i*AlgorithmParameters.nbComedy+j]));
                }
                timeList[i][0].add(Double.parseDouble(row[9+AlgorithmParameters.nbResident*AlgorithmParameters.nbComedy+i]));
                timeList[i][1].add(Double.parseDouble(row[9+AlgorithmParameters.nbResident*(AlgorithmParameters.nbComedy+1)+i]));
            }
            param1List.add(value1); // x坐标
            param2List.add(value2); // y坐标
            param3List.add(value3); // 服务时间
            //param4List.add(value4); // 需求
            param5List.add(value5); // 时间窗开始时间
            param6List.add(value6); // 时间窗结束时间
        }
        String[] lastRow = lines.get(lines.size() - 1).split(",");
        AlgorithmParameters.maximumLoad=Integer.parseInt(lastRow[1]); // 假设capacity值在第二列
        AlgorithmParameters.nbCommunity= lines.size() - 4; // 总行数减去4（表头和最后一行和仓库两行）
        if (AlgorithmParameters.nbCommunity<=25){
            AlgorithmParameters.running_time=1;
        }
        else if (AlgorithmParameters.nbCommunity<=50){
            AlgorithmParameters.running_time=2;
        }else{
            AlgorithmParameters.running_time=4;
        }
        String[] row=lines.get(1).split(",");
        AlgorithmParameters.maxDelayRange=0.3*(Double.parseDouble(row[8]));


        double[] param1 = param1List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param2 = param2List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param3 = param3List.stream().mapToDouble(Double::doubleValue).toArray();
        //double[] param4 = param4List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param5 = param5List.stream().mapToDouble(Double::doubleValue).toArray();
        double[] param6 = param6List.stream().mapToDouble(Double::doubleValue).toArray();
        double[][][] comedyDemand=new double[AlgorithmParameters.nbCommunity][AlgorithmParameters.nbResident][AlgorithmParameters.nbComedy];
        double[][][] timeInfo=new double[AlgorithmParameters.nbCommunity][AlgorithmParameters.nbResident][2];
        for(int i=0;i<AlgorithmParameters.nbResident;i++){
            for(int j=0;j<AlgorithmParameters.nbComedy;j++){
                for(int k=0;k<AlgorithmParameters.nbCommunity;k++){
                    comedyDemand[k][i][j]=Double.parseDouble(comedyList[i][j].get(k).toString());
                }
            }
            for(int j=0;j<2;j++){
                for(int k=0;k<AlgorithmParameters.nbCommunity;k++){
                    timeInfo[k][i][j]=Double.parseDouble(timeList[i][j].get(k).toString());
                }
            }
        }

        Customer[] customers = new Customer[AlgorithmParameters.nbCommunity];
        for (int i = 0; i < AlgorithmParameters.nbCommunity; i++) {
            customers[i] = new Customer(param1[i],param2[i],param3[i],comedyDemand[i],param5[i],param6[i],i,timeInfo[i]);
        }
//        for (int i = 0; i < AlgorithmParameters.nbCommunity; i++) {
//            System.out.println("------------------Customer:"+i+"------------------------");
//            for(int j=0;j<AlgorithmParameters.nbResident;j++){
//                for(int k=0;k<AlgorithmParameters.nbComedy;k++){
//                    System.out.print(" Resident:"+j+" ComedyType:"+k+" = ");
//                    System.out.println(customers[i].getResidents()[j].getComedyDemand()[k]);
//                }
//                System.out.print("Customer:"+i+" Resident:"+j+" ET = ");
//                System.out.println(customers[i].getResidents()[j].getStartTime());
//                System.out.print("Customer:"+i+" Resident:"+j+" LT = ");
//                System.out.println(customers[i].getResidents()[j].getEndTime());
//
//            }
//        }
        params.setParam(param1,param2);
        params.calculateParam();

        return customers;
    }

    public void RouteOutput(Individual individual,Parameters params,Customer[] customers){
        int i=1;
        int nodeIndex=0;
        for(DeliveryRoute route:individual.getRoutes()){
            System.out.println("route:"+i+" comedyNum:"+route.getNodeNum()+" routFitness:"+route.getFit());//BalanceFitness.route_fitness(individual.DeliveryOrder,nodeIndex,nodeIndex+route.getNodeNum()-1,params,customers));
            //            if(i!=individual.numRoutes){
//                System.out.println("&&&&&&&&&&&&&&&&&&&&&& this is a test start &&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//                double fitness1=BalanceFitness.route_fitness(individual.DeliveryOrder,nodeIndex,nodeIndex+route.getNodeNum()-1,params,customers);
//                double fitness2=BalanceFitness.route_fitness(individual.DeliveryOrder,nodeIndex+route.getNodeNum(),nodeIndex+route.getNodeNum()+individual.getRoute(i).getNodeNum()-1,params,customers);
//                double total_fitness=BalanceFitness.route_fitness(individual.DeliveryOrder,nodeIndex,nodeIndex+route.getNodeNum()+individual.getRoute(i).getNodeNum()-1,params,customers);
//                System.out.println("this route is not the last one");
//                System.out.println("route:"+(i+1)+" comedyNum:"+individual.getRoute(i).getNodeNum()+" routFitness:"+fitness2);
//                System.out.println("route"+i+" is from "+nodeIndex+" to "+(nodeIndex+route.getNodeNum()-1));
//                System.out.println("route"+(i+1)+" is from "+(nodeIndex+route.getNodeNum())+" to "+(nodeIndex+route.getNodeNum()+individual.getRoute(i).getNodeNum()-1));
//                System.out.println("this+nest fitness="+total_fitness);
//                System.out.println("route total is from "+nodeIndex+" to "+(nodeIndex+route.getNodeNum()+individual.getRoute(i).getNodeNum()-1));
//                if(total_fitness-fitness1-fitness2>AlgorithmParameters.eps){
//                    System.out.println("split true");
//                }else{
//                    System.out.println("split false");
//                }
//                System.out.println("----------------------------- this is a test end -------------------------------");
//            }
            nodeIndex+=route.getNodeNum();
            for(int j=0;j<route.getNodeNum();j++){
//                char type= (char) ('A'+route.getNode(j).getComedy().getType());
//                System.out.print((route.getNode(j).getComedy().getIndex()+1)+type+"("+route.getNode(j).getCumulativeArrivalTime()+")"+" ");
                if(route.getNode(j).getComedy().getType()==0){
                    System.out.print((route.getNode(j).getComedy().getIndex()+1)+"A"+"("+String.format("%.1f",route.getNode(j).getCumTime())+","+String.format("%.1f",route.getNode(j).time)+")"+" ");
                }else if(route.getNode(j).getComedy().getType()==1){
                    System.out.print((route.getNode(j).getComedy().getIndex()+1)+"B"+"("+String.format("%.1f",route.getNode(j).getCumTime())+","+String.format("%.1f",route.getNode(j).time)+")"+" ");
                }else if(route.getNode(j).getComedy().getType()==2){
                    System.out.print((route.getNode(j).getComedy().getIndex()+1)+"C"+"("+String.format("%.1f",route.getNode(j).getCumTime())+","+String.format("%.1f",route.getNode(j).time)+")"+" ");
                }else if(route.getNode(j).getComedy().getType()==3){
                    System.out.print((route.getNode(j).getComedy().getIndex()+1)+"D"+"("+String.format("%.1f",route.getNode(j).getCumTime())+","+String.format("%.1f",route.getNode(j).time)+")"+" ");
                }
            }
            System.out.print("\n");
            i++;
        }
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~next part is route test~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        int routeIndex=0;
//        for(DeliveryRoute route:individual.getRoutes()){
//            double c=BalanceFitness.route_test_fitness(individual.DeliveryOrder,routeIndex,routeIndex+route.getNodeNum()-1,params,customers);
//            System.out.println("route_fitness="+c);
//            routeIndex+=route.getNodeNum();
//        }
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~route test end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }
    public void CSVResultOutput(Individual optimalIndividual,  double total_time, int actual_nbIter, String fileName){
        try {
            // 创建一个 FileWriter 对象，指定要写入的 CSV 文件路径
            FileWriter writer = new FileWriter(fileName);

            // 创建一个 BufferedWriter 对象，用于写入数据
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            int routenum=0;
            double Fund=0;
            double Potential=0;
            double Clear=0;
            double Delay=0;
            double Penalty=0;
            for(DeliveryRoute route:optimalIndividual.getRoutes()){
                Fund+=route.getFundFit();
                Potential+=route.getPotentialFit();
                Clear+=route.getClearFit();
                Delay+=route.getDelayFit();
                Penalty+=route.getPenalFit();
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append("Route" + (routenum + 1) + ",");
                routenum++;
                for(int j=0;j<route.getNodeNum();j++){
                    String temp=" ";
                    int type = route.getNode(j).getComedy().getType();
                    char typeChar = (char) ('A' + type);
                    temp = (route.getNode(j).getComedy().getIndex() + 1) + String.valueOf(typeChar) + " ";
                    lineBuilder.append(temp).append(",");
                }
                bufferedWriter.write(lineBuilder.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.write("Total"+","+String.format("%.2f", optimalIndividual.fitness)+","+
                    "Fund"+","+String.format("%.2f",Fund)+","+
                    "Penalty"+","+String.format("%.2f",Penalty)+","+
                    "Potential"+","+String.format("%.2f",Potential)+","+
                    "Clear"+","+String.format("%.2f",Clear)+","+
                    "Delay"+","+String.format("%.2f",Delay));
            System.out.println(("Total"+","+String.format("%.2f", optimalIndividual.fitness)+
                    "Fund"+","+String.format("%.2f",Fund)+
                    "Penalty"+","+String.format("%.2f",Penalty)+
                    "Potential"+","+String.format("%.2f",Potential)+
                    "Clear"+","+String.format("%.2f",Clear)+
                    "Delay"+","+String.format("%.2f",Delay)));
            bufferedWriter.newLine();
            bufferedWriter.write("Time s"+","+String.format("%.2f", total_time));
            System.out.println("Time s"+","+String.format("%.2f", total_time));
            bufferedWriter.newLine();
            bufferedWriter.write("NbIter"+","+(actual_nbIter+1));
            System.out.println("NbIter"+","+(actual_nbIter+1));
            bufferedWriter.newLine();
            String line="node"+","+"Potential"+","+"Clear"+","+"Delay"+","+"Penalty";
            bufferedWriter.write(line);
            bufferedWriter.newLine(); // 换行，为下一行数据做准备
            for(DeliveryRoute route:optimalIndividual.getRoutes()){
                for(int j=0;j<route.getNodeNum();j++){
                    StringBuilder lineBuilder = new StringBuilder();
                    DeliveryNode node = route.getNode(j);
                    int type = node.getComedy().getType();
                    char typeChar = (char) ('A' + type);
                    lineBuilder.append((node.getComedy().getIndex() + 1) + String.valueOf(typeChar)).append(",");
                    line =node.getPotential() + ", " +
                            node.getClear() + ", " +
                            node.getDelay()+","+node.getPenalty();
                    lineBuilder.append(line);
                    bufferedWriter.write(lineBuilder.toString());
                    bufferedWriter.newLine();
                }
            }
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
            String line="NbIter"+","+"Population"+","+"Feasilbe Ratio"+","+"Time s"+","+"Iter_best"+","+"Best_delta";
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
    public Individual readVrptwResult(Parameters params,Customer[] customers,String fileName) {
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
        // 使用正则表达式匹配o_后面的数字
        String regex = "o_(\\d+)_";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileName);
        String number=" ";
        if (matcher.find()) {
            number = matcher.group(1); // 第一个匹配组即为我们需要的数字
            System.out.println("Extracted Number: " + number);
        } else {
            System.out.println("No number found after 'o_' in the file path.");
        }
        int start,end;
        if(Objects.equals(number, "2")){
            start=0;
            end=AlgorithmParameters.nbComedy;
            System.out.println("start=0;end=nbComedy");
        }else if ((Character.getNumericValue(fileName.charAt(fileName.length()-5))==1)&(Objects.equals(number,"4"))){
            start=0;
            end=3;
        }else{
            start=3;
            end=4;
        }
        Individual individual=new Individual();
        individual.numRoutes=0;
        int routestartindex=0;
        for (int i = 0; i < lines.size() - 1; i++) {
            String[] row = lines.get(i).split(",");
            if (row[0].contains("Route")) {
                individual.numRoutes++;
                individual.routeStartIndex[i]=routestartindex;
                individual.nodeNum[i]=row.length-1;
                for (int j = 1; j <row.length ; j++) {
                    int node=Integer.parseInt(row[j]);
                    for (int l = start; l < end; l++) {
                        double demand=0;
                        for(int h=0;h<AlgorithmParameters.nbResident;h++){
                            demand+=customers[node-1].getResidents()[h].getComedyDemand()[l];
                        }
                        System.out.println(demand);
                        if(Math.abs(demand)>AlgorithmParameters.eps){
                            individual.DeliveryOrder.add(new DeliveryNode(new Comedy(customers[node-1].getServiceTime(),l,demand,node-1)));
                            routestartindex++;
                        }
                    }
                }
            }else{
                break;
            }
        }
        Conversion.OrderSetRoute(individual, params,customers);
        BalanceFitness.fitness_computation(individual,params,customers);
        return individual;
    }
//    public void test_arrival_time(Individual individual){
//        for(DeliveryRoute route:individual.getRoutes()){
//            for(int i=0;i< route.getNodeNum();i++){
//                if(route.getNode(i).time-route.getNode(i).cumulativeArrivalTime>AlgorithmParameters.eps){
//                    for(int j=0;j<route.getNodeNum();j++){
//                        if(route.getNode(j).getComedy().getType()==0){
//                            System.out.print((route.getNode(j).getComedy().getIndex()+1)+"A"+"("+String.format("%.1f",route.getNode(j).getCumulativeArrivalTime())+")"+" ");
//                        }else if(route.getNode(j).getComedy().getType()==1){
//                            System.out.print((route.getNode(j).getComedy().getIndex()+1)+"B"+"("+String.format("%.1f",route.getNode(j).getCumulativeArrivalTime())+")"+" ");
//                        }else if(route.getNode(j).getComedy().getType()==2){
//                            System.out.print((route.getNode(j).getComedy().getIndex()+1)+"C"+"("+String.format("%.1f",route.getNode(j).getCumulativeArrivalTime())+")"+" ");
//                        }else if(route.getNode(j).getComedy().getType()==3){
//                            System.out.print((route.getNode(j).getComedy().getIndex()+1)+"D"+"("+String.format("%.1f",route.getNode(j).getCumulativeArrivalTime())+")"+" ");
//                        }
//                    }
//                    System.out.print("\n");
//                    System.out.print("time!=cumulativeArrivalTime, index="+(route.getNode(i).getComedy().getIndex()+1)+" time="+route.getNode(i).time+" cumulative="+route.getNode(i).cumulativeArrivalTime);
//                }
//            }
//        }
//    }
//    public static void CSVResultOutput(Individual optimalIndividual){
//        try {
//            // 创建一个 FileWriter 对象，指定要写入的 CSV 文件路径
//            FileWriter writer = new FileWriter("D:/BUAA TRAFFIC CODE/ColdChain/src/VRPTW/output.csv");
//
//            // 创建一个 BufferedWriter 对象，用于写入数据
//            BufferedWriter bufferedWriter = new BufferedWriter(writer);
//
//            int[] routestart = Arrays.copyOfRange(optimalIndividual.routeStart, 0, optimalIndividual.getNumRoutes());
//
//            // 输出提取的数据
//            System.out.println(Arrays.toString(routestart));
//            int[] customerorder = optimalIndividual.customerOrder;
//
//            // 逐行输出
//            for (int j = 0; j < routestart.length; j++) {
//                // 截取对应的部分
//                int start = routestart[j];
//                int end;
//                if (j!=routestart.length-1){
//                    end=routestart[j+1];
//                }else{
//                    end=customerorder[customerorder.length-1];
//                }
//                // 将对应部分写入 CSV
//                int startindex=0;
//                for (int k = 0; k < customerorder.length; k++) {
//                    if (customerorder[k] == start) {
//                        startindex = k;
//                        break;
//                    }
//                }
//                int endindex=0;
//                for (int k = 0; k < customerorder.length; k++) {
//                    if (customerorder[k] == end) {
//                        endindex = k;
//                        break;
//                    }
//                }
//                StringBuilder lineBuilder = new StringBuilder();
//                for (int k = startindex; k < endindex; k++) {
//                    lineBuilder.append(customerorder[k]).append(" ");
//                }
//
//                // 将拼接好的字符串写入 CSV
//                bufferedWriter.write(lineBuilder.toString());
//                bufferedWriter.newLine();
//
//            }
//
//            // 关闭 BufferedWriter
//            bufferedWriter.write(String.format("%.2f", optimalIndividual.fitness));
//            bufferedWriter.newLine();
//            bufferedWriter.close();
//            System.out.println("CSV has founded.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
