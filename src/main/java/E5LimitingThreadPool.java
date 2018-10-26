import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class E5LimitingThreadPool {

    static class TransformationService {

        private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

        private final ExecutorService threadPool = new ThreadPoolExecutor(
                CPU_NUM, CPU_NUM, 1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(CPU_NUM * 2)
        );

        byte[] transform(byte[] photo) throws ExecutionException, InterruptedException {
            // limit number of concurrent transformations
            return threadPool.submit(() -> cpuBoundTransformation(photo)).get();
        }

        private byte[] cpuBoundTransformation(byte[] photo) {
            byte[] result = new byte[photo.length];
            for (int i = 0; i < photo.length; i++) {
                result[i] = (byte) (photo[i] + 1);
            }
            return result;
        }

    }

    public static void main(String[] args) {
    }
}
