package com.digg.cassandra.utils;

import java.util.concurrent.ArrayBlockingQueue;

public class RealBlockingQueue<E> extends ArrayBlockingQueue<E> {

    public RealBlockingQueue(int capacity) {
        super(capacity);
    }

    public boolean offer(E o) {
        try {
            System.out.println(this.getClass() + "@" + 
                    this.hashCode() + " queue has " + this.size() + " elements.");
            put(o);
        } catch (InterruptedException e) {
            System.out.println("Could not insert object " + o + "into queue");
            return false;
        }
        return true;
    }

    public static void main(String args[]) throws InterruptedException {
        RealBlockingQueue<Integer> blockingQueue = new RealBlockingQueue<Integer>(
                2);
        for (int i = 0; i < 5; i++) {
            System.out.println("Putting in " + i);
            blockingQueue.offer(i);
        }

    }
}

