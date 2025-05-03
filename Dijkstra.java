import java.util.*;

public class Dijkstra {
    static class Node {
        int id, distance;
        Node(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }
    }

    public static int findNearestProduct(String targetName, List<Product> products, int[][] graph, int startId) {
        int n = products.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[startId] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.distance));
        pq.offer(new Node(startId, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            for (int i = 0; i < n; i++) {
                if (graph[current.id][i] > 0 && dist[i] > dist[current.id] + graph[current.id][i]) {
                    dist[i] = dist[current.id] + graph[current.id][i];
                    pq.offer(new Node(i, dist[i]));
                }
            }
        }

        int minDist = Integer.MAX_VALUE;
        int nearestId = -1;
        for (int i = 0; i < n; i++) {
            if (products.get(i).name.equalsIgnoreCase(targetName) && i != startId && dist[i] < minDist) {
                minDist = dist[i];
                nearestId = i;
            }
        }

        return nearestId;
    }
}
