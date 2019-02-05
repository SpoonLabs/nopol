package xxl.java.support;

public class OperatingSystem {

    public static void main(String[] args) {
        System.out.println(osName() + ": " + simpleName());
    }

    public static String simpleName() {
        if (isWindows()) {
            return windowsSimpleName();
        }
        if (isUnix()) {
            return unixSimpleName();
        }
        if (isMac()) {
            return macSimpleName();
        }
        if (isSolaris()) {
            return solarisSimpleName();
        }
        return "unidentified";
    }

    public static boolean isWindows() {
        if (isWindows == null) {
            isWindows = nameContainsIdentifier("win");
        }
        return isWindows;
    }

    public static boolean isUnix() {
        if (isUnix == null) {
            isUnix = nameContainsIdentifier("nix") || nameContainsIdentifier("nux") || nameContainsIdentifier("aix");
        }
        return isUnix;
    }

    public static boolean isMac() {
        if (isMac == null) {
            isMac = nameContainsIdentifier("mac");
        }
        return isMac;
    }

    public static boolean isSolaris() {
        if (isSolaris == null) {
            isSolaris = nameContainsIdentifier("sunos");
        }
        return isSolaris;
    }

    public static String windowsSimpleName() {
        return "windows";
    }

    public static String unixSimpleName() {
        return "unix";
    }

    public static String macSimpleName() {
        return "macosx";
    }

    public static String solarisSimpleName() {
        return "solaris";
    }

    private static String osName() {
        if (osName == null) {
            osName = System.getProperty("os.name").toLowerCase();
        }
        return osName;
    }

    private static boolean nameContainsIdentifier(String identifier) {
        return osName().indexOf(identifier) >= 0;
    }

    private static String osName;
    private static Boolean isMac;
    private static Boolean isUnix;
    private static Boolean isWindows;
    private static Boolean isSolaris;
}