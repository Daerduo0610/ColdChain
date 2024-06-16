package VRPTW;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Individual implements Serializable{
    public List<Route> routes;
    public int numRoutes;
    public int[] routeStart=new int[AlgorithmParameters.nbCommunity];
    public int[] customerOrder=new int[AlgorithmParameters.nbCommunity];
    public int[] nodeNum=new int[AlgorithmParameters.nbCommunity];//每条路径的客户节点数量
    public int flag=1;
    public double fitness=-10;


    public Individual() {
        this.routes = new ArrayList<>();
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setNumRoutes(int numRoutes) {
        this.numRoutes = numRoutes;
    }
    public void setRouteStart(int[] routeStart) {
        this.routeStart = routeStart;
    }
    public void addRoute(Route route) {
        routes.add(route);
    }
    public void clearRoutes() {
        routes.clear();
    }

    public int getNumRoutes() {
        return numRoutes;
    }

    public int getRoutesSize() {
        return routes.size();
    }

    public Route getRoute(int index) {
        return routes.get(index);
    }

    public int getRouteStart(int index) {
        return routeStart[index];
    }
    //int[] route=new int[AlgorithmParameters.nbCommunity];//类，存储node组成的链表
    //node存每个customer的信息：时间窗，需求，坐标 node指向customer类
    //增加cargo类，每个货物在一天内新鲜度的下降情况
    //int[] route={4,2,6,7,9,10,8,5,3,1,0,31,32,30,34,36,37,38,35,33,19,23,24,26,28,29,27,25,22,21,20,42,41,40,39,43,45,44,47,49,48,46,12,16,17,18,14,15,13,11};
    int[] route1={6,2,4,7,9,10,8,5,48,1,0,31,37,30,34,36,32,38,35,27,19,23,24,22,28,17,33,11,26,21,20,42,41,40,39,43,45,44,47,49,3,46,12,16,29,18,14,15,13,25};
    //int[] route_start=new int[AlgorithmParameters.nbCommunity];//二级信息 route_start[i]代表第i+1辆车访问的第一个客户节点
    //int k;//所用车辆数
    //int[] node=new int[AlgorithmParameters.nbCommunity];//每条路径的客户节点数量

    public void InitializeOrder(){
        Random random=new Random();
        boolean[] bool=new boolean[AlgorithmParameters.nbCommunity];
        int rand=0;
        for(int i=0;i<AlgorithmParameters.nbCommunity;i++){
            do{
                rand=random.nextInt(AlgorithmParameters.nbCommunity);
                customerOrder[i]=rand;
            }while(bool[rand]);
            bool[rand]=true;
        }
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

}
