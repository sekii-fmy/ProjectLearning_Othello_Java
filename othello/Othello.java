public class Othello{

  private static final int  L       = 8, // �I�Z���Ղ̎��͂́u�ǁv���܂߂��A�I�Z���Ղ̏c�Ɖ��̃}�X�̐��B�I�Z���ł�8�~8�����A���͂ɕǂ�݂����10�~10�ɂȂ�B
                             X       =  0, // X���̒l���i�[����Ă���z��̈ʒu�B�񎟌��z��dir���Q�Ƃ���ۂɗp����B
                             Y       =  1, // Y���̒l���i�[����Ă���z��̈ʒu�B�񎟌��z��dir���Q�Ƃ���ۂɗp����B
                             EMPTY   =  0, // �I�Z���Ղ̃}�X�ɉ����u����Ă��Ȃ�����\���B
                             BLACK   =  1, // �I�Z���Ղ̃}�X�ɍ��΂��u����Ă��邱�ƁA���̎�Ԃł��邱�ƁA���Ԃ̃v���C���[�̂���
                             WHITE   =  2, // �I�Z���Ղ̃}�X�ɔ��΂��u����Ă��邱�ƁA���̎�Ԃł��邱�ƁA���Ԃ̃v���C���[�̂���
                             PUT     =  3, //�\���p�I�Z���Ֆʂ̃^�[���v���C���̒u����ꏊ
                             DRAW    =  3,// ����������\���B
                             PASS    =  1, // ��Ԃ��p�X����R�}���h
                             GIVE_UP =  3,// ��������R�}���h
                             EXIT    =  2, // �Q�[������߂�R�}���h
  							 NOGIF   =  -1;

/** �I�Z���̃}�X�ɒu�����΂̎���8������\�����W�̑g�B */
private static final int[][] dir     = {{-1,-1},// ����
                                        { 0,-1},// ��
                                        { 1,-1},// �E��
                                        { 1, 0},//�E
                                        { 1, 1},//�E��
                                        { 0, 1},//��
                                        {-1, 1},//����
                                        {-1, 0}};//��

  /** �I�Z���̔Ֆʂ����W�ƌ��􂵂��Ƃ���x���Ay���̒l */
  private static  int x,y;

  private static String command;

  /** �I�Z���Ղ̔Ֆ� */
  private static int[][] board  = new int[L][L];

  //�\���p�I�Z���Ֆ�
  private static int[][] displayBoard  = new int[L][L];

  /* �I�Z���̎�ԁB���Ԃ���Ƃ���B */
  private static int  turn = BLACK;

  /* ���҂�\���B���������̏ꍇ������ */
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

  /** ��ԃN���X */
  static class Turn{

      /** ��Ԃ�\������B*/
      private static void show(){
          if    (turn==BLACK){print("���̃^�[��\n");}
          else               {print("���̃^�[��\n");}
      }

      /** ��Ԃ����ւ��� */
      private static void shift(){
          turn = 3 - turn;
      }
  }

  static class Board{

      /** �I�Z���Ղ��R�}���h�v�����v�g��ɕ\������ */
    public static void show(){

      for(int i=0;i<L;i++){
        if(0<i && i<L-1){
          print("\n");
        }else {
          print("\n");
        }
        for(int j=0;j<L;j++){
          switch(board[i][j]){
            case EMPTY : {print("��"); break;}
            case WHITE : {print("�Z"); break;}
            case BLACK : {print("��");       }
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

  /** ���[�U�[�̓��͂�W�����͂���ǂݍ��� */
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
  /** �G���g���|�C���g */
  public boolean gamestart(String s){

    Turn.show();
    switch(input(s)){
      case GIVE_UP : {
        victory = 3 - turn ; // ��������
        show_result();
        return true;
      }case PASS : {
        Turn.shift();// ��Ԃ�PASS����
        return true;
      }default :
        if(update()){
          Board.show();// �Ֆʂ��X�V
          if(judge()){         // ���s�𔻒�
            show_result();  // ���s��\��
          }else{
          Turn.shift();
          }        // ��Ԃ���シ��
        }else{
          print("��u���܂���B�ʂ̃}�X��I�����ĉ������B\n");     // �΂��u���Ȃ������Ƃ�
          return false;
        }
        return true;
    }
  }

  /**
   * ���s�𔻒肷��
   * @return ���s�����܂����Ƃ��Atrue
   */
  private static boolean judge(){
    int black = 0;
    int white = 0;

    // �΂𐔂���
    for(int i=0;i<L;i++){
      for(int j=0;j<L;j++){
        if(board[i][j]==BLACK){
          black++;
        }else if(board[i][j]==WHITE){
          white++;
        }
      }
    }

        // �Ֆʂ��΂Ŗ��܂��Ă���Ƃ��A�΂̐��̑����������҂Ƃ���B
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
   * �Ղɐ΂�u���A�Ԃ��鑊��̐΂������J��Ԃ�
   * @return �΂�u����Ƃ��Atrue
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
   * �u�����Ǝ��݂��}�X�ɐ΂��u���邩�ǂ������肷��B
   * @return �΂��u����Ƃ��Atrue
   */
  private static boolean chk_cell(){
    if(board[y][x]!=EMPTY){
      return false;
    } // �΂�u�����Ƃ����}�X����łȂ��Ƃ��́A�΂�u���Ȃ��B
    boolean result = false;
    out:for(int i=0;i<dir.length;i++){ // �΂̎��͂𔪕����S�ă`�F�b�N����B
      int j=x,k=y;
      j += dir[i][X]; // j,k�ɐ΂̎���8�}�X�̂��������ꂩ�ЂƂ̍��W�����B
      k += dir[i][Y];
      if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
        if(board[k][j] == 3 - turn){ // �΂�u�����Ƃ����}�X�̎���ɑ���̐΂�����B
          while(true){
            j += dir[i][X]; // �X�ɐ�̃}�X���`�F�b�N���Ă݂�B
            k += dir[i][Y];
            if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
              if(board[k][j]==turn){ // ����̐΂̂���ɐ�Ɏ����̐΂�����̂ŁA����̐΂𗠕Ԃ���B
                result = true; // �΂��u���邱�Ƃ����������̂ŁAtrue��Ԃ��ă��\�b�h�𔲂���B
                break out;
              }else if(board[k][j]== 3 - turn){ // ����̐΂��܂������Ă���̂ŁA�X�ɐ���`�F�b�N�B
                continue;
              }
              break; // ����̐΂̐悪��̃}�X���ǂȂ̂ŁA���̕����͐΂�u�����ԂɂȂ��B
            }else{
              break;
            }
          }
        }
      }
    }
    return result; // ���茋�ʂ�ԋp����B
  }

  private static boolean cellCheck(){
    boolean result = false;
    for(int raw = 0; raw < L; raw++){
      for(int column = 0; column < L; column++){
        if(board[column][raw] == EMPTY){
          for(int i=0;i<dir.length;i++){ // �΂̎��͂𔪕����S�ă`�F�b�N����B
            int j = raw;
            int k = column;
            j += dir[i][X]; // j,k�ɐ΂̎���8�}�X�̂��������ꂩ�ЂƂ̍��W�����B
            k += dir[i][Y];
            if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
              if(board[k][j] == 3 - turn){ // �΂�u�����Ƃ����}�X�̎���ɑ���̐΂�����B
                while(true){
                  j += dir[i][X]; // �X�ɐ�̃}�X���`�F�b�N���Ă݂�B
                  k += dir[i][Y];
                  if(j >= 0 && k >= 0 && j <= 7 && k <= 7){
                    if(board[k][j]==turn){ // ����̐΂̂���ɐ�Ɏ����̐΂�����̂ŁA����̐΂𗠕Ԃ���B
                      displayBoard[column][raw] = PUT; // �΂��u���邱�Ƃ����������̂ŁAtrue��Ԃ��ă��\�b�h�𔲂���B
                      result = true;
                      break;
                    }else if(board[k][j]== 3 - turn){ // ����̐΂��܂������Ă���̂ŁA�X�ɐ���`�F�b�N�B
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

  /** ����̐΂������J��Ԃ� */
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

  /** ���s�̌��ʂ��R�}���h�v�����v�g��ɕ\������ */
  private static void show_result(){
    String ret;
    if   (victory == DRAW) {
      ret = "\n   ---   ��������   ---   \n" ;
      drawFlag = true;
      gameJudge = true;
    }else{
      String v;
      if    (victory == BLACK){
        v = "BLACK" ;
      }else{
        v = "WHITE" ;
      }
      ret = v + "�̏���" ;
    }
      print(ret);
      Board.show();
      gameJudge = true;
  }

  /** ������o�͂̂��߂̊ȈՃ��\�b�h */
  private static void print(String s,Object... i){System.out.printf(s,i);}
  /** ���l�𕶎���o�͂��邽�߂̊ȈՃ��\�b�h */
  private static void print(int i){System.out.print(i);}
  /** �W�����͂��烆�[�U�[�̓��͂�ǂݍ��� */
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
