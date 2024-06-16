package VRPTWMTMC;

import VRPTWMTMC.Resident;
import java.io.Serializable;

public class Comedy implements Serializable {
//    private double xCoordinate;
//    private double yCoordinate;
    private double serviceTime;
    private int type;
    private double demand;
    private int index;
    private double value;
    public Comedy(double serviceTime,int type, double demand, int index) {
//        this.xCoordinate = xCoordinate;
//        this.yCoordinate = yCoordinate;
        this.serviceTime = serviceTime;
        this.type=type;
        this.demand = demand;
        this.index = index;
        this.value = AlgorithmParameters.commodityValue[type];
    }

//    public double getXCoordinate() {
//        return xCoordinate;
//    }
//
//    public double getYCoordinate() {
//        return yCoordinate;
//    }

    public double getServiceTime() {
        return serviceTime;
    }

    public int getType(){return type;}
    public double getValue(){return value;}

    public double getDemand() {
        return demand;
    }

    public int getIndex() {
        return index;
    }
    public Comedy Clone() {
        // 直接使用现有的构造函数创建一个新的Comedy实例，复制所有字段的值
        Comedy clone = new Comedy(this.serviceTime, this.type, this.demand, this.index);
        // 由于所有字段都是基本数据类型或不可变，直接复制值即可实现深克隆
        return clone;
    }
}
