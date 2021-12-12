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
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;

import assignment6.Flight.SeatClass;
import assignment6.Flight.TicketOffice;

public class BookingClient {

    private Map<String, Flight.SeatClass[]> offices;
    private Flight flight;

	/**
     * @param offices maps ticket office id to seat class preferences of customers in line
     * @param flight the flight for which tickets are sold for
     */
    public BookingClient(Map<String, SeatClass[]> offices, Flight flight) {
        // TODO: Implement this constructor
        this.offices = offices;
        this.flight = flight;
    }

    /**
     * Starts the ticket office simulation by creating (and starting) threads
     * for each ticket office to sell tickets for the given flight
     *
     * @return list of threads used in the simulation,
     * should have as many threads as there are ticket offices
     */
    public List<Thread> simulate() 
    {
        // TODO: Implement this method
        List<Thread> threads = new ArrayList<Thread>();
        for(Map.Entry<String, SeatClass[]> office: offices.entrySet())
        {
            TicketOffice TicketO = flight.new TicketOffice(office.getKey(), office.getValue(), flight);
            Thread t = new Thread(TicketO);
            threads.add(t);
        }

        for(Thread t: threads)
        {
            t.start();
        }
        

        return threads;
    }

    // Main method to run the program!
    public static void main(String[] args) 
    {
        // TODO: Initialize test data to description
        SeatClass[] seatChoices = {SeatClass.FIRST, SeatClass.BUSINESS, SeatClass.ECONOMY};

        Random rand = new Random();

        SeatClass[] first =  new SeatClass[8];
        SeatClass[] second = new SeatClass[9];
        SeatClass[] third =  new SeatClass[8];
        SeatClass[] fourth = new SeatClass[9];
        SeatClass[] fifth = new SeatClass[8];

        for(int i = 0; i < first.length; i++)
        {
            first[i] = seatChoices[rand.nextInt(seatChoices.length)];
        }

        for(int i = 0; i < second.length; i++)
        {
            second[i] = seatChoices[rand.nextInt(seatChoices.length)];
        }

        for(int i = 0; i < third.length; i++)
        {
            third[i] = seatChoices[rand.nextInt(seatChoices.length)];
        }

        for(int i = 0; i < fourth.length; i++)
        {
            fourth[i] = seatChoices[rand.nextInt(seatChoices.length)];
        }

        for(int i = 0; i < fifth.length; i++)
        {
            fifth[i] = seatChoices[rand.nextInt(seatChoices.length)];
        }

        String t1 = "TO1";
        String t2 = "TO2";
        String t3 = "TO3";
        String t4 = "TO4";
        String t5 = "TO5";

        Flight f = new Flight("SG123", 2, 3, 3);

        Map<String, Flight.SeatClass[]> start = new HashMap<String, SeatClass[]>();

        start.put(t1, first);
        start.put(t2, second);
        start.put(t3, third);
        start.put(t4, fourth);
        start.put(t5, fifth); 

        BookingClient bc = new BookingClient(start, f);

        List<Thread> allThreads = bc.simulate();

        for(Thread t: allThreads)
        {
            try
            {
                t.join();
            }

            catch(InterruptedException e)
            {

            }
        }
    }
}
