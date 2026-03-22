import java.sql.*;
import java.util.Scanner;

public class Main  {

    private static final String url="jdbc:mysql://127.0.0.1:3306/hotel_db";
    private static final String username="root";
    private static final String password="kashish@25";

    public static void main(String[] args) throws Exception
    {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection con= DriverManager.getConnection(url,username,password);
            System.out.println("connection established successfully");
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM!!");
                Scanner sc=new Scanner(System.in);
                System.out.println("1. Reserve a room.");
                System.out.println("2. View Reservations.");
                System.out.println("3. Get Room Number.");
                System.out.println("4. Update Reservations.");
                System.out.println("5. Delete Reservations.");
                System.out.println("0. Exit.");
                System.out.print("Choose an option:");
                int choice=sc.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(con,sc);
                        break;
                        case 2:
                            viewReservation(con);
                            break;
                    case 3:
                        getRoomNumber(con,sc);
                        break;
                    case 4:
                        updateReservation(con,sc);
                        break;
                    case 5:
                        deleteReservation(con,sc);
                        break;
                    case 0:
                        exit();
                        break;
                    default:
                        System.out.println("Wrong choice");
                }
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }

    }

    public static void reserveRoom(Connection con,Scanner sc){
        try{
            System.out.println("Enter Guest Name: ");
            String guestName=sc.next();
            sc.nextLine();
            System.out.println("Enter Room Number: ");
            int room_number=sc.nextInt();
            System.out.println("Enter Contact Number");
            String contact_number=sc.next();

            String sql="INSERT INTO RESERVATIONS(GUEST_NAME,ROOM_NUMBER,CONTACT_NO) VALUES('"+guestName+"','"+room_number+"','"+contact_number+"')";

            try(Statement st=con.createStatement()){
                int rows_affected=st.executeUpdate(sql);

                if(rows_affected>0){
                    System.out.println("Reservation successful!!");
                }
                else {
                    System.out.println("Reservation failed!!");
                }
            }
        }
        catch(SQLException e)
        {
           e.printStackTrace();
        }

    }

    public static void viewReservation(Connection con){
        String sql="SELECT RESERVATION_ID,GUEST_NAME,ROOM_NUMBER,CONTACT_NO,RESERVATION_DATE FROM RESERVATIONS";
        try(Statement st=con.createStatement();
        ResultSet rs=st.executeQuery(sql)){
            System.out.println("Current Reservations:");
            System.out.println("+----------------+------------------------+------------------+------------------+--------------------+");
            System.out.println("| Reservation ID | Guest name      | Room Number   | Contact Number      | Reservation Date          |");
            System.out.println("+----------------+------------------------+------------------+------------------+--------------------+");

            while(rs.next()){
                int reservation_id=rs.getInt("RESERVATION_ID");
                String guest_name=rs.getString("GUEST_NAME");
                int room_number=rs.getInt("ROOM_NUMBER");
                String contact_number=rs.getString("CONTACT_NO");
                String reservation_date=rs.getTimestamp("RESERVATION_DATE").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservation_id, guest_name, room_number, contact_number, reservation_date);
            }


            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");



        }catch(SQLException e)
        {
            e.printStackTrace();
        }

    }

    public static void getRoomNumber(Connection con,Scanner sc){
      try {
          System.out.println("Enter Reservation ID: ");
          int reservation_id = sc.nextInt();
          System.out.println("Enter Guest Name: ");
          String guestName = sc.next();
          sc.nextLine();


          String sql = "SELECT ROOM_NUMBER FROM RESERVATIONS WHERE RESERVATION_ID=" + reservation_id ;
          try (Statement st = con.createStatement();
               ResultSet rs = st.executeQuery(sql)) {
              if (rs.next()) {
                  int room_number = rs.getInt("ROOM_NUMBER");
                  System.out.println("Room Number for the Reservation id "+reservation_id+" with guest "+guestName+" is "+room_number);
              }
              else {
                  System.out.println("Reservation for the given ID and Guest Name not found");
              }

          }


      }catch(SQLException e)
      {
          System.out.println(e.getMessage());
      }

    }

    public static void updateReservation(Connection con,Scanner sc){
        try{
            System.out.println("Enter Reservation ID to update:");
            int reservation_id=sc.nextInt();
            if(!reservationExists(con,reservation_id))
            {
                System.out.println("Reservation ID does not exist for the given ID");
                return;
            }

            System.out.println("Enter New Guest Name: ");
            String newGuestName=sc.next();
            sc.nextLine();
            System.out.println("Enter New Room Number: ");
            int newRoom_number=sc.nextInt();
            System.out.println("Enter New Contact Number: ");
            String newContact_number=sc.next();

            String sql="UPDATE RESERVATIONS SET GUEST_NAME = '"+newGuestName +"',"+
                    "room_number = "+newRoom_number+","+
                    "contact_no='"+newContact_number+"' WHERE RESERVATION_ID="+reservation_id;

            try(Statement st=con.createStatement()){
                int rows_affected=st.executeUpdate(sql);
                if(rows_affected>0){
                    System.out.println("Reservation updated successfully!");

                }
                else{
                    System.out.println("Reservation Updation failed!!");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void deleteReservation(Connection con,Scanner sc){
        try{
            System.out.println("Enter Reservation ID: ");
            int reservation_id=sc.nextInt();
            if(!reservationExists(con,reservation_id))
            {
                System.out.println("Reservation ID does not exist for the given ID");
                return;
            }

            String sql="DELETE FROM RESERVATIONS WHERE RESERVATION_ID= "+reservation_id;

            try(Statement st=con.createStatement())
            {
                int rows_affected=st.executeUpdate(sql);
                if(rows_affected>0){
                    System.out.println("Reservation Deleted successfully!");
                }
                else{
                    System.out.println("Reservation Deletion failed!!");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public static boolean reservationExists(Connection con,int reservation_id){
        try{
            String sql="SELECT RESERVATION_ID FROM RESERVATIONS WHERE RESERVATION_ID="+reservation_id;

            try(Statement st=con.createStatement();
            ResultSet rs=st.executeQuery(sql)) {
                return rs.next();

            }

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }

    }
    public static void exit()throws InterruptedException{
        System.out.print("Exiting System");
        int i=5;
        while(i!=0)
        {
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for Using Hotel Reservation System.");
    }
}
