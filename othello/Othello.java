public class Othello{

  private static final int  L       = 8, // オセロ盤の周囲の「壁」を含めた、オセロ盤の縦と横のマスの数。オセロ版は8×8だが、周囲に壁を設けると10×10になる。
                             X       =  0, // X軸の値が格納されている配列の位置。二次元配列dirを参照する際に用いる。
                             Y       =  1, // Y軸の値が格納されている配列の位置。二次元配列dirを参照する際に用いる。
                             EMPTY   =  0, // オセロ盤のマスに何も置かれていない事を表す。
                             BLACK   =  1, // オセロ盤のマスに黒石が置かれていること、黒の手番であること、黒番のプレイヤーのこと
                             WHITE   =  2, // オセロ盤のマスに白石が置かれていること、白の手番であること、白番のプレイヤーのこと
                             PUT     =  3, //表示用オセロ盤面のターンプレイヤの置ける場所
                             DRAW    =  3,// 引き分けを表す。
                             PASS    =  1, // 手番をパスするコマンド
                             GIVE_UP =  3,// 投了するコマンド
                             EXIT    =  2, // ゲームをやめるコマンド
  							 NOGIF   =  -1;

/** オセロのマスに置いた石の周囲8方向を表す座標の組。 */
private static final int[][] dir     = {{-1,-1},// 左下
                                        { 0,-1},// 下
                                        { 1,-1},// 右下
                                        { 1, 0},//右
                                        { 1, 1},//右上
                                        { 0, 1},//上
                                        {-1, 1},//左上
                                        {-1, 0}};//左

  /** オセロの盤面を座標と見做したときのx軸、y軸の値 */
  private static  int x,y;

  private static String command;

  /** オセロ盤の盤面 */
  private static int[][] board  = new int[L][L];

  //表示用オセロ盤面
  private static int[][] displayBoard  = new int[L][L];

  /* オセロの手番。黒番を先手とする。 */
  private static int  turn = BLACK;

  /* 勝者を表す。引き分けの場合もある */
  private static int victory;

  private static boolean gameJudge = false;

  private static boolean drawFlag = false;

  //animation mode W2B = 1, B2W =2 NotActivated = 0;
  private int animationMode = 0;
  private static int[][] gifBoard = new int[L][L];


  public Othello(){

    for(int i = 0 ; i < L ; i++){
      for(int j = 0 ; j < L ; j++){
        board[i][j] = EMPTY;
        displayBoard[i][j] = EMPTY;
        gifBoard[i][j] = NOGIF;
      }
    }

    board[3][3] = WHITE;
    board[4][4] = WHITE;
    board[3][4] = BLACK;
    board[4][3] = BLACK;

    displayBoard[3][3] = WHITE;
    displayBoard[4][4] = WHITE;
    displayBoard[3][4] = BLACK;
    displayBoard[4][3] = BLACK;
    displayBoard[3][2] = PUT;
    displayBoard[2][3] = PUT;
    displayBoard[5][4] = PUT;
    displayBoard[4][5] = PUT;

    Board.show();
  }

  public void turnShift(){
    Turn.shift();
    cellCheck();
  }

  /** 手番クラス */
  static class Turn{

      /** 手番を表示する。*/
      private static void show(){
          if    (turn==BLACK){print("黒のターン\n");}
          else               {print("白のターン\n");}
      }

      /** 手番を入れ替える */
      private static void shift(){
          turn = 3 - turn;
      }
  }

  static class Board{

      /** オセロ盤をコマンドプロンプト上に表示する */
    public static void show(){

      for(int i=0;i<L;i++){
        if(0<i && i<L-1){
          print("\n");
        }else {
          print("\n");
        }
        for(int j=0;j<L;j++){
          switch(board[i][j]){
            case EMPTY : {print("□"); break;}
            case WHITE : {print("〇"); break;}
            case BLACK : {print("●");       }
          }
          print(" ");
        }
        if(i<L-1){
          print(" ");
        }else{
          print("\n");
        }
      }
    }
  }

  /** ユーザーの入力を標準入力から読み込む */
  public static void gameClear(){

    for(int i = 0 ; i < L ; i++){
      for(int j = 0 ; j < L ; j++){
        board[i][j] = EMPTY;
        displayBoard[i][j] = EMPTY;
      }
    }

    board[3][3] = WHITE;
    board[4][4] = WHITE;
    board[3][4] = BLACK;
    board[4][3] = BLACK;
    displayBoard[3][3] = WHITE;
    displayBoard[4][4] = WHITE;
    displayBoard[3][4] = BLACK;
    displayBoard[4][3] = BLACK;
    displayBoard[3][2] = PUT;
    displayBoard[2][3] = PUT;
    displayBoard[5][4] = PUT;
    displayBoard[4][5] = PUT;

    Board.show();

    gameJudge = false;
    drawFlag = false;
}

  private static void setAnimationMode(int n) {
	  if(turn == BLACK) {
		  n = 2;
	  }else
		  n = 1;
  }


  public void resetAnimationXY () {
	  for(int i = 0 ; i < L ; i++){
	      for(int j = 0 ; j < L ; j++){
	        gifBoard[i][j] = NOGIF;
	      }
	    }
  }
  /** エントリポイント */
  public boolean gamestart(String s){

    Turn.show();
    switch(input(s)){
      case GIVE_UP : {
        victory = 3 - turn ; // 投了する
        show_result();
        return true;
      }case PASS : {
        Turn.shift();// 手番をPASSする
        return true;
      }default :
        if(update()){
          Board.show();// 盤面を更新
          if(judge()){         // 勝敗を判定
            show_result();  // 勝敗を表示
          }else{
          Turn.shift();
          }        // 手番を交代する
        }else{
          print("駒が置けません。別のマスを選択して下さい。\n");     // 石が置けなかったとき
          return false;
        }
        return true;
    }
  }

  /**
   * 勝敗を判定する
   * @return 勝敗が決まったとき、true
   */
  private static boolean judge(){
    int black = 0;
    int white = 0;

    // 石を数える
    for(int i=0;i<L;i++){
      for(int j=0;j<L;j++){
        if(board[i][j]==BLACK){
          black++;
        }else if(board[i][j]==WHITE){
          white++;
        }
      }
    }

        // 盤面が石で埋まっているとき、石の数の多い方を勝者とする。
    if(black+white==8*8){
      if(black<white){
        victory = WHITE;
      }else if(black==white){
        victory = DRAW;
      }else {
        victory = BLACK;
      }
      return true;
    }else if(black*white==0){
      if(black==0){
        victory = WHITE;
      }else if(white==0){
        victory = BLACK;
      }
      return true;
    }
    return false;
  }

  /**
   * 盤に石を置き、返せる相手の石を引っ繰り返す
   * @return 石を置けるとき、true
   */
  private static boolean update(){
    if(chk_cell()){
      flip();
      return true;
    }else {
      return false;
    }
  }

  /**
   * 置こうと試みたマスに石が置けるかどうか判定する。
   * @return 石が置けるとき、true
   */
  private static boolean chk_cell(){
    if(board[y][x]!=EMPTY){
      return false;
    } // 石を置こうとしたマスが空でないときは、石を置けない。
    boolean result = false;
    out:for(int i=0;i<dir.length;i++){ // 石の周囲を八方向全てチェックする。
      int j=x,k=y;
      j += dir[i][X]; // j,kに石の周囲8マスのうちいずれかひとつの座標を取る。
      k += dir[i][Y];
      if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
        if(board[k][j] == 3 - turn){ // 石を置こうとしたマスの周りに相手の石がある。
          while(true){
            j += dir[i][X]; // 更に先のマスをチェックしてみる。
            k += dir[i][Y];
            if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
              if(board[k][j]==turn){ // 相手の石のさらに先に自分の石があるので、相手の石を裏返せる。
                result = true; // 石が置けることが分かったので、trueを返してメソッドを抜ける。
                break out;
              }else if(board[k][j]== 3 - turn){ // 相手の石がまだ続いているので、更に先をチェック。
                continue;
              }
              break; // 相手の石の先が空のマスか壁なので、この方向は石を置ける状態にない。
            }else{
              break;
            }
          }
        }
      }
    }
    return result; // 判定結果を返却する。
  }

  private static boolean cellCheck(){
    boolean result = false;
    for(int raw = 0; raw < L; raw++){
      for(int column = 0; column < L; column++){
        if(board[column][raw] == EMPTY){
          for(int i=0;i<dir.length;i++){ // 石の周囲を八方向全てチェックする。
            int j = raw;
            int k = column;
            j += dir[i][X]; // j,kに石の周囲8マスのうちいずれかひとつの座標を取る。
            k += dir[i][Y];
            if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
              if(board[k][j] == 3 - turn){ // 石を置こうとしたマスの周りに相手の石がある。
                while(true){
                  j += dir[i][X]; // 更に先のマスをチェックしてみる。
                  k += dir[i][Y];
                  if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
                    if(board[k][j]==turn){ // 相手の石のさらに先に自分の石があるので、相手の石を裏返せる。
                      displayBoard[column][raw] = PUT; // 石が置けることが分かったので、trueを返してメソッドを抜ける。
                      result = true;
                      break;
                    }else if(board[k][j]== 3 - turn){ // 相手の石がまだ続いているので、更に先をチェック。
                      continue;
                    }else{
                      break;
                    }
                  }else{
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
    return result;

  }

  /** 相手の石を引っ繰り返す */
  private static void flip(){
    board[y][x] = turn;
    for(int i=0;i<dir.length;i++){
      int j=x,k=y;
      j += dir[i][X];
      k += dir[i][Y];
      if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
        if(board[k][j] == 3 - turn){
          out:while(true){
            j += dir[i][X];
            k += dir[i][Y];
            if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
              if(board[k][j]==turn){
                while(true){
                  int n = 0;

                  j -= dir[i][X];
                  k -= dir[i][Y];
                  if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
                    if(board[k][j]==turn){
                      break out;
                    }
                    board[k][j] = turn;

                  //  setAnimationMode(gifBoard[j][k]);
                   if(turn == BLACK) {
                    	gifBoard[k][j] = 2;
              	  }else {
              		  	gifBoard[k][j] = 1;
              	  }

                  }else{
                    break;
                  }
                }
              }else if(board[k][j]== 3 - turn){
                continue;
              }
              break;
            }else{
              break;
            }
          }
        }
      }
    }
  }

  /** 勝敗の結果をコマンドプロンプト上に表示する */
  private static void show_result(){
    String ret;
    if   (victory == DRAW) {
      ret = "\n   ---   引き分け   ---   \n" ;
      drawFlag = true;
      gameJudge = true;
    }else{
      String v;
      if    (victory == BLACK){
        v = "BLACK" ;
      }else{
        v = "WHITE" ;
      }
      ret = v + "の勝ち" ;
    }
      print(ret);
      Board.show();
      gameJudge = true;
  }

  /** 文字列出力のための簡易メソッド */
  private static void print(String s,Object... i){System.out.printf(s,i);}
  /** 数値を文字列出力するための簡易メソッド */
  private static void print(int i){System.out.print(i);}
  /** 標準入力からユーザーの入力を読み込む */
  private static int input(String s){
    int ret = 0;

    if(s.equals("pass")){
      ret = PASS ;
    }else if(s.equals("exit")){
      ret = EXIT ;
    }else if(s.equals("giveup")){
      ret = GIVE_UP ;
    }else{
      int number = Integer.parseInt(s);
      y = number / 8;
      x = number % 8;
    }
    return ret;
  }

  public boolean setGrids(){
    for(int i=0;i<L;i++){
      for(int j=0;j<L;j++){
        displayBoard[i][j] = board[i][j];
      }
    }
    return cellCheck();
  }


  public int[][] getDisplayGrids(){
    return displayBoard;
  }
  public int[][] getGrids(){
    return board;
  }

  public String getTurn(){

    if (turn==BLACK){
      return "Black";
    }else{
      return "White";
    }
  }
  public String getWinColor(){

    if    (victory == BLACK){
      return "Black";
    }else{
      return "White";
    }

  }
  public boolean getDrawFlag(){
    return drawFlag;
  }
  public boolean getJudge(){
    return gameJudge;
  }

public int[][] getGifBoard() {
	// TODO Auto-generated method stub
	return gifBoard;
}
}
