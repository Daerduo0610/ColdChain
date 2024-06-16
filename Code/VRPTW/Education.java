package VRPTW;

import VRPTW.Individual;
import VRPTW.Node;
import VRPTW.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Education {
    public static void education(Individual individual,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        M1(individual,params,customers);
        M2(individual,params,customers);
        M3(individual,params,customers);
        M4(individual,params,customers);
        M5(individual,params,customers);
        M6(individual,params,customers);
        M7(individual,params,customers);
        M8(individual,params,customers);
        M9(individual,params,customers);
    }
    public static void M1(Individual individualfirst,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for (int i = 0; i <individualfirst.numRoutes; i++)
        {
            int flag=0;
            for (int j = 0; j < individualfirst.getRoute(i).getNodeNum(); j++)
            {
                for (int k = 0; k < individualfirst.numRoutes; k++)
                {
                    for (int l = 0; l <individualfirst.getRoute(k).getNodeNum(); l++)
                    {
                        if(i!=k||j!=l)
                        {
                            Individual individual=individualfirst.deepClone();
                            u_to_vback(individual,i,j,k,l);
                            Initiation.RouteSetOrder(individual, params);
                            if(BalanceFitness.fitness(individual,params,customers)<BalanceFitness.fitness(individualfirst,params,customers))
                            {//如果调整后方案更优，则执行操作
                                u_to_vback(individualfirst,i,j,k,l);
                                Initiation.RouteSetOrder(individualfirst, params);
                                flag=1;
                                //System.out.println(Arrays.toString(individual.customerOrder));
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }
                if(flag==1)
                {
                    i=-1;
                    break;
                }
            }
        }
    }
    public static void M2(Individual individualfirst,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for (int i = 0; i <individualfirst.numRoutes; i++)
        {
            int flag=0;
            for (int j = 0; j < individualfirst.getRoute(i).getNodeNum()-1; j++)
            {
                for (int k = 0; k < individualfirst.numRoutes; k++)
                {
                    for (int l = 0; l <individualfirst.getRoute(k).getNodeNum(); l++)
                    {
                        if(i!=k||(j!=l&&j+1!=l))
                        {
                            Individual individual=individualfirst.deepClone();
                            ux_to_vback(individual,i,j,k,l);
                            Initiation.RouteSetOrder(individual, params);
                            if(BalanceFitness.fitness(individual,params,customers)<BalanceFitness.fitness(individualfirst,params,customers))
                            {//如果调整后方案更优，则执行操作
                                ux_to_vback(individualfirst,i,j,k,l);
                                Initiation.RouteSetOrder(individualfirst, params);
                                flag=1;
                                //System.out.println(Arrays.toString(individual.customerOrder));
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }
                if(flag==1)
                {
                    i=-1;
                    break;
                }
            }
        }
    }
    public static void M3(Individual individualfirst,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for (int i = 0; i <individualfirst.numRoutes; i++)
        {
            int flag=0;
            for (int j = 0; j < individualfirst.getRoute(i).getNodeNum()-1; j++)
            {
                for (int k = 0; k < individualfirst.numRoutes; k++)
                {
                    for (int l = 0; l <individualfirst.getRoute(k).getNodeNum(); l++)
                    {
                        if(i!=k||(j!=l&&j+1!=l))
                        {
                            Individual individual=individualfirst.deepClone();
                            u_to_vback(individual,i,j,i,j+1);//现将xu互换位置
                            ux_to_vback(individual,i,j,k,l);
                            Initiation.RouteSetOrder(individual, params);
                            if(BalanceFitness.fitness(individual,params,customers)<BalanceFitness.fitness(individualfirst,params,customers))
                            {//如果调整后方案更优，则执行操作
                                u_to_vback(individualfirst,i,j,i,j+1);
                                ux_to_vback(individualfirst,i,j,k,l);
                                Initiation.RouteSetOrder(individualfirst, params);
                                flag=1;
                                //System.out.println(Arrays.toString(individual.customerOrder));
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }
                if(flag==1)
                {
                    i=-1;
                    break;
                }
            }
        }
    }


    public static void M4(Individual individualfirst,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for (int i = 0; i <individualfirst.numRoutes; i++)
        {
            int flag=0;
            for (int j = 0; j < individualfirst.getRoute(i).getNodeNum(); j++)
            {
                for (int k = 0; k < individualfirst.numRoutes; k++)
                {
                    for (int l = 0; l <individualfirst.getRoute(k).getNodeNum(); l++)
                    {
                        if(i!=k||j!=l)
                        {
                            Individual individual=individualfirst.deepClone();
                            swap(individual,i,j,k,l);
                            Initiation.RouteSetOrder(individual, params);
                            if(BalanceFitness.fitness(individual,params,customers)<BalanceFitness.fitness(individualfirst,params,customers))
                            {//如果调整后方案更优，则执行操作
                                swap(individualfirst,i,j,k,l);
                                Initiation.RouteSetOrder(individualfirst, params);
                                flag=1;
                                //System.out.println(Arrays.toString(individual.customerOrder));
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }
                if(flag==1)
                {
                    i=-1;
                    break;
                }
            }
        }
    }

    public static void M5(Individual individualfirst,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for (int i = 0; i <individualfirst.numRoutes; i++)
        {
            int flag=0;
            for (int j = 0; j < individualfirst.getRoute(i).getNodeNum()-1; j++)
            {
                for (int k = 0; k < individualfirst.numRoutes; k++)
                {
                    for (int l = 0; l <individualfirst.getRoute(k).getNodeNum(); l++)
                    {
                        if(i!=k||(j!=l&&j+1!=l))
                        {
                            Individual individual=individualfirst.deepClone();
                            ux_swap_v(individual,i,j,k,l);
                            Initiation.RouteSetOrder(individual, params);
                            if(BalanceFitness.fitness(individual,params,customers)<BalanceFitness.fitness(individualfirst,params,customers))
                            {//如果调整后方案更优，则执行操作
                                ux_swap_v(individualfirst,i,j,k,l);
                                Initiation.RouteSetOrder(individualfirst, params);
                                flag=1;
                                //System.out.println(Arrays.toString(individual.customerOrder));
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }
                if(flag==1)
                {
                    i=-1;
                    break;
                }
            }
        }
    }
    public static void M6(Individual individualfirst,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for (int i = 0; i <individualfirst.numRoutes; i++)
        {
            int flag=0;
            for (int j = 0; j < individualfirst.getRoute(i).getNodeNum()-1; j++)
            {
                for (int k = 0; k < individualfirst.numRoutes; k++)
                {
                    for (int l = 0; l <individualfirst.getRoute(k).getNodeNum()-1; l++)
                    {
                        //System.out.println("hello");
                        if(i!=k||(j!=l-1&j!=l&&j!=l+1))
                        {
                            Individual individual=individualfirst.deepClone();
                            //System.out.println('1');
                            ux_swap_vy(individual,i,j,k,l);
                            //System.out.println('2');
                            //System.out.println(Arrays.toString(individual.customerOrder));
                            Initiation.RouteSetOrder(individual, params);
                            //System.out.println('3');
                            //System.out.println(Arrays.toString(individual.customerOrder));
                            if(BalanceFitness.fitness(individual,params,customers)<BalanceFitness.fitness(individualfirst,params,customers))
                            {//如果调整后方案更优，则执行操作
                                //System.out.println("change");
                                ux_swap_vy(individualfirst,i,j,k,l);
                                Initiation.RouteSetOrder(individualfirst, params);
                                flag=1;

                                //System.out.println(Arrays.toString(individualfirst.customerOrder));
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }
                if(flag==1)
                {
                    i=-1;
                    break;
                }
            }
        }
    }
    public static void M7(Individual initialIndividual,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for(int i=0;i<initialIndividual.numRoutes;i++){
            for(int j=0;j<initialIndividual.getRoute(i).getNodeNum()-1;j++){
                for(int k=0;k<initialIndividual.getRoute(i).getNodeNum()-1;k++){
                    if(j!=k&&j+1!=k){
                        Individual operatedIndividual=initialIndividual.deepClone();
                        swap(operatedIndividual,i,j+1,i,k);
                        Initiation.RouteSetOrder(operatedIndividual,params);
                        if(BalanceFitness.fitness(operatedIndividual,params,customers)<BalanceFitness.fitness(initialIndividual,params,customers)){
                            swap(initialIndividual,i,j+1,i,k);
                            Initiation.RouteSetOrder(initialIndividual,params);
                            i=0;
                            j=-1;
                            break;
                        }
                    }
                }
            }
        }
    }
    public static void M8(Individual initialIndividual,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for(int i=0;i<initialIndividual.numRoutes;i++){
            int flag=0;
            for(int j=0;j<initialIndividual.getRoute(i).getNodeNum()-1;j++){
                for(int k=0;k<initialIndividual.numRoutes;k++){
                    for(int l=0;l<initialIndividual.getRoute(k).getNodeNum()-1;l++){
                        if (i != k) {
                            Individual operatedIndividual=initialIndividual.deepClone();
                            swap(operatedIndividual,i,j+1,k,l);
                            Initiation.RouteSetOrder(operatedIndividual,params);
                            if(BalanceFitness.fitness(operatedIndividual,params,customers)<BalanceFitness.fitness(initialIndividual,params,customers)){
                                swap(initialIndividual,i,j+1,k,l);
                                Initiation.RouteSetOrder(initialIndividual,params);
                                flag=1;
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }
                if(flag==1){
                    i=-1;
                    break;
                }
            }
        }
    }
    public static void M9(Individual initialIndividual,Parameters params,Customer[] customers) throws IOException, ClassNotFoundException {
        for(int i=0;i<initialIndividual.numRoutes;i++){
            int flag=0;
            for(int j=0;j<initialIndividual.getRoute(i).getNodeNum()-1;j++){
                for(int k=0;k<initialIndividual.numRoutes;k++){
                    for(int l=0;l<initialIndividual.getRoute(k).getNodeNum()-1;l++){
                        if (i != k) {
                            Individual operatedIndividual=initialIndividual.deepClone();
                            swap(operatedIndividual,i,j+1,k,l+1);
                            swap(operatedIndividual,k,l,k,l+1);
                            Initiation.RouteSetOrder(operatedIndividual,params);
                            if(BalanceFitness.fitness(operatedIndividual,params,customers)<BalanceFitness.fitness(initialIndividual,params,customers)){
                                swap(initialIndividual,i,j+1,k,l+1);
                                swap(initialIndividual,k,l,k,l+1);
                                Initiation.RouteSetOrder(initialIndividual,params);
                                flag=1;
                                break;
                            }
                        }
                    }
                    if(flag==1) break;
                }if(flag==1){
                    i=-1;
                    break;
                }
            }
        }
    }
    public static void u_to_vback(Individual individual,int u_route_index,int u_index,int v_route_index,int v_index){//将u移到v后
        Route u_route=individual.getRoute(u_route_index);
        Node u=u_route.getNode(u_index);
        Route v_route=individual.getRoute(v_route_index);
        Node v=v_route.getNode(v_index);
        //将u从原路径断出
        if(u_index!=0)
        {
            u_route.getNode(u_index-1).setNext(u.getNext());//若u不是第一个节点
        }else
        {
            if(u.getNext()==null) individual.routes.remove(u_route_index);//若u是第一个节点且只有一个节点，说明该路径在操作后不存在
            else u_route.setHead(u.getNext());//若u是第一个节点，直接将其下一个节点设为头节点
        }
        u.setNext(v.getNext());
        v.setNext(u);
    }

    public static void ux_to_vback(Individual individual,int u_route_index,int u_index,int v_route_index,int v_index){//将u移到v后
        Route u_route=individual.getRoute(u_route_index);
        Node u=u_route.getNode(u_index);
        Node x=u.getNext();
        Route v_route=individual.getRoute(v_route_index);
        Node v=v_route.getNode(v_index);
        //将ux从原路径断出
        if(u_index!=0)
        {
            u_route.getNode(u_index-1).setNext(x.getNext());//若u不是第一个节点
        }else
        {
            if(x.getNext()==null) individual.routes.remove(u_route_index);//若u是第一个节点且只有一个节点，说明该路径在操作后不存在
            else u_route.setHead(x.getNext());//若u是第一个节点，直接将其下一个节点设为头节点
        }
        x.setNext(v.getNext());
        v.setNext(u);
    }

    public static void swap(Individual individual,int u_route_index,int u_index,int v_route_index,int v_index){//将u移到v后
        Route u_route=individual.getRoute(u_route_index);
        Node u=u_route.getNode(u_index);
        Node x=u.getNext();
        Route v_route=individual.getRoute(v_route_index);
        Node v=v_route.getNode(v_index);
        Node y=v.getNext();//分别随机选取两条路径中的两个点
        Node u_pre=null;//u的前一个节点
        Node v_pre=null;//v的前一个节点
        if(u_index!=0){
            u_pre=u_route.getNode(u_index-1);
        }
        if(v_index!=0){
            v_pre=v_route.getNode(v_index-1);
        }
        if(v==x){
            u_to_vback(individual,u_route_index,u_index,v_route_index,v_index);
        } else if (u==y) {
            u_to_vback(individual,v_route_index,v_index,u_route_index,u_index);
        }else if(u!=v){
            if(u_index!=0){
                u_pre.setNext(v);
            }else{
                u_route.setHead(v);
            }
            v.setNext(x);
            if(v_index!=0){
                v_pre.setNext(u);
            }else{
                v_route.setHead(u);
            }
            u.setNext(y);
        }
    }

    public static void ux_swap_v(Individual individual,int u_route_index,int u_index,int v_route_index,int v_index){//将u移到v后
        Route u_route=individual.getRoute(u_route_index);
        Node u=u_route.getNode(u_index);
        Node x=u.getNext();
        Route v_route=individual.getRoute(v_route_index);
        Node v=v_route.getNode(v_index);
        Node y=v.getNext();//分别随机选取两条路径中的两个点
        Node u_pre=null;//u的前一个节点
        Node v_pre=null;//v的前一个节点
        if(u_index!=0){
            u_pre=u_route.getNode(u_index-1);
        }
        if(v_index!=0){
            v_pre=v_route.getNode(v_index-1);
        }
        if(v==x.getNext()){
            ux_to_vback(individual,u_route_index,u_index,v_route_index,v_index);
        } else if (u==y) {
            u_to_vback(individual,v_route_index,v_index,u_route_index,u_index+1);
        }else if(u!=v){
            if(u_index!=0){
                u_pre.setNext(v);
            }else{
                u_route.setHead(v);
            }
            v.setNext(x.getNext());
            if(v_index!=0){
                v_pre.setNext(u);
            }else{
                v_route.setHead(u);
            }
            x.setNext(y);
        }
    }

    public static void ux_swap_vy(Individual individual,int u_route_index,int u_index,int v_route_index,int v_index){//将u移到v后
        Route u_route=individual.getRoute(u_route_index);
        Node u=u_route.getNode(u_index);
        Node x=u.getNext();
        Route v_route=individual.getRoute(v_route_index);
        Node v=v_route.getNode(v_index);
        Node y=v.getNext();//分别随机选取两条路径中的两个点
        Node u_pre=null;//u的前一个节点
        Node v_pre=null;//v的前一个节点
        if(u_index!=0){
            u_pre=u_route.getNode(u_index-1);
        }
        if(v_index!=0){
            v_pre=v_route.getNode(v_index-1);
        }
        if(v==x.getNext()){
            ux_to_vback(individual,u_route_index,u_index,v_route_index,v_index+1);
        } else if (u==y.getNext()) {
            ux_to_vback(individual,v_route_index,v_index,u_route_index,u_index+1);
        }else{
            if(u_index!=0){
                u_pre.setNext(v);
            }else{
                u_route.setHead(v);
            }
            Node tempt=y.getNext();
            y.setNext(x.getNext());
            if(v_index!=0){
                v_pre.setNext(u);
            }else{
                v_route.setHead(u);
            }
            x.setNext(tempt);
        }
    }
}
