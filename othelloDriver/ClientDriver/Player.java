public class Player {

  private String myName;
  private String myColor;
  private String myPass;
  private int winCount=0;
  private int loseCount=0;
  private int drawCount=0;
  private int resignCount=0;
  private int myRate=1000;
  private int disConnectedCount;


  public void setNamePass(String myName,String myPass){
    this.myName = myName;
    this.myPass = myPass;
  }

  public void setWinCount(int winCount){
    this.winCount = winCount;
  }
  public void setLoseCount(int loseCount){
    this.loseCount = loseCount;
  }
  public void setResignCount(int resignCount){
    this.resignCount = resignCount;
  }
  public void setDrawCount(int drawCount){
    this.drawCount = drawCount;
  }
  public void setDisConnectedCount(int disConnectedCount){
    this.disConnectedCount = disConnectedCount;
  }
  public void setRate(int myRate){
    this.myRate = myRate;
  }
  public void setColor(String color){
    this.myColor = color;
  }
  public void colorChange(){
    if(myColor.equals("Black")){
      myColor = "White";
    }else{
      myColor = "Black";
    }
  }

  public String getStatus(){
    String myStatus = myName + "," + myPass + "," + winCount + "," + loseCount + "," + drawCount + "," + resignCount + "," + disConnectedCount + "," + myRate;
    return myStatus;
  }

  public String getName(){
    return myName;
  }
  public String getColor(){
    return myColor;
  }
  public String getPass(){
    return myPass;
  }
  public int getRate(){
    return myRate;
  }
  public int getWinCount(){
    return winCount;
  }
  public int getLoseCount(){
    return loseCount;
  }
  public int getDrawCount(){
    return drawCount;
  }
  public int getResignCount(){
    return resignCount;
  }
  public void addDrawCount(){
    drawCount++;
  }
  public void addResignCount(){
    resignCount++;
  }
  public void addDisConnectCount(){
    disConnectedCount++;
  }

  public void addRate(boolean victory,int player2Rate){

    if(victory){
      winCount++;
      myRate += (int)(200 + ( player2Rate - myRate) * 0.05);
    }else{
      loseCount++;
      if(player2Rate - myRate < 2000){
        myRate -= (int)(200 + ( myRate - player2Rate) * 0.05);
      }else{
        myRate -= 100;
      }
    }

    if(myRate<0){
      myRate = 1;
    }else if(myRate > 10000){
      myRate = 9999;
    }

  }
}
