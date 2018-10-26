import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

final class E12LoadBalancing {

    private static BalanceStrategy BAL_STRATEGY = BalanceStrategy.LeastLoaded;
    private static final int AVG_REQ_TIME_MS = 750;
    private static final int AVG_TIME_BETWEEN_REQS_MS = 1000;

    public static void main(String[] args) throws InterruptedException {
        Server[] servers = new Server[]{new Server(0.8), new Server(1.0), new Server(1.2)};

        for (int i = 0; i < 3; i++) {
            new Client(AVG_REQ_TIME_MS, AVG_TIME_BETWEEN_REQS_MS, servers, BAL_STRATEGY);
        }

        while (true) {
            for (Server server : servers) {
                printServerStat(server);
            }
            System.out.println();
            Thread.sleep(1000);
        }
    }

    private enum BalanceStrategy {
        Random, RoundRobin, WeightedRandom, LeastLoaded,
    }

    private static class Request {
        private final Client client;
        private final int timeMs;

        Request(Client client, int timeMs) {
            this.client = client;
            this.timeMs = timeMs;
        }
    }

    private static class Result {
        private final Server server;

        Result(Server server) {
            this.server = server;
        }
    }

    static class Server {

        final double power;
        final BlockingQueue<Request> requests = new ArrayBlockingQueue<>(100);
        volatile boolean isWorking;

        Server(double power) {
            this.power = power;
            new Thread(() -> {
                while (true) {
                    try {
                        Request request = requests.take();
                        isWorking = true;
                        Thread.sleep((long) (request.timeMs / power));
                        request.client.results.add(new Result(this));
                        isWorking = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Request processor").start();
        }

    }

    private static class Client {

        final BlockingQueue<Result> results = new ArrayBlockingQueue<>(100);

        private Client(int avgReqTimeMs, int avgTimeBetweenReqsMs, Server[] thatServers, BalanceStrategy balanceStrategy) {

            List<Server> servers = new ArrayList<>(Arrays.asList(thatServers));
            Collections.shuffle(servers);  // for round robin

            Map<Server, AtomicInteger> serverToLoad = new HashMap<>();
            for (Server server : servers) {
                serverToLoad.put(server, new AtomicInteger());
            }

            new Thread(() -> {
                int halfReqTimeMs = avgReqTimeMs / 2;
                int halfTimeBetweenReqsMs = avgTimeBetweenReqsMs / 2;
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int nextServIdx = 0;
                double powerSum = servers.stream().mapToDouble(srv -> srv.power).sum();
                while (true) {
                    int reqTimeMs = avgReqTimeMs + random.nextInt(-halfReqTimeMs, halfReqTimeMs + 1);

                    Server server = null;
                    switch (balanceStrategy) {
                        case Random:
                            server = servers.get(random.nextInt(0, servers.size()));
                            break;
                        case RoundRobin:
                            server = servers.get(nextServIdx++ % servers.size());
                            break;
                        case WeightedRandom:
                            getWeightedRandom(servers, powerSum);
                        case LeastLoaded:
                            server = getLeastLoaded(serverToLoad);
                            break;
                    }

                    serverToLoad.get(server).getAndIncrement();
                    server.requests.add(new Request(this, reqTimeMs));

                    int sleepMs = avgTimeBetweenReqsMs + random.nextInt(-halfTimeBetweenReqsMs, halfTimeBetweenReqsMs + 1);
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Request sender").start();

            new Thread(() -> {
                while (true) {
                    try {
                        Result result = results.take();
                        serverToLoad.get(result.server).decrementAndGet();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Result receiver").start();
        }

        private Server getWeightedRandom(List<Server> servers, double powerSum) {
            double choice = ThreadLocalRandom.current().nextDouble(0, powerSum);
            double curPowerSum = 0.0;
            for (Server srv : servers) {
                curPowerSum += srv.power;
                if (curPowerSum > choice) {
                    return srv;
                }
            }
            return null;
        }

        private static Server getLeastLoaded(Map<Server, AtomicInteger> serverToLoad) {
            int minLoad = Integer.MAX_VALUE;
            Server bestServer = null;
            for (Map.Entry<Server, AtomicInteger> servAndLoad : serverToLoad.entrySet()) {
                int load = servAndLoad.getValue().get();
                if (load < minLoad) {
                    minLoad = load;
                    bestServer = servAndLoad.getKey();
                }
            }
            return bestServer;
        }

    }

    private static void printServerStat(Server server) {
        int pendingTasks = server.requests.size();
        boolean isWorking = server.isWorking;
        int padding = 12 - pendingTasks - 1;
        for (int i = 0; i < padding; i++) {
            System.out.print(' ');
        }
        for (int i = 0; i < pendingTasks; i++) {
            System.out.print('W');
        }
        if (isWorking) {
            System.out.print('R');
        } else {
            System.out.print('-');
        }
    }

}
