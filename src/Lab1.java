import TSim.*;

public class Lab1 {

  public Lab1(int speed1, int speed2) {
    TSimInterface tsi = TSimInterface.getInstance();
    // Need to create individual sensor objects to be able to separate different events?
    //SensorEvent sensor1 = new SensorEvent(1, 10, 7, 0);
    //System.out.println(sensor1);
    try {

      tsi.setSpeed(1, speed1);
      if (tsi.getSensor(1).getStatus() == 1) {
        tsi.setSpeed(1, 0);
      }
      //tsi.getSensor(1);

      tsi.setSwitch(15,9, TSimInterface.SWITCH_RIGHT) ;

      //tsi.setSpeed(2,0);
      //sensorX = tsi.getSensor(2);


      // tsi.getSensor(2) = sensor1;


    } catch (CommandException e) {
      e.printStackTrace();// or only e.getMessage() for the error
      System.err.println(e.getMessage());
      System.exit(1);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

