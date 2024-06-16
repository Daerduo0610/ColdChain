package VRPTW;
import java.io.Serializable;

public class Node implements Serializable {
    private Customer customer;
    public double cumulativeArrivalTime;
    public double cumulativeDemand;
    private Node next;

    public Node(Customer customer) {
        this.customer = customer;
        this.cumulativeArrivalTime = 0;
        this.cumulativeDemand = 0;
        this.next = null;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getCumulativeArrivalTime() {
        return cumulativeArrivalTime;
    }

    public double getCumulativeDemand() {
        return cumulativeDemand;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setCumulativeArrivalTime(double previousCumulativeArrivalTime,Customer previousCustomer,Parameters params) {
        this.cumulativeArrivalTime = previousCumulativeArrivalTime + previousCustomer.getServiceTime() + params.getDist()[previousCustomer.getIndex()][this.customer.getIndex()];
    }

    public void setCumulativeDemand(double previousCumulativeDemand) {
        this.cumulativeDemand = previousCumulativeDemand + this.customer.getDemand();
    }
}
