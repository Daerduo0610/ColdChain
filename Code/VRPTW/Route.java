package VRPTW;
import java.io.Serializable;

public class Route implements Serializable {

    private Node head;
    private int nodeNum;

    public Route() {
        this.head = null;
        this.nodeNum = 0;
    }

    public void addNode(Node node) {
        if (head == null) {
            head = node;
        } else {
            Node current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(node);
        }
    }

    public Node getLastNode() {
        if (head == null) {
            return null;
        } else {
            Node current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            return current;
        }
    }

    public Node getNode(int index) {
        int num=0;
        if (head == null) {
            return null;
        } else {
            Node current = head;
            while (current!= null) {
                if(num==index) return current;
                current = current.getNext();
                num++;
            }
            return null;
        }
    }
    public void printNode() {
        Node current = head;
        while (current!= null) {
            System.out.print(current.getCustomer().getIndex()+" ");
            current = current.getNext();
        }
    }

    public Node getHead() {
        return head;
    }
    public void setHead(Node node) {
        this.head=node;
    }

    public void setNodeNum() {
        int nodeNum=0;
        Node current = head;
        while (current!= null) {
            current=current.getNext();
            nodeNum++;
        }
        this.nodeNum = nodeNum;
    }
    public int getNodeNum() {
        return nodeNum;
    }
    public void setNodeInfo(Parameters params){
        Node lastNode=head;
        if(head!=null) {
            Node current = head.getNext();
            while(current!=null){
                current.setCumulativeArrivalTime(lastNode.getCumulativeArrivalTime(),lastNode.getCustomer(),params);
                current.setCumulativeDemand(lastNode.getCumulativeDemand());
                lastNode=current;
                current=current.getNext();
            }
        }
    }
}