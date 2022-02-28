import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.io.*;

public class Server{
	private ServerSocket server;
	private int port;
	private FileWriter fw;
	private PrintWriter pw;
	private ArrayList<Receiver> gameRoom;
	private ArrayList<ArrayList<Receiver>> gameRoomList;
	private ArrayList<String> users;
	private ArrayList<ArrayList<String>> userDataList;

	public Server(int port) {

			this.port = port;
			users = new ArrayList<String>();
			gameRoom = new ArrayList<Receiver>();
			gameRoomList = new ArrayList<ArrayList<Receiver>>();
			userDataList = new ArrayList<ArrayList<String>>();
			setData();
			gameRoomList.add(gameRoom);
	}

	public void setData(){
		try{
			File file = new File("idPassKeep.csv");
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<String> userData;
			String line;

			while((line = br.readLine()) != null){
					userData = new ArrayList<String>();
					String[] data = line.split(",", 0);
					userData.add(data[0]);
					userData.add(data[1]);
					userData.add(line);
					userDataList.add(userData);
			}
		} catch (Exception e) {
			e.printStackTrace();//例外時処理
		}
	}

	public void addUser(Receiver user){
		if(!users.contains(user.getUserName())){
			users.add(user.getUserName());
		}
	}

	public void removeUser(Receiver user){
		users.remove(user.getUserName());
	}

	public void addGameRoom(Receiver user) {

		removeGameRoom(user);

		ArrayList<Receiver> room;

		int matchingLower = user.matchingLower;
		int matchingUpper = user.matchingUpper;

		out:while(true){
			for(int i = 0 ; i < gameRoomList.size() ; i++){
				if(gameRoomList.get(i).size() == 2){
					continue;
				}else if(gameRoomList.get(i).size() == 1 ){
					int player2Rate = Integer.parseInt(gameRoomList.get(i).get(0).getUserRate());
					if(player2Rate > matchingLower && player2Rate < matchingUpper){
						gameRoomList.get(i).add(user);
						room = gameRoomList.get(i);
						break out;
					}else{
						continue;
					}
				}else if(gameRoomList.get(i).size() == 0 ){
					gameRoomList.get(i).add(user);
					room = gameRoomList.get(i);
					break out;
				}
			}

			ArrayList<Receiver> newGameRoom = new ArrayList<Receiver>();
			newGameRoom.add(user);
			gameRoomList.add(newGameRoom);
			room = newGameRoom;
			break;
		}

		user.setMyGameRoom(room);
		user.setPlayerNumber(room.size() -1);
		user.setColor(user.getPlayerNumber());
		user.player1SendMessage("setColor" + " " + user.getColor()); //色データの送信
	}

	public void removeGameRoom(Receiver user) {
		for(int i=0;i < gameRoomList.size();i++){
			if(gameRoomList.get(i).contains(user)){
				gameRoomList.get(i).remove(user);
				break;
			}
		}
	}

	public void removeGameRoomList(ArrayList<Receiver> room) {
		if(gameRoomList.contains(room)){
			gameRoomList.remove(room);
		}
	}

	public void disConnectedJudge(ArrayList<Receiver> room,Receiver disUser){

		String msg = "DisConnected" + " " + disUser.getColor();

		if(disUser.getGameEnd() && room.size() > 1){
			System.out.println("DisConnet");
			disUser.disConnectPenalty();
			disUser.player2SendMessage(msg);
		}
		return;
	}

	public void dataUpdate(String name,String value){

		boolean newFlag = true;

		for(int i = 0 ; i < userDataList.size() ; i++){
			if(userDataList.get(i).contains(name)){
				userDataList.get(i).set(2,value);
				newFlag = false;
			}
		}

		if(newFlag){
			String[] data = value.split(",", 0);
			ArrayList<String> userData = new ArrayList<String>();
			userData.add(data[0]);
			userData.add(data[1]);
			userData.add(value);
			userDataList.add(userData);
		}
		dataSave();
	}

	public void printAccessStatus(){
		System.out.println("接続人数　: " + users.size());
		for(int i = 0 ; i < gameRoomList.size() ; i++){
			System.out.println("部屋[" + i + "]の人数　: " + gameRoomList.get(i).size());
		}
		System.out.println("現在の部屋の数　: " + String.valueOf(gameRoomList.size()));
	}


	public void dataSave(){

		try{

			fw = new FileWriter("idPassKeep.csv", false);
			pw = new PrintWriter(new BufferedWriter(fw));

			for(int i = 0 ; i < userDataList.size() ; i++){
				pw.print(userDataList.get(i).get(2));
				pw.println();
			}
			pw.close();	//ファイルに書き出す

		} catch (IOException e){
		}
	}

	public void start(){
		try {
			server = new ServerSocket(10011);
			while (!server.isClosed()) {
				Socket socket = server.accept();
				Receiver user = new Receiver(socket);
				user.start();
			}
		} catch (Exception e) {
			System.err.println("Build Socket Error: " + e);
		}
	}

	private static Server instance;

	public static Server getInstance() {
		if (instance == null) {
			instance = new Server(10011);
		}
		return instance;
	}

	public static void main(String[] args){
		Server application = Server.getInstance();
		application.start();
	}

//------------------------------------------------------------------------

	class Receiver extends Thread {

		private int playerNo;
		private Socket socket;
		private PrintWriter output;
		private Server server = Server.getInstance();
		private ArrayList<Receiver> myGameRoom;
		private String myName;
		private String myRate="2000";
		private String myData;
		private boolean gameEnd;
		private	boolean startFlag;
		private String myColor;
		private Receiver user;
		private int matchingLower;
		private int matchingUpper;
		private Timer timer1;
		private Thread thread;
		private InputStream sisr;
		private BufferedReader br;

		Receiver (Socket socket){

			try{

				this.user = this;
				this.socket = socket;
				output = new PrintWriter(socket.getOutputStream(), true);
				sisr = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(sisr));

			} catch (IOException e){
				removeUser(this);
			}
		}

		public void run(){

			try{
				setLogin();
				addUser(this);

				while(true){
					String line = br.readLine();
					String[] msg = line.split(" ", 2);
					String msgName = msg[0];
					String msgValue = (msg.length < 2 ? "" : msg[1]);

					receiveMessage(msgName, msgValue);

					if(msgName.equals("gamematch")){
						int rate = Integer.parseInt(getUserRate());
						if(rate < 3334){
							matchingUpper = 3333;
							matchingLower = 1;
						}else if(rate < 6667){
							matchingUpper = 6666;
							matchingLower = 3334;
						}else if(rate < 10000){
							matchingUpper = 9999;
							matchingLower = 6667;
						}
						gameStart();
					}
				}

			} catch (IOException e){
				removeUser(this);
			}
		}

		public void gameStart(){
			try{

				gameEnd = true;
				startFlag = true;

				out:while(true){

					addGameRoom(this);	//サーバに接続しているクライアントの保持

					timer1 = new Timer(false);

					TimerTask task1 = new TimerTask(){
						@Override
							public void run(){
								if(myGameRoom.size() == 1){
									addGameRoom(user);
									printAccessStatus(); //接続状況の表示
								}else if(myGameRoom.size() > 1){
										timer1.cancel();
								}
								matchingUpper += 3333;
								matchingLower -= 3333;
							}
					};

					timer1.schedule(task1,0,10000);

					while(!socket.isClosed()){

						if(myGameRoom.size()>1 && startFlag){
							player1SendMessage("Matched");
							startFlag = false;
							player2SendMessage("setPlayer2Data" + " " + getMyData() + "," + getUserRate());
						}

						String line = br.readLine();
						String[] msg = line.split(" ", 2);
						String msgName = msg[0];
						String msgValue = (msg.length < 2 ? "" : msg[1]);

						receiveMessage(msgName, msgValue);

						if(msgName.equals("reject")){
							forwardMessage("reject");
						}else if(msgName.equals("finish")){
							timer1.cancel();
							server.removeGameRoomList(myGameRoom);
							server.removeGameRoom(this);//ゲーム部屋からユーザを退出
							updateData(getUserName());
							break out;
						}
					}
				}

			} catch (IOException e){
				System.err.println(getUserName() + "：" + "Access Error");
				timer1.cancel();
				server.removeUser(this);//接続ユーザの削除
				server.disConnectedJudge(myGameRoom,this);//切断切れ勝ち判定
				server.removeGameRoomList(myGameRoom);//ゲーム部屋リストから部屋を削除
				server.removeGameRoom(this);//ゲーム部屋からユーザを退出
			}
		}

		public void setMyGameRoom(ArrayList<Receiver> myGameRoom){
			this.myGameRoom = myGameRoom;
		}

		public boolean getGameEnd(){
			return gameEnd;
		}

		public void setUserName(String name){
			myName = name;
		}

		public String getUserName(){
			return myName;
		}

		public void setUserRate(String rate){
			this.myRate = rate;
		}

		public String getUserRate(){
			return myRate;
		}

		public void setMyData(String myData){
			this.myData = myData;
		}

		public String getMyData(){
			return myData;
		}

		public void setPlayerNumber(int playerNo){
			this.playerNo = playerNo;
		}

		public int getPlayerNumber(){
			return playerNo;
		}

		public void setColor(int playerNo){
			if(playerNo == 0){
				myColor = "White";
			}else if(playerNo == 1){
				myColor = "Black";
			}else{
				myColor = "unknown";
			}
		}

		public String getColor(){
			return myColor;
		}

		public void colorChange(){
			if(myColor.equals("Black")){
				myColor = "White";
			}else{
				myColor = "Black";
			}
		}

		public void forwardMessage(String msg){ //両者に送信
			myGameRoom.get(0).output.println(msg);
		  myGameRoom.get(0).output.flush();
			myGameRoom.get(1).output.println(msg);
		  myGameRoom.get(1).output.flush();
		}

		public void player1SendMessage(String msg){	//自身のクライアントに送信
			output.println(msg);
			output.flush();
		}

		public void player2SendMessage(String msg){	//相手のクライアントに送信
			Receiver user = myGameRoom.get((playerNo+1)%2);
			user.output.println(msg);
			user.output.flush();
		}

		public void receiveMessage(String name,String value){

			if(name.equals("Waiting")){
				player1SendMessage("Matching");
			}else if(name.equals("command")){
				forwardMessage("command" + " " + value);
			}else if(name.equals("dataSave")){
				dataUpdate(getUserName(),value);
			}else if(name.equals("setNamePass1")){
				String[] data = value.split(",", 0);
				userAuthentication(data[0],data[1]);
			}else if(name.equals("setNamePass2")){
				String[] data = value.split(",", 0);
				userRegistration(data[0],data[1]);
			}else if(name.equals("userUpData")){
				player2SendMessage("userUpData" + " " + value);
			}else if(name.equals("setPlayer1Data")){
				setMyData(value);
			}else if(name.equals("gameEnd")){
				gameEnd = false;
			}else if(name.equals("rematch")){
				if(value.equals("winner")){
					forwardMessage("ReMatchSuccess");
				}else{
					player1SendMessage("reMatchReceive");
					player2SendMessage("loserReMatch");
				}
			}else if(name.equals("ReMatchSuccess")){
				colorChange();
				player1SendMessage("setColor" + " " + getColor());
				gameEnd = true;
				startFlag = true;
			}else if(name.equals("ranking")){
				player1SendMessage("ranking" + " " + getRanking());
			}else if(name.equals("gamematch")){
				player1SendMessage("gamematch");
			}
		}

		public String getRanking(){

			String rankingData = userDataList.get(0).get(2);
			for(int i = 1; i < userDataList.size() ; i++){
				rankingData = rankingData + "/" + userDataList.get(i).get(2);
			}

			return rankingData;
		}

		public void updateData(String name){
			for(int i = 0 ; i < userDataList.size() ; i++){
				if(userDataList.get(i).get(0).equals(name)){
						String msg = userDataList.get(i).get(2);
						String[] data = msg.split(",", 0);
						setUserRate(data[7]);
						player1SendMessage("setPlayer1Data" + " " + msg);
						break;
				}
			}
		}

		public void setLogin(){

			try{
				String line = br.readLine();
				String[] msg = line.split(" ", 2);
				String msgName = msg[0];
				String msgValue = (msg.length < 2 ? "" : msg[1]);

				receiveMessage(msgName, msgValue);

			} catch (IOException e){
				System.err.println("Login Error");
			}
		}

		public void userAuthentication(String name,String pass){

			if(users.contains(name)){
				System.out.println("ユーザーはログイン中です。");
				player1SendMessage("NO1");
				setLogin();
				return;
			}

			for(int i = 0 ; i < userDataList.size() ; i++){
				if(userDataList.get(i).get(0).equals(name)){
					if(userDataList.get(i).get(1).equals(pass)){
						player1SendMessage("OK");
						String msg = userDataList.get(i).get(2);
						String[] data = msg.split(",", 0);
						setUserName(data[0]);
						setUserRate(data[7]);
						player1SendMessage("setPlayer1Data" + " " + msg);
						return;
					}else{
						break;
					}
				}
			}
			System.out.println("IDまたはパスワードが違います。");
			player1SendMessage("NO1");
			setLogin();
		}

		public void userRegistration(String name,String pass){

			boolean newFlag = true;

			for(int i = 0 ; i < userDataList.size() ; i++){
				if(userDataList.get(i).get(0).equals(name)){
					newFlag = false;
					break;
				}
			}
			if(newFlag){
				System.out.println("新規登録しました");
				player1SendMessage("OK");
				setUserName(name);
				setUserRate("2000");
				player1SendMessage("setPlayer1Data" + " " + name + "," + pass + ",0,0,0,0,0,2000");
				dataUpdate(name,name + "," + pass + ",0,0,0,0,0,2000");
			}else{
				player1SendMessage("NO2");
				setLogin();
			}
		}

		public void disConnectPenalty(){

			for(int i = 0 ; i < userDataList.size() ; i++){
				if(userDataList.get(i).contains(getUserName())){
					String value = userDataList.get(i).get(2);
					String[] data = value.split(",", 0);
					String str = data[0] + "," + data[1] + "," + data[2] + "," + String.valueOf(Integer.parseInt(data[3])+1) + "," + data[4] + "," + data[5] + "," + String.valueOf(Integer.parseInt(data[6]) + 1)
					+ "," + String.valueOf((int)(Integer.parseInt(data[7])*0.95-200));
					userDataList.get(i).set(2,str);
					dataSave();
					break;
				}
			}
		}
	}

}
