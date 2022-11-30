package com.daniel107x.concurrency;

import java.util.ArrayList;
import java.util.List;

public class DataRace {

    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();
        List<Thread> threads = new ArrayList<>();
        threads.add(new Thread(()->{
            for(int i = 0 ; i < Integer.MAX_VALUE ; i++) {
                sharedClass.increment();
            }
        }));
        threads.add(new Thread(()-> {
            for(int i = 0 ; i < Integer.MAX_VALUE ; i++) {
                sharedClass.checkDataRace();
            }
        }));
        threads.forEach(Thread::start);
    }

    public static class SharedClass {
        private volatile int x;
        private volatile int y;

        public void increment() {
            this.x++;
            this.y++;
        }

        public void checkDataRace() {
            if (this.y > this.x) {
                System.out.println("y > x Data race detected!");
            }
        }
    }
}
