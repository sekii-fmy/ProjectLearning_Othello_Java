import java.io.*;
public class OthelloDriver {
	public static void main (String [] args) throws Exception{
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in), 1);
		Othello game = new Othello(); //初期化
		System.out.println("テスト１：Othelloクラスのオブジェクトを初期化した結果：");
		printStatus(game);
		printGrids(game);
		while(true){
			System.out.println("石を置く場所(数字またはgiveup)をキーボードで入力してください");
			String s = r.readLine();//文字列の入力
			System.out.println(s + " が入力されました。手番は " + game.getTurn() + " です。");

      if(game.gamestart(s)){
	       System.out.println("手番を変更します。\n");
      }
      printStatus(game);
      printGrids(game);
		}
	}
	//状態を表示する
	public static void printStatus(Othello game){
		System.out.println("checkWinner出力:" + game.getWinColor());
		System.out.println("isGameover出力:" + game.getJudge());
		System.out.println("getTurn出力：" + game.getTurn());
	}
	//テスト用に盤面を表示する
	public static void printGrids(Othello game){
		int [][] grids = game.getGrids();

		// System.out.println("getGridsテスト出力：(BLACK=1,WHITE=2,EMPTY=0)");
		// for(int i = 0 ; i < 8 ; i++){
    //   for(int j = 0 ; j < 8 ; j++){
  	// 		System.out.print(grids[i][j] + " ");
		// 	}
    //   System.out.print("\n");
		// }
	}
}
