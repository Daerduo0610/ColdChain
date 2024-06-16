/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Master
 * Creation Date: 2024��1��25�� at ����22:32:22
 *********************************************/

   /****** ����ָ��  *******/
 int Nb_Cus=...;	//�ͻ�������
 int Nb_Veh=...;	//������
 int Nb_CusTyp=...;
 int Nb_Pro=...;
 //int Nb_Weight=...;	//���������
 
  /****** ��������  *******/
 range N = 0..Nb_Cus+1;			//���������нڵ�ļ��ϣ�0��ʾdepot��㣬Nb_N_Cus+1��ʾ�ص�depot�յ�
 range N_C = 1..Nb_Cus;			//�����пͻ��ļ���
 range N_Veh = 1..Nb_Veh;			//��������
 range N_CT = 1..Nb_CusTyp;     //��������
 range N_Pro = 1..Nb_Pro;
 //range N_W = 1..Nb_Weight;	//G���������ֶΣ��ļ��ϣ����ڶ�����߱���
 //range N_Weight = 1..Nb_Weight+1;	//G���������ֶΣ��ļ��ϣ����ڶ������������G����=Nb_Weight+1
 
 
  /****** ����  *******/
 int Q=...;	//ÿ������������
 int M=...;	//��������
 int M_Delay=...; //��Խʱ�䴰��������
 float Max_time=...;	//�������ʻʱ��
 float Start_cap=...;	//�����ӳ�վ����ʱ��������
 float Start_time=...;	//�����ӳ�վ����ʱ��ʱ��
 //float Sens_dis=...;	//�����Է���ʱ��������������ϵ��
// float Omega=...; //���ʶ��½��ͷ��ɱ�ϵ��
// float a=...; //����ʱ�䴰�ͷ��ɱ�ϵ��
 //float a1=...;	//ÿ�����ͺ�Ϊ(a1+a2*V/a3)/100
 //float a2=...;	//ÿ�����ͺ�Ϊ(a1+a2*V/a3)/100
 //float a3=...;	//ÿ�����ͺ�Ϊ(a1+a2*V/a3)/100

 //��ά����
 int Rou[N_Veh]=...; //ÿ����װ����Ʒ�������������
 float Travel_dis[N][N] = ...;	//����ľ����ڽӾ���
 float Travel_time[N][N] = ...;	//�����ʱ���ڽӾ���
 float Service_time[N]=...;	//ÿ���ڵ��ϵķ���ʱ��
 float ET[N]= ...;	//���нڵ���������ʼʱ��
 float LT[N]= ...;	//���нڵ���������ʼʱ��
 float ET_Cus[N][N_CT]=...; //���нڵ㲻ͬ�ͻ�������ȡ��ʱ��
 float LT_Cus[N][N_CT]=...; //���нڵ㲻ͬ�ͻ�������ȡ��ʱ��
 float Alpha[N_Pro]=...; //����س��ϵĲ�ͬ��Ʒ���ʶ��½�ϵ��
 float Beta[N_Pro]=...; //���ų�����ͬ��Ʒ���ʶ��½�ϵ��
 float Value[N_Pro]=...; //ÿ����Ʒ�ļ�ֵϵ��
 //float H[N][N_CT][N_Pro]=...; //��i���ڵ��c�ֿͻ���p����Ʒ���ܳɱ�
 //float H_Poten[N_CT][N_Pro]=...; //Ǳ�ڳɱ�
 //float H_Clear[N_CT][N_Pro]=...; //��ȷ�ɱ�
 //float H_Penal[N_CT][N_Pro]=...; //�ͷ��ɱ�
 //float Bound[N_Weight]= ...;	//�������������ε�i�����ֵ��ϵ�������
 //float P_g[N_W]= ...;  //��ÿ�����������������ϵ�ÿ�����ͺ�
 
 //��ά���ϲ���
 //float Demand[N][N_CT][N_Pro] = ...;	//ÿ���ڵ������
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


 /****** ���߱���  *******/
 dvar boolean X_ij_k[N][N][N_Veh];	//���k��������(i,j)Ϊ1������Ϊ0
 dvar boolean L_i_p_k[N][N_Pro][N_Veh]; //���k��Ϊ�����ڵ�i�ṩ��p�ֻ���Ϊ1������Ϊ0
 dvar boolean L_p_k[N_Pro][N_Veh];//���k��װ�ص�p�ֻ���Ϊ1������Ϊ0
 dvar boolean L_i_c_p_k[N][N_CT][N_Pro][N_Veh];
 
 dvar float+ T_i_k[N][N_Veh];	//k����i��ķ���ʼʱ��
 dvar float+ V_i_k[N][N_Veh];	//k����i�����ʱ��������
 dvar float+ W_p_k[N_Pro][N_Veh]; //k���Ӳֿ����ʱ��ͬ�ֻ����װ����
 //dvar float+ H[N][N_CT][N_Pro][N_Veh]; //��k������i���ڵ��c�ֿͻ���p����Ʒ���ܳɱ�
 dvar float+ H_ClearPenal[N][N_CT][N_Pro][N_Veh]; //��k�����ڵ�i���ڵ��c�ֿͻ���p����Ʒ����ȷ�ɱ�
 dvar float+ H_PotenPenal[N][N_CT][N_Pro][N_Veh]; //��k�����ڵ�i���ڵ��c�ֿͻ���p����Ʒ��Ǳ�ڳɱ�
 dvar float+ H_DelayPenal[N][N_CT][N_Pro][N_Veh]; //��k�����ڵ�i���ڵ��c�ֿͻ���p����Ʒ�ĳ�Խʱ�䴰�ͷ��ɱ�
 dvar boolean Demand_ip[N_C][N_Pro]; //��¼i������p��Ʒ������
 dvar boolean H_1[N][N_CT][N_Pro][N_Veh]; //��һ����ȷ�ͷ��ɱ��ľ��߱���
 dvar boolean H_2[N][N_CT][N_Pro][N_Veh]; //ȫ������س�Ǳ�ڳͷ��ɱ��ľ��߱���
 dvar boolean H_3[N][N_CT][N_Pro][N_Veh]; //�����γ���ʱ�䴰�ͷ��ɱ��ľ��߱���
// dvar float test;
// dexpr int shapeX[i in N][j in N][k in N_Veh][g in N_W] = X_ij_k_g[i][j][k][g];
// dexpr float shapeT[i in N][k in N_Veh] = T_i_k[i][k];
// dexpr float shapeV[i in N][k in N_Veh] = V_i_k[i][k];
 
 //��Ϊ�������е�һЩָ��ʱ��������ǰ���þ��߱������洢��ָ��
 dvar float+ Cost;	//�ɱ�
  
 /****** �������������  *******/ 
 execute PARAMS { 
 	cplex.Tilim = 21600;	//cplex���ʱ����������Ϊ100s
 	cplex.EpGap=0.01;	//���gapС��10%����ֹͣcplex
 	cplex.NodeSel=2;	//ʹ�û������Ź��ƵĽڵ�ѡ����ԣ�����NodeSel��Ϊ2������best-estimate search
 	cplex.HeurFreq=1;	//��������ʽ(Heuristics)��Ƶ�ʣ�����HeurFreq=10, 5����1��ԽС��Ƶ��Խ��
 	cplex.VarSel=3;		//ʹ��Strong Branching�����ʹCplex��һ�����֧�ı���һ���̶���ʹ�������������������VarSel��Ϊ3
 	cplex.MIPEmphasis=1;//MIPEmphsisBestBound���ڿ�ʼʱ���ڽ�������������֤�������Եķ���Ӧ�ý��ٵļ��㹤�����������ྫ��������������ʼ�����������ڣ���֮�󾭸Ľ����Ŀ��нⷨ�ļ���
 	cplex.Probe=1;		//��̽�����Probe��Ϊ3�����̽��ʱ��̫�����𽥼�С���������ֵ��һ�£�
 }
 /****** Ŀ�꺯��  *******/
 minimize  Cost;
 
 /****** Լ��  *******/
 subject to{
 //����ָ��
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
 //1��12�հ汾
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
 /****** Flow����  *******/
 //(1)ÿ������������   *(��Ӧģ����(2))*
 forall(k in N_Veh)
     sum(i in N)
       (X_ij_k[0][i][k])==1; 
// //(2)ÿ���������յ�   *(��Ӧģ����(3))*   
 forall(k in N_Veh)
     sum(i in N)
     	(X_ij_k[i][Nb_Cus+1][k])==1; 
 //(3)ÿ���ڵ����ٱ�����һ��   *(��Ӧģ����(4))*
 /*forall(i in N_C,k in N_Veh)
     sum(j in N)
       (X_ij_k[i][j][k])==(X_ij_k[j][i][k]); */
 forall(i in N_C)
     sum(k in N_Veh,j in N)
       (X_ij_k[i][j][k])>=1;      
 //(4)��ƽ��Լ��   *(��Ӧģ����(4))*
 forall(i in N_C)
   forall(k in N_Veh)
     	sum(j in N) (X_ij_k[j][i][k])
     	  ==sum(j in N) (X_ij_k[i][j][k]);
 //(5)�����Veh_m����   *(��Ӧģ����(δ����))*
 sum(k in N_Veh,i in N)
   (X_ij_k[0][i][k])<=Nb_Veh;     	  
 //(6)ÿ���������ڵ�iʱ���ſ��ṩ����   *(��Ӧģ����(5))*
 forall(i in N_C)
   forall(p in N_Pro)
     forall(k in N_Veh)
     	  L_i_p_k[i][p][k]<=sum(j in N) (X_ij_k[i][j][k]);
 //ÿ�������ڵ��ÿ����Ʒֻ����һ��������
 forall(i in N_C)
   forall(p in N_Pro)
     sum(k in N_Veh)L_i_p_k[i][p][k]<=1;
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         L_i_p_k[i][p][k]>=L_i_c_p_k[i][c][p][k];
 //����k����ĳ�ڵ㣬������������1�ֻ���
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
 //����������Ϊ0�����������õ�
 forall(i in N_C)
   forall(p in N_Pro)
     sum(k in N_Veh)L_i_p_k[i][p][k]<=M*sum(c in N_CT)Demand[i][c][p];
 //k������i������p����Ʒ��Լ��
 forall(i in N_C)
   forall(p in N_Pro)
     forall(k in N_Veh)
       sum(c in N_CT)L_i_c_p_k[i][c][p][k]>=L_i_p_k[i][p][k];
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         L_i_c_p_k[i][c][p][k]<=sum(j in N)X_ij_k[i][j][k];
         
 //i��c����p����Ʒֻ����һ������
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       sum(k in N_Veh)L_i_c_p_k[i][c][p][k]<=Demand_ip[i][p];
 //(7)�����������   *(��Ӧģ����(δ����))*
 forall(k in N_Veh)
   T_i_k[Nb_Cus+1][k]<=Max_time;       
/******������Լ��  *******/
 //(8)�����ӳ�վ����ʱ��������   *(��Ӧģ����(11))*
 forall(k in N_Veh)
   V_i_k[0][k]==Start_cap;  
 //(9)�ڵ�������������   *(��Ӧģ����(10))*
 forall(i in N)
   forall(j in N)
     forall(k in N_Veh)
			V_i_k[j][k]>=V_i_k[i][k]+sum(c in N_CT)sum(p in N_Pro)(Demand[j][c][p]*L_i_c_p_k[j][c][p][k])-M*(1-X_ij_k[i][j][k]); 
   
 forall(j in N_C)
   forall(k in N_Veh)
	 V_i_k[j][k]<=M*(sum(i in N)X_ij_k[j][i][k]);
 //(10)����������������   *(��Ӧģ����(8)(9))*
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
 //(11)�����ڵ�iʱ����������1�ֻ���*(��Ӧģ����(7))*
 forall(k in N_Veh,i in N_C)
	sum(j in N)
	  X_ij_k[i][j][k]<=sum(p in N_Pro)L_i_p_k[i][p][k]; 
 //(11)�������װ����Ʒ����*(��Ӧģ����(6))*
 forall(k in N_Veh)
	Rou[k]>=
	sum(p in N_Pro) L_p_k[p][k];
	
 forall(k in N_Veh,p in N_Pro)
     sum(i in N_C)L_i_p_k[i][p][k]<=0.9+M*L_p_k[p][k];
    
 forall(k in N_Veh,p in N_Pro)
    sum(i in N_C)L_i_p_k[i][p][k]>=L_p_k[p][k];
	  		
/******ʱ�䴰Լ��  *******/
 //(12)�����ӳ�վ����ʱ��ʱ��		
 forall(k in N_Veh)
 	T_i_k[0][k]==Start_time;  
 //(12)�ڵ��ķ���ʼʱ�����
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
 //(12)����ʱ�䴰�½� 
 forall(k in N_Veh)
 	forall(i in N_C)
		T_i_k[i][k]>=ET[i]-M*(1-sum(j in N)(X_ij_k[i][j][k])); 

 //(13)����ʱ�䴰�Ͻ�
 forall(k in N_Veh)
    forall(i in N_C)
		T_i_k[i][k]<=LT[i]+M*(1-sum(j in N)(X_ij_k[i][j][k])); 
 //(14)���ʶ��½��ɱ�����
 /*forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       H[i][c][p]>=
         sum(k in N_Veh) 
           (Demand[i][c][p]*Omega*((1-(1-Alpha[p]*T_i_k[i][k])))
           +H_1[i][c][p]*Omega*(1-(1-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])))
           +H_3[i][c][p]*a*(T_i_k[i][k]-LT_Cus[i][c]));*/
 
 /*1��12�汾
 forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       H[i][c][p]>=
         sum(k in N_Veh) 
           (Demand[i][c][p]*Omega*((1-(1-Alpha[p]*T_i_k[i][k])))
           +H_1[i][c][p]*Omega*(1-(1-Beta[p]*(ET_Cus[i][c]-T_i_k[i][k])))
           +H_3[i][c][p]*a*(T_i_k[i][k]-LT_Cus[i][c]));*/
  
  //��ȷ�ɱ��������Ի�
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
  //Ǳ�ڳɱ�����
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
  //��Խʱ�䴰�ɱ��������Ի�         
  /*forall(i in N_C)
   forall(c in N_CT)
     forall(p in N_Pro)
       forall(k in N_Veh)
         H_DelayPenal[i][c][p][k]<=a*(T_i_k[i][k]-LT_Cus[i][c]);//����Ҫ�����Ͻ� */
  //��Խʱ�䴰�ɱ��������Ի�   
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
   //�ɱ��������ֳ������ֽڵ㴦Ϊ0
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
  // �����״��Ϣ
  //writeln("Shape of T_i_k:", shapeT.shape);
  //writeln("Shape of V_i_k:", shapeV.shape);
}