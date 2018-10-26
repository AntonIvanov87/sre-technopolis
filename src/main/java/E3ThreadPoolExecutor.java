import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

final class E3ThreadPoolExecutor {

    static class UserService {

        private final ExecutorService threadPool = new ThreadPoolExecutor(
                256, 256, 100, TimeUnit.SECONDS,
                new SynchronousQueue<>());

        public List<Future<String>> fetchUserNames(List<Integer> userIds) {
            return userIds.stream()
                    .map(userId -> threadPool.submit(createFetchNameTask(userId)))
                    .collect(Collectors.toList());
        }

        private Callable<String> createFetchNameTask(final int userId) {
            return () -> {
                Thread.sleep(1000);
                return "Anton Ivanov";
            };
        }
    }

    public static void main(String[] args) {
        UserService userNameService = new UserService();
        while (true) {
            userNameService.fetchUserNames(genUserIds());
        }
    }

    private static List<Integer> genUserIds() {
        List<Integer> userIds = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            userIds.add(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        }
        return userIds;
    }
}
