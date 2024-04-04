package Lab2;

public class Main {

    private String name;
    private String password;

    private static final int CONST = -10;

    public static void main(String[] args) {
        int y1 = 0;
        int y2 = 0;
        for(int i = 0; i < 15; i++) {
            if(i <= 4) {
                y1 = 4 * i;
            } else {
                y1 = i - CONST;
            }

            if(i % 2 == 1) {
                y2 = 7;
            } else {
                y2 = i / 2 + CONST;
            }

            System.out.println(i + " | " + (y1 + y2));
        }
    }

    public boolean login() {
        String name = this.getName();
        String password = this.getPassword();
        System.out.println(name);
        System.out.println(password);
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
