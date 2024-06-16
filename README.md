# ColdChain
## INTRODUCTION:
- Code supplement for the paper "Fresh-Food Community Group-Buying Delivery Problem for Heterogeneous Customer Demands".
- If you need help using the code, please send an email to zhaokaiqibuaa@buaa.edu.cn


## DATASETS:
- The instance data are provided in the compressed file "data.rar". 
- Each instance is named "N_K_R_P_B", where N represents the number of community nodes, K represents the type of the instance, R represents the number of resident types, P represents the number of product types, and B represents the instance sequence number. The designation of B is due to instances in the VRPTW algorithm input where the number of product types exceeds three, resulting in the instances being split into two, hence the use of 1 and 2 for differentiation.
- In the folder "VRPTW", we put the examples for the traditional VRPTW algorithm; in the folder "VRPTWMTMC", we put the examples for the VRPTWMTMC algorithm we proposed. The instances with the same name in both folders correspond to each other, only the format is different.
- Each instance CSV file's "CUST NO." represents the community node number, "XCOORD." and "YCOORD." indicate its location, "updated_SERVICE_TIME" represents the service time consumed by the delivery vehicle at that community, "DEMAND" represents the total product demand of that community, and "TW_START" and "TW_END" represent the service time window at the community leader.In the instances CSV files within the VRPTWMTC folder, "PROD_DEMAND_i_j" represents the demand of the Resident i for Product j in that community, while "RES_TW_i_START" and "RES_TW_i_END" indicate the pickup time window for Resident i in that community.

## CASE STUDYS:
- The instance data are provided in the compressed file "data.rar". 
- Each instance is named "N_R_P_T", where N represents the number of community nodes, R represents the number of resident types, P represents the number of product types, and T represents the case study type. 
- In the folder "refrigeration_equipment", T denotes the proportion of group-buying leaders with refrigeration equipment; in the folder "customer_types", T denotes the customer type.
- Each instance CSV file's "CUST NO." represents the community node number, "longitude" and "latitude" indicate its location, "SERVICE_TIME" represents the service time consumed by the delivery vehicle at that community, and "TW_START" and "TW_END" represent the service time window at the community leader. "PROD_DEMAND_i_j" represents the demand of the Resident i for Product j in that community, while "RES_TW_i_START" and "RES_TW_i_END" indicate the pickup time window for Resident i in that community. "fresh" indicates whether the community leader has refrigeration equipment. The last few columns are accompanied by distance matrix, including the depot node.


## RESULTS:
- The detailed results obtained by each solution method for all instances are provided in the folder "Result". 

## CODE:
- Code of all algorithms used in our computational experiments is provided in the folder "Code", including CPLEX, traditional VRPTW algorithms, and our proposed VRPTWMTMC algorithms. 

## USAGE:
- To run an algorithm for solving an instance:
  1. Replace `input_filePath` with the location of the instance and specify the output result as `output_filePath`.
  2. Build and run the code.
