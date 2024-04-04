package Lab1;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Cipher {

    // ТРЕБУЕМАЯ длина ключа. Не устанавливать длину ключа больше 7 при необходимости использования метода searchKeys
    private static final int KEY_LENGTH = 7;

    //Алфавит английских букв (ТЕКУЩАЯ РЕАЛИЗАЦИЯ)
    //Для заполнения алфавита используется метод createAlphabet
    private static final Set<Character> alphabet = new TreeSet<>();

    public static void main(String[] args) {
        Cipher.createAlphabet();

        /**
         * Фактически, ключом является любая последовательность символов,
         * которая в числовом виде будет аналогична ключу.
         * Например ключ actions = abfcde = 015234
         **/

        String message = "authority is a power";
        String key = "actions";

        System.out.println(encryptRoundOne(message, key));

        System.out.println("Ключ в формате числа: " + Arrays.toString(convertKeyToNumber(key)));

        String tmp1 = encryptMessage(message, key);
        System.out.println("Зашифрованное сообщение R1+R2: " + tmp1);
        System.out.println("Расшифрованное сообщение: " + decryptMessage(tmp1, key));

        Cipher.searchKeys(message, key);

    }

    public static void createAlphabet() {

        for(int i = 'a'; i <= 'z'; i++) {
            alphabet.add((char)i);
        }
        alphabet.add(' ');

    }

    public static int[] convertKeyToNumber(String key) {

        if (key.trim().length() == 0 || key.trim().length() > KEY_LENGTH) {
            System.out.println("Некорректный ключ");
            return null;
        }

        key = key.toLowerCase();
        int[] numericKey = new int[KEY_LENGTH];
        var num = 1;
        boolean flag = false;
        for (Character c: alphabet) {
            if (key.contains(c.toString())) {
                for (int j = 0; j < KEY_LENGTH; j++) {
                    if (!Character.isDigit(c) && key.charAt(j) == c) {
                        flag = true;
                        numericKey[j] = num++;
                        numericKey[j]--;
                    }
                }
            }
        }

        if (!flag) {
            int i = 0;
            for(Character c: key.toCharArray()){
                if(c - '0' < KEY_LENGTH && i < KEY_LENGTH){
                    flag = true;
                    numericKey[i++] = c - '0';
                } else if (c - '0' >= KEY_LENGTH) {
                    flag = false;
                    break;
                }
            }
        }

        return flag ? numericKey: null;
    }

    // Нужен для подстановки в 0 ячейку ключа числа 0 (для обработки ключей длины KEY_LENGTH - 1)
    public static int[] convertKeyToNumber(String key, boolean zeroFirst) {

        if (key.trim().length() == 0 || key.trim().length() > KEY_LENGTH) {
            System.out.println("Некорректный ключ");
            return null;
        }

        int[] numericKey = new int[KEY_LENGTH];
        boolean flag = false;

        char[] keyChars = key.toCharArray();
        numericKey[0] = 0;
        for(int i = 1, j = 0; i < KEY_LENGTH; i++, j++){
            if(Character.isDigit(keyChars[j]) && keyChars[j] - '0' < KEY_LENGTH){
                flag = true;
                numericKey[i] = keyChars[j] - '0';
            } else if (keyChars[j] - '0' >= KEY_LENGTH) {
                flag = false;
                break;
            }
        }


        return flag ? numericKey: null;
    }

    // Метод, дополняющий пробелами шифруемое сообщение до кратной блоку длины
    public static String addSpacesToMessage(String message) {
        //Если длина сообщения НЕ КРАТКА длине ключа,
        //то дополняем сообщение пробелами (для дальнейшего разделения на блоки)
        StringBuilder messageBuilder = new StringBuilder(message);
        while (messageBuilder.length() % KEY_LENGTH != 0) {
            messageBuilder.append(" ");
        }
        return messageBuilder.toString();
    }

    // Методы шифрования
    // Блочное шифрование перестановками (первый раунд).
    // Блочное шифрование с использованием XOR (второй раунд)
    public static String encryptRoundOne(String message, String key) {

        if (key.length() < KEY_LENGTH) {
            System.out.println("Длина ключа не соответствует требованиям");
            return null;
        }

        message = addSpacesToMessage(message);

        // numKey - ключ в формате массива чисел (позиции для перестановки)
        // resultArray - результирующая строка в формате массива чаров
        // offset - смещение относительно начала строки
        StringBuilder stringBuilder = new StringBuilder();
        int[] numKey = convertKeyToNumber(key);
        char[] resultArray = new char[message.length()];
        int offset = 0;

        for (int i = 0, j = 0; i < message.length(); i++, j++) {
            if (j == KEY_LENGTH) {
                j = 0;
                offset += KEY_LENGTH;
            }

            //Собственно блок с шифровкой
            if(numKey != null) {
                resultArray[numKey[j] + offset] = message.charAt(i);
            } else {
                break;
            }
        }

        return new String(resultArray);
    }

    public static String encryptRoundTwo(String cipheredMessage, String key){

        if (key.length() < KEY_LENGTH) {
            System.out.println("Длина ключа не соответствует требованиям");
            return null;
        }

        cipheredMessage = addSpacesToMessage(cipheredMessage);

        // numKey - ключ в формате массива чисел (позиции для перестановки)
        // resultArray - результирующая строка в формате массива чаров
        // offset - смещение относительно начала строки

        StringBuilder stringBuilder = new StringBuilder();
        int[] numKey = convertKeyToNumber(key);
        char[] resultArray = new char[cipheredMessage.length()];

        for (int i = 0, j = 0; i < cipheredMessage.length(); i++, j++) {
            if (j == KEY_LENGTH) {
                j = 0;
            }

            //Собственно блок с шифровкой
            if(numKey != null) {
                resultArray[i] = (char) ((int)cipheredMessage.charAt(i) ^ numKey[j]);
            } else {
                break;
            }
        }

        return new String(resultArray);

    }

    public static String encryptMessage(String message, String key) {
        String tmp = encryptRoundOne(message, key);
        tmp = encryptRoundTwo(tmp, key);
        return tmp;
    }

    public static String decryptRoundOne(String message, String key) {
        if (key.length() < KEY_LENGTH) {
            System.out.println("Длина ключа не соответствует требованиям");
            return null;
        }

        message = addSpacesToMessage(message);

        // numKey - ключ в формате массива чисел (позиции для перестановки)
        // resultArray - результирующая строка в формате массива чаров
        // offset - смещение относительно начала строки
        int[] numKey = convertKeyToNumber(key);
        char[] resultArray = new char[message.length()];
        int offset = 0;

        for (int i = 0, j = 0; i < message.length(); i++, j++) {
            if (j == KEY_LENGTH) {
                j = 0;
                offset += KEY_LENGTH;
            }

            //Собственно блок с дешифровкой
            if(numKey != null) {
                resultArray[i] = message.charAt(numKey[j] + offset);
            } else {
                break;
            }

        }

        return new String(resultArray).trim();
    }

    public static String decryptRoundTwo(String message, String key) {
        if (key.length() < KEY_LENGTH) {
            System.out.println("Длина ключа не соответствует требованиям");
            return null;
        }

        message = addSpacesToMessage(message);

        // numKey - ключ в формате массива чисел (позиции для перестановки)
        // resultArray - результирующая строка в формате массива чаров
        // offset - смещение относительно начала строки
        int[] numKey = convertKeyToNumber(key);
        char[] resultArray = new char[message.length()];
        int offset = 0;

        for (int i = 0, j = 0; i < message.length(); i++, j++) {
            if (j == KEY_LENGTH) {
                j = 0;
                offset += KEY_LENGTH;
            }

            //Собственно блок с дешифровкой
            if(numKey != null) {
                resultArray[i] = (char) ((int)message.charAt(i) ^ numKey[j]);
            } else {
                break;
            }

        }

        return new String(resultArray).trim();
    }

    public static String decryptMessage(String message, String key){
        String tmp = decryptRoundTwo(message, key);
        tmp = decryptRoundOne(tmp, key);
        //String tmp = decryptRoundOne(message, key);
        return tmp;
    }

    // Методы дешифровки для работы с ключами длины KEY_LENGTH - 1. НУЖНЫ ТОЛЬКО ДЛЯ МЕТОДА searchKeys
    private static String decryptRoundOne(String message, String key, boolean zeroFirst) {
        if (key.length() < KEY_LENGTH - 1) {
            System.out.println("Длина ключа не соответствует требованиям");
            return null;
        }

        message = addSpacesToMessage(message);

        // numKey - ключ в формате массива чисел (позиции для перестановки)
        // resultArray - результирующая строка в формате массива чаров
        // offset - смещение относительно начала строки
        StringBuilder stringBuilder = new StringBuilder();
        int[] numKey = convertKeyToNumber(key, true);
        char[] resultArray = new char[message.length()];
        int offset = 0;

        for (int i = 0, j = 0; i < message.length(); i++, j++) {
            if (j == KEY_LENGTH) {
                j = 0;
                offset += KEY_LENGTH;
            }

            //Собственно блок с дешифровкой
            if(numKey != null) {
                resultArray[i] = message.charAt(numKey[j] + offset);
            } else {
                break;
            }
        }

        return new String(resultArray).trim();
    }

    private static String decryptRoundTwo(String message, String key, boolean zeroFirst) {
        if (key.length() < KEY_LENGTH - 1) {
            System.out.println("Длина ключа не соответствует требованиям");
            return null;
        }

        message = addSpacesToMessage(message);

        // numKey - ключ в формате массива чисел (позиции для перестановки)
        // resultArray - результирующая строка в формате массива чаров
        // offset - смещение относительно начала строки
        int[] numKey = convertKeyToNumber(key, true);
        char[] resultArray = new char[message.length()];
        int offset = 0;

        for (int i = 0, j = 0; i < message.length(); i++, j++) {
            if (j == KEY_LENGTH) {
                j = 0;
                offset += KEY_LENGTH;
            }

            //Собственно блок с дешифровкой
            if(numKey != null) {
                resultArray[i] = (char) ((int)message.charAt(i) ^ numKey[j]);
            } else {
                break;
            }

        }

        return new String(resultArray).trim();
    }

    private static String decryptMessage(String message, String key, boolean zeroFirst) {
        String tmp = decryptRoundTwo(message, key, true);
        tmp = decryptRoundOne(tmp, key, true);
        //String tmp = decryptRoundOne(message, key, true);
        return tmp;
    }

    public static List<String> searchKeys(String message, String key) {
        List<String> keys = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(4);

        String word1 = "power";
        String word2 = "authority";

        Object monitor1 = new Object();
        Object monitor2 = new Object();
        Object monitor3 = new Object();
        Object monitor4 = new Object();

        String tmp = encryptMessage(message, key);
        long start = (long)Math.pow(10, KEY_LENGTH - 1);
        long end = (long)Math.pow(10, KEY_LENGTH);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i = start; i < end / 2; i++) {
                    synchronized (monitor1) {
                        String potentialKey = Long.toString(i);
                        String tmp2 = decryptMessage(tmp, potentialKey);
                        if(tmp2.contains(word1)) {
                            keys.add(potentialKey);
                        }
                    }
                }
                countDownLatch.countDown();
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i = end / 2; i < end; i++) {
                    synchronized (monitor2) {
                        String potentialKey = Long.toString(i);
                        String tmp2 = decryptMessage(tmp, potentialKey);
                        if(tmp2.contains(word1)) {
                            keys.add(potentialKey);
                        }
                    }
                }
                countDownLatch.countDown();
            }
        });

        // Поиск ключей, которые НАЧИНАЮТСЯ с 0
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i = start / 10; i < end / 20; i++) {
                    synchronized (monitor3) {
                        String potentialKey = Long.toString(i);
                        String tmp2 = decryptMessage(tmp, potentialKey, true);
                        if(tmp2.contains(word1)) {
                            keys.add("0" + potentialKey);
                        }
                    }
                }
                countDownLatch.countDown();
            }
        });

        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i = end / 20; i < end / 10; i++) {
                    synchronized (monitor4) {
                        String potentialKey = Long.toString(i);
                        String tmp2 = decryptMessage(tmp, potentialKey, true);
                        if(tmp2.contains(word1)) {
                            keys.add("0" + potentialKey);
                        }
                    }
                }
                countDownLatch.countDown();
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            countDownLatch.await();
            System.out.println("Количество других ключей на первой итерации: " + (keys.size() - 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (keys.size() - 1 > 2){
            List<String> keys2 = new ArrayList<>();
            CountDownLatch countDownLatch2 = new CountDownLatch(4);
            Thread thread11 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(long i = start; i < end / 2; i++) {
                        synchronized (monitor1) {
                            String potentialKey = Long.toString(i);
                            String tmp2 = decryptMessage(tmp, potentialKey);
                            if(tmp2.contains(word2)) {
                                keys2.add(potentialKey);
                            }
                        }
                    }
                    countDownLatch2.countDown();
                }
            });

            Thread thread22 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(long i = end / 2; i < end; i++) {
                        synchronized (monitor2) {
                            String potentialKey = Long.toString(i);
                            String tmp2 = decryptMessage(tmp, potentialKey);
                            if(tmp2.contains(word2)) {
                                keys2.add(potentialKey);
                            }
                        }
                    }
                    countDownLatch2.countDown();
                }
            });

            // Поиск ключей, которые НАЧИНАЮТСЯ с 0
            Thread thread33 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(long i = start / 10; i < end / 20; i++) {
                        synchronized (monitor3) {
                            String potentialKey = Long.toString(i);
                            String tmp2 = decryptMessage(tmp, potentialKey, true);
                            if(tmp2.contains(word2)) {
                                keys2.add("0" + potentialKey);
                            }
                        }
                    }
                    countDownLatch2.countDown();
                }
            });

            Thread thread44 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(long i = end / 20; i < end / 10; i++) {
                        synchronized (monitor4) {
                            String potentialKey = Long.toString(i);
                            String tmp2 = decryptMessage(tmp, potentialKey, true);
                            if(tmp2.contains(word2)) {
                                keys2.add("0" + potentialKey);
                            }
                        }
                    }
                    countDownLatch2.countDown();
                }
            });

            thread11.start();
            thread22.start();
            thread33.start();
            thread44.start();

            try {
                countDownLatch2.await();
                System.out.println("Количество других ключей на второй итерации: " + (keys2.size() - 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println("Первая итерация поиска ключей: " + keys);
            //System.out.println("Вторая итерация поиска ключей: " + keys2);

            return keys2;

        }

        return keys;
    }
}
