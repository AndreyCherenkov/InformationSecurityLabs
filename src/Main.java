public class Main {
    public static void main(String[] args) {
        String message = "SXYTZJCJBJXE";
        String alphabet = "ABCDEFGHIi_JKLMNOPQRSTjUVWXYZ";

        for (int i = 0; i < alphabet.length(); i++) {
            System.out.println("Сдвиг " + i + ": " + decrypt(message, i, alphabet));
        }
    }

    public static String decrypt(String message, int shift, String alphabet) {
        StringBuilder decryptedMessage = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char currentChar = message.charAt(i);
            int charIndex = alphabet.indexOf(currentChar);

            if (charIndex != -1) {
                int newIndex = (charIndex - shift + alphabet.length()) % alphabet.length();
                decryptedMessage.append(alphabet.charAt(newIndex));
            } else {
                decryptedMessage.append(currentChar);
            }
        }

        return decryptedMessage.toString();
    }
}
