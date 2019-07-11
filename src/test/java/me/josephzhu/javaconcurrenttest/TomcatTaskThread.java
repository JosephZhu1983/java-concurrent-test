package me.josephzhu.javaconcurrenttest;

public class TomcatTaskThread extends Thread {

    private final long creationTime;

    public TomcatTaskThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        this.creationTime = System.currentTimeMillis();
    }

    public TomcatTaskThread(ThreadGroup group, Runnable target, String name,
                            long stackSize) {
        super(group, target, name, stackSize);
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * @return the time (in ms) at which this thread was created
     */
    public final long getCreationTime() {
        return creationTime;
    }
}
