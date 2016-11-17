package counter;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Created by bdanglot on 11/10/16.
 */
public class CounterTest {

	@Test
	public void testCounter() throws Exception {
		Counter.run();
		String url = "http://localhost:5050";
		sendTo(url, "test");
		sendTo(url, "START_PLUGIN");
		sendTo(url, "REPAIR_ATTEMPT");
		sendTo(url, "REPAIR_SUCCESS");
		sendTo(url, "GENERATE_TOY_PROJECT");
		Counter.stop();
	}

	private void sendTo(String url, String cmd) throws IOException {
		URL obj = new URL(url + "/" + cmd);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.getInputStream();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		assertEquals(cmd, response.toString());
	}
}
