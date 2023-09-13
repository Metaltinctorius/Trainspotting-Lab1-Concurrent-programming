import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import TSim.*;

public class Lab1 {

  public Lab1(int speed1, int speed2) {

    /**
     *  Communication between simulator and program. (channel).
     */
    TSimInterface tsi = TSimInterface.getInstance();

    /**
     * Creation of different semaphores.
     */
    Semaphore critical_section_1 = new Semaphore(1);
    Semaphore critical_section_2 = new Semaphore(1);
    Semaphore critical_section_3 = new Semaphore(1);
    Semaphore critical_section_4 = new Semaphore(1);
    Semaphore critical_section_intersection = new Semaphore(1);
    Semaphore critical_section_blockade = new Semaphore(1);


    /**
     * ArrayList for semaphores which is passed into the subclass "Train".
     */
    ArrayList<Semaphore> semaphores = new ArrayList<>();

    /**
     * Adding semaphores at fixed indexes in the arrayList.
     */
    semaphores.add(0, critical_section_1);
    semaphores.add(1, critical_section_2);
    semaphores.add(2, critical_section_3);
    semaphores.add(3, critical_section_4);
    semaphores.add(4,critical_section_intersection);
    semaphores.add(5, critical_section_blockade);

    /**
     * Creation of two different train object. Both with a "set" direction.
     */
    Train train1 = new Train(1, speed1, Train.Direction.SOUTH, semaphores);
    Train train2 = new Train(2, speed2, Train.Direction.NORTH, semaphores);

    /**
     * Threads for trains.
     */
    Thread trainThread1 = new Thread(train1);
    Thread trainThread2 = new Thread(train2);

    /**
     * Starting the threads.
     */
    trainThread1.start();
    trainThread2.start();

    try {

      /**
       * Default parameters for the program initialization.
       */
      tsi.setSpeed(1, speed1);
      tsi.setSpeed(2, speed2);

    } catch (CommandException e) {
      e.printStackTrace(); // or only e.getMessage() for the error
      System.exit(1);

    }
  }

   
    //#####################################   TRAIN CLASS  #########################################
  /**
   * Class for the trains that runs in the simulator.
   */
  class Train implements Runnable {

    /**
     * Interface for running the simulation.
     */
    TSimInterface tsi = TSimInterface.getInstance();

    /**
     * Train parameters.
     */
    int id;
    int speed;
    Direction direction;
    ArrayList<Semaphore> semaphores;

    /**
     * Train constructor
     */
    public Train(int id, int speed, Direction direction, ArrayList<Semaphore> semaphores) {
      this.id = id;
      this.speed = speed;
      this.direction = direction;
      this.semaphores = semaphores;
    }

    /**
     * Helper method to return current direction.
     * @return returns new direction.
     */
    public Direction getDirection() {
      return this.direction;
    }

    /**
     * Helper method to set (change) current direction to input direction.
     * @param dir is the input direction we want the train to take
     */
    public void setDirection(Direction dir) {
      this.direction = dir;
    }

    /**
     * Enum for directions a train can have.
     */
    enum Direction {
      NORTH,
      SOUTH
    }
    //#############################   FUNCTIONALITY AND METHODS   ##################################

    /**
     * Method for reversing direction. Basically inverts the Enum Direction.
     * 
     * @param direction this input direction will be inverted.
     */
    public void reverseDirection(Direction direction) {
      if (direction == Train.Direction.NORTH) {
        setDirection(Train.Direction.SOUTH);
      } else {
        setDirection(Train.Direction.NORTH);
      }
    }

    /**
     * Returns true if sensor at given coords with given train id is active.
     * Basically instead of writing:
     * "se.getStatus() == se.ACTIVE"
     *
     * @param x Input x coordinate, method compares with sensorEvent.getX()
     * @param y Input y coordinate, method compares with sensorEvent.getY()
     * @param id The identity of the train currently activating the sensor.
     * @param se short for SensorEvent.
     * @return returns true if any
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
      Semaphore critical_section_blockade = semaphores.get(5);

      try {

        while (true) {
          SensorEvent sensorEvent = tsi.getSensor(id);

        




          //------------------------------CRITICAL SECTION INTERSECTION --------------------------------

            

          // KÖR UT
          if(sensor_active(9, 5, id, sensorEvent)){
            if(getDirection() == Train.Direction.SOUTH){
              try{
                stop();
                critical_section_intersection.acquire();
                System.out.println(id +  " asdadaad INTERSECTION");
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


          // KÖR UT
          if(sensor_active(6, 5, id, sensorEvent)){
            if(getDirection() == Train.Direction.SOUTH){
              try{
                stop();
                critical_section_intersection.acquire();
                System.out.println(id +  " asdadaad INTERSECTION");
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
          
       
          // KÖR IN
          if(sensor_active(11, 8, id, sensorEvent)){
            System.out.println(id + " " + getDirection());
            if(getDirection() == Train.Direction.NORTH){
              
              System.out.println(id + "arrived at intersection");
              stop();
              try{
                critical_section_intersection.acquire();
                System.out.println(id +  " ACQUIRED INTERSECTION");
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

          // KÖR IN
           if(sensor_active(11, 7, id, sensorEvent)){
            System.out.println(id + " " + getDirection());
            if(getDirection() == Train.Direction.NORTH){
              
              System.out.println(id + "arrived at intersection");
              stop();
              try{
                critical_section_intersection.acquire();
                System.out.println(id +  " ACQUIRED INTERSECTION");
                drive();
              }catch (Exception e) {
                 e.printStackTrace();
                }
            }else{
              if(critical_section_intersection.availablePermits() < 1){
                critical_section_intersection.release();
                System.out.println("released  ashdahdnashdna" + critical_section_intersection);
              }
            }
          }





          // -----------------------------------------------------------------------------------------------------------

          //----------------------------------- CRITICAL SECTION 4 ---------------------------------

          if(sensor_active(19, 8, id, sensorEvent)){
            if(getDirection() == Train.Direction.NORTH){
              tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
              critical_section_3.release();
              System.out.println("Released 3 " + critical_section_3 + " at sensor 19 8");
              try{
                Thread.sleep(500);
                critical_section_4.acquire();
                System.out.println("ACQUIRE CRIT 4");
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



          //----------------------------------------------------------------------------------------



          //------ Semaphore release from sensor positions, probably need some more of those SECTION 2-----------------------------------------------------------------------------------------------
          if (sensor_active(18, 9, id, sensorEvent)) {
            if(direction == Train.Direction.NORTH) {
              if(critical_section_2.availablePermits() < 1){
                critical_section_2.release();
              }
                
                }  
              }

          if (sensor_active(1, 10, id, sensorEvent)) {
            if(direction == Train.Direction.SOUTH) {
              if(critical_section_2.availablePermits() < 1){
                critical_section_2.release();
              }
                
                }  
              }




          
           //Critical Section 2.
           //-----------------------------------------------------------------------------------------------------------
        

           if (sensor_active(18, 9, id, sensorEvent)) {
            if (direction == Train.Direction.SOUTH) {
              try {
                if (critical_section_2.tryAcquire()){
                  System.out.println("Acquired " + critical_section_2);
                  tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT);
                } else {
                  tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT);
                } 
              
              }
              catch (Exception e) {
                e.printStackTrace();
              }
            }else{
              if(critical_section_2.availablePermits() < 1){
                critical_section_2.release();
              }
            }
            
          } 
            
         if (sensor_active(1, 10, id, sensorEvent)) {
            if (direction == Train.Direction.NORTH) {
              try {
                if (critical_section_2.tryAcquire()) {
                  System.out.println(critical_section_2.availablePermits());
                  System.out.println("TOOK SECTION 2");
                  tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT);
                } else {
                  System.out.println("COULDNT TAKE 2");
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
              try{
                stop();
                critical_section_3.acquire();
                drive();
                tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT);
              }
              catch (Exception e) {
                e.printStackTrace();
            } 
            }
            else{
              if(critical_section_3.availablePermits() < 1){
                  critical_section_3.release();
            }
           }
          }
          // -----------------------------------------------------------------------------------------------------------

          // ----------------------------------------- CRITICAL SECTION 3 // -------------------------------------------


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
                critical_section_3.release();
                tsi.setSwitch(17,7, TSimInterface.SWITCH_RIGHT);
                 System.out.println("Released semaphore " + critical_section_3);
                if(critical_section_3.availablePermits() < 1){
                  
                  critical_section_3.release();
                  System.out.println("Released semaphore " + critical_section_3);
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
                  System.out.println("Train " + id + "Acquired semaphore " + critical_section_3);
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

         

            if(sensor_active(14, 8, id, sensorEvent)){
              //tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT);
              if(getDirection() == Train.Direction.SOUTH){
                try{
                stop();
                critical_section_3.acquire();
                Thread.sleep(2000);
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
                if(critical_section_2.availablePermits() < 1){
                critical_section_2.release();
                }
                try{
                if(critical_section_1.tryAcquire()){
                  tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT);
                }else{
                  tsi.setSwitch(3, 11, TSimInterface.SWITCH_LEFT);
                }
                }
                catch (Exception e) {
                 e.printStackTrace();
                }
              }
            } 


               if(sensor_active(6, 13, id , sensorEvent)){
              if(getDirection() == Train.Direction.NORTH){
                try{
                  stop();
                  critical_section_1.release();
                  critical_section_1.acquire();
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
              }else{
                if(critical_section_1.availablePermits() < 1){
                  critical_section_1.release();
                }
              }
            }

            //+++++++++++++++++++ UNDER CONSTRUCTION +++++++++++++++++++++

            if(sensor_active(6, 9, id, sensorEvent)){
              if(getDirection() == Train.Direction.SOUTH){
                try{
                  stop();
                  if(critical_section_2.availablePermits() < 1){
                    critical_section_2.release();
                  }
                  critical_section_2.acquire();
                  drive();
                  tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT);
                }catch (Exception e) {
                 e.printStackTrace();
                }
              }else{
                if(critical_section_1.availablePermits() < 1){
                  critical_section_1.release();
                }
              }
            }
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++

            if(sensor_active(6, 11, id , sensorEvent)){
              if(getDirection() == Train.Direction.NORTH){
                try{
                  stop();
                  critical_section_1.acquire();

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
