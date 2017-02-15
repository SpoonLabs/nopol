package fr.inria.lille.repair.vm;

import com.sun.jdi.ClassType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import fr.inria.lille.repair.MethodTestRunner;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * is an utility class used to run JUnit test in debug mode
 * <p/>
 * Created by Thomas Durieux on 04/03/15.
 */
public class DebugJUnitRunner {
    public static int port = 8000;
    public static Process process;

    static void copy(final InputStream in, final OutputStream out) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buf = new byte[1024];
                int len;
                try {
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean loadClass(String className, VirtualMachine vm) {
        boolean result = false;
        ClassType classReference = (ClassType) vm.classesByName("java.lang.Class").get(0);
        for (int i = 0; i < vm.allThreads().size(); i++) {
            ThreadReference thread = vm.allThreads().get(i);
            try {
                classReference.invokeMethod(thread, classReference.methodsByName("forName").get(0), Arrays.asList(vm.mirrorOf(className)), ObjectReference.INVOKE_SINGLE_THREADED);
            } catch (Exception e) {
                continue;
            }
            result = true;
            break;
        }
        return result;
    }

    public static VirtualMachine run(String[] testClasses, URL[] classpath) throws IOException {
        String strClasspath = "";
        for (int i = 0; i < classpath.length; i++) {
            URL url = classpath[i];
            if (url == null) {
                continue;
            }
            File file = new File(url.getFile());
            if (file.exists()) {
                strClasspath += file.getAbsolutePath() + File.pathSeparatorChar;
            }
        }

        String testList = "";
        for (int i = 0; i < testClasses.length; i++) {
            String testClass = testClasses[i];
            testList += testClass + " ";
        }
        ProcessBuilder processBuilder = new ProcessBuilder("java",
                "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y",
                "-cp",
                strClasspath,
                MethodTestRunner.class.getCanonicalName(),
                testList
        );
        System.out.println("java -cp " + strClasspath + " " + MethodTestRunner.class.getCanonicalName() + " " + testList);
        File log = new File("log");
        log.delete();
        log.createNewFile();
        OutputStream out = new FileOutputStream(log);

        process = processBuilder.start();

        InputStreamReader isr = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String lineRead = br.readLine();
        Pattern pattern = Pattern.compile("([0-9]{4,})");
        Matcher matcher = pattern.matcher(lineRead);
        matcher.find();
        port = Integer.parseInt(matcher.group());
        try {
            final VirtualMachine vm = new VMAcquirer().connect(port);
            copy(process.getInputStream(), out);
            copy(process.getErrorStream(), out);
            // kill process when the program exit
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    shutdown(vm);
                }
            });
            return vm;
        } catch (ConnectException e) {
            process.destroy();
            throw e;
        }
    }

    public static void shutdown(VirtualMachine vm) {
        try {
            process.destroy();
            // process.waitFor();
            vm.exit(0);
        } catch (Exception e) {
            // ignore
        }
    }
}
