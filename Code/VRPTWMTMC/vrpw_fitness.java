package VRPTWMTMC;

import java.io.IOException;

public class vrpw_fitness {
    public static void main(String[] args) throws IOException, ClassNotFoundException, CloneNotSupportedException {
        String output_filePath = "D:\\BUAA TRAFFIC CODE\\ColdChain\\src\\generated_data\\vrptw_solomon_25_data - swgg\\output\\o_4_vrptw_updated_C107+r2+p4_1.csv";
        String fileName = "D:\\BUAA TRAFFIC CODE\\ColdChain\\src\\generated_data\\solomon_25_data\\updated_C107+r2+p4.csv";
        InputAndOutput inputAndOutput = new InputAndOutput(fileName);
        Parameters params = new Parameters();
        Customer[] customers = inputAndOutput.readAndSaveToCustomers(params);
        Individual individual=inputAndOutput.readVrptwResult(params,customers,output_filePath);
        inputAndOutput.RouteOutput(individual,params,customers);

    }
}
