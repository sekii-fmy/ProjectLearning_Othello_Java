import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Client extends JFrame implements ActionListener {
	private JButton buttonArray[];//�I�Z���՗p�̃{�^���z��
	private JButton stop,rematch,ranking,reject; //��~�A�X�L�b�v�p�{�^��
	private JList<String> rankingList;	//���ݓ������̃`���b�g���[���̃��[�U�[
	private JLabel colorLabel1,colorLabel2;
	private JLabel turnLabel1,turnLabel2; // ��ԕ\���p���x��
	private JLabel myLabel = new JLabel("");
	private JLabel yourLabel;//�����A����̖��O���x��
	private JLabel statusLabel; //���ݏ󋵂̃��x��
	private JLabel timeLabel; //���ԃJ�E���g���x��
	private JLabel loginLabel;
	private Container container; // �R���e�i
	private ImageIcon blackIcon, whiteIcon, boardIcon,highlightIcon,turnWhite,turnBlack,turnBar; //�A�C�R��
	private ImageIcon gameIcon,titleIcon,loginIcon,rankingIcon,rankingIcon2,resignIcon,backIcon,backIcon2;
	private PrintWriter out;//�f�[�^���M�p�I�u�W�F�N�g
	private InputStreamReader sisr; //��M�f�[�^�p�����X�g���[��
	private BufferedReader br; //�����X�g���[���p�̃o�b�t�@
	private Receiver receiver; //�f�[�^��M�p�I�u�W�F�N�g
	private Othello game; //Othello�I�u�W�F�N�g
	private Player player; //Player�I�u�W�F�N�g
	private int board[][];
	private int row = 8;
	private String myColor;
	private boolean switchFlag=true;
	private boolean disConnectFlag=true;
	private	int timeCount = 0 ;
	private	int battleCount = 0;
	private boolean startFlag=false;
	private JTextField text1;
	private JPasswordField text2;
	private int myIndex;
	private Font font;
	private int rate2;
	private ImageIcon gifIcon1 = new ImageIcon("W2B.gif");
	private ImageIcon gifIcon2 = new ImageIcon("B2W.gif");
	private ImageIcon newI = new ImageIcon("whiteNew.jpg");
	private ImageIcon newI2 = new ImageIcon("blackNew.jpg");
	private BufferedImage catPic;
	// �R���X�g���N�^
	public Client(Othello game, Player player) { //Othello�I�u�W�F�N�g��Player�I�u�W�F�N�g�������Ƃ���
		try{
			this.game = game; //������Othello�I�u�W�F�N�g��n��
			this.player = player; //������Player�I�u�W�F�N�g��n��
			this.board = game.getGrids(); //getGrid���\�b�h�ɂ��ǖʏ����擾

			//�A�C�R���ݒ�(�摜�t�@�C�����A�C�R���Ƃ��Ďg��)
			whiteIcon = new ImageIcon("WhiteNew.jpg");
			blackIcon = new ImageIcon("BlackNew.jpg");
			boardIcon = new ImageIcon("GreenFrame.jpg");
			highlightIcon = new ImageIcon("highlight.jpg");
			gameIcon = new ImageIcon("game.jpg");
			titleIcon = new ImageIcon("title.jpg");
			loginIcon = new ImageIcon("login.jpg");
			rankingIcon = new ImageIcon("ranking.jpg");
			rankingIcon2 = new ImageIcon("ranking2.jpg");
			resignIcon = new ImageIcon("resign.jpg");
			backIcon = new ImageIcon("back.jpg");
			backIcon2 = new ImageIcon("back2.jpg");
			turnBlack = new ImageIcon("turnBlack.jpg");
			turnWhite = new ImageIcon("turnWhite.jpg");
			turnBar = new ImageIcon("turnBar.jpg");
			catPic = ImageIO.read(new File("cat.png"));
			font = Font.createFont(Font.TRUETYPE_FONT,new File("ackaisyo.ttf"));

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����ꍇ�̏���
			setTitle("�ԕt�I�Z���Q�[��");//�E�B���h�E�̃^�C�g��
			setSize(row * 80 , row * 80 + 200);//�E�B���h�E�̃T�C�Y��ݒ�
			setResizable(false);
			container = getContentPane();//�t���[���̃y�C�����擾
			setVisible(true);

			JPanel mainPanel = new JPanel();
			mainPanel.setBackground(Color.white);
			mainPanel.setLayout(null);

			BufferedImage birdPic = ImageIO.read(new File("bird.jpg"));
			JLabel birdLabel = new JLabel(new ImageIcon(birdPic));
			birdLabel.setBounds(50, 30, 601, 800);

			BufferedImage othelloPic = ImageIO.read(new File("title.jpg"));
			JLabel titleLabel = new JLabel(new ImageIcon(othelloPic));
			titleLabel.setBounds(20,100,300,108);


			JButton loginButton = new JButton(loginIcon);
			loginButton.setActionCommand("login");
			loginButton.addActionListener(this);
			loginButton.setBounds(105,350,429,114);

			JButton newButton = new JButton(resignIcon);
			newButton.setActionCommand("new");
			newButton.addActionListener(this);
			newButton.setBounds(105,550,429,114);

			mainPanel.add(loginButton);
			mainPanel.add(newButton);
			mainPanel.add(titleLabel);
			mainPanel.add(birdLabel);

			container.add(mainPanel);
		}catch(Exception e){
		}
	}

	public void setcolorLabel1(){
		//�F�\���p���x���̕ύX
		myColor = player.getColor();
		if(myColor.equals("Black")){
			colorLabel1.setIcon(turnBlack);
			colorLabel2.setIcon(turnWhite);
		}else{
			colorLabel1.setIcon(turnWhite);
			colorLabel2.setIcon(turnBlack);
		}
	}

	public void setTurnLabel(){
		String myColor = player.getColor();
		if(myColor.equals(game.getTurn())){
			turnLabel1.setEnabled(true);
			turnLabel2.setEnabled(false);
		}else{
			turnLabel1.setEnabled(false);
			turnLabel2.setEnabled(true);
		}
	}


	// ���\�b�h
	public void connectServer(String ipAddress, int port){	// �T�[�o�ɐڑ�
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); //�T�[�o(ipAddress, port)�ɐڑ�
			out = new PrintWriter(socket.getOutputStream(), true); //�f�[�^���M�p�I�u�W�F�N�g�̗p��
			receiver = new Receiver(socket); //��M�p�I�u�W�F�N�g�̏���

			receiver.start();

		} catch (UnknownHostException e) {
			System.err.println("�z�X�g��IP�A�h���X������ł��܂���: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("�T�[�o�ڑ����ɃG���[���������܂���: " + e);
			System.exit(-1);
		}
	}

	public void sendMessage(String msg){	// �T�[�o�ɑ�����𑗐M
		out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
		out.flush();//���M�f�[�^�𑗂�
	}

	// �f�[�^��M�p�X���b�h(�����N���X)
	class Receiver extends Thread {
		private boolean victory;
		private Timer timer1;
		private Timer timer2;
		private Timer timer3;
		private Timer timer4;

		// �����N���XReceiver�̃R���X�g���N�^
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //��M�����o�C�g�f�[�^�𕶎��X�g���[����
				br = new BufferedReader(sisr);//�����X�g���[�����o�b�t�@�����O����
			} catch (IOException e) {
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}
		}
		// �����N���X Receiver�̃��\�b�h
		public void run(){
			try{

				while(true){		//�����񓙂̎�M
					String line = br.readLine();
					String[] msg = line.split(" ", 2);
					String msgName = msg[0];
					String msgValue = (msg.length < 2 ? "" : msg[1]);

					receiveMessage(msgName, msgValue);

					if(line.equals("OK")){
						homePanel();
					}else if(line.equals("NO1")){
						loginLabel.setText("ID�܂��̓p�X���[�h���Ⴄ�A�܂��͊��Ƀ��O�C�����ł��B");
					}else if(line.equals("NO2")){
						loginLabel.setText("���ɓ������[�U���̐l�����݂��܂��B");
					}

					if(msgName.equals("gamematch")){
						gameStart();
					}
				}

			}catch(IOException e){
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}

		}


		public void gameStart(){

			try{

				battleCount = 0;

				out:while(true){

					stop.setEnabled(true);
					victory = false;
					timer1 = new Timer(false);
					timer2 = new Timer(false);
					timer3 = new Timer(false);
					timer4 = new Timer(false);
					timeReset();

					TimerTask task1 = new TimerTask(){
						@Override
							public void run(){
								if(battleCount==0){
									sendMessage("Waiting");
								}else{
									timer1.cancel();
								}
							}
					};
					timer1.schedule(task1,0,500);

					TimerTask task2 = new TimerTask(){
						@Override
							public void run(){
								timeCount++;
								timeLabel.setText("�������ԁF" + timeCount + "/60");
								if(timeCount > 60 && myColor.equals(game.getTurn())){
									timeLabel.setText("���Ԑ؂�");
									rate2 = 0;
									player.addDisConnectCount();
									sendMessage("command" + " " + "giveup");
									displayClear();
									sendMessage("reject");
									homePanel();
									timer3.cancel();
									timer4.cancel();
								}
							}
					};

					TimerTask task3 = new TimerTask(){
						@Override
							public void run(){
								timeCount++;
								timeLabel.setText("�������ԁF" + timeCount + "/30");
								if(timeCount > 30){
									displayClear();
									sendMessage("reject");
									homePanel();
									timer3.cancel();
									timer4.cancel();
								}
							}
					};

					TimerTask task4 = new TimerTask(){
						@Override
							public void run(){
								timeCount++;
								timeLabel.setText("�������ԁF" + timeCount + "/30");
								if(timeCount > 30){
									displayClear();
									sendMessage("reject");
									homePanel();
									timer3.cancel();
									timer4.cancel();
								}
							}
					};


					while(true){		//�����񓙂̎�M
						String line = br.readLine();
						String[] msg = line.split(" ", 2);
						String msgName = msg[0];
						String msgValue = (msg.length < 2 ? "" : msg[1]);

						if(msgName.equals("Matched")){
							setTurnLabel();//��ԃ��x���̍X�V
							battleCount++;
							startFlag = true;
							reject.setEnabled(false);
							timer2.schedule(task2,0,1000);
							statusLabel.setText("�Q�[����");
						}else{
							receiveMessage(msgName, msgValue);
						}

						if(game.getJudge()){
							reject.setEnabled(true);
							stop.setEnabled(false);
							timer2.cancel();
							if(disConnectFlag){

								if(game.getDrawFlag()){
									player.addDrawCount();
									statusLabel.setText("���������ł�");
									reject.setEnabled(true);
									rematch.setEnabled(true);
									timer3.schedule(task3,0,1000);
								}else if(game.getWinColor().equals(player.getColor())){
									statusLabel.setText("���Ȃ��͏��҂ł�");
									victory = true;
								}else{
									statusLabel.setText("���Ȃ��͔s�҂ł�");
									reject.setEnabled(true);
									rematch.setEnabled(true);
									rematch.setText("�Đ��\������");
									timer3.schedule(task3,0,1000);
								}

							}else{
								statusLabel.setText("����̐ؒf�����ł�");
							}

							sendMessage("gameEnd");
							break;
						}

					}

					player.addRate(game.getWinColor().equals(player.getColor()),rate2);
					sendMessage("dataSave" + " " + player.getStatus());
					String msg = "<html>" + player.getName() + "�@���[�g" +"("+ player.getRate() +")"+ "<br>" + "�@����:" + player.getWinCount() + "�@" + "����:" + player.getLoseCount() +"<html>";
					myLabel.setText(msg);
					sendMessage("userUpData" + " " + msg);

					if(battleCount > 2){
						statusLabel.setText("3�킪�I�����܂���");
						rematch.setEnabled(false);
						timer3.cancel();
					}

					while(true){		//�����񓙂̎�M
						String line2 = br.readLine();
						String[] msg2 = line2.split(" ", 0);
						String msgName2 = msg2[0];
						String msgValue2 = (msg2.length < 2 ? "" : msg2[1]);

						if(msgName2.equals("ReMatchSuccess")){ //�Đ���s��
							timer4.cancel();
							sendMessage("ReMatchSuccess");
							displayClear();
							updateDisp();
							break;
						}else if(msgName2.equals("loserReMatch")){ //���҂ɍĐ�̐\��
							timeReset();
							timer4.schedule(task4,0,1000);
							statusLabel.setText("�Đ�̐\�����݂�����܂���");
							reject.setEnabled(true);
							rematch.setText("�Đ�����F����");
							rematch.setEnabled(true);
						}else if(msgName2.equals("reMatchReceive")){
							timer3.cancel();
							timeReset();
							timer4.schedule(task4,0,1000);
							statusLabel.setText("�Đ��\�����݂܂���");
							reject.setEnabled(false);
						}else if(msgName2.equals("reject")){
							if(battleCount > 2){
							}else if(victory){
								statusLabel.setText("�Đ�̐\�����݂͂���܂���ł���");
							}else{
								statusLabel.setText("�Đ�̐\�����݂����ۂ���܂���");
								reject.setEnabled(true);
							}
							startFlag = false;
							sendMessage("finish");
							timer3.cancel();
							timer4.cancel();
							break out;
						}else{
							receiveMessage(msgName2,msgValue2);
						}

					}
				}

			}catch(IOException e){
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}
		}
	}

	public void displayClear(){
		disConnectFlag = true;
		game.gameClear();
		rematch.setEnabled(false);
		rematch.setText("");
		statusLabel.setText("");
		timeLabel.setText("");
		yourLabel.setText("");
	}

	public void timeReset(){
		timeCount = 0;
	}

	public void receiveMessage(String name,String value){	// ���b�Z�[�W�̎�M

		if(name.equals("setColor")){
			player.setColor(value);
			if(!game.getTurn().equals("Black")){
				game.turnShift();
			}
			setcolorLabel1();
			updateDisp();

		}else if(name.equals("setPlayer1Data")){
			String[] data = value.split(",", 0);
			player.setNamePass(data[0],data[1]);
			player.setWinCount(Integer.parseInt(data[2]));
			player.setLoseCount(Integer.parseInt(data[3]));
			player.setDrawCount(Integer.parseInt(data[4]));
			player.setResignCount(Integer.parseInt(data[5]));
			player.setDisConnectedCount(Integer.parseInt(data[6]));
			player.setRate(Integer.parseInt(data[7]));
			String msg = "<html>" + player.getName() + "�@���[�g" +"("+ player.getRate()+")" + "<br>" + " ����:" + player.getWinCount() + "�@" + "����:" + player.getLoseCount() +"<html>";
			myLabel.setText(msg);
			sendMessage("setPlayer1Data" + " " + msg);
		}else if(name.equals("setPlayer2Data")){
			String[] data = value.split(",", 0);
			yourLabel.setText(data[0]);
			rate2 = Integer.parseInt(data[1]);
			statusLabel.setText("�Q�[����");
		}else if(name.equals("Matching")){
			if(switchFlag){
				statusLabel.setText("�}�b�`���O��");
				switchFlag = false;
			}else{
				statusLabel.setText("");
				switchFlag = true;
			}
		}else if(name.equals("DisConnected")){
			disConnectFlag = false;
			if(value.equals(game.getTurn())){
				game.gamestart("giveup");
			}else{
				game.turnShift();
				game.gamestart("giveup");
			}
			updateDisp();//�ǖʍX�V
		}else if(name.equals("command")){
			if(game.gamestart(value)){	//�Q�[������
				timeReset();
			}

			if(!game.setGrids()){
				game.turnShift();
			}
			setTurnLabel();//��ԃ��x���̍X�V
			updateDisp();//�ǖʍX�V

		}else if(name.equals("userUpData")){
			yourLabel.setText(value);
		}else if(name.equals("ranking")){

			String[] rankingData = value.split("/",-1);
			rankingList.setListData(getRanking(rankingData));
			rankingList.setCellRenderer(new StripedListCellRenderer());
			rankingList.ensureIndexIsVisible(getRankingIndex()+10);

		}
	}

	public class StripedListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);
				label.setFont(font.deriveFont(22f));

        if(!(index % 4 == 0 || index % 4 == 1)){
					if (list.isSelectedIndex(index)) {
							// �I���s�̓f�t�H���g�̐F
							label.setBackground(list.getSelectionBackground());
					} else {
							// �I�����ĂȂ��s�͔�����
							label.setBackground(new Color(220, 220, 220));
					}
				}

				// ��s�𔖂��ɂ���
				if (index == getRankingIndex() || index == (getRankingIndex()+1) ){
						if (list.isSelectedIndex(index)) {
								// �I���s�̓f�t�H���g�̐F
								label.setBackground(list.getSelectionBackground());
						} else {
								// �I�����ĂȂ��s�͔�����
								label.setBackground(new Color(240, 190, 190));
						}
				}
        return label;
    }

	}

	public int getRankingIndex(){
		return myIndex;
	}

	public void setRankingIndex(int myIndex){
		this.myIndex = myIndex;
	}


	public String[] getRanking(String[] rankingData){

		String[][] iddata = new String[rankingData.length][3];
		String myData="";

		for(int i = 0; i < rankingData.length ; i++){
			String[] data = rankingData[i].split(",",-1);
			String id = " ID�F" + data[0] + " ���[�g�F" + data[7];
			String status = "    ���F" + data[2] + " ���F" + data[3] + " " + "�����F" + data[4] +
			  " " +"�����F" + data[5] + " �ؒf�F" + data[6] ;
			if(player.getName().equals(data[0])){
				myData = id;
			}
			iddata[i][1] = data[7];
			iddata[i][0] = id;
			iddata[i][2] = status;
		}
		ArrayList<User> memberList = new ArrayList<User>();
		for(int i = 0; i < rankingData.length ; i++){
			memberList.add(new User(Integer.parseInt(iddata[i][1]), iddata[i][0],iddata[i][2]));
		}
		Collections.sort(memberList, new RateComparator());
		String[] rankingDataList = new String[memberList.size()*2];
		for(int i=0;i<memberList.size();i++){
			if(myData.equals(memberList.get(i).idData)){
				setRankingIndex(i*2);
			}
			rankingDataList[i*2] = String.valueOf(i+1) + "��" + memberList.get(i).idData;
			rankingDataList[i*2+1] = memberList.get(i).status;
		}
		return rankingDataList;
	}

	public class User {
		public int rate;
		public String idData;
		public String status;

		public User(int rate, String idData,String status){
			this.rate = rate;
			this.idData = idData;
			this.status = status;
		}
	}
	public class RateComparator implements Comparator<User> {
		@Override
		public int compare(User p1, User p2) {
			return p1.rate > p2.rate ? -1 : 1;
		}
	}


	public void updateDisp(){	// ��ʂ��X�V����


		if(myColor.equals(game.getTurn())){
			this.board = game.getDisplayGrids(); //getGrid���\�b�h�ɂ��ǖʏ����擾
			}else{
				this.board = game.getGrids();
			}
		int animationBoard[][]= game.getGifBoard();

        System.out.println("game.turn: " + game.getTurn());

        Timer timer1[] = new Timer[64];
        Timer timer2[] = new Timer[64];
        int n = 0;
        int m = 0;
		for(int i = 0 ; i < row ; i++){
			for(int j = 0 ; j < row ; j++){

				int arrayNumber = i*row + j;
				if(board[i][j]==1){
					if(animationBoard[i][j] == 2) {

			            buttonArray[arrayNumber].setIcon(gifIcon1);

			     //       Timer timer1;
			    		timer1[n] = new Timer();
			    		timer1[n].schedule(new TimerTask() {
			    			  @Override
			    			  public void run() {

			    					  buttonArray[arrayNumber].setIcon(newI2);
			    			  }
			    			}, 900);
			    			n++;
					}else {
			            buttonArray[arrayNumber].setIcon(blackIcon);
					}
				}else if(board[i][j]==2){
					if(animationBoard[i][j] == 1) {

			            buttonArray[arrayNumber].setIcon(gifIcon2);


			       //     Timer timer2;
			    		timer2[m] = new Timer();
			    		timer2[m].schedule(new TimerTask() {
			    			  @Override
			    			  public void run() {

			    					  buttonArray[arrayNumber].setIcon(newI);
			    			  }
			    			}, 1000);
			    			m++;
					}else {
			            buttonArray[arrayNumber].setIcon(whiteIcon);
					}
				}else if(board[i][j]==3){
					buttonArray[arrayNumber].setIcon(highlightIcon);//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
				}else if(board[i][j]==0){
					buttonArray[arrayNumber].setIcon(boardIcon);//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
				}

			}
		}
		game.resetAnimationXY();

	}

  	//�}�E�X�N���b�N���̏���
	@Override
	public void actionPerformed(ActionEvent e) {


		String command = e.getActionCommand();//�{�^���̖��O�����o��

		switch (command) {
			case "new":
				newPanel();
				break;

			case "login":
				loginPanel();
				break;

			case "loginAgree":
				login();
				break;

			case "newAgree":
				newLogin();
				break;

			case "home":
				homePanel();
				break;

			case "main":
				mainPanel();
				break;

			case "game":
				gamePanel();
				sendMessage("gamematch");
				break;

			case "ranking":
				rankingPanel();
				sendMessage("ranking");
				break;

			default:
				break;
		}

		if(command.equals("reject") && reject.isEnabled()){
			if(game.getJudge()){
				displayClear();
				sendMessage("reject");
				homePanel();
				return;
			}else{
				sendMessage("finish");
				homePanel();
				return;
			}

		}else if(command.equals("rematch") && rematch.isEnabled()){
			if(game.getJudge()){
				if(game.getWinColor().equals(player.getColor())){
					sendMessage("rematch" + " " + "winner");
				}else{
					sendMessage("rematch" + " " + "loser");
				}
				rematch.setEnabled(false);
				rematch.setText("");
				return;
			}
		}

		if(startFlag && myColor.equals(game.getTurn()) && !game.getJudge()){
			if(command.equals("giveup")){
				player.addResignCount();
			}
			sendMessage("command" + " " + command); //�e�X�g�p�Ƀ��b�Z�[�W�𑗐M
		}

	}

	public void login(){

					String name = text1.getText();
					String pass = text2.getText();

				if(name.length() < 13 && pass.length() < 13 && name.indexOf("/") == -1 && name.indexOf(",") == -1 && pass.indexOf(",") == -1 && name.indexOf(" ") == -1 && pass.indexOf(" ") == -1 && !name.isEmpty() && !pass.isEmpty()){

					sendMessage("setNamePass1" + " " + name + "," + pass );

				}else{
					loginLabel.setText("[,]�A[��]���܂߂��ɁA12�����ȉ��œ��͂��Ă��������B");
				}

	}

	public void newLogin(){

					String name = text1.getText();
					String pass = text2.getText();

				if(name.length() < 13 && pass.length() < 13 && name.indexOf("/") == -1 && name.indexOf(",") == -1 && pass.indexOf(",") == -1 && name.indexOf(" ") == -1 && pass.indexOf(" ") == -1 && !name.isEmpty() && !pass.isEmpty()){

					sendMessage("setNamePass2" + " " + name + "," + pass);

				}else{
					loginLabel.setText("[,]�A[��]���܂߂���12�����ȉ��œ��͂��Ă��������B");
				}

	}

	public void homePanel(){

		JLabel catLabel = new JLabel(new ImageIcon(catPic));
		catLabel.setBounds(30, 30, 350, 700);

		JPanel homePanel = new JPanel();
		homePanel.setBackground(Color.white);
		homePanel.setLayout(null);

		JButton rankingButton = new JButton(rankingIcon);
		rankingButton.setActionCommand("ranking");
		rankingButton.addActionListener(this);
		rankingButton.setBounds(300,155,266,134);

		JButton gameButton = new JButton(gameIcon);
		gameButton.setActionCommand("game");
		gameButton.addActionListener(this);
		gameButton.setBounds(300,455,266,134);


		homePanel.add(rankingButton);
		homePanel.add(gameButton);
		homePanel.add(catLabel);

		container.removeAll();
		container.add(homePanel);
		setVisible(true);

	}

	public void newPanel(){

	JPanel newPanel = new JPanel();
	newPanel.setBackground(Color.white);
	newPanel.setLayout(null);

	JLabel titleLabel = new JLabel(titleIcon);
	titleLabel.setBounds(20,100,601,142);

	loginLabel = new JLabel();
	loginLabel.setFont(font.deriveFont(18f));
	loginLabel.setBounds(100,250,600,100);
	loginLabel.setForeground(Color.red);


	JPanel namePanel = new JPanel();
	namePanel.setLayout(new FlowLayout());
	JLabel nameLabel = new JLabel("Username:", SwingConstants.LEFT);
	nameLabel.setFont(font.deriveFont(35f));
	nameLabel.setBounds(80,280,300,100);
	text1 = new JTextField();
	text1.setFont(font.deriveFont(24f));
	text1.setBounds(240,315,300,32);

	JPanel passPanel = new JPanel();
	JLabel passLabel = new JLabel("Password:", SwingConstants.LEFT);
	passLabel.setFont(font.deriveFont(35f));
	passLabel.setBounds(80,330,300,100);
	text2 = new JPasswordField();
	text2.setFont(new Font("�l�r �S�V�b�N", Font.BOLD, 24));
	text2.setBounds(240,365,300,32);

	JButton loginButton = new JButton(resignIcon);
	loginButton.setActionCommand("newAgree");
	loginButton.addActionListener(this);
	loginButton.setBounds(105,475,429,114);

	JButton backButton = new JButton(backIcon);
	backButton.setActionCommand("main");
	backButton.addActionListener(this);
	backButton.setBounds(105,650,429,114);

	newPanel.add(loginLabel);
	newPanel.add(titleLabel);
	newPanel.add(nameLabel);
	newPanel.add(text1);
	newPanel.add(passLabel);
	newPanel.add(text2);
	newPanel.add(loginButton);
	newPanel.add(backButton);

	container.removeAll();
	container.add(newPanel);
	setVisible(true);
}

public void loginPanel(){

	JPanel loginPanel = new JPanel();
	loginPanel.setBackground(Color.white);
	loginPanel.setLayout(null);

	JLabel titleLabel = new JLabel(titleIcon);
	titleLabel.setBounds(20,100,601,142);


	loginLabel = new JLabel();
	loginLabel.setFont(font.deriveFont(18f));
	loginLabel.setBounds(100,250,600,100);
	loginLabel.setForeground(Color.red);


	JPanel namePanel = new JPanel();
	namePanel.setLayout(new FlowLayout());
	JLabel nameLabel = new JLabel("Username:", SwingConstants.LEFT);
	nameLabel.setFont(font.deriveFont(35f));
	nameLabel.setBounds(80,280,300,100);
	text1 = new JTextField();
	text1.setFont(font.deriveFont(24f));
	text1.setBounds(240,315,300,32);



	JPanel passPanel = new JPanel();
	JLabel passLabel = new JLabel("Password:", SwingConstants.LEFT);
	passLabel.setFont(font.deriveFont(35f));
	passLabel.setBounds(80,330,300,100);
	text2 = new JPasswordField();
	text2.setFont(new Font("�l�r �S�V�b�N", Font.BOLD, 24));
	text2.setBounds(240,365,300,32);

	JButton loginButton = new JButton(loginIcon);
	loginButton.setActionCommand("loginAgree");
	loginButton.addActionListener(this);
	loginButton.setBounds(105,475,429,114);

	JButton backButton = new JButton(backIcon);
	backButton.setActionCommand("main");
	backButton.addActionListener(this);
	backButton.setBounds(105,650,429,114);

	loginPanel.add(loginLabel);
	loginPanel.add(titleLabel);
	loginPanel.add(nameLabel);
	loginPanel.add(text1);
	loginPanel.add(passLabel);
	loginPanel.add(text2);
	loginPanel.add(loginButton);
	loginPanel.add(backButton);

	container.removeAll();
	container.add(loginPanel);
	setVisible(true);
}

private void mainPanel(){

	try{

		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(Color.white);
		mainPanel.setLayout(null);

		BufferedImage birdPic = ImageIO.read(new File("bird.jpg"));
		JLabel birdLabel = new JLabel(new ImageIcon(birdPic));
		birdLabel.setBounds(50, 30, 601, 800);

		BufferedImage othelloPic = ImageIO.read(new File("title.jpg"));
		JLabel titleLabel = new JLabel(new ImageIcon(othelloPic));
		titleLabel.setBounds(20,100,300,108);

		JButton loginButton = new JButton(loginIcon);
		loginButton.setActionCommand("login");
		loginButton.addActionListener(this);
		loginButton.setBounds(105,350,429,114);

		JButton newButton = new JButton(resignIcon);
		newButton.setActionCommand("new");
		newButton.addActionListener(this);
		newButton.setBounds(105,550,429,114);

		mainPanel.add(loginButton);
		mainPanel.add(newButton);
		mainPanel.add(titleLabel);
		mainPanel.add(birdLabel);

		container.removeAll();
		container.add(mainPanel);
		setVisible(true);
	}catch(Exception e){
	}
}

public void gamePanel(){


			JPanel battlePanel = new JPanel();
			battlePanel.setLayout(null);
			battlePanel.setBackground(Color.white);

			JPanel boardPanel = new JPanel();
			boardPanel.setLayout(new GridLayout(8,8));
			boardPanel.setBounds(0,0,640,640);
			battlePanel.add(boardPanel);

			//�I�Z���Ղ̐���
			this.buttonArray = new JButton[row * row];//�{�^���̔z����쐬
			for(int i = 0 ; i < row ; i++){
				for(int j = 0 ; j < row ; j++){
					int arrayNumber = i*row + j;
					if(board[i][j]==1){ buttonArray[arrayNumber] = new JButton(blackIcon);}//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
					if(board[i][j]==2){ buttonArray[arrayNumber] = new JButton(whiteIcon);}//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
					if(board[i][j]==3){ buttonArray[arrayNumber] = new JButton(highlightIcon);}//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
					if(board[i][j]==0){ buttonArray[arrayNumber] = new JButton(boardIcon);}//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
					boardPanel.add(buttonArray[arrayNumber]);//�{�^���̔z����y�C���ɓ\��t��
					// �{�^����z�u����
					buttonArray[arrayNumber].addActionListener(this);//�}�E�X�����F���ł���悤�ɂ���
					buttonArray[arrayNumber].setActionCommand(Integer.toString(arrayNumber));//�{�^�������ʂ��邽�߂̖��O(�ԍ�)��t������
				}
			}
				//�F�\���p���x��
			this.colorLabel1 = new JLabel();//�F����\�����邽�߂̃��x�����쐬
			colorLabel1.setBounds(20,650,50,50);
			battlePanel.add(colorLabel1);

			myLabel.setFont(font.deriveFont(18f));
			myLabel.setBounds(90,625,250,100);
			battlePanel.add(myLabel);//�����\���p���x�����y�C���ɓ\��t��

			this.turnLabel1 = new JLabel(turnBar);
			turnLabel1.setBounds(10,700,300,8);
			battlePanel.add(turnLabel1);
			turnLabel1.setEnabled(false);

			this.colorLabel2 = new JLabel();//�F����\�����邽�߂̃��x�����쐬
			colorLabel2.setBounds(340,650,50,50);
			battlePanel.add(colorLabel2);

			//����̖��O
			this.yourLabel = new JLabel("");//�������\�����邽�߂̃��x�����쐬
			yourLabel.setFont(font.deriveFont(18f));
			yourLabel.setBounds(410,625,250,100);
			battlePanel.add(yourLabel);//�����\���p���x�����y�C���ɓ\��t��

			this.turnLabel2 = new JLabel(turnBar);
			turnLabel2.setBounds(330,700,300,8);
			battlePanel.add(turnLabel2);
			turnLabel2.setEnabled(false);



			this.statusLabel = new JLabel("");//�F����\�����邽�߂̃��x�����쐬
			statusLabel.setFont(font.deriveFont(18f));
			statusLabel.setBounds(20,720,300,100);
			battlePanel.add(statusLabel);//�����\���p���x�����y�C���ɓ\��t��

			//���ԃJ�E���g���x��
			this.timeLabel = new JLabel("");//��ԏ���\�����邽�߂̃��x�����쐬
			timeLabel.setFont(font.deriveFont(18f));
			timeLabel.setBounds(20,680,210,100);
			battlePanel.add(timeLabel);//�����\���p���x�����y�C���ɓ\��t��


			//�I���{�^��
			reject = new JButton("�z�[����ʂɖ߂�");//�I���{�^�����쐬
			reject.setBackground(Color.white);
			reject.setFont(font.deriveFont(15f));
			reject.addActionListener(this);//�}�E�X�����F���ł���悤�ɂ���
			reject.setActionCommand("reject");//�{�^�������ʂ��邽�߂̖��O��t������
			reject.setBounds(320,760,300,40);
			battlePanel.add(reject);

			//�I���{�^��
			stop = new JButton("����");//�I���{�^�����쐬
			stop.setBackground(Color.white);
			stop.setFont(font.deriveFont(15f));
			stop.addActionListener(this);//�}�E�X�����F���ł���悤�ɂ���
			stop.setActionCommand("giveup");//�{�^�������ʂ��邽�߂̖��O��t������
			stop.setBounds(320,720,150,40);
			battlePanel.add(stop);

			//�Đ�{�^��
			rematch = new JButton("�Đ�");//�p�X�{�^�����쐬
			rematch.setBackground(Color.white);
			rematch.setFont(font.deriveFont(15f));
			rematch.addActionListener(this);//�}�E�X�����F���ł���悤�ɂ���
			rematch.setActionCommand("rematch");//�{�^�������ʂ��邽�߂̖��O��t������
			rematch.setEnabled(false);
			rematch.setBounds(470,720,150,40);
			battlePanel.add(rematch);

			container.removeAll();
			container.add(battlePanel);
			setVisible(true);
		}

		public void rankingPanel(){
			  JPanel userPanel = new JPanel();
				userPanel.setBackground(Color.white);
				userPanel.setLayout(null);
				//�����L���O�{�^��

				JLabel rankingLabel = new JLabel(rankingIcon2);
				rankingLabel.setBounds(0,0,640,58);
				userPanel.add(rankingLabel);

				rankingList = new JList<String>();
				JScrollPane scrollPanel = new JScrollPane(rankingList);
				scrollPanel.setBounds(0,58,635,692);
				userPanel.add(scrollPanel);

				JButton backButton = new JButton(backIcon2);
				backButton.setActionCommand("home");
				backButton.addActionListener(this);
				backButton.setBounds(105,750,416,58);
				userPanel.add(backButton);

				container.removeAll();
				container.add(userPanel);
				setVisible(true);
		}

	//�e�X�g�p��main
	public static void main(String args[]){
		Player player = new Player(); //�v���C���I�u�W�F�N�g�̗p��(���O�C��)
		Othello game = new Othello(); //�I�Z���I�u�W�F�N�g��p��
		Client oclient = new Client(game, player); //�����Ƃ��ăI�Z���I�u�W�F�N�g��n��
		oclient.setVisible(true);
		oclient.connectServer("localhost", 10011);
	}
}
