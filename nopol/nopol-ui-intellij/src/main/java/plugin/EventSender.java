package plugin;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Benjamin DANGLOT (benjamin.danglot@inria.fr)
 *
 * EventSender is responsible to communicate usage of the plugin to build statistic.
 * It sends only GET http request at the address specified in the config.properties.
 *
 */
public class EventSender {

    public enum Event {START_PLUGIN, REPAIR_ATTEMPT, REPAIR_SUCCESS, GENERATE_TOY_PROJECT}

    public static void send(Event verb) {
        String address_server = String.valueOf(Plugin.properties.get("address_server")) + "/" + verb;
        try {
            URL url = new URL(address_server);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.getInputStream();
        } catch (Exception ignored) {
            System.err.println("Unable to send the request count " + verb + " to " + address_server);
        }
    }

}
