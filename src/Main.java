public class Main {
    public static void main(String[] args) {
        MD5 MD5 = new MD5();
        String hex = MD5.getMD5("хэш");
        System.out.println(hex);
    }
}
