import java.util.*;
/**
* The Transaction Class Uses the codes from the Transaction File
* to gather the important information from Current User File
* and Available Ticket File to make changes in the Ticket Service
* by updating necessary information.
*
* @author  Sinthooran Ravinathan
* @author  Samuel Pilkey
* @author  Hamza Naseer
* @version 1.0
* @since   2019-03-04
*/

public class TransactionHandler{
    private
        Validator tran_validator;
    public TransactionHandler(){
        tran_validator = new Validator();
    }

    /**
    * Function takes in a line from the daily transaction file to create user
    *
    * @param addLine is a line from the daily transaction file
    *                it has all the information to create a new user.
    *
    * @param user_file is a
    */
    public void createUser(String addLine, List<String> user_file){

        String user_to_add = addLine.substring(3, addLine.length());

        if (tran_validator.checkUser_exist(user_to_add, user_file) == false){
            user_file.add(user_to_add);
        } else {
            System.out.println("Error: Cannot add user as user already exist");
        }
    }

    /**
    * Function takes in a line from the daily transaction file to create an event
    *
    * @param sellLine is a line from the daily transaction file
    *                it has all the information to create events.
    */
    public void sellTickets(String sellLine, List<String> tickets_file, List<String> user_file) {

        String ticket_to_add = sellLine.substring(3, sellLine.length());

        if (tran_validator.checkTicket_exists(ticket_to_add, tickets_file) == false && getUser(sellLine.substring(29, 44),  user_file) != null){
            tickets_file.add(ticket_to_add);
        } else {
            System.out.println("Error: Cannot sell tickets as these tickets is already being");
        }
    }

    /**
    * Function takes in the current user line and the amount to add line
    * adds the amount to the current user.
    *
    * @param addLine this string is the line from the daily transaction file
    *                breaks the line to get the amount to add.
    *
    * @param currentLine this string takes in thr line from the current user file
    *                    it gets the usernameme, code, and amount.
    */
    public void addCredit(String addLine, String currentline){
        double newAmount;
        String newString;

        // get the amount to add
        String addCode = addLine.substring(0,2);
        String addUsername = addLine.substring(2,15);
        double addAmount = Double.parseDouble(addLine.substring(39,9));

        // get the current users amount
        String usernameCode = currentline.substring(0,19);
        double cuAmount = Double.parseDouble(currentline.substring(20,9));
        // new amount to add to user account file
        newAmount = addAmount + cuAmount;
        // new stirng to replace in file
        newString = usernameCode + Double.toString(newAmount);
    }

    /**
    * Function takes in the current user line and deletes that User
    * from the System.
    *
    * @param deleteLine this string grabs the line from the daily transaction file
    *                then gets the substrings for the respective attributes
    *
    * @param currentline this string takes in thr line from the current user file
    *                    and gets substrings for respective attributes
    */
    public void deleteUser(String deleteLine, List<String> user_file){
        //String used to Update the deleted User in the Current Users File
        String user_to_delete = deleteLine.substring(3, deleteLine.length());

        if(tran_validator.checkUser_exist(user_to_delete, user_file) == true){
            user_file.remove(user_to_delete);
        }else{
            System.out.println("Error: User to be deleted was not found");
        }
        //Updates the String
    }

    /**
    * Function takes in the refind line from the daily transaction file
    * then it takes the lines from the user file and gives credit to the
    * buyer and takes away credits from the seller
    *
    * @param refundLine takes in the line needed to be refunded from the transaction file
    *                   and gives the buyer username and seller name and amount
    *
    * @param userLine takes in the buyer and seller information
    *                 gets the amount of credits they have and refunds the buyer
    *                 whole taking money back from the seller
    */
    public void refundUser(String refundLine, String userLine, String sellerLine){

        String newBuyerString;
        String newSellerString;
        String sellerName = refundLine.substring(4,16);
        String buyerName = refundLine.substring(20,12);
        Double refundAmount = Double.parseDouble(refundLine.substring(36,9));

        if (buyerName == userLine.substring(0,15))
        {
            String buyerinfo = userLine.substring(0,19);
            double buyerAmount = Double.parseDouble(userLine.substring(20,9));

            buyerAmount += refundAmount;
            newBuyerString = buyerinfo +  Double.toString(buyerAmount);
        }
        else if (sellerName == userLine.substring(0,15))
        {
            String sellerinfo = userLine.substring(0,19);
            double sellerAmount = Double.parseDouble(userLine.substring(20,9));

            sellerAmount -= refundAmount;

            newSellerString = sellerinfo + Double.toString(sellerAmount);
        }
    }

    /**
    * Function takes in the buying ticket line from the file
    * then checks if the number of tickets a available.
    * then checks if the user has enough credit to buy the tickets
    *
    * @param ticketLine gets the line where it tells the function which
    *                   user wants to buy and how much tickets
    *
    * @param userLine gets the userline that would give the user information
    *                 checks username and if the user has enough credits.
    */
    public void buyTickets(String ticketLine, String userLine, List<String> user_file, List<String> tickets_file, List<String> init_tickets_file){
        String event_name = ticketLine.substring(3, 28);
        String seller_username = ticketLine.substring(29, 44);

        String ticket_info = ticketLine.substring(3, ticketLine.length());
        String remaining_ticket_info = getTickets(event_name, tickets_file);
        String init_ticket_info = getTickets(event_name, init_tickets_file);

        String buyer_info = userLine.substring(3,userLine.length());
        Double buyer_credit = Double.parseDouble(userLine.substring(22, 31));

        String seller_info = getUser(seller_username, user_file);

        if (ticket_info != null && remaining_ticket_info != null){

            int tickets_sold = Integer.parseInt(init_ticket_info.substring(42,45)) - Integer.parseInt(ticket_info.substring(42,45));
            int tickets_left = Integer.parseInt(remaining_ticket_info.substring(42,45));
            if (tickets_left > tickets_sold) {
                tickets_left -= tickets_sold;
                if (seller_info != null){

                    double seller_credit = Double.parseDouble(seller_info.substring(19,28));
                    double ticket_price = Double.parseDouble(init_ticket_info.substring(46, 51));
                    double total_price = tickets_sold * ticket_price;


                    if (total_price < seller_credit && total_price < buyer_credit && seller_credit + total_price < 999999.99){

                        buyer_credit -= total_price;
                        seller_credit += total_price;

                        String format = "%6.2f";  // width == 6 and 2 digits after the dot

                        String b_credit_padded = String.format(format, buyer_credit);
                        String s_credit_padded = String.format(format, seller_credit);

                        String pad = "0";

                        b_credit_padded = pad.repeat(9 - b_credit_padded.length()) + b_credit_padded;
                        s_credit_padded = pad.repeat(9 - s_credit_padded.length()) + s_credit_padded;
                        //b_credit_padded = ("000000000" + b_credit_padded).substring(b_credit_padded.length());
                        //s_credit_padded = ("000000000" + s_credit_padded).substring(s_credit_padded.length());

                        String new_buyer_info = buyer_info.substring(0, 19);
                        String new_seller_info = seller_info.substring(0, 19);

                        new_buyer_info += b_credit_padded;
                        new_seller_info += s_credit_padded;

                        user_file.remove(buyer_info);
                        user_file.remove(seller_info);

                        user_file.add(new_buyer_info);
                        user_file.add(new_seller_info);

                    } else {
                        System.out.println("Error: seller or buyer do not have enough credit remaining or Seller exceeded mac credit");
                    }
                } else {
                    System.out.println("Error: Seller no longer exists in this data set");
                }
            } else {
                System.out.println("Error: Not enough tickets remaining");
            }
        } else {
            System.out.println("Error: Ticket information was not found");
        }
    }

    public String getUser(String username, List<String> user_file){
        for(String line: user_file){
            String line_username = line.substring(0, Math.min(15, line.length()));
            if(username.equals(line_username)){
                return line;
            }
        }
        return null;
    }

    public String getTickets(String event_name, List<String> tickets_file){
        for(String line: tickets_file){
            String line_event_name = line.substring(0, 25);
            if(event_name.equals(line_event_name)){
                return line;
            }
        }
        return null;
    }



}
