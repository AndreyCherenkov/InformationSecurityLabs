package Lab3;

import java.util.*;

public class Main {

    private static final List<Blockchain> chains = new ArrayList<>();
    private static int broadcastCounter;

    public static void main(String[] args) {
        Blockchain chain1 = new Blockchain();
        Blockchain chain2 = new Blockchain();
        Blockchain chain3 = new Blockchain();
        chains.add(chain1);
        chains.add(chain2);
        chains.add(chain3);
        broadcastCounter = 2;

        System.out.printf("Инициализация  %d цепочек прошла успешно\n", chains.size());
        System.out.println("Введите 'help' для вывода списка команд");

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (input) {
                case "help" -> help();
                case "print" -> printChains();
                case "leninfo" -> getLenInfo();
                case "hashinfo" -> getHashInfo();
                case "modify" -> {
                    try {
                        System.out.println("Введите номер цепочки для модификации");
                        input = scanner.nextLine().trim();
                        int numOfChain = Integer.parseInt(input);
                        System.out.println("Введите номер блока указанной ранее цепочки для модификации");
                        input = scanner.nextLine().trim();
                        int numOfBlock = Integer.parseInt(input);
                        System.out.println("Введите модифицированные данные");
                        String data = scanner.nextLine();
                        modifyChain(numOfChain, numOfBlock, data);
                    } catch (Exception e) {
                        System.out.println("Введено не число");
                    }
                }
                case "broadcast" -> {
                    broadcastBlock();
                    broadcastCounter++;
                }
                case "fixchains" -> fixChains();
                default -> {
                    if(!input.equals("exit")) {
                        System.out.println("Команда не найдена");
                    }

                }
            }

            if(input.equals("exit")) {
                System.out.println("Завершение работы программы");
                break;
            }
        }
    }

    private static void help() {
        System.out.println("Введите 'getLenInfo' для вывода информации о длине цепочек");
        System.out.println("Введите 'getHashInfo' для вывода хеша цепочки");
        System.out.println("Введите 'modify' для внесения модификации в цепочку");
        System.out.println("Введите 'broadcast' для создания нового блока во всех узлах");
        System.out.println("Введите 'addBlock' для создания нового блока  В ОДНОМ УЗЛЕ");
        System.out.println("Введите 'fixChains' для исправления цепочек");
        System.out.println("Введите 'exit' или 'выход' для завершения программы");
    }
    private static void printChains() {
        int i = 0;
        for(Blockchain blockchain: chains) {
            System.out.println("chain" + (i++) + ": " + blockchain);
        }
    }

    private static void getLenInfo() {
        int i = 0;
        for(Blockchain blockchain: chains) {
            System.out.printf("Blockchain chain%d contains %d blocks\n", i, blockchain.getChainSize());
            i++;
        }
    }

    private static void getHashInfo() {
        int i = 0;
        for(Blockchain blockchain: chains) {
            System.out.printf("Blockchain chain%d hash %s: \n", i, blockchain.getBlockchainHash());
            i++;
        }
    }

    private static void broadcastBlock() {
        for (Blockchain blockchain : chains) {
            Block newBlock = new Block(broadcastCounter,
                    "Data" + broadcastCounter,
                    blockchain.getBlock(blockchain.getChainSize() - 1).calculateHash());
            blockchain.addBlock(newBlock);
        }
    }

    private static void modifyChain(int numOfChain, int numOfBlock, String data) {
        try {
            if(numOfBlock == 0) {
                throw new ModifySourceBlockException();
            }

            Blockchain blockchain = chains.get(numOfChain);
            blockchain.getBlock(numOfBlock).setData(data);

            for(int i = 1; i < blockchain.getChainSize(); i++) {
                blockchain.getBlock(i).setPreviousHash(blockchain.getBlock(i - 1).getHash());
                blockchain.getBlock(i).setHash(blockchain.getBlock(i).calculateHash());
            }

            System.out.printf("Blockchain %d modified \n", numOfChain);
            System.out.printf("Block %d modified in blockchain %d \n", numOfBlock, numOfChain);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Введенный номер превышает количество цепочек");
        } catch (ModifySourceBlockException e) {
            System.out.println("Нельзя модифицировать корневой элемент");
        }
    }

    private static void fixChains() {
        Map<String, Integer> hashes = new HashMap<>();
        for(Blockchain blockchain: chains) {
            if (hashes.containsKey(blockchain.getBlockchainHash())) {
                int count = hashes.get(blockchain.getBlockchainHash());
                hashes.put(blockchain.getBlockchainHash(), count + 1);
            } else {
                hashes.put(blockchain.getBlockchainHash(), 1);
            }
        }

        String maxKey = null;
        int maxValue = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : hashes.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxKey = entry.getKey();
                maxValue = entry.getValue();
            }
        }

        List<Blockchain> modifiedChains = new ArrayList<>();
        Blockchain correctChain = null;
        for(Blockchain blockchain: chains) {
            if(!(blockchain.getBlockchainHash().equals(maxKey))) {
                modifiedChains.add(blockchain);
            } else {
                correctChain = blockchain;
            }
        }

        if(modifiedChains.size() > 0 && correctChain != null) {
            for (Blockchain blockchain : modifiedChains) {
                for (int i = 1; i < blockchain.getChainSize(); i++) {
                    Block correctBlock = new Block(correctChain.getBlock(i).getIndex(),
                            correctChain.getBlock(i).getData(),
                            correctChain.getBlock(i - 1).getHash());
                    blockchain.getChain().set(i, correctBlock);
                }
            }
        } else {
            System.out.println("Fatal Error");
        }
    }
}
