import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import TSim.*;

public class Lab1 {

  public Lab1(int speed1, int speed2) {

    // Communication between simulator and program. (channel).
    TSimInterface tsi = TSimInterface.getInstance();

    // ArrayList för Semaforer.
    // 11 September. 17:45
    // -----------------------------------------------------------

    Semaphore critical_section_1 = new Semaphore(1);
    Semaphore critical_section_2 = new Semaphore(1);
    Semaphore critical_section_3 = new Semaphore(1);
    Semaphore critical_section_4 = new Semaphore(1);
    Semaphore critical_section_intersection = new Semaphore(1);

    ArrayList<Semaphore> semaphores = new ArrayList<>();

    semaphores.add(0, critical_section_1);
    semaphores.add(1, critical_section_2);
    semaphores.add(2, critical_section_3);
    semaphores.add(3, critical_section_4);
    semaphores.add(4,critical_section_intersection);
    System.out.println(semaphores);

    // ------------------------------------------------------------

    // Train objects.
    Train train1 = new Train(1, speed1, Train.Direction.SOUTH, semaphores);
    Train train2 = new Train(2, speed2, Train.Direction.NORTH, semaphores);

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

    // According to slides, a semaphore with one count is also a binary semaphore.
    // Semaphore critical_section_3 = new Semaphore(1);

    // Creation of a tsi interface
    TSimInterface tsi = TSimInterface.getInstance();

    // Stack for checking semaphores at critical sections
    Stack<Double> stack = new Stack<>();

    // Parameters for a train
    int id;
    int speed;
    Direction direction;
    ArrayList<Semaphore> semaphores;

    // Constructor for a train
    public Train(int id, int speed, Direction direction, ArrayList<Semaphore> semaphores) {
      this.id = id;
      this.speed = speed;
      this.direction = direction;
      this.semaphores = semaphores;
    }

    public Direction getDirection() {
      return this.direction;
    }

    public void setDirection(Direction dir) {
      this.direction = dir;
    }

    // Directions a train can go, either north or south (more develop to come)
    enum Direction {
      NORTH,
      SOUTH
    }

    // ------------- Functionality and Methods --------------

    /**
     * Method for reversing direction. Basically inverts the Enum Direction.
     * 
     * @param direction
     */
    public void reverseDirection(Direction direction) {
      if (direction == Train.Direction.NORTH) {
        setDirection(Train.Direction.SOUTH);
        System.out.println("Changed direction to " + getDirection());
      } else {
        setDirection(Train.Direction.NORTH);
        System.out.println("Changed direction to " + getDirection());
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
        if (direction == Train.Direction.SOUTH) {
          tsi.setSpeed(id, -speed);
          reverseDirection(direction);
        } else {
          tsi.setSpeed(id, -speed);
          setDirection(Train.Direction.SOUTH);
          // reverseDirection(direction);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    void drive() throws CommandException {
      tsi.setSpeed(id, speed);
    }

    void stop() throws CommandException {
      tsi.setSpeed(id, 0);
    }

    void reverse() throws CommandException {
      speed = -1 * speed;
      drive();
    }

    int updateSpeed(int speed) {
      int newSpeed = speed;
      return newSpeed;
    }

    int currentSpeed() {
      return speed;
    }

    /* Sensor events in a somewhat south to north direction */
    @Override
    public void run() {

      Semaphore critical_section_1 = semaphores.get(0);
      Semaphore critical_section_2 = semaphores.get(1);
      Semaphore critical_section_3 = semaphores.get(2);
      Semaphore critical_section_4 = semaphores.get(3);
      Semaphore critical_section_intersection = semaphores.get(4);

      try {

        while (true) {
          SensorEvent sensorEvent = tsi.getSensor(id);

        




          //------------------------------CRITICAL SECTION INTERSECTION --------------------------------

            

          if(sensor_active(9, 5, id, sensorEvent) || sensor_active(6, 5, id, sensorEvent)){
            if(getDirection() == Train.Direction.SOUTH){
              try{
                stop();
                critical_section_intersection.acquire();
                drive();
              }catch (Exception e) {
                 e.printStackTrace();
                }
            }else{
              if(critical_section_intersection.availablePermits() < 1){
                critical_section_intersection.release();
              }
            }
            
          }
          
       
          if(sensor_active(10, 8, id, sensorEvent) || sensor_active(10, 7, id, sensorEvent)){
            if(getDirection() == Train.Direction.NORTH){
              try{
                stop();
                critical_section_intersection.acquire();
                drive();
              }catch (Exception e) {
                 e.printStackTrace();
                }
            }else{
              if(critical_section_intersection.availablePermits() < 1){
                critical_section_intersection.release();
              }
            }
          }





          // -----------------------------------------------------------------------------------------------------------

          //----------------------------------- CRITICAL SECTION 4 ---------------------------------

          if(sensor_active(19, 8, id, sensorEvent)){
            if(getDirection() == Train.Direction.NORTH){
              tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
              try{
                critical_section_4.acquire();
                tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
              }catch (Exception e) {
                 e.printStackTrace();
                }
            }else{
              if(critical_section_4.availablePermits() < 1){
                critical_section_4.release();
              }
              
            }
          }

          if(sensor_active(14, 8, id, sensorEvent)){
            if(getDirection() == Train.Direction.SOUTH){
              if(critical_section_4.availablePermits() < 1){
              critical_section_4.release();
              }
            }
          }



          //----------------------------------------------------------------------------------------



          //------ Semaphore release from sensor positions, probably need some more of those SECTION 2-----------------------------------------------------------------------------------------------
          if (sensor_active(18, 9, id, sensorEvent)) {
            if(direction == Train.Direction.NORTH) {
              if(critical_section_2.availablePermits() < 1){
                critical_section_2.release();
                  System.out.println(" Released critical section "); 
              }
                
                }  
              }

          if (sensor_active(18, 9, id, sensorEvent)) {
            if(direction == Train.Direction.SOUTH) {
              if(critical_section_2.availablePermits() < 1){
                critical_section_2.release();
                  System.out.println(" Released critical section "); 
              }
                
                }  
              }




          
           //Critical Section 2.
           //-----------------------------------------------------------------------------------------------------------
        

           if (sensor_active(18, 9, id, sensorEvent)) {
            if (direction == Train.Direction.SOUTH) {
              try {
                if (critical_section_2.tryAcquire()){
                  tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT);
                  System.out.println(" acquired Semaphore Critical Section " + semaphores.get(id));
                } else {
                  tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
                } 
              
              }
              catch (Exception e) {
                e.printStackTrace();
              }
            }
            
          } 
            
         if (sensor_active(1, 10, id, sensorEvent)) {
            if (direction == Train.Direction.NORTH) {
              try {
                if (critical_section_2.tryAcquire()) {
                  System.out.println(" acquired critical section ");
                  tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT);
                } else {
                  tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);
              }
            } 
              catch (Exception e) {
                e.printStackTrace();
            } 
          }
         } 
        
           if(sensor_active(12, 10, id, sensorEvent)){
            if(getDirection() == Train.Direction.NORTH){
              tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
            }
           }


          // -----------------------------------------------------------------------------------------------------------

          // ----------------------------------------- CRITICAL SECTION 3 // -------------------------------------------


          boolean entry_protocol = false;
          boolean exit_protocol = false;

       
          

            // If driving south, acquire semaphore, if not possible, stop and wait.
            // This is a case of "företräde", where one has to wait. This one binds with 13,9.
          if(sensor_active(14, 7, id, sensorEvent)){
            if(getDirection() == Train.Direction.SOUTH){
                try{
                tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
                tsi.setSpeed(id, 0);
                critical_section_3.acquire();
                tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
                tsi.setSpeed(id, speed);
                }catch (Exception e) {
                 e.printStackTrace();
                }
              }
              else{
                if(critical_section_3.availablePermits() < 1){
                  critical_section_3.release();
                }
              }
            }

            // If driving north, aim for station, set switch to left. 
            if(sensor_active(12, 9, id, sensorEvent)){
              if(getDirection() == Train.Direction.NORTH){
                try{
                  //tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT);
                  tsi.setSpeed(id, 0);
                  critical_section_3.acquire();
                  tsi.setSpeed(id, speed);
                  tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
                  tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
                }catch (Exception e) {
                 e.printStackTrace();
                }
              }else{
                if(critical_section_3.availablePermits() <1){
                critical_section_3.release();
                System.out.print("Released " + critical_section_3);
              }
            }
            }

            // If for some reason the train has the semaphore, and travels south on the 
            // diverted path, release semaphore.
            // MIGHT NOT BE NEEDED!!!
            if(sensor_active(12, 10, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                if(critical_section_3.availablePermits() < 1){
                  critical_section_3.release();
              }
            }
          }

            if(sensor_active(14, 8, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                try{
                stop();
                critical_section_3.acquire();
                drive();
                tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
                }catch (Exception e) {
                 e.printStackTrace();
                }
                
              }else{
                tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
                if(critical_section_3.availablePermits() < 1){
                critical_section_3.release();
                }
              }
            }

            /* if(sensor_active(18, 9, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                if(critical_section_3.availablePermits() < 1){
                  critical_section_3.release();
                }
                
              }
              else {
              }
            } */



            //------------------------------CRITICAL SECTION 1 A--------------------------------


            if(sensor_active(1, 10, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                try{
                tsi.setSwitch(3, 11, TSimInterface.SWITCH_LEFT);
                critical_section_2.release();
                critical_section_1.acquire();
                System.out.println("Aqcuired " + critical_section_1);
                tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT);
                }
                catch (Exception e) {
                 e.printStackTrace();
                }
              }else{
                System.out.println("SWITCHED");
                tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT);
                if(critical_section_1.availablePermits() < 1){
                critical_section_1.release();
                System.out.println("Released " + critical_section_1);
                }
              }
            } 


               if(sensor_active(6, 13, id , sensorEvent)){
              if(getDirection() == Train.Direction.NORTH){
                try{
                  stop();
                  System.out.println("Started");
                  critical_section_1.release();
                  critical_section_1.acquire();
                  System.out.println("Took " + critical_section_1);
                  drive();
                  tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT);
                }
                catch (Exception e) {
                 e.printStackTrace();
                }
              }
              }
            
            

            //---------------------------------------------------------------------------------------




            //------------------------------CRITICAL SECTION 2 (Sub part).--------------------------------

            if(sensor_active(6, 10, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                try{
                  stop();
                  critical_section_2.acquire();
                  drive();
                  tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);
                }catch (Exception e) {
                 e.printStackTrace();
                }
              }
            }


            if(sensor_active(6, 9, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                try{
                  stop();
                  Thread.sleep(1000);
                  tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT);
                  critical_section_2.acquire();
                  drive();
                }catch (Exception e) {
                 e.printStackTrace();
                }
              }
            }


            if(sensor_active(6, 11, id , sensorEvent)){
              if(getDirection() == Train.Direction.NORTH){
                try{
                  stop();
                  System.out.println("Started");
                  critical_section_1.acquire();
                  System.out.println("Took " + critical_section_1);
                  drive();
                  tsi.setSwitch(3, 11, TSimInterface.SWITCH_LEFT);
                }
                catch (Exception e) {
                 e.printStackTrace();
                }
              }
            }
    

          

    

            

            
            //--------------------------------------------------------------------



            if(sensor_active(6, 10, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);
              }
            }



          /*
           * // if(sensor_active(1, 10, id, sensorEvent))
           * 
           * if (sensor_active(1, 10, 1, sensorEvent)) {
           * tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT);
           * }
           * 
           * if (sensor_active(12, 10, 1, sensorEvent)) {
           * tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT);
           * }
           * if (sensor_active(6, 10, 1, sensorEvent)) {
           * tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);
           * }
           * /* if (sensor_active(10, 9, 2, sensorEvent)) {
           * semaphore.acquire(1);
           * 
           * // try {
           * // tsi.setSpeed(id, 0);
           * // } finally {
           * tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
           * // critical_section_2.release();
           * // }
           * }
           * 
           * if (sensor_active(19, 8, 2, sensorEvent)) {
           * tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
           * }
           * if (sensor_active(14, 7, 1, sensorEvent)) {
           * tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
           * }
           * if (sensor_active(id, speed, id, sensorEvent)) {
           * break_and_reverse(id, speed, sensorEvent);
           * }
           * if (sensor_active(id, speed, id, sensorEvent)) {
           * break_and_reverse(id, speed, sensorEvent);
           * }
           * if (sensor_active(id, speed, id, sensorEvent)) {
           * }
           * if (sensor_active(id, speed, id, sensorEvent)) {
           * }
           */

          // ----- Sensors for the spawn locations ------

          /*
           * Train 1 spawn location sensor, method for making any train that reaches
           * this point to brake and turn around, depending on direction, hence
           * only train 1 will be able to pass through at first initiation.
           */
          if (sensor_active(14, 3, id, sensorEvent) || sensor_active(14, 5, id, sensorEvent)) {
            if (direction == Train.Direction.NORTH) {
              stop();
              reverseDirection(direction);
              Thread.sleep(1000 + (20 * speed));
              reverse();
            }
          }

          if (sensor_active(13, 11, id, sensorEvent) || sensor_active(15, 13, id, sensorEvent)) {
            if (getDirection() == Train.Direction.SOUTH) {
              stop();
              reverseDirection(direction);
              Thread.sleep(1000 + (20 * speed));
              reverse();
            }
          }
        } 
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
