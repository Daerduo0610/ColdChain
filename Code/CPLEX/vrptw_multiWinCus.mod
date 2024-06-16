/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Master
 * Creation Date: 2024年1月25日 at 下午22:32:22
 *********************************************/

   /****** 索引指定  *******/
 int Nb_Cus=...;	//客户的数量
 int Nb_Veh=...;	//车辆数
 int Nb_CusTyp=...;
 int Nb_Pro=...;
 //int Nb_Weight=...;	//最大载重量
 
  /****** 构建集合  *******/
 range N = 0..Nb_Cus+1;			//网络中所有节点的集合，0表示depot起点，Nb_N_Cus+1表示回到depot终点
 range N_C = 1..Nb_Cus;			//网络中客户的集合
 range N_Veh = 1..Nb_Veh;			//车辆集合
 range N_CT = 1..Nb_CusTyp;     //居民类型
 range N_Pro = 1..Nb_Pro;
 //range N_W = 1..Nb_Weight;	//G（载重量分段）的集合，用于定义决策变量
 //range N_Weight = 1..Nb_Weight+1;	//G（载重量分段）的集合，由于定义参数，集合G的势=Nb_Weight+1
 
 
  /****** 参数  *******/
 int Q=...;	//每辆车的载重量
 int M=...;	//辅助参数
 int M_Delay=...; //超越时间窗辅助参数
 float Max_time=...;	//车辆最长行驶时间
 float Start_cap=...;	//车辆从场站出发时的载重量
 float Start_time=...;	//车辆从场站出发时的时间
 //float Sens_dis=...;	//敏感性分析时对运输距离的增减系数
// float Omega=...; //新鲜度下降惩罚成本系数
// float a=...; //超过时间窗惩罚成本系数
 //float a1=...;	//每公里油耗为(a1+a2*V/a3)/100
 //float a2=...;	//每公里油耗为(a1+a2*V/a3)/100
 //float a3=...;	//每公里油耗为(a1+a2*V/a3)/100

 //多维参数
 int Rou[N_Veh]=...; //每辆车装载商品的最大种类数量
 float Travel_dis[N][N] = ...;	//网络的距离邻接矩阵
 float Travel_time[N][N] = ...;	//网络的时间邻接矩阵
 float Service_time[N]=...;	//每个节点上的服务时间
 float ET[N]= ...;	//所有节点的最早服务开始时间
 float LT[N]= ...;	//所有节点的最晚服务开始时间
 float ET_Cus[N][N_CT]=...; //所有节点不同客户的最早取货时间
 float LT_Cus[N][N_CT]=...; //所有节点不同客户的最晚取货时间
 float Alpha[N_Pro]=...; //在冷藏车上的不同商品新鲜度下降系数
 float Beta[N_Pro]=...; //在团长处不同商品新鲜度下降系数
 float Value[N_Pro]=...; //每类商品的价值系数
 //float H[N][N_CT][N_Pro]=...; //第i个节点第c种客户第p种商品的总成本
 //float H_Poten[N_CT][N_Pro]=...; //潜在成本
 //float H_Clear[N_CT][N_Pro]=...; //明确成本
 //float H_Penal[N_CT][N_Pro]=...; //惩罚成本
 //float Bound[N_Weight]= ...;	//载重量划分区段第i个划分点上的载重量
 //float P_g[N_W]= ...;  //在每个载重量划分区段上的每公里油耗
 
 //三维以上参数
 //float Demand[N][N_CT][N_Pro] = ...;	//每个节点的需求
 // If the data elements are in a 2 dimensional array
 float TwoDementionDemand[N, 1..Nb_CusTyp*Nb_Pro] = ...;
 float Demand[i in N, c in N_CT,p in N_Pro] = TwoDementionDemand[i,p+Nb_Pro*(c-1)];
 
// float TwoDementionX[N,1..(Nb_Cus+2)*Nb_Veh]=...;
// float xTest[i in N,j in N,k in N_Veh]=TwoDementionX[i,k+Nb_Veh*j];
 /*tuple DemandTuple{
     key int i;
     key int c;
     key int p;
     float value; 
 };
 {DemandTuple} DemandSet = ...;
 float Demand[N][N_CT][N_Pro]=item(DemandSet,<i,c,p>).value;*/


 /****** 决策变量  *******/
 dvar boolean X_ij_k[N][N][N_Veh];	//如果k车经过弧(i,j)为1，否则为0
 dvar boolean L_i_p_k[N][N_Pro][N_Veh]; //如果k车为社区节点i提供第p种货物为1，否则为0
 dvar boolean L_p_k[N_Pro][N_Veh];//如果k车装载第p种货物为1，否则为0
 dvar boolean L_i_c_p_k[N][N_CT][N_Pro][N_Veh];
 
 dvar float+ T_i_k[N][N_Veh];	//k车在i点的服务开始时间
 dvar float+ V_i_k[N][N_Veh];	//k车从i点出发时的载重量
 dvar float+ W_p_k[N_Pro][N_Veh]; //k车从仓库出发时不同种货物的装载量
 //dvar float+ H[N][N_CT][N_Pro][N_Veh]; //第k辆车在i个节点第c种客户第p种商品的总成本
 dvar float+ H_ClearPenal[N][N_CT][N_Pro][N_Veh]; //第k辆车在第i个节点第c种客户第p种商品的明确成本
 dvar float+ H_PotenPenal[N][N_CT][N_Pro][N_Veh]; //第k辆车在第i个节点第c种客户第p种商品的潜在成本
 dvar float+ H_DelayPenal[N][N_CT][N_Pro][N_Veh]; //第k辆车在第i个节点第c种客户第p种商品的超越时间窗惩罚成本
 dvar boolean Demand_ip[N_C][N_Pro]; //记录i点有无p商品的需求
 dvar boolean H_1[N][N_CT][N_Pro][N_Veh]; //第一段明确惩罚成本的决策变量
 dvar boolean H_2[N][N_CT][N_Pro][N_Veh]; //全过程冷藏车潜在惩罚成本的决策变量
 dvar boolean H_3[N][N_CT][N_Pro][N_Veh]; //第三段超过时间窗惩罚成本的决策变量
// dvar float test;
// dexpr int shapeX[i in N][j in N][k in N_Veh][g in N_W] = X_ij_k_g[i][j][k][g];
// dexpr float shapeT[i in N][k in N_Veh] = T_i_k[i][k];
// dexpr float shapeV[i in N][k in N_Veh] = V_i_k[i][k];
 
 //当为计算结果中的一些指标时，可以提前设置决策变量来存储该指标
 dvar float+ Cost;	//成本
  
 /****** 求解器参数设置  *******/ 
 execute PARAMS { 
 	cplex.Tilim = 21600;	//cplex求解时间上限限制为100s
 	cplex.EpGap=0.01;	//如果gap小于10%，则停止cplex
 	cplex.NodeSel=2;	//使用基于最优估计的节点选择策略：参数NodeSel置为2，激活best-estimate search
 	cplex.HeurFreq=1;	//增大启发式(Heuristics)的频率：参数HeurFreq=10, 5甚至1（越小，频率越大）
 	cplex.VarSel=3;		//使用Strong Branching，这会使Cplex对一组待分支的变量一定程度上使用深度优先搜索：参数VarSel置为3
 	cplex.MIPEmphasis=1;//MIPEmphsisBestBound，在开始时对于进行有助于最终证明最优性的分析应用较少的计算工作，并将更多精力花费在立即开始用于搜索早期（和之后经改进）的可行解法的计算
 	cplex.Probe=1;		//将探测参数Probe设为3（如果探测时间太长，逐渐减小这个参数的值试一下）
 }
 /****** 目标函数  *******/
 minimize  Cost;
 
 /****** 约束  *******/
 subject to{
 //计算指标
 Cost==
 sum(i in N)
 	sum(c in N_CT)
 		sum(p in N_Pro)
 			sum(k in N_Veh)
                H_ClearPenal[i][c][p][k]*Demand[i][c][p]*Value[p]+
 sum(i in N)
 	sum(c in N_CT)
 		sum(p in N_Pro)
 			sum(k in N_Veh)
                H_PotenPenal[i][c][p][k]*Demand[i][c][p]*Value[p]+
 sum(i in N)
 	sum(c in N_CT)
 		sum(p in N_Pro)
 			sum(k in N_Veh)
                H_DelayPenal[i][c][p][k]*Demand[i][c][p]*Value[p]*0.5+
 sum(k in N_Veh)
 		sum(i in N)
 			sum(j in N)
				(Travel_dis[i][j]*X_ij_k[i][j][k]);
 //1月12日版本
 /*Cost==
    sum(i in N)
 		sum(c in N_CT)
 			sum(p in N_Pro)
				H[i][c][p]+
 	sum(k in N_Veh)
 		sum(i in N)
 			sum(j in N)
				(Travel_dis[i][j]*X_ij_k[i][j][k]);*/

// forall(i in N)
//   forall(j in N)
//     forall(k in N_Veh)
//       X_ij_k[i][j][k]==xTest[i][j][k];	
 /****** Flow部分  *******/
 //(1)每辆车从起点出发   *(对应模型中(2))*
 forall(k in N_Veh)
     sum(i in N)
       (X_ij_k[0][i][k])==1; 
// //(2)每辆车到达终点   *(对应模型中(3))*   
 forall(k in N_Veh)
     sum(i in N)
     	(X_ij_k[i][Nb_Cus+1][k])==1; 
 //(3)每个节点至少被经过一次   *(对应模型中(4))*
 /*forall(i in N_C,k in N_Veh)
     sum(j in N)
       (X_ij_k[i][j][k])==(X_ij_k[j][i][k]); */
 forall(i in N_C)
     sum(k in N_Veh,j in N)
       (X_ij_k[i][j][k])>=1;      
 //(4)流平衡约束   *(对应模型中(4))*
 forall(i in N_C)
   forall(k in N_Veh)
     	sum(j in N) (X_ij_k[j][i][k])
     	  ==sum(j in N) (X_ij_k[i][j][k]);
 //(5)最多用Veh_m辆车   *(对应模型中(未出现))*
 sum(k in N_Veh,i in N)
   (X_ij_k[0][i][k])<=Nb_Veh;     	  
 //(6)每辆车经过节点i时，才可提供货物   *(对应模型中(5))*
 forall(i in N_C)
   forall(p in N_Pro)
     forall(k in N_Veh)
     	  L_i_p_k[i][p][k]<=sum(j in N) (X_ij_k[i][j][k]);
 //每个社区节点的每种商品只能由一辆车配送
 forall(i in N_C)
   forall(p in N_Pro)
     sum(k in N_Veh)L_i_p_k[i][p][k]<=1;
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         L_i_p_k[i][p][k]>=L_i_c_p_k[i][c][p][k];
 //车辆k经过某节点，可以配送至少1种货物
 forall(i in N_C)
   forall(k in N_Veh)
     sum(j in N)X_ij_k[i][j][k]<=sum(p in N_Pro)L_i_p_k[i][p][k];
 
 forall(i in N_C)
   forall(p in N_Pro)
     (1-Demand_ip[i][p])*sum(c in N_CT)Demand[i][c][p]==0;
 
 forall(i in N_C)
   forall(p in N_Pro)
     Demand_ip[i][p]==sum(k in N_Veh)L_i_p_k[i][p][k];
//     Demand_ip[i][p]<=sum(k in N_Veh)L_i_p_k[i][p][k];
 //若配送需求为0，则不允许经过该点
 forall(i in N_C)
   forall(p in N_Pro)
     sum(k in N_Veh)L_i_p_k[i][p][k]<=M*sum(c in N_CT)Demand[i][c][p];
 //k车经过i地配送p种商品的约束
 forall(i in N_C)
   forall(p in N_Pro)
     forall(k in N_Veh)
       sum(c in N_CT)L_i_c_p_k[i][c][p][k]>=L_i_p_k[i][p][k];
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         L_i_c_p_k[i][c][p][k]<=sum(j in N)X_ij_k[i][j][k];
         
 //i地c居民p种商品只能由一辆车送
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       sum(k in N_Veh)L_i_c_p_k[i][c][p][k]<=Demand_ip[i][p];
 //(7)不超过最长距离   *(对应模型中(未出现))*
 forall(k in N_Veh)
   T_i_k[Nb_Cus+1][k]<=Max_time;       
/******载重量约束  *******/
 //(8)车辆从场站出发时的载重量   *(对应模型中(11))*
 forall(k in N_Veh)
   V_i_k[0][k]==Start_cap;  
 //(9)节点间的载重量更新   *(对应模型中(10))*
 forall(i in N)
   forall(j in N)
     forall(k in N_Veh)
			V_i_k[j][k]>=V_i_k[i][k]+sum(c in N_CT)sum(p in N_Pro)(Demand[j][c][p]*L_i_c_p_k[j][c][p][k])-M*(1-X_ij_k[i][j][k]); 
   
 forall(j in N_C)
   forall(k in N_Veh)
	 V_i_k[j][k]<=M*(sum(i in N)X_ij_k[j][i][k]);
 //(10)载重量不超过上限   *(对应模型中(8)(9))*
 forall(p in N_Pro)
 	forall(k in N_Veh)
		W_p_k[p][k]==sum(i in N_C)sum(c in N_CT)(Demand[i][c][p]*L_i_c_p_k[i][c][p][k]);
 forall(p in N_Pro)
		sum(k in N_Veh)W_p_k[p][k]==sum(i in N_C)sum(c in N_CT)Demand[i][c][p];		
 forall(k in N_Veh)
	sum(p in N_Pro)W_p_k[p][k]==V_i_k[Nb_Cus+1][k];
 forall(i in N)
 	forall(k in N_Veh)
		V_i_k[i][k]<=Q;
 //(11)经过节点i时可配送至少1种货物*(对应模型中(7))*
 forall(k in N_Veh,i in N_C)
	sum(j in N)
	  X_ij_k[i][j][k]<=sum(p in N_Pro)L_i_p_k[i][p][k]; 
 //(11)车辆最大装载商品数量*(对应模型中(6))*
 forall(k in N_Veh)
	Rou[k]>=
	sum(p in N_Pro) L_p_k[p][k];
	
 forall(k in N_Veh,p in N_Pro)
     sum(i in N_C)L_i_p_k[i][p][k]<=0.9+M*L_p_k[p][k];
    
 forall(k in N_Veh,p in N_Pro)
    sum(i in N_C)L_i_p_k[i][p][k]>=L_p_k[p][k];
	  		
/******时间窗约束  *******/
 //(12)车辆从场站出发时的时间		
 forall(k in N_Veh)
 	T_i_k[0][k]==Start_time;  
 //(12)节点间的服务开始时间更新
 forall(k in N_Veh)
 	forall(i in N:i<=Nb_Cus)
 		forall(j in N)
 		   T_i_k[j][k]>=(T_i_k[i][k]+Travel_time[i][j]+Service_time[i]-M*(1-(X_ij_k[i][j][k])));
 forall(k in N_Veh)
 	forall(i in N:i<=Nb_Cus)
 		forall(j in N)
 		   T_i_k[j][k]<=(T_i_k[i][k]+Travel_time[i][j]+Service_time[i]+M*(1-(X_ij_k[i][j][k])));
 forall(k in N_Veh)
   forall(j in N)
 	 T_i_k[j][k]<=M*(sum(i in N:i<=Nb_Cus)X_ij_k[i][j][k]);
 //(12)社区时间窗下界 
 forall(k in N_Veh)
 	forall(i in N_C)
		T_i_k[i][k]>=ET[i]-M*(1-sum(j in N)(X_ij_k[i][j][k])); 

 //(13)社区时间窗上界
 forall(k in N_Veh)
    forall(i in N_C)
		T_i_k[i][k]<=LT[i]+M*(1-sum(j in N)(X_ij_k[i][j][k])); 
 //(14)新鲜度下降成本函数
 /*forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       H[i][c][p]>=
         sum(k in N_Veh) 
           (Demand[i][c][p]*Omega*((1-(1-Alpha[p]*T_i_k[i][k])))
           +H_1[i][c][p]*Omega*(1-(1-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])))
           +H_3[i][c][p]*a*(T_i_k[i][k]-LT_Cus[i][c]));*/
 
 /*1月12版本
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       H[i][c][p]>=
         sum(k in N_Veh) 
           (Demand[i][c][p]*Omega*((1-(1-Alpha[p]*T_i_k[i][k])))
           +H_1[i][c][p]*Omega*(1-(1-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])))
           +H_3[i][c][p]*a*(T_i_k[i][k]-LT_Cus[i][c]));*/
  
  //明确成本函数线性化
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_ClearPenal[i][c][p][k]<=(1-(1-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])))+M*(1-H_1[i][c][p][k])+M*(1-(sum(j in N)X_ij_k[i][j][k]));
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)   
         H_ClearPenal[i][c][p][k]>=(1-(1-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])))-M*(1-H_1[i][c][p][k])-M*(1-(sum(j in N)X_ij_k[i][j][k])); 
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)   
         H_ClearPenal[i][c][p][k]>=0; 
  //潜在成本函数
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_PotenPenal[i][c][p][k]<=((1-(1-Alpha[p]*T_i_k[i][k])))+M*(1-L_i_c_p_k[i][c][p][k])+M*(1-(sum(j in N)X_ij_k[i][j][k]));
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_PotenPenal[i][c][p][k]>=((1-(1-Alpha[p]*T_i_k[i][k])))-M*(1-L_i_c_p_k[i][c][p][k])-M*(1-(sum(j in N)X_ij_k[i][j][k]));//-M*(1-(sum(j in N)X_ij_k[i][j][k]));
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_PotenPenal[i][c][p][k]>=0;
  //test==M*(1-L_i_c_p_k[3][1][1][1]);
  //H_PotenPenal[2][2][1][3]==Omega*((1-(1-Alpha[1]*T_i_k[2][3])));  
  //H_PotenPenal[2][2][1][3]==0;     
  //L_i_c_p_k[2][2][1][3]==1;
   //H_PotenPenal[2][2][1][3]==Alpha[1]*T_i_k[2][3];                    
  //超越时间窗成本函数线性化         
  /*forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_DelayPenal[i][c][p][k]<=a*(T_i_k[i][k]-LT_Cus[i][c]);//不需要限制上界 */
  //超越时间窗成本函数线性化   
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)   
         H_DelayPenal[i][c][p][k]<=(T_i_k[i][k]-LT_Cus[i][c])+M_Delay*(1-H_3[i][c][p][k])+M_Delay*(1-(sum(j in N)X_ij_k[i][j][k]));
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)   
         H_DelayPenal[i][c][p][k]>=(T_i_k[i][k]-LT_Cus[i][c])-M_Delay*(1-H_3[i][c][p][k])-M_Delay*(1-(sum(j in N)X_ij_k[i][j][k]));
   forall(i in N_C)
     forall(c in N_CT)
       forall(p in N_Pro)
         forall(k in N_Veh)   
           H_DelayPenal[i][c][p][k]>=0;                                   
   //成本函数部分车辆部分节点处为0
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_ClearPenal[i][c][p][k]<=M*(sum(j in N)X_ij_k[i][j][k]); 
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_PotenPenal[i][c][p][k]<=M*(sum(j in N)X_ij_k[i][j][k]);
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_DelayPenal[i][c][p][k]<=M*(sum(j in N)X_ij_k[i][j][k]);     
                                       
/*forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       H[i][c][p]>=
         sum(k in N_Veh) 
           H_2[i][c][p]*(Demand[i][c][p]*Omega*((1-exp(-Alpha[p]*T_i_k[i][k])))
           +H_1[i][c][p]*Omega*(1-exp(-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])))
           +H_3[i][c][p]*a*(T_i_k[i][k]-LT_Cus[i][c]));*/
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)         
       forall(k in N_Veh)
         T_i_k[i][k]<=H_1[i][c][p][k]*ET_Cus[i][c]+H_2[i][c][p][k]*LT_Cus[i][c]+H_3[i][c][p][k]*M+M*(1-L_i_c_p_k[i][c][p][k]);
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)         
       forall(k in N_Veh)
         T_i_k[i][k]>=H_2[i][c][p][k]*ET_Cus[i][c]+H_3[i][c][p][k]*LT_Cus[i][c]-M*(1-L_i_c_p_k[i][c][p][k]);
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)   
       forall(k in N_Veh)      
         H_1[i][c][p][k]+H_2[i][c][p][k]+H_3[i][c][p][k]==L_i_c_p_k[i][c][p][k];
           /*H[i][c][p]>=
           sum(k in N_Veh)
             Demand[i][c][p]*Omega*((1-exp(-Alpha[p]*T_i_k[i][k]))+()*(1-exp(-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k]))));
       if (ET_Cus[i][c]-T_i_k[i][k]>0){
         H[i][c][p]==
           sum(k in N_Veh)
             Demand[i][c][p]*Omega*((1-exp(-Alpha[p]*T_i_k[i][k]))+1-exp(-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])));
       }else if(T_i_k[i][k]-LT_Cus[i][c]>0){
         H[i][c][p]==
           sum(k in N_Veh)
             Demand[i][c][p]*Omega*((1-exp(-Alpha[p]*T_i_k[i][k]))
             +a*(T_i_k[i][k]-LT_Cus[i][c]));
       }else{
         H[i][c][p]==
           sum(k in N_Veh)
             Demand[i][c][p]*Omega*((1-exp(-Alpha[p]*T_i_k[i][k])));
  }
 /*H[i][c][p]>=sum(k in N_Veh) Demand[i][c][p]*Omega*((1-exp(-Alpha[p]*T_i_k[i][k]));
  forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)         
           H[i][c][p]*/
 } 

execute DISPLAY {   
  //cplex.exportModel("model.lp");
  writeln(" Maximum profit = " , cplex.getObjValue());
  writeln(Demand);
  // 输出形状信息
  //writeln("Shape of T_i_k:", shapeT.shape);
  //writeln("Shape of V_i_k:", shapeV.shape);
}