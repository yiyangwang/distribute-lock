package com.wzy.distribute.lock.util;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
	
	static class Task implements  Runnable  {
		
		private Integer i = 0;
		
		Task(Integer i){
			this.i = i;
		}
		
		public void run() {
			try {
				System.out.println(Thread.currentThread().getName());
				System.in.read();
				System.out.println(Thread.currentThread().getName() + " ， 执行完毕");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createThreadPool() {
		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(5);
		ExecutorService executorService = 
				new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, workQueue , Executors.defaultThreadFactory() , new ThreadPoolExecutor.DiscardPolicy());
	    
		for(int i=0;i<10;i++) {
			Task task = new Task(i);
			executorService.execute(task);
		}
		
		
		Executors.newScheduledThreadPool(10);
	
	}
	
	public static void main(String[] args) {
		createThreadPool();
	}
	
}
