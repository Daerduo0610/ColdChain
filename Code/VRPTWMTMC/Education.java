package VRPTWMTMC;
import java.util.*;

import java.io.IOException;

public class Education {
    public static void  education(Individual individual, Parameters params, Customer[] customers,int M10_flag) throws IOException, ClassNotFoundException {
        double originalFit=0;
//        if (M10_flag>0&&M10_flag%10==0){
//            System.out.println("before M10 fit="+individual.fitness+" route num="+individual.numRoutes);
            originalFit=individual.fitness;
//            M10(individual,params,customers);
//            System.out.println("after M10 fit="+individual.fitness);
//            System.out.println("use M10="+M10_flag);
//        }
//        System.out.println("begin M1");
        M1(individual,params,customers);
//        System.out.println("begin M2");
        M2(individual,params,customers);
//        System.out.println("begin M3");
        M3(individual,params,customers);
//        System.out.println("begin M4");
        M4(individual,params,customers);
//        System.out.println("begin M5");
        M5(individual,params,customers);
//        System.out.println("begin M6");
        M6(individual,params,customers);
//        System.out.println("begin M7");
        M7(individual,params,customers);
//        System.out.println("begin M8");
        M8(individual,params,customers);
//        System.out.println("begin M9");
        M9(individual,params,customers);
//        M0(individual,params,customers);
//        if(flag==1&&originalFit-individual.fitness>AlgorithmParameters.eps){
//            System.out.println("work!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println("opriginalFit="+originalFit);
//            System.out.println("now fit="+individual.fitness);
//        }
    }
    public static List<Integer> getUniqueRandomIntegers(int min, int max, int count) {
            Set<Integer> set = new HashSet<>();
            while (set.size() < count) {
                set.add((int)(Math.random() * (max-min)) + min);
            }
            return new ArrayList<>(set);
    }
    public static int findLeastFrequentComedyType(DeliveryRoute route) {
        Map<Integer, Integer> comedyCount = new HashMap<>();
        DeliveryNode current = route.getHead();

        // 遍历链表，统计每种类型的出现次数
        while (current != null) {
            int comedyType = current.getComedy().getType();
            comedyCount.put(comedyType, comedyCount.getOrDefault(comedyType, 0) + 1);
            current = current.getNext();
        }
        //如果只有一种，返回-1
        if (comedyCount.size() == 1) {
            return -1;
        }
        // 找出最少出现的类型
        int leastFrequentType = -1;
        int minCount = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : comedyCount.entrySet()) {
            if (entry.getValue() < minCount) {
                minCount = entry.getValue();
                leastFrequentType = entry.getKey();
            }
        }

        return leastFrequentType; // 直接返回出现最少的类型的整数标识
    }

    public static DeliveryRoute removeLeastComedy(DeliveryRoute originalRoute) {
        int leastFrequentType = findLeastFrequentComedyType(originalRoute);
        if (leastFrequentType == -1) {
            return null; // 如果只有一种类型，返回空的DeliveryRoute
        }

        DeliveryRoute removedNodesRoute = new DeliveryRoute(); // 存储被移除节点的新路线
        DeliveryNode current = originalRoute.getHead();
        DeliveryNode prev = null;
        DeliveryNode lastRemovedNode = null; // 记录最后一个被添加到removedNodesRoute的节点

        while (current != null) {
            if (current.getComedy().getType() == leastFrequentType) {
                DeliveryNode toBeRemoved = current.Clone(); // 假设存在克隆方法
                if (removedNodesRoute.getHead() == null) {
                    removedNodesRoute.setHead(toBeRemoved);
                } else {
                    assert lastRemovedNode != null;
                    lastRemovedNode.setNext(toBeRemoved);
                }
                lastRemovedNode = toBeRemoved;

                // 从原始路径中移除节点
                if (prev == null) {
                    originalRoute.setHead(current.getNext());
                } else {
                    prev.setNext(current.getNext());
                }
            } else {
                prev = current; // 只有当我们不移除节点时才更新prev
            }
            current = current.getNext(); // 移动到下一个节点
        }
        removedNodesRoute.setNodeNum();
        return removedNodesRoute;
    }
    public static void M0(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
        for (int i = 0; i <individualfirst.numRoutes; i++) {
            int flag = 0;
            for (int j = 0; j < individualfirst.getRoute(i).getNodeNum(); j++) {
                DeliveryRoute u_route=individualfirst.getRoute(i).Clone();
                DeliveryNode u=u_route.getNode(j);
                double u_fitness=u_route.getFit();
//                System.out.println("before node num="+u_route.getNodeNum());
                //将u从原路径断出
                if(j!=0)
                {
                    u_route.getNode(j-1).setNext(u.getNext());//若u不是第一个节点
                }else
                {
                    if(u.getNext()!=null) u_route.setHead(u.getNext());
                }
                DeliveryRoute v_route=new DeliveryRoute();
                u.setNext(null);
                v_route.addDeliveryNode(u);
                u_route.setNodeNum();
                v_route.setNodeNum();
                BalanceFitness.route_fitness_computation(u_route,params,customers);
                BalanceFitness.route_fitness_computation(v_route,params,customers);
//                System.out.println("u="+u_fitness);
//                System.out.println("u="+u_route.getFit()+" v="+v_route.getFit()+" u num="+u_route.getNodeNum()+" v num="+v_route.getNodeNum());
                if(u_fitness-(v_route.getFit()+u_route.getFit())>AlgorithmParameters.eps){
                    //执行操作
                    System.out.println("------------------------");
                    System.out.println("before="+u_fitness);
                    System.out.println("after u="+u_route.getFit()+" v="+v_route.getFit());
                    individualfirst.fitness-=u_fitness-(v_route.getFit()+u_route.getFit());
                    individualfirst.DeliveryRoutes.set(i,u_route);
                    individualfirst.addRoute(v_route);
                    individualfirst.numRoutes++;
                    flag=1;
                    break;
                }
            }
            if(flag==1) break;
        }
    }

    public static void M10(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
        //获得20%的要变化的路径
//        String fileName = "D:/BUAA TRAFFIC CODE/ColdChain/src/VRPTWMTMC/updated_R201.csv";
//        InputAndOutput inputAndOutput=new InputAndOutput(fileName);
//        inputAndOutput.RouteOutput(individualfirst,params,customers);
        long Time0=System.currentTimeMillis();
        List<Integer> randomRoutes = getUniqueRandomIntegers(0, individualfirst.numRoutes, (int)Math.ceil(individualfirst.numRoutes * 0.2));
        System.out.println("randomRoutes"+randomRoutes);
        for (int route_index : randomRoutes) {
            DeliveryRoute new_route = removeLeastComedy(individualfirst.getRoute(route_index));//对每条路径进行操作，删除comedy最少的点，并返回一条新的路径（新路径中只有链表信息和节点数，其他均未初始化，方案此时并未变动）
            BalanceFitness.replace_route_fitness(individualfirst, route_index, -1, params, customers);//更新原路径和方案的fitness
            if (new_route != null) {//因为原路径可能未变动（比如只有一种商品,则返回null)
                individualfirst.addRoute(new_route);
                individualfirst.setNumRoutes(individualfirst.getNumRoutes() + 1);
                BalanceFitness.replace_route_fitness(individualfirst, individualfirst.getRoutesSize() - 1, -1, params, customers);//计算新路径的fitness并更新方案fitness
            }
        }
        long Time1=System.currentTimeMillis();
//        inputAndOutput.RouteOutput(individualfirst,params,customers);

    }
    public static void M1(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
        int count=0;
        int total_count=0;
        double clone_time=0;
        double utovback_time=0;
        double gotobetter_time=0;
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
                            total_count++;
                            long Time0=System.currentTimeMillis();
                            DeliveryRoute u_route=individualfirst.getRoute(i).Clone();
                            DeliveryRoute v_route=u_route;
                            if(i!=k) v_route=individualfirst.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();

                            long Time1=System.currentTimeMillis();
                            clone_time+=(Time1-Time0);

//                            Individual individual=individualfirst.Clone();
//                            u_to_vback(individualfirst,i,j,k,l);
                            long Time2=System.currentTimeMillis();
//                            for(int h=0;h<u_route.getNodeNum();h++){
////                char type= (char) ('A'+route.getNode(h).getComedy().getType());
////                System.out.print((route.getNode(h).getComedy().getIndex()+1)+type+"("+route.getNode(h).getCumulativeArrivalTime()+")"+" ");
//                                if(u_route.getNode(h).getComedy().getType()==0){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"A"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }else if(u_route.getNode(h).getComedy().getType()==1){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"B"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }else if(u_route.getNode(h).getComedy().getType()==2){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"C"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }else if(u_route.getNode(h).getComedy().getType()==3){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"D"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }
//                            }
//                            System.out.println("u_route");
//                            for(DeliveryNode node=u_route.getHead();node!=null;node=node.getNext()){
//                                System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                            }
//                            System.out.println("v_route");
//                            for(DeliveryNode node=v_route.getHead();node!=null;node=node.getNext()){
//                                System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                            }
                            u_to_vback(u_route,j,v_route,l);
//                            System.out.println("after u_route");
//                            for(DeliveryNode node=u_route.getHead();node!=null;node=node.getNext()){
//                                System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                            }
//                            System.out.println("after v_route");
//                            for(DeliveryNode node=v_route.getHead();node!=null;node=node.getNext()){
//                                System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                            }
//                            for(int h=0;h<u_route.getNodeNum();h++){
////                char type= (char) ('A'+route.getNode(h).getComedy().getType());
////                System.out.print((route.getNode(h).getComedy().getIndex()+1)+type+"("+route.getNode(h).getCumulativeArrivalTime()+")"+" ");
//                                if(u_route.getNode(h).getComedy().getType()==0){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"A"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }else if(u_route.getNode(h).getComedy().getType()==1){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"B"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }else if(u_route.getNode(h).getComedy().getType()==2){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"C"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }else if(u_route.getNode(h).getComedy().getType()==3){
//                                    System.out.print((u_route.getNode(h).getComedy().getIndex()+1)+"D"+"("+String.format("%.1f",u_route.getNode(h).getCumTime())+","+String.format("%.1f",u_route.getNode(h).time)+")"+" ");
//                                }
//                            }
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();
                            long Time3=System.currentTimeMillis();
                            utovback_time+=(Time3-Time2);

//                            if(i!=k){
//                                BalanceFitness.replace_route_fitness(individual,k,l,params,customers);
//                                BalanceFitness.replace_route_fitness(individual,i,j-1,params,customers); //这两个顺序不能颠倒，因为后者可能会有remove
//
//                            }else{
//                                BalanceFitness.replace_route_fitness(individual,i,Math.min(j-1,l-1),params,customers);
//                            }
//                            long Time3=System.currentTimeMillis();
//
////                            Conversion.RouteSetOrder(individual, params,customers);
////                            individual.fitness=BalanceFitness.fitness(individual,params,customers);

                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps)
                            {//如果调整后方案更优，则执行操作

//                                System.out.println("routefitness="+routes_fitness);
//                                System.out.println("updated_fitness="+updated_fitness);
//                                System.out.println("up fund="+individualfirst.getRoute(i).getFundFit());
//                                if(Math.abs(individualfirst.getRoute(i).getFundFit()-BalanceFitness.fundamental_fitness(individualfirst.getRoute(i),params))>AlgorithmParameters.eps){
//                                    System.out.println("fund!=");
//                                    System.out.println("fundfit="+individualfirst.getRoute(i).getFundFit());
//                                    System.out.println("old fund="+BalanceFitness.fundamental_fitness(individualfirst.getRoute(i),params));
//                                }if(Math.abs(individualfirst.getRoute(i).getPunishFit()-BalanceFitness.punishment_fitness(individualfirst.getRoute(i),params,customers))>AlgorithmParameters.eps){
//                                    System.out.println("punish!=");
//                                    System.out.println("punishfit="+individualfirst.getRoute(i).getPunishFit());
//                                    System.out.println("old pnish="+BalanceFitness.punishment_fitness(individualfirst.getRoute(i),params,customers));
//                                }
//                                if(Math.abs(individualfirst.getRoute(i).getPenalFit()-BalanceFitness.penalty_fitness(individualfirst.getRoute(i),customers))>AlgorithmParameters.eps){
//                                    System.out.println("penalty!=");
//                                    System.out.println("penalfit="+individualfirst.getRoute(i).getPenalFit());
//                                    System.out.println("old penal="+BalanceFitness.penalty_fitness(individualfirst.getRoute(i),customers));
//                                }
//                                System.out.println("up punish="+individualfirst.getRoute(i).getPunishFit());
//                                System.out.println("up penalty="+individualfirst.getRoute(i).getPenalFit());
//                                System.out.println("total_count"+total_count);
//                                System.out.println("route_fitness"+routes_fitness);
//                                System.out.println("updated_fitness"+updated_fitness);
//                                System.out.println(individualfirst.fitness);
                                long Time4=System.currentTimeMillis();
//                                String fileName = "D:/BUAA TRAFFIC CODE/ColdChain/src/VRPTWMTMC/updated_R201.csv";
//                                InputAndOutput inputAndOutput=new InputAndOutput(fileName);
//                                inputAndOutput.RouteOutput(individualfirst,params,customers);

                                u_to_vback(individualfirst,i,j,k,l);

                                if(i!=k){
                                    BalanceFitness.replace_route_fitness(individualfirst,k,l,params,customers);
                                    BalanceFitness.replace_route_fitness(individualfirst,i,j-1,params,customers); //这两个顺序不能颠倒，因为后者可能会有remove
                                }else{
//                                    System.out.println(" before set"+individualfirst.getRoute(i).getLastNode().getTypeSet().size());
//                                    System.out.println("set arrat:"+individualfirst.getRoute(i).getLastNode().getTypeSet().toArray().toString());
                                    BalanceFitness.replace_route_fitness(individualfirst,i,Math.min(j-1,l-1),params,customers);
//                                    System.out.println(" after set"+individualfirst.getRoute(i).getLastNode().getTypeSet().size());
//                                    System.out.println("j-1="+(j-1)+"replace="+individualfirst.getRoute(i).getFit());
//                                    System.out.println("fund="+individualfirst.getRoute(i).getFundFit());
//                                    System.out.println("punish="+individualfirst.getRoute(i).getPunishFit());
//                                    System.out.println("penalty="+individualfirst.getRoute(i).getPenalFit());
                                }
//                                if(after-before>AlgorithmParameters.eps){
//                                    System.out.println("before="+before+"  route fit="+routes_fitness);
//                                    System.out.println("after="+after+"  updated fit="+updated_fitness);
//                                    System.out.println("u_copy_before fit="+copy_u.getFit());
//                                    for(DeliveryNode node=copy_u.getHead();node!=null;node=node.getNext()){
//                                        System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                                    }
//                                    System.out.println();
//                                    System.out.println("v_copy_before fit="+copy_v.getFit());
//                                    System.out.println("v_before set="+copy_v.getLastNode().getTypeSet().size());
//                                    for(DeliveryNode node=copy_v.getHead();node!=null;node=node.getNext()){
//                                        System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                                    }
//
//                                    System.out.println();
//                                    System.out.println("u_copy_after fit="+u_after.getFit());
//                                    for(DeliveryNode node=u_after.getHead();node!=null;node=node.getNext()){
//                                        System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                                    }
//                                    System.out.println();
//                                    System.out.println("v_copy_after fit="+v_after.getFit());
//                                    System.out.println("v_after set="+v_after.getLastNode().getTypeSet().size());
//                                    System.out.println("fund="+v_after.getFundFit());
//                                    System.out.println("punish="+v_after.getPunishFit());
//                                    System.out.println("penalty="+v_after.getPenalFit());
//                                    for(DeliveryNode node=v_after.getHead();node!=null;node=node.getNext()){
//                                        System.out.print(" index="+node.getComedy().getIndex()+" type="+node.getComedy().getType());
//                                    }
//                                }
//                                inputAndOutput.RouteOutput(individualfirst,params,customers);
                                long Time5=System.currentTimeMillis();
//                                u_to_vback(individualfirst,i,j,k,l);
//                                if(i!=k){
//                                    BalanceFitness.replace_route_fitness(individualfirst,k,l,params,customers);
//                                    BalanceFitness.replace_route_fitness(individualfirst,i,j-1,params,customers); //这两个顺序不能颠倒，因为后者可能会有remove
//                                }else{
//                                    BalanceFitness.replace_route_fitness(individualfirst,i,Math.min(j-1,l-1),params,customers);
//                                }
                                gotobetter_time+=Time5-Time4;
                                flag=1;
                                count++;
//                                if((count+1)%100==0) {
//                                    System.out.println("M1 go to better=" + (count+1));
//                                    System.out.println(individualfirst.fitness);
//                                }
//                                System.out.println("i="+i+" j="+j+" k="+k+" l="+l);
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
//        System.out.println("count="+count);
    }
    public static void M2(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
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
//                           Individual individual=individualfirst.Clone();

                            DeliveryRoute u_route=individualfirst.getRoute(i).Clone();
                            DeliveryRoute v_route=u_route;
                            if(i!=k) v_route=individualfirst.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();
                            ux_to_vback(u_route,j,v_route,l);
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();

//                            ux_to_vback(individual,i,j,k,l);
//                            if(i!=k){
//                                BalanceFitness.replace_route_fitness(individual,k,l,params,customers);
//                                BalanceFitness.replace_route_fitness(individual,i,j-1,params,customers);
//                            }else{
//                                BalanceFitness.replace_route_fitness(individual,i,Math.min(j-1,l-1),params,customers);
//                            }
////                            Conversion.RouteSetOrder(individual, params,customers);
////                            individual.fitness=BalanceFitness.fitness(individual,params,customers);
                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps)
                            {//如果调整后方案更优，则执行操作
                                ux_to_vback(individualfirst,i,j,k,l);
//                                Conversion.RouteSetOrder(individualfirst, params,customers);
//                                individualfirst.fitness=individual.fitness;
                                if(i!=k){
                                    BalanceFitness.replace_route_fitness(individualfirst,k,l,params,customers);
                                    BalanceFitness.replace_route_fitness(individualfirst,i,j-1,params,customers);
                                }else{
                                    BalanceFitness.replace_route_fitness(individualfirst,i,Math.min(j-1,l-1),params,customers);
                                }
                                flag=1;
//                                count++;
//                                System.out.println("go to better"+count);
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
    public static void M3(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
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
                            DeliveryRoute u_route=individualfirst.getRoute(i).Clone();
                            DeliveryRoute v_route=u_route;
                            if(i!=k) v_route=individualfirst.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();
                            u_to_vback(u_route,j,u_route,j+1);//现将xu互换位置
                            ux_to_vback(u_route,j,v_route,l);
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();
//                            Individual individual=individualfirst.Clone();
//                            u_to_vback(individual,i,j,i,j+1);//现将xu互换位置
//                            ux_to_vback(individual,i,j,k,l);
//                            if(i!=k){
//                                BalanceFitness.replace_route_fitness(individual,k,l,params,customers);
//                                BalanceFitness.replace_route_fitness(individual,i,j-1,params,customers);
//                            }else{
//                                BalanceFitness.replace_route_fitness(individual,i,Math.min(j-1,l-1),params,customers);
//                            }
//                            Conversion.RouteSetOrder(individual, params,customers);
//                            individual.fitness=BalanceFitness.fitness(individual,params,customers);
                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps)
                            {//如果调整后方案更优，则执行操作
                                u_to_vback(individualfirst,i,j,i,j+1);
                                ux_to_vback(individualfirst,i,j,k,l);
                                if(i!=k){
                                    BalanceFitness.replace_route_fitness(individualfirst,k,l,params,customers);
                                    BalanceFitness.replace_route_fitness(individualfirst,i,j-1,params,customers);
                                }else{
                                    BalanceFitness.replace_route_fitness(individualfirst,i,Math.min(j-1,l-1),params,customers);
                                }
                                flag=1;
//                                count++;
//                                System.out.println("go to better"+count);
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


    public static void M4(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
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
                            DeliveryRoute u_route=individualfirst.getRoute(i).Clone();
                            DeliveryRoute v_route=u_route;
                            if(i!=k) v_route=individualfirst.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();
                            swap(u_route,j,v_route,l);
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();
//                            Individual individual=individualfirst.Clone();
//                            swap(individual,i,j,k,l);
//                            if(i!=k){
//                                BalanceFitness.replace_route_fitness(individual,i,j-1,params,customers);
//                                BalanceFitness.replace_route_fitness(individual,k,l-1,params,customers);
//                            }else{
//                                BalanceFitness.replace_route_fitness(individual,i,Math.min(j-1,l-1),params,customers);
//                            }
//                            Conversion.RouteSetOrder(individual, params,customers);
//                            individual.fitness=BalanceFitness.fitness(individual,params,customers);
                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps)
                            {//如果调整后方案更优，则执行操作
                                swap(individualfirst,i,j,k,l);
                                if(i!=k){
                                    BalanceFitness.replace_route_fitness(individualfirst,i,j-1,params,customers);
                                    BalanceFitness.replace_route_fitness(individualfirst,k,l-1,params,customers);
                                }else{
                                    BalanceFitness.replace_route_fitness(individualfirst,i,Math.min(j-1,l-1),params,customers);
                                }
                                flag=1;
//                                count++;
//                                System.out.println("go to better"+count);
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

    public static void M5(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
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
                            DeliveryRoute u_route=individualfirst.getRoute(i).Clone();
                            DeliveryRoute v_route=u_route;
                            if(i!=k) v_route=individualfirst.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();
                            ux_swap_v(u_route,j,v_route,l);
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();
//                            Individual individual=individualfirst.Clone();
//                            ux_swap_v(individual,i,j,k,l);
//                            if(i!=k){
//                                BalanceFitness.replace_route_fitness(individual,i,j-1,params,customers);
//                                BalanceFitness.replace_route_fitness(individual,k,l-1,params,customers);
//                            }else{
//                                BalanceFitness.replace_route_fitness(individual,i,Math.min(j-1,l-1),params,customers);
//                            }
//                            Conversion.RouteSetOrder(individual, params,customers);
//                            individual.fitness=BalanceFitness.fitness(individual,params,customers);
                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps)
                            {//如果调整后方案更优，则执行操作
                                ux_swap_v(individualfirst,i,j,k,l);
                                if(i!=k){
                                    BalanceFitness.replace_route_fitness(individualfirst,i,j-1,params,customers);
                                    BalanceFitness.replace_route_fitness(individualfirst,k,l-1,params,customers);
                                }else{
                                    BalanceFitness.replace_route_fitness(individualfirst,i,Math.min(j-1,l-1),params,customers);
                                }
                                flag=1;
//                                count++;
//                                System.out.println("go to better"+count);
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
    public static void M6(Individual individualfirst, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
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
                            DeliveryRoute u_route=individualfirst.getRoute(i).Clone();
                            DeliveryRoute v_route=u_route;
                            if(i!=k) v_route=individualfirst.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();
                            ux_swap_vy(u_route,j,v_route,l);
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();
//                            Individual individual=individualfirst.Clone();
//                            //System.out.println('1');
//                            ux_swap_vy(individual,i,j,k,l);
//                            if(i!=k){
//                                BalanceFitness.replace_route_fitness(individual,i,j-1,params,customers);
//                                BalanceFitness.replace_route_fitness(individual,k,l-1,params,customers);
//                            }else{
//                                BalanceFitness.replace_route_fitness(individual,i,Math.min(j-1,l-1),params,customers);
//                            }
                            //System.out.println('2');
                            //System.out.println(Arrays.toString(individual.customerOrder));
//                            Conversion.RouteSetOrder(individual, params,customers);
                            //System.out.println('3');
                            //System.out.println(Arrays.toString(individual.customerOrder));
//                            individual.fitness=BalanceFitness.fitness(individual,params,customers);
                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps)
                            {//如果调整后方案更优，则执行操作
                                ux_swap_vy(individualfirst,i,j,k,l);
                                if(i!=k){
                                    BalanceFitness.replace_route_fitness(individualfirst,i,j-1,params,customers);
                                    BalanceFitness.replace_route_fitness(individualfirst,k,l-1,params,customers);
                                }else{
                                    BalanceFitness.replace_route_fitness(individualfirst,i,Math.min(j-1,l-1),params,customers);
                                }
//                                Conversion.RouteSetOrder(individualfirst, params,customers);
//                                individualfirst.fitness=individual.fitness;
                                flag=1;
//                                count++;
//                                System.out.println("go to better"+count);
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
    public static void M7(Individual initialIndividual, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
        for(int i=0;i<initialIndividual.numRoutes;i++){
            for(int j=0;j<initialIndividual.getRoute(i).getNodeNum()-1;j++){
                for(int k=0;k<initialIndividual.getRoute(i).getNodeNum()-1;k++){
                    if(j!=k&&j+1!=k){
                        DeliveryRoute u_route=initialIndividual.getRoute(i).Clone();
                        double routes_fitness=u_route.getFit();
                        swap(u_route,j+1,u_route,k);
                        BalanceFitness.route_fitness_computation(u_route,params,customers);
                        double updated_fitness=u_route.getFit();

//                        Individual operatedIndividual=initialIndividual.Clone();
//                        swap(operatedIndividual,i,j+1,i,k);
//
//                        BalanceFitness.replace_route_fitness(operatedIndividual,i,Math.min(j,k-1),params,customers);

//                        Conversion.RouteSetOrder(operatedIndividual,params,customers);
//                        operatedIndividual.fitness=BalanceFitness.fitness(operatedIndividual,params,customers);
                        if(routes_fitness-updated_fitness>AlgorithmParameters.eps){

                            swap(initialIndividual,i,j+1,i,k);
                            BalanceFitness.replace_route_fitness(initialIndividual,i,Math.min(j,k-1),params,customers);
//                            Conversion.RouteSetOrder(initialIndividual,params,customers);
//                            initialIndividual.fitness=operatedIndividual.fitness;
                            i=0;
                            j=-1;
//                            count++;
//                            System.out.println("go to better"+count);
                            break;
                        }
                    }
                }
            }
        }
    }
    public static void M8(Individual initialIndividual, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
        for(int i=0;i<initialIndividual.numRoutes;i++){
            int flag=0;
            for(int j=0;j<initialIndividual.getRoute(i).getNodeNum()-1;j++){
                for(int k=0;k<initialIndividual.numRoutes;k++){
                    for(int l=0;l<initialIndividual.getRoute(k).getNodeNum()-1;l++){
                        if (i != k) {
                            DeliveryRoute u_route=initialIndividual.getRoute(i).Clone();
                            DeliveryRoute v_route=initialIndividual.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();
                            swap(u_route,j+1,v_route,l);
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();

//                            Individual operatedIndividual=initialIndividual.Clone();
//                            swap(operatedIndividual,i,j+1,k,l);
//                            BalanceFitness.replace_route_fitness(operatedIndividual,i,j,params,customers);
//                            BalanceFitness.replace_route_fitness(operatedIndividual,k,l-1,params,customers);
//                            Conversion.RouteSetOrder(operatedIndividual,params,customers);
//                            operatedIndividual.fitness=BalanceFitness.fitness(operatedIndividual,params,customers);
                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps){

                                swap(initialIndividual,i,j+1,k,l);
                                BalanceFitness.replace_route_fitness(initialIndividual,i,j,params,customers);
                                BalanceFitness.replace_route_fitness(initialIndividual,k,l-1,params,customers);
//                                Conversion.RouteSetOrder(initialIndividual,params,customers);
//                                initialIndividual.fitness=operatedIndividual.fitness;
                                flag=1;
//                                count++;
//                                System.out.println("go to better"+count);
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
    public static void M9(Individual initialIndividual, Parameters params, Customer[] customers) throws IOException, ClassNotFoundException {
//        int count=0;
        for(int i=0;i<initialIndividual.numRoutes;i++){
            int flag=0;
            for(int j=0;j<initialIndividual.getRoute(i).getNodeNum()-1;j++){
                for(int k=0;k<initialIndividual.numRoutes;k++){
                    for(int l=0;l<initialIndividual.getRoute(k).getNodeNum()-1;l++){
                        if (i != k) {
                            DeliveryRoute u_route=initialIndividual.getRoute(i).Clone();
                            DeliveryRoute v_route=initialIndividual.getRoute(k).Clone();
                            double routes_fitness=u_route.getFit()+v_route.getFit();
                            swap(u_route,j+1,v_route,l+1);
                            swap(v_route,l,v_route,l+1);
                            BalanceFitness.route_fitness_computation(u_route,params,customers);
                            BalanceFitness.route_fitness_computation(v_route,params,customers);
                            double updated_fitness=u_route.getFit()+v_route.getFit();

//                            Individual operatedIndividual=initialIndividual.Clone();
//                            swap(operatedIndividual,i,j+1,k,l+1);
//                            swap(operatedIndividual,k,l,k,l+1);
//                            BalanceFitness.replace_route_fitness(operatedIndividual,i,j,params,customers);
//                            BalanceFitness.replace_route_fitness(operatedIndividual,k,l-1,params,customers);
//                            Conversion.RouteSetOrder(operatedIndividual,params,customers);
//                            operatedIndividual.fitness=BalanceFitness.fitness(operatedIndividual,params,customers);
                            if(routes_fitness-updated_fitness>AlgorithmParameters.eps){
                                swap(initialIndividual,i,j+1,k,l+1);
                                swap(initialIndividual,k,l,k,l+1);
                                BalanceFitness.replace_route_fitness(initialIndividual,i,j,params,customers);
                                BalanceFitness.replace_route_fitness(initialIndividual,k,l-1,params,customers);
//                                Conversion.RouteSetOrder(initialIndividual,params,customers);
//                                initialIndividual.fitness=operatedIndividual.fitness;
                                flag=1;
//                                count++;
//                                System.out.println("go to better"+count);
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
    public static void u_to_vback(Individual individual, int u_route_index, int u_index, int v_route_index, int v_index){//将u移到v后
        DeliveryRoute u_route=individual.getRoute(u_route_index);
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryRoute v_route=individual.getRoute(v_route_index);
        DeliveryNode v=v_route.getNode(v_index);
        if(v==null){
            System.out.println("error");
            System.out.println(individual.DeliveryOrder);
        }
        //将u从原路径断出
        if(u_index!=0)
        {
            u_route.getNode(u_index-1).setNext(u.getNext());//若u不是第一个节点
        }else
        {
//            if(u.getNext()==null) individual.DeliveryRoutes.remove(u_route_index);//若u是第一个节点且只有一个节点，说明该路径在操作后不存在
//            else u_route.setHead(u.getNext());//若u是第一个节点，直接将其下一个节点设为头节点
            if(u.getNext()!=null) u_route.setHead(u.getNext());
            else individual.getRoute(u_route_index).setHead(null);
        }
        u.setNext(v.getNext());
        v.setNext(u);
    }
    public static void u_to_vback(DeliveryRoute u_route,DeliveryRoute v_route,int u_index,int v_index){//将u移到v后
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode v=v_route.getNode(v_index);
        //将u从原路径断出
        if(u_index!=0)
        {
            u_route.getNode(u_index-1).setNext(u.getNext());//若u不是第一个节点
        }else
        {
//            if(u.getNext()==null) individual.DeliveryRoutes.remove(u_route_index);//若u是第一个节点且只有一个节点，说明该路径在操作后不存在
//            else u_route.setHead(u.getNext());//若u是第一个节点，直接将其下一个节点设为头节点
            if(u.getNext()!=null) u_route.setHead(u.getNext());
            else u_route.setHead(null);
        }
        u.setNext(v.getNext());
        v.setNext(u);
    }

    public static void ux_to_vback(Individual individual, int u_route_index, int u_index, int v_route_index, int v_index){//将u移到v后
        DeliveryRoute u_route=individual.getRoute(u_route_index);
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryRoute v_route=individual.getRoute(v_route_index);
        DeliveryNode v=v_route.getNode(v_index);
        //将ux从原路径断出
        if(u_index!=0)
        {
            u_route.getNode(u_index-1).setNext(x.getNext());//若u不是第一个节点
        }else
        {
//            if(x.getNext()==null) individual.DeliveryRoutes.remove(u_route_index);//若u是第一个节点且只有一个节点，说明该路径在操作后不存在
//            else u_route.setHead(x.getNext());//若u是第一个节点，直接将其下一个节点设为头节点
            if(x.getNext()!=null) u_route.setHead(x.getNext());
            else individual.DeliveryRoutes.get(u_route_index).setHead(null);
        }
        x.setNext(v.getNext());
        v.setNext(u);
    }

    public static void swap(Individual individual, int u_route_index, int u_index, int v_route_index, int v_index){//将u移到v后
        DeliveryRoute u_route=individual.getRoute(u_route_index);
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryRoute v_route=individual.getRoute(v_route_index);
        DeliveryNode v=v_route.getNode(v_index);
        DeliveryNode y=v.getNext();//分别随机选取两条路径中的两个点
        DeliveryNode u_pre=null;//u的前一个节点
        DeliveryNode v_pre=null;//v的前一个节点
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

    public static void ux_swap_v(Individual individual, int u_route_index, int u_index, int v_route_index, int v_index){//将u移到v后
        DeliveryRoute u_route=individual.getRoute(u_route_index);
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryRoute v_route=individual.getRoute(v_route_index);
        DeliveryNode v=v_route.getNode(v_index);
        DeliveryNode y=v.getNext();//分别随机选取两条路径中的两个点
        DeliveryNode u_pre=null;//u的前一个节点
        DeliveryNode v_pre=null;//v的前一个节点
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

    public static void ux_swap_vy(Individual individual, int u_route_index, int u_index, int v_route_index, int v_index){//将u移到v后
        DeliveryRoute u_route=individual.getRoute(u_route_index);
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryRoute v_route=individual.getRoute(v_route_index);
        DeliveryNode v=v_route.getNode(v_index);
        DeliveryNode y=v.getNext();//分别随机选取两条路径中的两个点
        DeliveryNode u_pre=null;//u的前一个节点
        DeliveryNode v_pre=null;//v的前一个节点
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
            DeliveryNode tempt=y.getNext();
            y.setNext(x.getNext());
            if(v_index!=0){
                v_pre.setNext(u);
            }else{
                v_route.setHead(u);
            }
            x.setNext(tempt);
        }
    }
    public static void u_to_vback(DeliveryRoute u_route, int u_index, DeliveryRoute v_route, int v_index){//将u移到v后
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode v=v_route.getNode(v_index);
        //将u从原路径断出
        if(u_index!=0)
        {
            u_route.getNode(u_index-1).setNext(u.getNext());//若u不是第一个节点
        }else
        {
//            if(u.getNext()==null) individual.DeliveryRoutes.remove(u_route_index);若u是第一个节点且只有一个节点，说明该路径在操作后不存在
//            else u_route.setHead(u.getNext());若u是第一个节点，直接将其下一个节点设为头节点
            if(u.getNext()!=null) u_route.setHead(u.getNext());
            else u_route.setHead(null);
        }
        u.setNext(v.getNext());
        v.setNext(u);
    }

    public static void ux_to_vback(DeliveryRoute u_route, int u_index, DeliveryRoute v_route, int v_index){//将u移到v后
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryNode v=v_route.getNode(v_index);
        //将ux从原路径断出
        if(u_index!=0)
        {
            u_route.getNode(u_index-1).setNext(x.getNext());//若u不是第一个节点
        }else
        {
//            if(x.getNext()==null) individual.DeliveryRoutes.remove(u_route_index);若u是第一个节点且只有一个节点，说明该路径在操作后不存在
//            else u_route.setHead(x.getNext());//若u是第一个节点，直接将其下一个节点设为头节点
            if(x.getNext()!=null) u_route.setHead(x.getNext());
            else u_route.setHead(null);
        }
        x.setNext(v.getNext());
        v.setNext(u);
    }

    public static void swap(DeliveryRoute u_route, int u_index, DeliveryRoute v_route, int v_index){//将u移到v后
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryNode v=v_route.getNode(v_index);
        DeliveryNode y=v.getNext();//分别随机选取两条路径中的两个点
        DeliveryNode u_pre=null;//u的前一个节点
        DeliveryNode v_pre=null;//v的前一个节点
        if(u_index!=0){
            u_pre=u_route.getNode(u_index-1);
        }
        if(v_index!=0){
            v_pre=v_route.getNode(v_index-1);
        }
        if(v==x){
            u_to_vback(u_route,u_index,v_route,v_index);
        } else if (u==y) {
            u_to_vback(v_route,v_index,u_route,u_index);
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

    public static void ux_swap_v(DeliveryRoute u_route, int u_index, DeliveryRoute v_route, int v_index){//将u移到v后
        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryNode v=v_route.getNode(v_index);
        DeliveryNode y=v.getNext();//分别随机选取两条路径中的两个点
        DeliveryNode u_pre=null;//u的前一个节点
        DeliveryNode v_pre=null;//v的前一个节点
        if(u_index!=0){
            u_pre=u_route.getNode(u_index-1);
        }
        if(v_index!=0){
            v_pre=v_route.getNode(v_index-1);
        }
        if(v==x.getNext()){
            ux_to_vback(u_route,u_index,v_route,v_index);
        } else if (u==y) {
            u_to_vback(v_route,v_index,u_route,u_index+1);
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

    public static void ux_swap_vy(DeliveryRoute u_route, int u_index, DeliveryRoute v_route, int v_index){//将u移到v后

        DeliveryNode u=u_route.getNode(u_index);
        DeliveryNode x=u.getNext();
        DeliveryNode v=v_route.getNode(v_index);
        DeliveryNode y=v.getNext();//分别随机选取两条路径中的两个点
        DeliveryNode u_pre=null;//u的前一个节点
        DeliveryNode v_pre=null;//v的前一个节点
        if(u_index!=0){
            u_pre=u_route.getNode(u_index-1);
        }
        if(v_index!=0){
            v_pre=v_route.getNode(v_index-1);
        }
        if(v==x.getNext()){
            ux_to_vback(u_route,u_index,v_route,v_index+1);
        } else if (u==y.getNext()) {
            ux_to_vback(v_route,v_index,u_route,u_index+1);
        }else{
            if(u_index!=0){
                u_pre.setNext(v);
            }else{
                u_route.setHead(v);
            }
            DeliveryNode tempt=y.getNext();
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
