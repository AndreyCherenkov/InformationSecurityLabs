package Lab3;

public class Block {

    private int index;
    private String data;
    private String previousHash;
    private String hash;

    public Block(int index,
                 String data,
                 String previousHash) {
        this.index = index;
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(this.getPreviousHash() + this.getData());
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "[" + index + ", " + data + "]";
    }
}
