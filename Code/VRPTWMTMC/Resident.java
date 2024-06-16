package VRPTWMTMC;


public class Resident {
    private double xCoordinate;
    private double yCoordinate;
    private double[] comedyDemand=new double[AlgorithmParameters.nbComedy];
    private double startTime;
    private double endTime;

    public Resident(double xCoordinate, double yCoordinate, double[] comedyDemand, double startTime, double endTime) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        for(int i = 0; i< AlgorithmParameters.nbComedy; i++){
            this.comedyDemand[i]=comedyDemand[i];
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public double getXCoordinate() {
        return xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public double[] getComedyDemand() {
        return comedyDemand;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }
}
