package VRPTWMTMC;
import java.io.Serializable;

public class DeliveryRoute implements Serializable {
    private DeliveryNode head;
    private int nodeNum;
    private double fitness;
    private boolean isFeasible;
    private double route_fundamental_fitness;
    private double route_punishment_fitness;
    private double route_penalty_fitness;
    private double penalty_clear;
    private double penalty_potential;
    private double penalty_delay;

    public DeliveryRoute() {
        this.head = null;
        this.nodeNum = 0;
        this.fitness=0;
        this.isFeasible=true;
        this.route_fundamental_fitness=0;
        this.route_punishment_fitness=0;
        this.route_penalty_fitness=0;
        this.penalty_clear=0;
        this.penalty_potential=0;
        this.penalty_delay=0;
    }

    public void addDeliveryNode(DeliveryNode node) {
        if (head == null) {
            head = node;
        } else {
            DeliveryNode current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(node);
        }
    }

    public DeliveryNode getLastNode() {
        if (head == null) {
            return null;
        } else {
            DeliveryNode current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            return current;
        }
    }

    public DeliveryNode getNode(int index) {
        int num=0;
        if (head == null) {
            return null;
        } else {
            DeliveryNode current = head;
            while (current!= null) {
                if(num==index) return current;
                current = current.getNext();
                num++;
            }
            return null;
        }
    }
    public DeliveryNode getPreNode(DeliveryNode node){
        if(node==this.head){
            return null;
        }else{
            DeliveryNode preNode=this.head;
            while(preNode.getNext()!=node){
                preNode=preNode.getNext();
            }
            return preNode;
        }
    }
    public void printNode() {
        DeliveryNode current = head;
        while (current!= null) {
            System.out.print(current.getComedy().getIndex()+" ");
            current = current.getNext();
        }
    }

    public DeliveryNode getHead() {
        return head;
    }
    public void setHead(DeliveryNode node) {
        this.head=node;
    }

    public void setFitness(double fund_fit,double punish_fit,double penal_fit,double clear,double potential,double delay){
        this.route_fundamental_fitness=fund_fit;
        this.route_punishment_fitness=punish_fit;
        this.route_penalty_fitness=penal_fit;
        this.penalty_clear=clear;
        this.penalty_potential=potential;
        this.penalty_delay=delay;
        this.fitness=fund_fit+punish_fit+penal_fit;
    }
    public double getFundFit(){return this.route_fundamental_fitness;}
    public double getPunishFit(){return this.route_punishment_fitness;}
    public double getPenalFit(){return this.route_penalty_fitness;}
    public double getClearFit(){return this.penalty_clear;}
    public double getPotentialFit(){return this.penalty_potential;}
    public double getDelayFit(){return this.penalty_delay;}
    public double getFit(){return this.fitness;}

    public void setNodeNum() {
        int nodeNum=0;
        DeliveryNode current = head;
        while (current!= null) {
            current=current.getNext();
            nodeNum++;
        }
        this.nodeNum = nodeNum;
    }
    public int getNodeNum() {
        return nodeNum;
    }
//    public void setNodeInfo(Parameters params,Customer[] customers){
//        DeliveryNode lastNode=head;
//        if(head!=null) {
//            head.cumulativeArrivalTime=params.dist[AlgorithmParameters.nbCommunity][head.getComedy().getIndex()];
//            if(customers[head.getComedy().getIndex()].getStartTW()-head.cumulativeArrivalTime>AlgorithmParameters.eps){
//                head.cumulativeArrivalTime=customers[head.getComedy().getIndex()].getStartTW();
//            }else if(head.cumulativeArrivalTime-customers[head.getComedy().getIndex()].getEndTW()>AlgorithmParameters.eps){
//                head.cumulativeArrivalTime=customers[head.getComedy().getIndex()].getEndTW();
//            }
//            DeliveryNode current = head.getNext();
//            while(current!=null){
//                current.setCumulativeArrivalTime(lastNode.getCumulativeArrivalTime(),lastNode.getComedy(),params,customers);
//                current.setCumulativeDemand(lastNode.getCumulativeDemand());
//                lastNode=current;
//                current=current.getNext();
//            }
//        }
//    }
    public void setfitness(double fitness){
        this.fitness=fitness;
    }
    public void setIsFeasible(boolean isFeasible){
        this.isFeasible=isFeasible;
    }
    public int getNodePosition(DeliveryNode targetNode) {
        int position = 0;
        DeliveryNode current = head;
        while (current != null) {
            if (current == targetNode) {
                return position;
            }
            current = current.getNext();
            position++;
        }
        return -1; // 如果未找到节点，则返回-1。
    }
       public DeliveryRoute
       Clone() {
        DeliveryRoute clone = new DeliveryRoute();

        // 克隆链表结构
        if (this.head != null) {
            clone.head = this.head.Clone(); // 假设DeliveryNode有一个deepClone方法
            DeliveryNode currentOriginal = this.head;
            DeliveryNode currentClone = clone.head;

            while (currentOriginal.getNext() != null) {
                currentClone.setNext(currentOriginal.getNext().Clone());
                currentOriginal = currentOriginal.getNext();
                currentClone = currentClone.getNext();
            }
        }

        // 复制其他基本类型和不可变类型的成员变量
        clone.nodeNum = this.nodeNum;
        clone.fitness = this.fitness;
        clone.isFeasible = this.isFeasible;
        clone.route_fundamental_fitness = this.route_fundamental_fitness;
        clone.route_punishment_fitness = this.route_punishment_fitness;
        clone.route_penalty_fitness = this.route_penalty_fitness;
        clone.penalty_clear = this.penalty_clear;
        clone.penalty_potential = this.penalty_potential;
        clone.penalty_delay = this.penalty_delay;

        return clone;
    }
}

