public class PlayerDriver {
  public static void main(String [] args) throws Exception{
    Player player = new Player();

    System.out.println("setNamePassで「ID:情報太郎、PASSWORD:zyouhou」を入力します");
    player.setNamePass("情報太郎","zyouhou");
    System.out.println("getName出力: " + player.getName());
    System.out.println("getPass出力: " + player.getPass());

    System.out.println("setWinCountで1を入力");
    player.setWinCount(1);
    System.out.println("getWinCount出力: " + player.getWinCount());

    System.out.println("setLoseCountで2を入力");
    player.setLoseCount(2);
    System.out.println("getLoseCount出力: " + player.getLoseCount());

    System.out.println("setResignCountで3を入力");
    player.setResignCount(3);
    System.out.println("getResignCount出力: " + player.getResignCount());

    System.out.println("setDrawCountで4を入力");
    player.setDrawCount(4);
    System.out.println("getDrawCount出力: " + player.getDrawCount());

    System.out.println("setRateで3000を入力");
    player.setRate(3000);
    System.out.println("getRate出力: " + player.getRate());

    System.out.println("setColorでBlackを入力");
    player.setColor("Black");
    System.out.println("getColor出力: " + player.getColor());

    System.out.println("colorChangeを実行");
    player.colorChange();
    System.out.println("getColor出力: " + player.getColor());

    System.out.println("setDisConnectedCountで5を入力");
    player.setDisConnectedCount(5);

    System.out.println("getStatus出力：" + player.getStatus());

    System.out.println("addDrawCountを実行");
    System.out.println("getDrawCount出力: " + player.getDrawCount());

    System.out.println("addResignCountを実行");
    System.out.println("getResignCount出力: " + player.getResignCount());

    System.out.println("addRateでtrue,2000を入力");
    player.addRate(true,2000);
    System.out.println("getStatus出力：" + player.getStatus());

    System.out.println("addRateでfalse,2000を入力");
    player.addRate(false,2000);
    System.out.println("getStatus出力：" + player.getStatus());
  }
}
