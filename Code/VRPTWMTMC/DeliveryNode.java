package VRPTWMTMC;
import java.io.Serializable;
import java.util.HashSet;

public class DeliveryNode implements Serializable {
    private Comedy comedy;
    private double cumulativeArrivalTime;
    public double time;
    private double cumulativeDemand;
    private double cumulativeDistance;
    private double cumulativePunish;
    private double cumulativePenalty;
    private double cumulativeClear;
    private double cumulativePotential;
    private double cumulativeDelay;
    private double WT;
    private double timeWarp;
    private double Punish;
    private double Penalty;
    private double Clear;
    private double Potential;
    private double Delay;
    private HashSet<Integer> typeSet = new HashSet<>(); //货物集合
    private DeliveryNode next;


    public DeliveryNode(Comedy comedy) {
        this.comedy = comedy;
        setCumResource();
        this.next = null;
        this.typeSet.clear();
        this.typeSet.add(comedy.getType());
        this.time=0;
        this.WT=0;
        this.timeWarp=0;
    }
    public void setCumResource(){
        this.cumulativeArrivalTime = 0;
        this.cumulativeDemand = 0;
        this.cumulativeDistance = 0;
        this.cumulativePunish = 0;
        this.cumulativePenalty = 0;
        this.cumulativeClear = 0;
        this.cumulativePotential = 0;
        this.cumulativeDelay = 0;
        this.WT=0;
        this.timeWarp=0;
        this.Punish = 0;
        this.Penalty = 0;
        this.Clear = 0;
        this.Potential = 0;
        this.Delay=0;
        this.typeSet.clear();
        this.typeSet.add(this.comedy.getType());
    }
    public Comedy getComedy() {
        return comedy;
    }

    public double getCumTime() {
        return cumulativeArrivalTime;
    }

    public double getCumDem() {
        return cumulativeDemand;
    }
    public double getCumDis() {
        return cumulativeDistance;
    }

    public double getCumPunish() {
        return cumulativePunish;
    }
    public double getCumPenalty() {
        return cumulativePenalty;
    }

    public double getCumClear() {
        return cumulativeClear;
    }
    public double getCumPotential() {
        return cumulativePotential;
    }
    public double getCumDelay() {
        return cumulativeDelay;
    }
    public double getClear() {
        return Clear;
    }
    public double getPotential() {
        return Potential;
    }
    public double getDelay() {
        return Delay;
    }
    public double getPenalty() {
        return Penalty;
    }
    public double getWT(){return WT;}
    public double getTimeWarp(){return timeWarp;}

    public HashSet<Integer> getTypeSet() {
        return new HashSet<>(typeSet);
    }

    public DeliveryNode getNext() {
        return next;
    }

    public void setNext(DeliveryNode next) {
        this.next = next;
    }

    public void setCumulativeArrivalTime(double previousCumulativeArrivalTime, Comedy previousComedy, Parameters params,Customer[] customers) {
        if(previousComedy.getIndex()!=this.getComedy().getIndex()){
            this.cumulativeArrivalTime = previousCumulativeArrivalTime + previousComedy.getServiceTime() + params.getDist()[previousComedy.getIndex()][this.comedy.getIndex()];
            if(customers[this.getComedy().getIndex()].getStartTW()-this.cumulativeArrivalTime>AlgorithmParameters.eps){
                this.cumulativeArrivalTime=customers[this.getComedy().getIndex()].getStartTW();
            }else if(this.cumulativeArrivalTime-customers[this.getComedy().getIndex()].getEndTW()>AlgorithmParameters.eps){
                this.cumulativeArrivalTime=customers[this.getComedy().getIndex()].getEndTW();
            }
        }else{
            this.cumulativeArrivalTime=previousCumulativeArrivalTime;
        }
    }
    public void setCumTime(double time){this.cumulativeArrivalTime=time;}
    public void setTime(double time){
        this.time=time;
    }
    public void setCumDem(double demand) {
        this.cumulativeDemand = demand;
    }
    public void setCumDis(double dist) {
        this.cumulativeDistance = dist;
    }
    public void setCumPunish(double punish) {
        this.cumulativePunish = punish;
    }
    public void setCumPenalty(double penalty) {
        this.cumulativePenalty = penalty;
    }
    public void setCumClear(double clear) {
        this.cumulativeClear = clear;
    }
    public void setCumPotential(double potential) {
        this.cumulativePotential = potential;
    }
    public void setCumDelay(double Delay) {
        this.cumulativeDelay = Delay;
    }
    public void setDelay(double Delay) {
        this.Delay = Delay;
    }
    public void setPunish(double punish) {
        this.Punish = punish;
    }
    public void setPenalty(double penalty) {
        this.Penalty = penalty;
    }
    public void setClear(double clear) {
        this.Clear = clear;
    }
    public void setPotential(double potential) {
        this.Potential = potential;
    }
    public void setWT(double waiteTime){this.WT=waiteTime;}
    public void setTimeWarp(double timeWarp){this.timeWarp=timeWarp;}
    public void setTypeSet(HashSet<Integer> set) {
        this.typeSet.clear();
        this.typeSet.addAll(set);
    }


    public void updateCumulativeArrivalTime(double time){
        this.cumulativeArrivalTime=time;
    }
    public void updateCumulativeDemand(double CumulativeDemand) {
        this.cumulativeDemand = CumulativeDemand ;
    }
    public void updateCumulativeDistance(double CumulativeDistance) {
        this.cumulativeDistance = CumulativeDistance ;
    }

    @Override
    public String toString() {
        int type = comedy.getType();
        char typeChar = (char) ('A' + type);
        return (comedy.getIndex() + 1) + String.valueOf(typeChar) ;

    }
    public DeliveryNode Clone() {
        // 创建一个新的DeliveryNode实例
        DeliveryNode clone = new DeliveryNode(this.comedy.Clone()); // 假设Comedy类有一个deepClone方法

        // 复制所有的基本类型字段和不可变字段
        clone.cumulativeArrivalTime = this.cumulativeArrivalTime;
        clone.time = this.time;
        clone.cumulativeDemand = this.cumulativeDemand;
        clone.cumulativeDistance = this.cumulativeDistance;
        clone.cumulativePunish = this.cumulativePunish;
        clone.cumulativePenalty = this.cumulativePenalty;
        clone.cumulativeClear = this.cumulativeClear;
        clone.cumulativePotential = this.cumulativePotential;
        clone.cumulativeDelay = this.cumulativeDelay;
        clone.WT = this.WT;
        clone.timeWarp = this.timeWarp;
        clone.Punish = this.Punish;
        clone.Penalty = this.Penalty ;
        clone.Clear =  this.Clear;
        clone.Potential = this.Potential;
        clone.Delay=this.Delay;
        // 注意: 不需要复制next字段，因为深克隆是逐个节点进行的

        return clone;
    }
}
