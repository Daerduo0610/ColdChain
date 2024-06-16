package VRPTWMTMC;


import java.util.List;
import java.util.stream.Collectors;

public class Main {
        public static void main(String[] args) {
            // 假设closedistance是你的链表
            List<Integer> closedistance = List.of(0, 0,0,0,0,1);

            // 使用Stream API进行处理
            double average = closedistance.stream()
                    .sorted()
                    .limit(5)  // 取前五个最小的数
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);  // 如果链表为空，则返回0.0

            System.out.println("前五个最小数的平均值：" + average);
        }
    }

