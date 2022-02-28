import java.io.*;
public class OthelloDriver {
	public static void main (String [] args) throws Exception{
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in), 1);
		Othello game = new Othello(); //������
		System.out.println("�e�X�g�P�FOthello�N���X�̃I�u�W�F�N�g���������������ʁF");
		printStatus(game);
		printGrids(game);
		while(true){
			System.out.println("�΂�u���ꏊ(�����܂���giveup)���L�[�{�[�h�œ��͂��Ă�������");
			String s = r.readLine();//������̓���
			System.out.println(s + " �����͂���܂����B��Ԃ� " + game.getTurn() + " �ł��B");

      if(game.gamestart(s)){
	       System.out.println("��Ԃ�ύX���܂��B\n");
      }
      printStatus(game);
      printGrids(game);
		}
	}
	//��Ԃ�\������
	public static void printStatus(Othello game){
		System.out.println("checkWinner�o��:" + game.getWinColor());
		System.out.println("isGameover�o��:" + game.getJudge());
		System.out.println("getTurn�o�́F" + game.getTurn());
	}
	//�e�X�g�p�ɔՖʂ�\������
	public static void printGrids(Othello game){
		int [][] grids = game.getGrids();

		// System.out.println("getGrids�e�X�g�o�́F(BLACK=1,WHITE=2,EMPTY=0)");
		// for(int i = 0 ; i < 8 ; i++){
    //   for(int j = 0 ; j < 8 ; j++){
  	// 		System.out.print(grids[i][j] + " ");
		// 	}
    //   System.out.print("\n");
		// }
	}
}
