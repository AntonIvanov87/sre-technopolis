import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class E4ThreadPoolDeadLock {

    public static final ExecutorService globalPool = new ThreadPoolExecutor(
            10, 10, 1, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10)
    );

    static class UserService {

        Future<String> getFullInfo(int userId) {
            return globalPool.submit(() -> {
                // fetch basic and extended info in parallel
                Future<String> extendedInfo = globalPool.submit(() -> getExtendedInfo(userId));
                String basicInfo = getBasicInfo(userId);
                return basicInfo + "-" + extendedInfo.get();
            });
        }

        private String getBasicInfo(int userId) {
            return String.valueOf(userId);
        }

        private String getExtendedInfo(int userId) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Ivanov Anton";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        UserService userService = new UserService();
        while (true) {
            List<Future<String>> futures = new ArrayList<>(10);
            for (int i = 0; i < 10; i++) {
                futures.add(userService.getFullInfo(ThreadLocalRandom.current().nextInt()));
            }
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        }
    }
}
