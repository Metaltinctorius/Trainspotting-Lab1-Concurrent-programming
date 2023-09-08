import java.util.ArrayList;

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

    @Override
    public void run() {
      try {

        while (true) {
          SensorEvent sensorEvent = tsi.getSensor(id);

          if (sensor_active(10, 7, id, sensorEvent)){
            System.out.println("HELLO");
          }
          if (sensorEvent.getXpos() == 17 && sensorEvent.getYpos() == 9
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            System.out.println("WORLD");
            tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
          }
          if (sensorEvent.getXpos() == 16 && sensorEvent.getYpos() == 7
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
          }
           if (sensorEvent.getXpos() == 5 && sensorEvent.getYpos() == 9
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT);
          } 
          /* if (sensorEvent.getXpos() == 2 && sensorEvent.getYpos() == 11
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT);
          } */
          if (sensorEvent.getXpos() == 10 && sensorEvent.getYpos() == 13
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            break_and_reverse(id, speed, sensorEvent);
          }

          // NEW SENSOR.. V2
          if (sensorEvent.getXpos() == 13 && sensorEvent.getYpos() == 11
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            if (direction == Train.Direction.SOUTH) {
              break_and_reverse(id, speed, sensorEvent);
            }
          }
          if (sensorEvent.getXpos() == 13 && sensorEvent.getYpos() == 3
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            break_and_reverse(id, speed, sensorEvent);
          }

          if (sensorEvent.getXpos() == 1 && sensorEvent.getYpos() == 10
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            if (direction == Train.Direction.SOUTH) {
              System.out.println("SWITCH 1");
              tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);
            }
            // set_switch_dir(4, 9, direction);
          }
          if (sensorEvent.getXpos() == 10 && sensorEvent.getYpos() == 9
              && sensorEvent.getStatus() == sensorEvent.ACTIVE) {
            if (direction == Train.Direction.NORTH) {
              System.out.println("SWITCH 2");
              tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
            }
            // set_switch_dir(4, 9, direction);
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
        ;
      }

    }

  }

}
