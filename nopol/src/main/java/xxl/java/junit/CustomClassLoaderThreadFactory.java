package xxl.java.junit;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class CustomClassLoaderThreadFactory implements ThreadFactory {

    public CustomClassLoaderThreadFactory(ClassLoader customClassLoader) {
        this.customClassLoader = customClassLoader;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = Executors.defaultThreadFactory().newThread(r);
        newThread.setDaemon(true); // use to avoid a main process to continue running waiting for this thread end at the end of execution
        newThread.setContextClassLoader(customClassLoader());
        return newThread;
    }

    private ClassLoader customClassLoader() {
        return customClassLoader;
    }

    private ClassLoader customClassLoader;
}
