import java.io.*;

public class ClientDriver{
  public static void main(String [] args) throws Exception{
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in), 1);
    Player player = new Player(); //プレイヤオブジェクトの用意(ログイン)
		Othello game = new Othello(); //オセロオブジェクトを用意
		Client oclient = new Client(game, player); //引数としてオセロオブジェクトを渡す
		oclient.setVisible(true);
    System.out.println("テスト用サーバに接続します");
    oclient.connectServer("localhost", 10011);
    System.out.println("受信用テストメッセージを入力してください");
    while(true){
      String s = r.readLine();
      String t = r.readLine();
      oclient.receiveMessage(s,t);
      System.out.println("テストメッセージ「" + s + t + "」を受信しました");
      System.out.println("テスト操作を行った後、受信用テストメッセージを入力してください");
    }
  }
}
