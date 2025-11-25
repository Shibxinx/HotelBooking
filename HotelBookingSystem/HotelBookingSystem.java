import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class HotelBookingSystem {

    static Scanner sc = new Scanner(System.in); //ประกาศ Scanner สำหรับรับค่าจากผู้ใช้ทั้งclass
//===========================================================================================================
    //เมนูหลักของโปรแกรม
    public static void main(String[] args) throws IOException {
        int choice;
        do {
            System.out.println("===== MAIN MENU =====");
            System.out.println("1. Book a Room");
            System.out.println("2. Check Booking Information");
            System.out.println("3. Exit Program");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    bookRoom();
                    break;
                case 2:
                    searchBooking();
                    break;
                case 3:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid menu option!");
            }
            System.out.println();
        } while (choice != 3);
    }
//===========================================================================================================
    // อ่านข้อมูลห้องจากไฟล์ room.txt และส่งกลับเป็นอาเรย์ของสตริง
    public static String[] readRooms() throws IOException {
        File file = new File("room.txt");
        if (!file.exists()) {
            throw new IOException("room.txt file not found");
        }

        BufferedReader x = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder data = new StringBuilder();

        while ((line = x.readLine()) != null) {
            data.append(line).append("\n");
        }
        x.close();

        return data.toString().split("\n");
    }
//===========================================================================================================
    // เช็คว่าห้องถูกจองแล้วหรือยัง
    public static boolean isRoomBooked(String roomId) throws IOException {
        File file = new File("customerroom.txt");
        if (!file.exists()) return false;

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("Room Info:") && line.contains(roomId)) {
                br.close();
                return true;
            }
        }
        br.close();
        return false;
    }
//===========================================================================================================
    // ดึงวันที่เช็คเอาท์ล่าสุดของห้อง
    public static String getLastCheckoutDate(String roomId) throws IOException {
        File file = new File("customerroom.txt");
        if (!file.exists()) return "N/A";

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        String lastCheckout = "N/A";
        boolean match = false;

        while ((line = br.readLine()) != null) {
            if (line.contains("Room Info:") && line.contains(roomId)) {
                match = true;
            } else if (match && line.startsWith("Check-out:")) {
                lastCheckout = line.replace("Check-out:", "").trim();
                match = false;
            }
        }
        br.close();
        return lastCheckout;
    }
//===========================================================================================================
    // เปรียบเทียบวันที่
    public static boolean isAfter(String date1, String date2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            return d1.after(d2);
        } catch (Exception e) {
            return false;
        }
    }
//===========================================================================================================
    // จองห้องพักโรงแรม
    public static void bookRoom() throws IOException {
        String[] rooms = readRooms();

        // เลือกระดับห้อง
        System.out.println("===== ROOM BOOKING =====");
        System.out.println("1.Standard Room");
        System.out.println("2.Superior Room");
        System.out.println("3.Deluxe Room");
        System.out.println("4.Suite Room");
        System.out.println("Select room type (1-4): ");
        int roomtype = 0;
        while (true){
            try{
                roomtype = Integer.parseInt(sc.nextLine());
                if (roomtype >=1 && roomtype <=4) break;
                else System.out.println("Please select a valid room type (1-4)!");
            }catch(Exception e){
                System.out.println("Invalid input! Please enter a number (1-4)!");
            }
        }//end while

        String roomType = "";
        switch (roomtype) {
            case 1 : 
                roomType = "Standard";
                break;
            case 2 : 
                roomType = "Superior";
                break;
            case 3 :
                roomType = "Deluxe";
                break;
            case 4 : 
                roomType = "Suite";
                break;
            default :
                roomType = "";
                break;
        };
        
        System.out.println("\n===== " + roomType + " ROOMS =====");
        for (String r : rooms) {
            if (r.toLowerCase().contains(roomType.toLowerCase())) {
                String roomId = r.split(" ")[0];
                boolean booked = isRoomBooked(roomId);
                if (booked) {
                    String lastCheckout = getLastCheckoutDate(roomId);
                    System.out.println(r + " (Already reserved until " + lastCheckout + ")");
                } else {
                    System.out.println(r + " (Available)");
                }
            }
        }

        System.out.print("\nEnter the room number to book: ");
        String roomId = sc.nextLine().trim();

        boolean found = false;
        String selectedRoom = "";
        for (String r : rooms) {
            if (r.startsWith(roomId)) {
                selectedRoom = r;
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Room not found in the system!");
            return;
        }

        System.out.print("Customer name: ");
        String name = sc.nextLine();
        
        int guests = 0;
        while (true){
            System.out.print("Number of guests: ");
            try{
                guests = Integer.parseInt(sc.nextLine());
                if (guests > 0 ) break;
                else System.out.print("Please enter a valid number of guests : ");
            }catch(Exception e){
                System.out.print("Invalid input! Please enter a number : ");
            }
        }//end while
        

        System.out.print("Check-in date (e.x. 15/11/2025): ");
        String checkIn = sc.nextLine();

        System.out.print("Check-out date (e.x. 20/11/2025): ");
        String checkOut = sc.nextLine();

        // ตรวจสอบถ้าห้องนี้ถูกจองแล้ว
        if (isRoomBooked(roomId)) {
            String lastCheckout = getLastCheckoutDate(roomId);
            
            if (!lastCheckout.equals("N/A") && !isAfter(checkIn, lastCheckout)) {
                System.out.println("This room is not available until after " + lastCheckout);
                return;
            }
        }

        // เขียนข้อมูลการจองลงในไฟล์ customerroom
        BufferedWriter xi = new BufferedWriter(new FileWriter("customerroom.txt", true));
        xi.write("Customer Name: " + name);
        xi.newLine();
        xi.write("Room Info: " + selectedRoom);
        xi.newLine();
        xi.write("Guests: " + guests);
        xi.newLine();
        xi.write("Check-in: " + checkIn);
        xi.newLine();
        xi.write("Check-out: " + checkOut);
        xi.newLine();
        xi.write("------------------------------");
        xi.newLine();
        xi.close();

        System.out.println("\n Room booked successfully!");
        System.out.println("===== BOOKING DETAILS =====");
        System.out.println("Customer Name: " + name);
        System.out.println("Room Info: " + selectedRoom);
        System.out.println("Guests: " + guests);
        System.out.println("Check-in: " + checkIn);
        System.out.println("Check-out: " + checkOut);
    }
//===========================================================================================================
    // method หาข้อมูลการจองโดยใช้ชื่อลูกค้า
    public static void searchBooking() throws IOException {
        File file = new File("customerroom.txt");
        if (!file.exists()) {
            System.out.println("No booking data found!");
            return;
        }

        System.out.print("Enter the customer name to search: ");
        String searchName = sc.nextLine().trim();

        BufferedReader xii = new BufferedReader(new FileReader(file));
        String line;
        boolean found = false;
        StringBuilder bookingInfo = new StringBuilder();

        while ((line = xii.readLine()) != null) {
            if (line.equals("------------------------------")) {
                if (found) break;
                bookingInfo.setLength(0);
            } else if (line.contains("Customer Name: " + searchName)) {
                found = true;
                bookingInfo.append(line).append("\n");
            } else if (found) {
                bookingInfo.append(line).append("\n");
            }
        }
        xii.close();

        if (found) {
            System.out.println("\n===== BOOKING DETAILS FOR " + searchName + " =====");
            System.out.println(bookingInfo);
        } else {
            System.out.println("No booking found for: " + searchName);
        }
    }
}
