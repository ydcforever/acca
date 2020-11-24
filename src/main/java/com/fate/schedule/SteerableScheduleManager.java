package com.fate.schedule;

import com.fate.annotation.handler.MethodUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by ydc on 2019/12/8.
 */
@Component
public class SteerableScheduleManager implements BeanPostProcessor, Ordered, DisposableBean {

    private ThreadPoolTaskScheduler taskScheduler;

    private boolean autoOpen = false;

    private final Map<String, CronTask> tasks = new HashMap<>();

    private final Map<String, ScheduledFuture<?>> futures = new LinkedHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    public SteerableScheduleManager(Integer poolSize) {
        createTaskScheduler(poolSize);
        this.autoOpen = false;
    }

    public SteerableScheduleManager(Integer poolSize, boolean autoOpen) {
        createTaskScheduler(poolSize);
        this.autoOpen = autoOpen;
    }

    private void createTaskScheduler(Integer poolSize){
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.initialize();
        taskScheduler.setThreadNamePrefix("SteerableSchedule-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(60);
    }

    public <T> boolean addTask(String id, Class<T> target, String methodName, String cron, Class<?>...parameterTypes){
        if(keyDetection(id)) {
            try {
                Method method = target.getMethod(methodName, parameterTypes);
                T t = objenesis.newInstance(target);
                Runnable runnable = new ScheduledMethodRunnable(t, method);
                CronTask task = new CronTask(runnable, cron);
                this.tasks.put(id, task);
                return true;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean addTask(String id, Runnable runnable, String cron, boolean open){
        if (keyDetection(id)) {
            CronTask task = new CronTask(runnable, cron);
            if (open) {
                ScheduledFuture<?> future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
                this.futures.put(id, future);
            }
            this.tasks.put(id, task);
            return true;
        } else {
            return false;
        }
    }

    public synchronized void startJob(String id) {
        stopJob(id, true);
        CronTask task = tasks.get(id);
        if(task != null) {
            ScheduledFuture<?> future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
            this.futures.put(id, future);
        }
    }

    public synchronized void modifyJob(String id, String cron) {
        stopJob(id, false);
        CronTask task = tasks.get(id);
        if(task != null) {
            Runnable runnable = task.getRunnable();
            CronTask newTask = new CronTask(runnable, cron);
            tasks.put(id, newTask);
            ScheduledFuture<?> future = this.taskScheduler.schedule(runnable, newTask.getTrigger());
            futures.put(id, future);
        }
    }

    public synchronized void stopJob(String id, boolean interruptRunning) {
        ScheduledFuture<?> future = futures.get(id);
        if(future != null) {
            if(!future.isCancelled()) {
                future.cancel(interruptRunning);
                futures.remove(id);
            }
        }
    }

    public synchronized void restartSchedule() {
        stopSchedule(false);
        startSchedule();
    }

    public synchronized void startSchedule(){
        for(Map.Entry<String, CronTask> entry : tasks.entrySet()) {
            CronTask task = entry.getValue();
            ScheduledFuture<?> future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
            this.futures.put(entry.getKey(), future);
        }
    }

    public synchronized void stopSchedule(boolean mayInterruptIfRunning) {
        for(Map.Entry<String, ScheduledFuture<?>> entry : futures.entrySet()) {
            ScheduledFuture<?> future = entry.getValue();
            if(!future.isCancelled()) {
                future.cancel(mayInterruptIfRunning);
            }
        }
        futures.clear();
    }

    private boolean keyDetection(String id) {
        Set<String> keys = tasks.keySet();
        for(String key : keys) {
            if(id.equals(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void destroy() throws Exception {
        stopSchedule(false);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> target = AopUtils.getTargetClass(bean);
        MethodUtils.doMethodAnnotation(target, SteerableSchedule.class, new MethodUtils.MetadataTraverse<SteerableSchedule>() {
            @Override
            public void inspect(Method method, SteerableSchedule steerableSchedule) {
                Runnable runnable = new ScheduledMethodRunnable(bean, method);
                addTask(steerableSchedule.id(), runnable, steerableSchedule.cron(), autoOpen);
            }
        });
        return bean;
    }
}
