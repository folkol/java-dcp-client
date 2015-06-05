package com.folkol;

public class MainInterrupt {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            while (true)
                System.out.println(Thread.currentThread().isInterrupted());
        });
        t.start();
        Thread.sleep(2000);
        t.interrupt();
    }
}
