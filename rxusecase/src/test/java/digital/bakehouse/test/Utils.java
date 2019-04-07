package digital.bakehouse.test;

public class Utils {
    public static void log(String message, Object... params) {
        String finalMessage = message;
        if (params != null && params.length > 0) {
            finalMessage = String.format(message, params);
        }
        System.out.println(finalMessage);
    }
}
