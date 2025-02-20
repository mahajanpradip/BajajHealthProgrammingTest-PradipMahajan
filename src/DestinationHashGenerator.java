import java.io.FileReader;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <prn_number> <json_file_path>");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase().trim();
        String jsonFilePath = args[1];

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(jsonFilePath));
            String destinationValue = findFirstDestinationValue(jsonObject);

            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the JSON file.");
                System.exit(1);
            }

            String randomString = generateRandomString(8);
            String toHash = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(toHash);
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String findFirstDestinationValue(JSONObject jsonObject) {
        for (Object key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.toString().equals("destination")) {
                return value.toString();
            }
            if (value instanceof JSONObject) {
                String result = findFirstDestinationValue((JSONObject) value);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
