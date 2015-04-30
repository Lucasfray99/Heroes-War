package server.cores;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class CoresManager {

	protected static volatile boolean shutdown;
	public static ExecutorService serverWorkerChannelExecutor;
	public static ExecutorService serverBossChannelExecutor;
	public static Timer fastExecutor;
	public static ScheduledExecutorService slowExecutor;
	public static int serverWorkersCount;

	public static void init() {
		int availableProcessors = Runtime.getRuntime().availableProcessors() - 1;// we minus one so the OS has a thread to operate on
		serverWorkersCount = availableProcessors >= 6 ? availableProcessors
				- (availableProcessors >= 12 ? 7 : 5) : 1;
		serverWorkerChannelExecutor = availableProcessors >= 6 ? Executors
				.newFixedThreadPool(availableProcessors
						- (availableProcessors >= 12 ? 7 : 5),
						new DecoderThreadFactory()) : Executors
				.newSingleThreadExecutor(new DecoderThreadFactory());
		serverBossChannelExecutor = Executors
				.newSingleThreadExecutor(new DecoderThreadFactory());
		fastExecutor = new Timer("Fast Executor");
		slowExecutor = availableProcessors >= 6 ? Executors
				.newScheduledThreadPool(availableProcessors >= 12 ? 4 : 2,
						new SlowThreadFactory()) : Executors
				.newSingleThreadScheduledExecutor(new SlowThreadFactory());
	}

	public static void shutdown() {
		serverWorkerChannelExecutor.shutdown();
		serverBossChannelExecutor.shutdown();
		fastExecutor.cancel();
		slowExecutor.shutdown();
		shutdown = true;
	}

	private CoresManager() {

	}
}
