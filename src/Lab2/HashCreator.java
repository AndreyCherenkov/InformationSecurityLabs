package Lab2;

import java.util.ArrayList;
import java.util.List;

public class HashCreator {

    private static final int HASH_LEN = 5;

    private static final List<String> combinations = new ArrayList<>();
    private static int collisions = 0;

    public static void main(String[] args) {
        int len = 3;
        System.out.println("Максимальная степень слагаемого в комбинации: " + len);
        generateCombinations(len, "");
        bruteForce();
    }

    //Метод конвертации строки в число
    public static int convertStringToNumber(String string) {

        if(string.trim().equals("")) {
            System.out.println("Пустая строка");
            return 0;
        }

        int result = 0;
        for(char c: string.toCharArray()) {
            result += c;
        }
        return result;
    }

    public static String generateHash(String message, int hashLength) {
        byte[] bytes = message.getBytes();
        long hash = 1; // начальное значение хеша

        for (byte b : bytes) {
            hash *= b;
        }

        // уменьшаем длину хеша до требуемой
        while (Long.toString(hash).length() > hashLength) {
            hash = Long.parseLong(Long.toString(hash).substring(1)); // удаляем старший разряд
        }

        return Long.toString(hash);
    }

    //Метод по созданию цепочек от 1 до maxLength
    public static void generateCombinations(int maxLength, String currentCombination) {
        for (int length = 1; length <= maxLength; length++) {
            generateCombinationsRecursive(length, currentCombination);
        }
    }

    //Рекурсивный метод создания символьных цепочек заданной длины
    private static void generateCombinationsRecursive(int length, String currentCombination) {
        if (currentCombination.length() == length) {
            combinations.add(currentCombination);
        } else {
            for (char letter = 'a'; letter <= 'z'; letter++) {
                generateCombinationsRecursive(length, currentCombination + letter);
            }
        }
    }

    public static List<String> bruteForce() {
        List<String> hashes = new ArrayList<>();

        Object monitor1 = new Object();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < (int)Math.sqrt(combinations.size()); i++) {
                    String hashCode = generateHash(combinations.get(i), HASH_LEN);
                    synchronized (monitor1) {
                        if(!hashes.contains(hashCode)) {
                            hashes.add(hashCode);
                        } else {
                            collisions++;
                        }
                    }
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = (int)Math.sqrt(combinations.size()); i < combinations.size(); i++) {
                    String hashCode = generateHash(combinations.get(i), HASH_LEN);
                    synchronized (monitor1) {
                        if(!hashes.contains(hashCode)) {
                            hashes.add(hashCode);
                        } else {
                            collisions++;
                        }
                    }
                }
            }
        });

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Максимальная длина хеша: " + HASH_LEN);
        System.out.println("Количество найденных коллизий: " + collisions);
        return hashes;
    }
}
