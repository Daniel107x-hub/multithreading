package com.daniel107x.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VaultHackers {
    /**
     * I want to design a secure vault where I'm going to store some money
     * I want to know how much time it would take to some multiple hackers to guess my code.
     *
     * I'll have multiple hacker threads trying to bruteforce my code concurrently.
     * Additionally, I'll have a police thread that will come to our rescue by counting down 10 seconds.
     *
     * If once the police arrives, the hackers have not taken my money, they will get arrested.
     * The police thread will keep showing the progress of its arrival time
     */
    public static int MAX_PASSWORD = 9999;

    public static void main(String[] args) {
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        List<Thread> threads = new ArrayList<>();
        threads.add(new AscendingHackerThread(vault));
        threads.add(new DescendingHackerThread(vault));
        threads.add(new PoliceThread());

        for(Thread thread : threads){
            thread.start();
        }
    }

    private static class Vault{
        private int password;

        public Vault(int password){
            this.password = password;
        }

        public boolean isCorrect(int password){
            try{
                Thread.sleep(5);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return this.password == password;
        }
    }

    private static abstract class HackerThread extends Thread{
        protected Vault vault;
        public HackerThread(Vault vault){
            this.vault = vault;
            this.setName(this.getClass().getName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start(){
            System.out.println("Starting thread: " + this.getName());
            super.start();
        }
    }

    public static class AscendingHackerThread extends HackerThread{
        public AscendingHackerThread(Vault vault){
            super(vault);
        }

        @Override
        public void run(){
            for(int guess = 0 ; guess < MAX_PASSWORD ; guess++){
                if(vault.isCorrect(guess)){
                    System.out.println(this.getName() + " guessed the password: " + guess);
                    System.exit(0);
                }
            }
        }
    }

    public static class DescendingHackerThread extends HackerThread{
            public DescendingHackerThread(Vault vault){
                super(vault);
            }

            @Override
            public void run(){
                for(int guess = 0; guess >= 0; guess--){
                    if(vault.isCorrect(guess)){
                        System.out.println(this.getName() + " guessed the password: " + guess);
                        System.exit(0);
                    }
                }
            }
    }

    public static class PoliceThread extends Thread{
        @Override
        public void run(){
            for(int i = 10 ; i > 0 ; i--){
                System.out.println(i);
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            System.out.println(0);
            System.out.println("Game over for hackers!");
            System.exit(0);
        }
    }
}
