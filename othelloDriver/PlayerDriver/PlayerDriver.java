public class PlayerDriver {
  public static void main(String [] args) throws Exception{
    Player player = new Player();

    System.out.println("setNamePass�ŁuID:��񑾘Y�APASSWORD:zyouhou�v����͂��܂�");
    player.setNamePass("��񑾘Y","zyouhou");
    System.out.println("getName�o��: " + player.getName());
    System.out.println("getPass�o��: " + player.getPass());

    System.out.println("setWinCount��1�����");
    player.setWinCount(1);
    System.out.println("getWinCount�o��: " + player.getWinCount());

    System.out.println("setLoseCount��2�����");
    player.setLoseCount(2);
    System.out.println("getLoseCount�o��: " + player.getLoseCount());

    System.out.println("setResignCount��3�����");
    player.setResignCount(3);
    System.out.println("getResignCount�o��: " + player.getResignCount());

    System.out.println("setDrawCount��4�����");
    player.setDrawCount(4);
    System.out.println("getDrawCount�o��: " + player.getDrawCount());

    System.out.println("setRate��3000�����");
    player.setRate(3000);
    System.out.println("getRate�o��: " + player.getRate());

    System.out.println("setColor��Black�����");
    player.setColor("Black");
    System.out.println("getColor�o��: " + player.getColor());

    System.out.println("colorChange�����s");
    player.colorChange();
    System.out.println("getColor�o��: " + player.getColor());

    System.out.println("setDisConnectedCount��5�����");
    player.setDisConnectedCount(5);

    System.out.println("getStatus�o�́F" + player.getStatus());

    System.out.println("addDrawCount�����s");
    System.out.println("getDrawCount�o��: " + player.getDrawCount());

    System.out.println("addResignCount�����s");
    System.out.println("getResignCount�o��: " + player.getResignCount());

    System.out.println("addRate��true,2000�����");
    player.addRate(true,2000);
    System.out.println("getStatus�o�́F" + player.getStatus());

    System.out.println("addRate��false,2000�����");
    player.addRate(false,2000);
    System.out.println("getStatus�o�́F" + player.getStatus());
  }
}
