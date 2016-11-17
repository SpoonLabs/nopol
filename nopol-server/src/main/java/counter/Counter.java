package counter;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static java.util.Arrays.asList;

/**
 * Created by bdanglot on 11/10/16.
 */
public class Counter {

	private static final int PORT = 5050;

	private static final Server server = new Server(PORT);

	public static void run() throws Exception {
		server.setHandler(new AbstractHandler() {
			@Override
			public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
				target = target.substring(1);
				if (isHandled(target)) {
					count(target);
					response.setContentType("text/html;charset=utf-8");
					response.setStatus(HttpServletResponse.SC_OK);
					baseRequest.setHandled(true);
					response.getWriter().println(target);
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					baseRequest.setHandled(true);
				}
			}
		});
		server.start();
	}

	public static void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isHandled(String target) {
		return asList("test", "START_PLUGIN", "REPAIR_ATTEMPT", "REPAIR_SUCCESS", "GENERATE_TOY_PROJECT").contains(target);
	}

	private static void count(String countName) throws IOException {
		checkpath();
		String path = "out/counter/" + countName;
		final File file = new File(path);
		String valueToWrite = "1";
		if (file.exists()) {
			valueToWrite = String.valueOf(Integer.parseInt(new BufferedReader(new FileReader(file)).readLine()) + 1);
		}
		final FileWriter writer = new FileWriter(path);
		writer.write(valueToWrite);
		writer.close();
	}

	private static void checkpath() {
		final File out = new File("out");
		if (out.exists())
			return ;
		else {
			out.mkdir();
			new File("out/counter").mkdir();
		}
	}

}
