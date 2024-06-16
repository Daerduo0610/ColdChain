package VRPTWMTMC;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

public class Individual implements Serializable {
    public List<DeliveryRoute> DeliveryRoutes;
    public List<DeliveryNode> DeliveryOrder;
    public int numRoutes;
    public int[] routeStartIndex=new int[AlgorithmParameters.nbCommunity*AlgorithmParameters.nbComedy];//只记录下标，不记录节点对应的社区序号
    //public int[] customerOrder={4,2,6,7,9,10,8,5,3,1,0,31,32,30,34,36,37,38,35,33,19,23,24,26,28,29,27,25,22,21,20,42,41,40,39,43,45,44,47,49,48,46,12,16,17,18,14,15,13,11};

    public int[] nodeNum=new int[AlgorithmParameters.nbCommunity*AlgorithmParameters.nbComedy];//每条路径的客户节点数量
    public double fitness;
    public List<Integer> closedistance; //存储距离
    public double distance=0;
    public int fitness_rank=0;
    public int distance_rank=0;
    public int rank;


    public Individual() {
        this.DeliveryRoutes = new ArrayList<>();
        this.DeliveryOrder=new ArrayList<>();
        this.closedistance=new ArrayList<>();
        this.fitness=0;
        this.numRoutes=0;
        this.rank=0;
    }

    public List<DeliveryRoute> getRoutes() {
        return DeliveryRoutes;
    }
    public List<DeliveryNode> getDeliveryOrder() {
        return DeliveryOrder;
    }

    public void setNumRoutes(int numRoutes) {
        this.numRoutes = numRoutes;
    }
    public void setRouteStartIndex(int[] routeStart) {
        for(int i=0;i<routeStart.length;i++){
            this.routeStartIndex[i]=routeStart[i];
        }
    }
    public void addRoute(DeliveryRoute route) {
        DeliveryRoutes.add(route);
    }
    public void clearRoutes() {
        DeliveryRoutes.clear();
    }

    public int getNumRoutes() {
        return numRoutes;
    }

    public int getRoutesSize() {
        return DeliveryRoutes.size();
    }

    public DeliveryRoute getRoute(int index) {
        return DeliveryRoutes.get(index);
    }
    public int getRouteStartIndex(int index) {
        return routeStartIndex[index];
    }
    //int[] route=new int[AlgorithmParameters.nbCommunity];//类，存储node组成的链表
    //node存每个customer的信息：时间窗，需求，坐标 node指向customer类
    //增加cargo类，每个货物在一天内新鲜度的下降情况
    //int[] route={4,2,6,7,9,10,8,5,3,1,0,31,32,30,34,36,37,38,35,33,19,23,24,26,28,29,27,25,22,21,20,42,41,40,39,43,45,44,47,49,48,46,12,16,17,18,14,15,13,11};
    //int[] route1={6,2,4,7,9,10,8,5,48,1,0,31,37,30,34,36,32,38,35,27,19,23,24,22,28,17,33,11,26,21,20,42,41,40,39,43,45,44,47,49,3,46,12,16,29,18,14,15,13,25};
    //int[] route_start=new int[AlgorithmParameters.nbCommunity];//二级信息 route_start[i]代表第i+1辆车访问的第一个客户节点
    //int k;//所用车辆数
    //int[] node=new int[AlgorithmParameters.nbCommunity];//每条路径的客户节点数量

    public void InitializeOrder(Customer[] customers){
        Random random=new Random();
        int[] customerOrder=new int[AlgorithmParameters.nbCommunity];
        boolean[] bool=new boolean[AlgorithmParameters.nbCommunity];
        int rand=0;
        for(int i = 0; i< AlgorithmParameters.nbCommunity; i++){
            do{
                rand=random.nextInt(AlgorithmParameters.nbCommunity);
                customerOrder[i]=rand;
            }while(bool[rand]);
            bool[rand]=true;
        }
        this.DeliveryOrder.clear();
        for(int i=0;i<AlgorithmParameters.nbCommunity;i++){
            for(int j=0;j<AlgorithmParameters.nbComedy;j++){
                double demand=0;
                for(int k=0;k<AlgorithmParameters.nbResident;k++){
                    demand+=customers[customerOrder[i]].getResidents()[k].getComedyDemand()[j];
                }
                if(Math.abs(demand)>AlgorithmParameters.eps){
                    this.DeliveryOrder.add(new DeliveryNode(new Comedy(customers[customerOrder[i]].getServiceTime(),j,demand,customerOrder[i])));
                }
            }
        }
    }
    public Individual Clone() {
        Individual clone = new Individual();

        // 克隆DeliveryRoutes
        for (DeliveryRoute route : this.DeliveryRoutes) {
            clone.DeliveryRoutes.add(route.Clone()); // 假设DeliveryRoute有一个deepClone方法
        }

        // 克隆DeliveryOrder
//        for (DeliveryNode node : this.DeliveryOrder) {
//            clone.DeliveryOrder.add(node.Clone()); // 假设DeliveryNode有一个deepClone方法
//        }

        // 克隆简单字段
        clone.numRoutes = this.numRoutes;
//        clone.routeStartIndex = Arrays.copyOf(this.routeStartIndex, this.routeStartIndex.length);
//        clone.nodeNum = Arrays.copyOf(this.nodeNum, this.nodeNum.length);
        clone.fitness = this.fitness;
        clone.distance = this.distance;
        clone.fitness_rank = this.fitness_rank;
        clone.distance_rank = this.distance_rank;
        clone.rank = this.rank;

        // 克隆closedistance（如果它是简单类型的List）
        clone.closedistance = new ArrayList<>(this.closedistance);

        return clone;
    }

    protected Individual deepClone() throws IOException, ClassNotFoundException {

        Individual son=null;
        //在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组中
        //默认创建一个大小为32的缓冲区
        ByteArrayOutputStream byOut=new ByteArrayOutputStream();
        //对象的序列化输出
        ObjectOutputStream outputStream=new ObjectOutputStream(byOut);//通过字节数组的方式进行传输
        outputStream.writeObject(this);  //将当前student对象写入字节数组中

        //在内存中创建一个字节数组缓冲区，从输入流读取的数据保存在该字节数组缓冲区
        ByteArrayInputStream byIn=new ByteArrayInputStream(byOut.toByteArray()); //接收字节数组作为参数进行创建
        ObjectInputStream inputStream=new ObjectInputStream(byIn);
        son=(Individual) inputStream.readObject(); //从字节数组中读取
        return  son;
    }

    @Override
    public Individual clone() throws CloneNotSupportedException {
        return (Individual) super.clone();
    }
}
