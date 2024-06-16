package VRPTWMTMC;

public class AlgorithmParameters {

    // 声明参数
    public static int running_time=-1;
    public static final int nbGranular = 20; // Granular search parameter, limits the number of moves in the RI local search
    public static final int min_population = 25; // Minimum population size
    public static final int population_range = 40; // Number of solutions created before reaching the maximum population size (i.e., generation size)
    public static final int nbElite = 4; // Number of elite individuals 可行解和不可行解分别大于等于nbElite
    public static final int nbClose = 5; // Number of the closest solutions/individuals considered when calculating diversity contribution

    public static final int nbInitial=1; //初始化中产生一个个体时从多少个个体中选择

    public static final int nbIterPenaltyManagement = 100; // Number of iterations between penalty updates
    public static final double targetFeasible = 0.2; // Reference proportion for the number of feasible individuals, used for the adaptation of the penalty parameters
    public static final double penaltyDecrease = 0.85; // Multiplier used to decrease penalty parameters if there are sufficient feasible individuals
    public static final double penaltyIncrease = 1.2; // Multiplier used to increase penalty parameters if there are insufficient feasible individuals

    public static final int seed = 0; // Random seed. Default value: 0
    public static final int crossNum=10;
    public static final int nbIter= 500; // The Nb iterations without improvement until termination (or restart if a time limit is specified). Default value: 20,000 iterations
    public static final int nbIterTraces = 500; // Number of iterations between traces display during HGS execution 每500代展示一次当前最优解
    public static final double timeLimit = 0; // CPU time limit until termination in seconds. Default value: 0 (i.e., inactive)
    //public static final int useSwapStar = 1; // Use SWAP* local search or not. Default value: 1. Only available when coordinates are provided.

    public static  int nbCommunity=-1; //社区节点数量
    public static  int nbResident=-1;
    public static  int nbComedy=-1;
    public static final int maxComedy=3;
    public static final int maximumDuration=1236; //最晚结束时间
    public static  int maximumLoad=1000; //最大载重量
    public static double penaltyCapacity=1; //载重量惩罚系数
    public static double penaltyDuration=3; //时间窗的惩罚系数
    public static final double velocity=1; //行驶速度
    public static final int M=99999;
    public static final double freshnessRange=0.6; //新鲜度范围
    public static  double maxDelayRange=300; //超过时间窗最大范围
    public static  double[] commodityValue=new double [4];//货物种类对应的价值
    public static  double[] alpha=new double [4];
    public static  double[] beta=new double [4];
    public static final double Omega=1; //新鲜度下降惩罚系数
    public static final double a=1; //超过时间窗惩罚系数
    // 声明常量
    public static final double eps = 1e-6;

    // 私有构造函数，防止实例化
    private AlgorithmParameters() {
        // 空构造函数
    }
}
