import java.io.*;

public class ClientDriver{
  public static void main(String [] args) throws Exception{
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in), 1);
    Player player = new Player(); //�v���C���I�u�W�F�N�g�̗p��(���O�C��)
		Othello game = new Othello(); //�I�Z���I�u�W�F�N�g��p��
		Client oclient = new Client(game, player); //�����Ƃ��ăI�Z���I�u�W�F�N�g��n��
		oclient.setVisible(true);
    System.out.println("�e�X�g�p�T�[�o�ɐڑ����܂�");
    oclient.connectServer("localhost", 10011);
    System.out.println("��M�p�e�X�g���b�Z�[�W����͂��Ă�������");
    while(true){
      String s = r.readLine();
      String t = r.readLine();
      oclient.receiveMessage(s,t);
      System.out.println("�e�X�g���b�Z�[�W�u" + s + t + "�v����M���܂���");
      System.out.println("�e�X�g������s������A��M�p�e�X�g���b�Z�[�W����͂��Ă�������");
    }
  }
}
