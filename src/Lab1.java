import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import TSim.*;

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
      tsi.setSpeed(1, speed1);
      tsi.setSpeed(2, speed2);

    } catch (CommandException e) {
      e.printStackTrace(); // or only e.getMessage() for the error
      System.exit(1);

    }
  }

  class Train implements Runnable {


    // According to slides, a semaphore with one count is also a binary semaphore.
    Semaphore first_switch_semaphore = new Semaphore(1);



    // Creation of a tsi interface
    TSimInterface tsi = TSimInterface.getInstance();



    // Stack for checking semaphores at critical sections
    Stack<Double> stack = new Stack<>();


    // Parameters for a train
    int id;
    int speed;
    Direction direction;


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

    //------------- Functionality and Methods --------------

    /**
     * Method for reversing direction. Basically inverts the Enum Direction.
     * 
     * @param direction
     */
    public void reverseDirection(Direction direction) {
      if (direction == Train.Direction.NORTH) {
        direction = Train.Direction.SOUTH;
        System.out.println("Changed direction to " + direction);
      } else {
        direction = Train.Direction.NORTH;
        System.out.println("Changed direction to " + direction);
      }
    }



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

    /**
     * Method for breaking and reversing (by setting speed to negative)
     * 
     * @param id
     * @param speed
     * @param se
     */
    public void break_and_reverse(int id, int speed, SensorEvent se) {
      try {
        tsi.setSpeed(id, 0);
        Thread.sleep(2000);
        System.out.println("Wait OK");
        tsi.setSpeed(id, -speed);
        reverseDirection(direction);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /**
     * Sets the switch based on direciton. Maybe not needed.
     * @param x
     * @param y
     * @param direction
     */
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

          // Sensor event triggered by the "first" switch (top to bottom).
          if (sensor_active(19, 8, id, sensorEvent)){
            System.out.println(id + "activated switch");
            tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
            System.out.println(id + "activated switch");
            if (direction == Train.Direction.NORTH) {
              try {
                first_switch_semaphore.acquire(1);
                System.out.println("Train " + id + " acquired Semaphore");
                tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
                System.out.println("Switch set to RIGHT");
                if(sensor_active(14, 7, id, sensorEvent))
                {
                  tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
                  System.out.println("Switch set to LEFT");
                }
                if (direction == Train.Direction.SOUTH) {
                  if (sensor_active(19, 8, id, sensorEvent)) {
                    first_switch_semaphore.release();
                    System.out.println("Train " + id + " released Semaphore");
                  }
                }
              } finally {
                tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
              }
            }
          }
        
            

          





          
          // ----- Sensors for the spawn locations ------

          /*
           * Train 1 spawn location sensor, method for making any train that reaches
           * this point to brake and turn around, depending on direction, hence
           * only train 1 will be able to pass through at first initiation.
           */
          if (sensor_active(14, 3, id, sensorEvent)) {
            if (direction == Train.Direction.NORTH)
              break_and_reverse(id, speed, sensorEvent);
          }
          /**
           * Train 2 spawn location sensor. Any train that reaches this point will brake
           * and turn around.
           */
          if (sensor_active(13, 11, id, sensorEvent)) {
            if (direction == Train.Direction.SOUTH) {
              break_and_reverse(id, speed, sensorEvent);
            }
          }
          /**
           * Destination for train 1 (Station in south part of map).
           */
          if (sensor_active(15, 13, id, sensorEvent)) {
            if (direction == Train.Direction.SOUTH) {
              break_and_reverse(id, speed, sensorEvent);
            }
          }
          /**
           * Destination for train 2 (station in north part of map)
           */
          if (sensor_active(14, 5, id, sensorEvent)) {
            if (direction == Train.Direction.NORTH) {
              break_and_reverse(id, speed, sensorEvent);
            }
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
  