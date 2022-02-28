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
	private JButton buttonArray[];//オセロ盤用のボタン配列
	private JButton stop,rematch,ranking,reject; //停止、スキップ用ボタン
	private JList<String> rankingList;	//現在入室中のチャットルームのユーザー
	private JLabel colorLabel1,colorLabel2;
	private JLabel turnLabel1,turnLabel2; // 手番表示用ラベル
	private JLabel myLabel = new JLabel("");
	private JLabel yourLabel;//自分、相手の名前ラベル
	private JLabel statusLabel; //現在状況のラベル
	private JLabel timeLabel; //時間カウントラベル
	private JLabel loginLabel;
	private Container container; // コンテナ
	private ImageIcon blackIcon, whiteIcon, boardIcon,highlightIcon,turnWhite,turnBlack,turnBar; //アイコン
	private ImageIcon gameIcon,titleIcon,loginIcon,rankingIcon,rankingIcon2,resignIcon,backIcon,backIcon2;
	private PrintWriter out;//データ送信用オブジェクト
	private InputStreamReader sisr; //受信データ用文字ストリーム
	private BufferedReader br; //文字ストリーム用のバッファ
	private Receiver receiver; //データ受信用オブジェクト
	private Othello game; //Othelloオブジェクト
	private Player player; //Playerオブジェクト
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
	// コンストラクタ
	public Client(Othello game, Player player) { //OthelloオブジェクトとPlayerオブジェクトを引数とする
		try{
			this.game = game; //引数のOthelloオブジェクトを渡す
			this.player = player; //引数のPlayerオブジェクトを渡す
			this.board = game.getGrids(); //getGridメソッドにより局面情報を取得

			//アイコン設定(画像ファイルをアイコンとして使う)
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

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じる場合の処理
			setTitle("番付オセロゲーム");//ウィンドウのタイトル
			setSize(row * 80 , row * 80 + 200);//ウィンドウのサイズを設定
			setResizable(false);
			container = getContentPane();//フレームのペインを取得
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
		//色表示用ラベルの変更
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


	// メソッド
	public void connectServer(String ipAddress, int port){	// サーバに接続
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); //サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); //データ送信用オブジェクトの用意
			receiver = new Receiver(socket); //受信用オブジェクトの準備

			receiver.start();

		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			System.exit(-1);
		}
	}

	public void sendMessage(String msg){	// サーバに操作情報を送信
		out.println(msg);//送信データをバッファに書き出す
		out.flush();//送信データを送る
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private boolean victory;
		private Timer timer1;
		private Timer timer2;
		private Timer timer3;
		private Timer timer4;

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);//文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{

				while(true){		//操作情報等の受信
					String line = br.readLine();
					String[] msg = line.split(" ", 2);
					String msgName = msg[0];
					String msgValue = (msg.length < 2 ? "" : msg[1]);

					receiveMessage(msgName, msgValue);

					if(line.equals("OK")){
						homePanel();
					}else if(line.equals("NO1")){
						loginLabel.setText("IDまたはパスワードが違う、または既にログイン中です。");
					}else if(line.equals("NO2")){
						loginLabel.setText("既に同じユーザ名の人が存在します。");
					}

					if(msgName.equals("gamematch")){
						gameStart();
					}
				}

			}catch(IOException e){
				System.err.println("データ受信時にエラーが発生しました: " + e);
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
								timeLabel.setText("制限時間：" + timeCount + "/60");
								if(timeCount > 60 && myColor.equals(game.getTurn())){
									timeLabel.setText("時間切れ");
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
								timeLabel.setText("制限時間：" + timeCount + "/30");
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
								timeLabel.setText("制限時間：" + timeCount + "/30");
								if(timeCount > 30){
									displayClear();
									sendMessage("reject");
									homePanel();
									timer3.cancel();
									timer4.cancel();
								}
							}
					};


					while(true){		//操作情報等の受信
						String line = br.readLine();
						String[] msg = line.split(" ", 2);
						String msgName = msg[0];
						String msgValue = (msg.length < 2 ? "" : msg[1]);

						if(msgName.equals("Matched")){
							setTurnLabel();//手番ラベルの更新
							battleCount++;
							startFlag = true;
							reject.setEnabled(false);
							timer2.schedule(task2,0,1000);
							statusLabel.setText("ゲーム中");
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
									statusLabel.setText("引き分けです");
									reject.setEnabled(true);
									rematch.setEnabled(true);
									timer3.schedule(task3,0,1000);
								}else if(game.getWinColor().equals(player.getColor())){
									statusLabel.setText("あなたは勝者です");
									victory = true;
								}else{
									statusLabel.setText("あなたは敗者です");
									reject.setEnabled(true);
									rematch.setEnabled(true);
									rematch.setText("再戦を申し込む");
									timer3.schedule(task3,0,1000);
								}

							}else{
								statusLabel.setText("相手の切断負けです");
							}

							sendMessage("gameEnd");
							break;
						}

					}

					player.addRate(game.getWinColor().equals(player.getColor()),rate2);
					sendMessage("dataSave" + " " + player.getStatus());
					String msg = "<html>" + player.getName() + "　レート" +"("+ player.getRate() +")"+ "<br>" + "　勝ち:" + player.getWinCount() + "　" + "負け:" + player.getLoseCount() +"<html>";
					myLabel.setText(msg);
					sendMessage("userUpData" + " " + msg);

					if(battleCount > 2){
						statusLabel.setText("3戦が終了しました");
						rematch.setEnabled(false);
						timer3.cancel();
					}

					while(true){		//操作情報等の受信
						String line2 = br.readLine();
						String[] msg2 = line2.split(" ", 0);
						String msgName2 = msg2[0];
						String msgValue2 = (msg2.length < 2 ? "" : msg2[1]);

						if(msgName2.equals("ReMatchSuccess")){ //再戦を行う
							timer4.cancel();
							sendMessage("ReMatchSuccess");
							displayClear();
							updateDisp();
							break;
						}else if(msgName2.equals("loserReMatch")){ //勝者に再戦の申請
							timeReset();
							timer4.schedule(task4,0,1000);
							statusLabel.setText("再戦の申し込みがありました");
							reject.setEnabled(true);
							rematch.setText("再戦を承認する");
							rematch.setEnabled(true);
						}else if(msgName2.equals("reMatchReceive")){
							timer3.cancel();
							timeReset();
							timer4.schedule(task4,0,1000);
							statusLabel.setText("再戦を申し込みました");
							reject.setEnabled(false);
						}else if(msgName2.equals("reject")){
							if(battleCount > 2){
							}else if(victory){
								statusLabel.setText("再戦の申し込みはありませんでした");
							}else{
								statusLabel.setText("再戦の申し込みが拒否されました");
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
				System.err.println("データ受信時にエラーが発生しました: " + e);
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

	public void receiveMessage(String name,String value){	// メッセージの受信

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
			String msg = "<html>" + player.getName() + "　レート" +"("+ player.getRate()+")" + "<br>" + " 勝ち:" + player.getWinCount() + "　" + "負け:" + player.getLoseCount() +"<html>";
			myLabel.setText(msg);
			sendMessage("setPlayer1Data" + " " + msg);
		}else if(name.equals("setPlayer2Data")){
			String[] data = value.split(",", 0);
			yourLabel.setText(data[0]);
			rate2 = Integer.parseInt(data[1]);
			statusLabel.setText("ゲーム中");
		}else if(name.equals("Matching")){
			if(switchFlag){
				statusLabel.setText("マッチング中");
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
			updateDisp();//局面更新
		}else if(name.equals("command")){
			if(game.gamestart(value)){	//ゲーム操作
				timeReset();
			}

			if(!game.setGrids()){
				game.turnShift();
			}
			setTurnLabel();//手番ラベルの更新
			updateDisp();//局面更新

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
							// 選択行はデフォルトの色
							label.setBackground(list.getSelectionBackground());
					} else {
							// 選択してない行は薄い青
							label.setBackground(new Color(220, 220, 220));
					}
				}

				// 奇数行を薄い青にする
				if (index == getRankingIndex() || index == (getRankingIndex()+1) ){
						if (list.isSelectedIndex(index)) {
								// 選択行はデフォルトの色
								label.setBackground(list.getSelectionBackground());
						} else {
								// 選択してない行は薄い青
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
			String id = " ID：" + data[0] + " レート：" + data[7];
			String status = "    勝：" + data[2] + " 負：" + data[3] + " " + "引分：" + data[4] +
			  " " +"投了：" + data[5] + " 切断：" + data[6] ;
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
			rankingDataList[i*2] = String.valueOf(i+1) + "位" + memberList.get(i).idData;
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


	public void updateDisp(){	// 画面を更新する


		if(myColor.equals(game.getTurn())){
			this.board = game.getDisplayGrids(); //getGridメソッドにより局面情報を取得
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
					buttonArray[arrayNumber].setIcon(highlightIcon);//盤面状態に応じたアイコンを設定
				}else if(board[i][j]==0){
					buttonArray[arrayNumber].setIcon(boardIcon);//盤面状態に応じたアイコンを設定
				}

			}
		}
		game.resetAnimationXY();

	}

  	//マウスクリック時の処理
	@Override
	public void actionPerformed(ActionEvent e) {


		String command = e.getActionCommand();//ボタンの名前を取り出す

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
			sendMessage("command" + " " + command); //テスト用にメッセージを送信
		}

	}

	public void login(){

					String name = text1.getText();
					String pass = text2.getText();

				if(name.length() < 13 && pass.length() < 13 && name.indexOf("/") == -1 && name.indexOf(",") == -1 && pass.indexOf(",") == -1 && name.indexOf(" ") == -1 && pass.indexOf(" ") == -1 && !name.isEmpty() && !pass.isEmpty()){

					sendMessage("setNamePass1" + " " + name + "," + pass );

				}else{
					loginLabel.setText("[,]、[空白]を含めずに、12文字以下で入力してください。");
				}

	}

	public void newLogin(){

					String name = text1.getText();
					String pass = text2.getText();

				if(name.length() < 13 && pass.length() < 13 && name.indexOf("/") == -1 && name.indexOf(",") == -1 && pass.indexOf(",") == -1 && name.indexOf(" ") == -1 && pass.indexOf(" ") == -1 && !name.isEmpty() && !pass.isEmpty()){

					sendMessage("setNamePass2" + " " + name + "," + pass);

				}else{
					loginLabel.setText("[,]、[空白]を含めずに12文字以下で入力してください。");
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
	text2.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 24));
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
	text2.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 24));
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

			//オセロ盤の生成
			this.buttonArray = new JButton[row * row];//ボタンの配列を作成
			for(int i = 0 ; i < row ; i++){
				for(int j = 0 ; j < row ; j++){
					int arrayNumber = i*row + j;
					if(board[i][j]==1){ buttonArray[arrayNumber] = new JButton(blackIcon);}//盤面状態に応じたアイコンを設定
					if(board[i][j]==2){ buttonArray[arrayNumber] = new JButton(whiteIcon);}//盤面状態に応じたアイコンを設定
					if(board[i][j]==3){ buttonArray[arrayNumber] = new JButton(highlightIcon);}//盤面状態に応じたアイコンを設定
					if(board[i][j]==0){ buttonArray[arrayNumber] = new JButton(boardIcon);}//盤面状態に応じたアイコンを設定
					boardPanel.add(buttonArray[arrayNumber]);//ボタンの配列をペインに貼り付け
					// ボタンを配置する
					buttonArray[arrayNumber].addActionListener(this);//マウス操作を認識できるようにする
					buttonArray[arrayNumber].setActionCommand(Integer.toString(arrayNumber));//ボタンを識別するための名前(番号)を付加する
				}
			}
				//色表示用ラベル
			this.colorLabel1 = new JLabel();//色情報を表示するためのラベルを作成
			colorLabel1.setBounds(20,650,50,50);
			battlePanel.add(colorLabel1);

			myLabel.setFont(font.deriveFont(18f));
			myLabel.setBounds(90,625,250,100);
			battlePanel.add(myLabel);//自分表示用ラベルをペインに貼り付け

			this.turnLabel1 = new JLabel(turnBar);
			turnLabel1.setBounds(10,700,300,8);
			battlePanel.add(turnLabel1);
			turnLabel1.setEnabled(false);

			this.colorLabel2 = new JLabel();//色情報を表示するためのラベルを作成
			colorLabel2.setBounds(340,650,50,50);
			battlePanel.add(colorLabel2);

			//相手の名前
			this.yourLabel = new JLabel("");//相手情報を表示するためのラベルを作成
			yourLabel.setFont(font.deriveFont(18f));
			yourLabel.setBounds(410,625,250,100);
			battlePanel.add(yourLabel);//自分表示用ラベルをペインに貼り付け

			this.turnLabel2 = new JLabel(turnBar);
			turnLabel2.setBounds(330,700,300,8);
			battlePanel.add(turnLabel2);
			turnLabel2.setEnabled(false);



			this.statusLabel = new JLabel("");//色情報を表示するためのラベルを作成
			statusLabel.setFont(font.deriveFont(18f));
			statusLabel.setBounds(20,720,300,100);
			battlePanel.add(statusLabel);//自分表示用ラベルをペインに貼り付け

			//時間カウントラベル
			this.timeLabel = new JLabel("");//手番情報を表示するためのラベルを作成
			timeLabel.setFont(font.deriveFont(18f));
			timeLabel.setBounds(20,680,210,100);
			battlePanel.add(timeLabel);//自分表示用ラベルをペインに貼り付け


			//終了ボタン
			reject = new JButton("ホーム画面に戻る");//終了ボタンを作成
			reject.setBackground(Color.white);
			reject.setFont(font.deriveFont(15f));
			reject.addActionListener(this);//マウス操作を認識できるようにする
			reject.setActionCommand("reject");//ボタンを識別するための名前を付加する
			reject.setBounds(320,760,300,40);
			battlePanel.add(reject);

			//終了ボタン
			stop = new JButton("投了");//終了ボタンを作成
			stop.setBackground(Color.white);
			stop.setFont(font.deriveFont(15f));
			stop.addActionListener(this);//マウス操作を認識できるようにする
			stop.setActionCommand("giveup");//ボタンを識別するための名前を付加する
			stop.setBounds(320,720,150,40);
			battlePanel.add(stop);

			//再戦ボタン
			rematch = new JButton("再戦");//パスボタンを作成
			rematch.setBackground(Color.white);
			rematch.setFont(font.deriveFont(15f));
			rematch.addActionListener(this);//マウス操作を認識できるようにする
			rematch.setActionCommand("rematch");//ボタンを識別するための名前を付加する
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
				//ランキングボタン

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

	//テスト用のmain
	public static void main(String args[]){
		Player player = new Player(); //プレイヤオブジェクトの用意(ログイン)
		Othello game = new Othello(); //オセロオブジェクトを用意
		Client oclient = new Client(game, player); //引数としてオセロオブジェクトを渡す
		oclient.setVisible(true);
		oclient.connectServer("localhost", 10011);
	}
}
