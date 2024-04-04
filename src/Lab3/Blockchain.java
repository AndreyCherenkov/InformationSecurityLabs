package Lab3;

import Lab3.Block;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {

    private List<Block> chain;

    //При инициализации каждая цепочка состоит из двух блоков
    public Blockchain() {
        chain = new ArrayList<>();
        Block source = new Block(0, "Source block", "0");
        chain.add(source);
        chain.add(new Block(1, "Data1", source.calculateHash()));
    }

    public void addBlock(Block newBlock) {
        newBlock.setPreviousHash(chain.get(chain.size() - 1).getHash());
        newBlock.setHash(newBlock.calculateHash());
        chain.add(newBlock);
    }

    public int getChainSize() {
        return chain.size();
    }

    public Block getBlock(int pos) {
        return chain.get(pos);
    }


    public List<Block> getChain() {
        return chain;
    }

    public String getBlockchainHash() {
        return chain.get(getChainSize() - 1).getHash();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < getChainSize(); i ++) {
            stringBuilder.append(chain.get(i)).append(" -> ");
        }
        return stringBuilder.toString();
    }
}
