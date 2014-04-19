package ru.javatalks.fundamentals.account.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TaskManager implements AutoCloseable {
    private final AtomicInteger currentNumber = new AtomicInteger();
    private final AtomicInteger maxNumber = new AtomicInteger();
    private final ThreadPoolExecutor executorService;
    private final TaskFactory taskFactory;
    private volatile boolean needToStop;

    public TaskManager(@Nonnull TaskFactory taskFactory) {
        this.taskFactory = requireNonNull(taskFactory);
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder()
                .setPriority(Thread.NORM_PRIORITY - 1);
        executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool(threadFactoryBuilder.build());
        executorService.submit(new ManagerThread());
    }

    public void setMaxNumber(int newValue) {
        maxNumber.getAndSet(newValue);
    }

    public boolean isRunning() {
        if (!needToStop) {
            return true;
        }
        if (currentNumber.get() > maxNumber.get()) {
            return false;
        }
        needToStop = false;
        return true;
    }

    public void started() {
        currentNumber.incrementAndGet();
    }

    public void stopped() {
        currentNumber.decrementAndGet();
    }

    public int getCurrentNumber() {
        return currentNumber.get();
    }

    @Override
    public void close() {
        needToStop = true;
        setMaxNumber(0);
        executorService.shutdownNow();
    }

    public class ManagerThread implements Runnable {
        // Запускаем/останавливаем поток в дополнительном управляющем потоке, чтобы не тормозить основное выполнение.
        @Override
        public void run() {
            while (true) {
                // По хорошему надо было бы завести очередь на изменение количества и эту очередь обрабатывать.
                try {
                    MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }

                int delta = maxNumber.get() - currentNumber.get();
                if (delta > 0) {
                    for (int i = 0; i < delta; i++) {
                        executorService.submit(taskFactory.createTask(TaskManager.this));
                    }
                } else {
                    needToStop = true;
                }
            }
        }
    }
}
