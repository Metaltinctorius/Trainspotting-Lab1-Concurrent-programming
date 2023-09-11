import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import TSim.*;
//fuck git
public class Lab1 {

  public Lab1(int speed1, int speed2) {

    // Communication between simulator and program. (channel).
    TSimInterface tsi = TSimInterface.getInstance();

    // Train objects.
    Train train1 = new Train(1, speed1, Train.Direction.SOUTH);
    Train train2 = new Train(2, speed2, Train.Direction.NORTH);
    // Threads for the trains (input: train)
    Thread trainThread1 = new Thread(train1);
    Thread trainThread2 = new Thread(train2);

    // Start the threads
    trainThread1.start();
    trainThread2.start();
 
    try {

      // Default params for program initialization.
      // train1 = start pos north, train2 = star pos south
     tsi.setSpeed(1, speed1);
     tsi.setSpeed(2, speed2);

    } catch (CommandException e) {
      e.printStackTrace(); // or only e.getMessage() for the error
      System.exit(1);

    }
  }

  class Train implements Runnable {

    // Creation of a tsi interface
    TSimInterface tsi = TSimInterface.getInstance();

    // Parameters for a train
    int id;
    int speed;
    Direction direction;

    //Introducing semaphores
    private Semaphore semaphore1 = new Semaphore(1);
    private Semaphore semaphore2 = new Semaphore(1);
    // Constructor for a train
    public Train(int id, int speed, Direction direction) {
      this.id = id;
      this.speed = speed;
      this.direction = direction;
    }

    // Directions a train can go, either north or south (more develop to come)
    enum Direction {
      NORTH,
      SOUTH
    }
    
    // FUNCTIONS:
     /**
     * Returns true if sensor at given coords with given train id is active.
     * Basically instead of writing:
     * "se.getStatus() == se.ACTIVE"
     *
     * @param x
     * @param y
     * @param id
     * @param se
     * @return
     */
    public boolean sensor_active(int x, int y, int id, SensorEvent se) {
      if (se.getXpos() == x && se.getYpos() == y && se.getTrainId() == id) {
        if (se.getStatus() == se.ACTIVE) {
          return true;
        }
      }
      return false;
    }





  /**
     * Takes in the coords for the sensor, uses the running train thread as id, and
     * takes as parameter
     * a speed that the simulator sets the train to. As in, input whishful speed
     * when train hits sensor.
     *
     * @param x
     * @param y
     * @param id
     * @param speed
     * @param se
     *
     *              This method is basically abstraction to:
     *              if (sensorEvent.getXpos() == 8 && sensorEvent.getYpos() == 3
     *              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
     *              System.out.println("HELLO");
     *              tsi.setSpeed(id, 2);
     *              }
     */

    public void set_speed_at_sensor(int x, int y, int id, int speed, SensorEvent se) {
      if (se.getXpos() == x
          && se.getYpos() == y
          && sensor_active(x, y, id, se)) {
        try {
          tsi.setSpeed(id, speed);

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }


    public void break_and_reverse(int id, int speed, SensorEvent se) {
      try {
        tsi.setSpeed(id, 0);
        Thread.sleep(1000);
        System.out.println("Wait OK");
        tsi.setSpeed(id, -speed);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void set_switch_dir(int x, int y, Direction direction) {
      try {
        if (direction == Train.Direction.NORTH) {
          tsi.setSwitch(x, y, TSimInterface.SWITCH_RIGHT);
        } else if (direction == Train.Direction.SOUTH) {
          tsi.setSwitch(x, y, TSimInterface.SWITCH_LEFT);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    
/* Sensor events in a somewhat south to north direction */
    @Override
    public void run() {
      try {

        while (true) {
          SensorEvent sensorEvent = tsi.getSensor(id);

          if (sensor_active(1, 10, 1, sensorEvent)) {
            tsi.setSwitch(3,11, TSimInterface.SWITCH_RIGHT);
          }       
          if (sensor_active(4, 10, 1 , sensorEvent)) {
            tsi.setSwitch(4,9, TSimInterface.SWITCH_RIGHT);
          } 
          if (sensor_active(10, 9, 2, sensorEvent)) {
            semaphore1.acquire(1);
            try {
            tsi.setSpeed(id, 0);
            } finally {
            tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
              semaphore1.release();
            }
          }
          if (sensor_active(17, 9, 1, sensorEvent)) {
            semaphore1.acquire(1); 
            try {
            tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
            } finally {
              tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT);
              semaphore1.release();
            }
          }
          if (sensor_active(19, 8, 2, sensorEvent)) {
            tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
          } 
          if (sensor_active(14, 7, 1 , sensorEvent)) {
            tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
          }
          if (sensor_active(id, speed, id, sensorEvent)) {
            break_and_reverse(id, speed, sensorEvent);
          }
          if (sensor_active(id, speed, id, sensorEvent)) {
            break_and_reverse(id, speed, sensorEvent);
          }
          if (sensor_active(id, speed, id, sensorEvent)) {
          }
          if (sensor_active(id, speed, id, sensorEvent)) {
          }

        }
      } catch (Exception e) {
        e.printStackTrace();
        
      }

    }

  }

}
