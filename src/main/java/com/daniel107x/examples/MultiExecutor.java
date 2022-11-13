package com.daniel107x.examples;

import java.util.ArrayList;
import java.util.List;

public class MultiExecutor {
    private final List<Thread> threads;

    public MultiExecutor(List<Runnable> tasks) {
        this.threads = new ArrayList<>();
        for(Runnable task : tasks){
            this.threads.add(new Thread(task));
        }
    }

    public void executeAll(){
        for(Thread thread : this.threads){
            thread.start();
        }
    }
}
