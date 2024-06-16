package VRPTWMTMC;

import java.io.Serializable;

public class Customer implements Serializable {
    private double xCoordinate;
    private double yCoordinate;
    private double serviceTime;
//    private double demand;
    private double startTW;
    private double endTW;
    private int index;
    private Resident[] residents;

    public Customer(double xCoordinate, double yCoordinate, double serviceTime, double[][] demand, double startTW, double endTW, int index,double[][] timeInfo) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.serviceTime = serviceTime;
//        this.demand = demand;
        this.startTW = startTW;
        this.endTW = endTW;
        this.index = index;
        residents=new Resident[AlgorithmParameters.nbResident];
        for(int i=0;i<AlgorithmParameters.nbResident;i++){
            this.residents[i]=new Resident(xCoordinate,yCoordinate,demand[i],timeInfo[i][0],timeInfo[i][1]);
        }
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

//    public double getDemand() {
//        return demand;
//    }

    public double getStartTW() {
        return startTW;
    }

    public double getEndTW() {
        return endTW;
    }
    public int getIndex() {
        return index;
    }
    public Resident[] getResidents(){
        return residents;
    }
}
