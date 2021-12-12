/* MULTITHREADING <BookingClient.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * <Sanjay Gorur>
 * <sg52879>
 * <17805>
 * Slip days used: <0>
 * Fall 2021
 */
package assignment6;

import java.lang.Runnable;
import java.util.ArrayList;
import java.util.List;

public class Flight implements Runnable{
    /**
     * the delay time you will use when print tickets
     */
    private int printDelay; // 50 ms. Use it to fix the delay time between prints.
    private SalesLogs log;
	private String flightNo;
	private int firstNumRows, businessNumRows, economyNumRows, currentFirst, currentSecond, currentThird;
	private SeatLetter firstL = SeatLetter.A; 
	private SeatLetter secondL = SeatLetter.A;
	private SeatLetter thirdL = SeatLetter.A;

	public Integer customerNum = 0;
	public Boolean alreadyDone = false;
	public Boolean soldOut = false;
	//public Boolean stopped = false;

    public Flight(String flightNo, int firstNumRows, int businessNumRows, int economyNumRows) {
		this.flightNo = flightNo;
    	this.printDelay = 50;// 50 ms. Use it to fix the delay time between
    	this.log = new SalesLogs();
        // TODO: Implement the rest of this constructor
		this.firstNumRows = firstNumRows;
		this.businessNumRows = businessNumRows;
		this.economyNumRows = economyNumRows;
		currentFirst = (firstNumRows > 0) ? 1 : 0;
		currentSecond = currentFirst + firstNumRows;
		currentThird = currentFirst + firstNumRows + businessNumRows;

    }
    
    public void setPrintDelay(int printDelay) {
        this.printDelay = printDelay;
    }

    public int getPrintDelay() {
        return printDelay;
    }

	@Override
    public void run()
    {
        System.out.println("Hi there");
    }

    /**
     * Returns the next available seat not yet reserved for a given class
     *
     * @param seatClass a seat class(FIRST, BUSINESS, ECONOMY)
     * @return the next available seat or null if flight is full
     */
	public Seat getNextAvailableSeat(SeatClass seatClass) {
		// TODO: Implement this method

		if(soldOut || currentThird > (firstNumRows + businessNumRows + economyNumRows))
		{
			return null;
		}

		Seat findSeat;

        if(seatClass.getIntValue().equals(0))
		{
			findSeat = new Seat(seatClass, currentFirst, firstL);
			if(currentFirst > firstNumRows)
			{
				findSeat.setSeatClass(SeatClass.BUSINESS);
				seatClass = findSeat.getSeatClass();
			}
			else
			{
				log.addSeat(findSeat);
				if(firstL.equals(SeatLetter.A))
				{
					firstL = SeatLetter.B;
				}
				else if(firstL.equals(SeatLetter.B))
				{
					firstL = SeatLetter.E;
				}
				else if(firstL.equals(SeatLetter.E))
				{
					firstL = SeatLetter.F;
				}
				else if(firstL.equals(SeatLetter.F))
				{
					currentFirst++;
					firstL = SeatLetter.A;
				}
				return findSeat;
			}
		}

		if(seatClass.getIntValue().equals(1))
		{
			findSeat = new Seat(seatClass, currentSecond, secondL);
			if(currentSecond > (firstNumRows + businessNumRows))
			{
				findSeat.setSeatClass(SeatClass.ECONOMY);
				seatClass = findSeat.getSeatClass();
			}
			else
			{
				log.addSeat(findSeat);
				if(secondL.equals(SeatLetter.A))
				{
					secondL = SeatLetter.B;
				}
				else if(secondL.equals(SeatLetter.B))
				{
					secondL = SeatLetter.C;
				}
				else if(secondL.equals(SeatLetter.C))
				{
					secondL = SeatLetter.D;
				}
				else if(secondL.equals(SeatLetter.D))
				{
					secondL = SeatLetter.E;
				}
				else if(secondL.equals(SeatLetter.E))
				{
					secondL = SeatLetter.F;
				}
				else if(secondL.equals(SeatLetter.F))
				{
					currentSecond++;
					secondL = SeatLetter.A;
				}
				return findSeat;
			}			
		}

        if(seatClass.getIntValue().equals(2))
		{
			findSeat = new Seat(seatClass, currentThird, thirdL);
			if(currentThird > (firstNumRows + businessNumRows + economyNumRows))
			{
				findSeat.setSeatClass(null);
				seatClass = findSeat.getSeatClass();
			}
			else
			{
				log.addSeat(findSeat);
				if(thirdL.equals(SeatLetter.A))
				{
					thirdL = SeatLetter.B;
				}
				else if(thirdL.equals(SeatLetter.B))
				{
					thirdL = SeatLetter.C;
				}
				else if(thirdL.equals(SeatLetter.C))
				{
					thirdL = SeatLetter.D;
				}
				else if(thirdL.equals(SeatLetter.D))
				{
					thirdL = SeatLetter.E;
				}
				else if(thirdL.equals(SeatLetter.E))
				{
					thirdL = SeatLetter.F;
				}
				else if(thirdL.equals(SeatLetter.F))
				{
					currentThird++;
					if(currentThird > (firstNumRows + businessNumRows + economyNumRows))
					{
						soldOut = true;
					}
					thirdL = SeatLetter.A;
				}
				return findSeat;
			}				
		}

		return null;
	}

	/**
     * Prints a ticket to the console for the customer after they reserve a seat.
     *
     * @param seat a particular seat in the airplane
     * @return a flight ticket or null if a ticket office failed to reserve the seat
     */
	public Ticket printTicket(String officeId, Seat seat, int customer) {
        // TODO: Implement this method
        if(seat == null)
		{
			return null;
		}

		Ticket customerTicket = null;

		customerTicket = new Ticket(flightNo, officeId, seat, customer);

		System.out.println(customerTicket);
	
		log.addTicket(customerTicket);

		return customerTicket;
    }

	/**
     * Lists all seats sold for this flight in order of purchase.
     *
     * @return list of seats sold
     */
    public List<Seat> getSeatLog() {
        // TODO: Implement this method
        return log.getSeatLog();
    }

    /**
     * Lists all tickets sold for this flight in order of printing.
     *
     * @return list of tickets sold
     */
    public List<Ticket> getTransactionLog() {
        // TODO: Implement this method
        return log.getTicketLog();
    }
    
    static enum SeatClass {
		FIRST(0), BUSINESS(1), ECONOMY(2);

		private Integer intValue;

		private SeatClass(final Integer intValue) {
			this.intValue = intValue;
		}

		public Integer getIntValue() {
			return intValue;
		}
	}

	static enum SeatLetter {
		A(0), B(1), C(2), D(3), E(4), F(5);

		private Integer intValue;

		private SeatLetter(final Integer intValue) {
			this.intValue = intValue;
		}

		public Integer getIntValue() {
			return intValue;
		}
	}

	/**
     * Represents a seat in the airplane
     * FIRST Class: 1A, 1B, 1E, 1F ... 
     * BUSINESS Class: 2A, 2B, 2C, 2D, 2E, 2F  ...
     * ECONOMY Class: 3A, 3B, 3C, 3D, 3E, 3F  ...
     * (Row numbers for each class are subject to change)
     */
	static class Seat {
		private SeatClass seatClass;
		private int row;
		private SeatLetter letter;

		public Seat(SeatClass seatClass, int row, SeatLetter letter) {
			this.seatClass = seatClass;
			this.row = row;
			this.letter = letter;
		}

		public SeatClass getSeatClass() {
			return seatClass;
		}

		public void setSeatClass(SeatClass seatClass) {
			this.seatClass = seatClass;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public SeatLetter getLetter() {
			return letter;
		}

		public void setLetter(SeatLetter letter) {
			this.letter = letter;
		}

		@Override
		public String toString() {
			return Integer.toString(row) + letter + " (" + seatClass.toString() + ")";
		}
	}

	/**
 	 * Represents a flight ticket purchased by a customer
 	 */
	static class Ticket {
		private String flightNo;
		private String officeId;
		private Seat seat;
		private int customer;
		public static final int TICKET_STRING_ROW_LENGTH = 31;

		public Ticket(String flightNo, String officeId, Seat seat, int customer) {
			this.flightNo = flightNo;
			this.officeId = officeId;
			this.seat = seat;
			this.customer = customer;
		}

		public int getCustomer() {
			return customer;
		}

		public void setCustomer(int customer) {
			this.customer = customer;
		}

		public String getOfficeId() {
			return officeId;
		}

		public void setOfficeId(String officeId) {
			this.officeId = officeId;
		}
		
		@Override
		public String toString() {
			String result, dashLine, flightLine, officeLine, seatLine, customerLine, eol;

			eol = System.getProperty("line.separator");

			dashLine = new String(new char[TICKET_STRING_ROW_LENGTH]).replace('\0', '-');

			flightLine = "| Flight Number: " + flightNo;
			for (int i = flightLine.length(); i < TICKET_STRING_ROW_LENGTH - 1; ++i) {
				flightLine += " ";
			}
			flightLine += "|";

			officeLine = "| Ticket Office ID: " + officeId;
			for (int i = officeLine.length(); i < TICKET_STRING_ROW_LENGTH - 1; ++i) {
				officeLine += " ";
			}
			officeLine += "|";

			seatLine = "| Seat: " + seat.toString();
			for (int i = seatLine.length(); i < TICKET_STRING_ROW_LENGTH - 1; ++i) {
				seatLine += " ";
			}
			seatLine += "|";

			customerLine = "| Client: " + customer;
			for (int i = customerLine.length(); i < TICKET_STRING_ROW_LENGTH - 1; ++i) {
				customerLine += " ";
			}
			customerLine += "|";

			result = dashLine + eol + flightLine + eol + officeLine + eol + seatLine + eol + customerLine + eol
					+ dashLine;

			return result;
		}
	}

	/* The TicketOffice class is used to model the simulation of threads.
	 * 	--> office ID is a string representing the name of the ticket office. (synchronized)
	 *  --> officeNum represents the number of the ticket office (for thread track)
	 *  --> seatTypes depicts the customers in line and their preferred seats (class[es])on the flight. 
	 *  --> flight is an instance variable to track the current flight for boarding
	 */
	class TicketOffice implements Runnable
	{
		private String officeID;
		private SeatClass[] seatTypes;
		private Flight flight;

		// Constructor for ticket office
		public TicketOffice(String officeID, SeatClass[] seatTypes, Flight flight)
		{
			this.officeID = officeID;
			this.seatTypes = seatTypes;
			this.flight = flight;
		}
		/* The checkSorry() method does the following:
		 * Prints sold out first time that flight has been notified to be full.
		 */
		private void checkSorry()
		{
			synchronized(flight)
			{
				if(!alreadyDone)
				{
					{
						System.out.println("Sorry, we are sold out!");
						alreadyDone = true;
					}
				}
			}
		}

		/* The run() method is overridden from the Runnable interface, applicable to any Task class. 
		 * This methods functions as the main operation for offering seats to customers who want to board the flight.
		 * If the flight is not already full, "Sorry, we are sold out" is printed.
		 */
		@Override 
		public void run()
		{
				for(int i = 0; i < seatTypes.length; i++)
				{
					Ticket getTicket = null;
					synchronized(flight)
					{
						Seat newSeat = flight.getNextAvailableSeat(seatTypes[i]);
						getTicket = flight.printTicket(officeID, newSeat, customerNum);
						customerNum++;
					}
						
					try
					{
						Thread.sleep(flight.getPrintDelay());
					}
					catch(InterruptedException e)
					{

					}

					if(getTicket == null)
					{
						checkSorry();
						return;
					}
				}
		}
	}

	/**
	 * SalesLogs are security wrappers around an ArrayList of Seats and one of Tickets
	 * that cannot be altered, except for adding to them.
	 * getSeatLog returns a copy of the internal ArrayList of Seats.
	 * getTicketLog returns a copy of the internal ArrayList of Tickets.
	 */
	static class SalesLogs {
		private ArrayList<Seat> seatLog;
		private ArrayList<Ticket> ticketLog;

		private SalesLogs() {
			seatLog = new ArrayList<Seat>();
			ticketLog = new ArrayList<Ticket>();
		}

		@SuppressWarnings("unchecked")
		public ArrayList<Seat> getSeatLog() {
			return (ArrayList<Seat>) seatLog.clone();
		}

		@SuppressWarnings("unchecked")
		public ArrayList<Ticket> getTicketLog() {
			return (ArrayList<Ticket>) ticketLog.clone();
		}

		public void addSeat(Seat s) {
			seatLog.add(s);
		}

		public void addTicket(Ticket t) {
			ticketLog.add(t);
		}
	}
}
