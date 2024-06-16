package VRPTW;

import java.io.Serializable;

public class Customer implements Serializable {
    private double xCoordinate;
    private double yCoordinate;
    private double serviceTime;
    private double demand;
    private double startTW;
    private double endTW;
    private int customerindex;//节点序号
    private int index;//数组索引
    private Resident[] residents;

    public Customer(double xCoordinate, double yCoordinate, double serviceTime, double demand, double startTW, double endTW, int customerindex,int index) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.serviceTime = serviceTime;
        this.demand = demand;
        this.startTW = startTW;
        this.endTW = endTW;
        this.customerindex=customerindex;
        this.index = index;
    }

    public double getXCoordinate() {
        return xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public double getDemand() {
        return demand;
    }

    public double getStartTW() {
        return startTW;
    }

    public double getEndTW() {
        return endTW;
    }
    public int getIndex() {
        return index;
    }
    public int getCustomerIndex() {
        return customerindex;
    }
    public Resident[] getResidents(){
        return residents;
    }
}
