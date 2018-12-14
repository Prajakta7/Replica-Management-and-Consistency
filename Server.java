/**
 * Submitted by: Prajakta Ganesh Jalisatgi
 * ID: 1001637722
 * <p>
 * References:
 * https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
 * https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/
 * http://www.java2s.com/Code/Java/Network-Protocol/AverysimpleWebserverWhenitreceivesaHTTPrequestitsendstherequestbackasthereply.htm
 * https://www.jmarshall.com/easy/http/#postmethod
 * https://stackoverflow.com/questions/15247752/gui-client-server-in-java
 * http://learningviacode.blogspot.com/2013/06/queues-in-java.html
 * https://stackoverflow.com/questions/9969585/how-do-i-send-data-to-all-threaded-clients-in-java
 * https://stackoverflow.com/questions/29273182/broadcasting-data-from-one-socket-thread-to-all-existing-socket-threads-in-java
 */

package uta.projects.ds;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Stack;

/**
 * Server accepts connections from multiple clients and serves them. It spawns
 * off multiple threads to be able to handle multiple clients at once.
 */
public class Server extends JFrame {

	static JTextArea textFromClient;
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane scrollPane1;
	private JPanel buttonBar;
	JButton cancelButton,pollButton;
	static double servervalue, sharedvalue;
	// counter for clients identification
	static int clientCount = 1;
	static boolean ispolling;
	static String expression ;

	// Vector to store active clients
	static Vector<ClientHandler> clientList = new Vector<>();


	Server() {

		servervalue = sharedvalue =1.0;	//		Initialize values when server starts
		ispolling = false; // flag to see if server is polling
		expression = "";
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		scrollPane1 = new JScrollPane();
		textFromClient = new JTextArea(80, 80);
		buttonBar = new JPanel();
		cancelButton = new JButton();
		pollButton = new JButton();
		// ======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setLayout(new BoxLayout(contentPanel,
						BoxLayout.X_AXIS));

				// ======== scrollPane1 ========
				{
					scrollPane1.setViewportView(textFromClient);
				}
				contentPanel.add(scrollPane1);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			// ======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {
						0, 85, 80 };
				((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {
						1.0, 0.0, 0.0 };

				// ---- cancelButton ----
				cancelButton.setText("Terminate");
				buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				// Poll Button
				pollButton.setText("Poll");
				buttonBar.add(pollButton, new GridBagConstraints(1, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		pollButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ispolling = true;
				int i = 1;
				String exp ="";
				textFromClient.append("Polling started\n");
				double val =sharedvalue;
				for(ClientHandler a: clientList) {
					if(i<= clientCount) {
						System.out.println("Client"+i);
						a.polling = true;
						a.evalvalue = sharedvalue;
						exp = a.receiveFile(a.br,a.pw, a.socket, true);
						expression = expression.concat(exp);
						i++;
					}
					else
						break;
				}
				expression = expression.replace("\n", "");
				expression = parseExpression(expression);// Add space after symbols for computeExpression
				expression = Double.toString(sharedvalue).concat(expression);
				sharedvalue = calculateExpression(expression);
				sharedvalue = (double)Math.round(sharedvalue * 10000d) / 10000d;//4 decimal digit precision
				System.out.println(sharedvalue);
				for(ClientHandler a: clientList) {
					a.polling = false;
					a.evalvalue = sharedvalue;
					a.sendMessage(a.br,a.pw, a.socket,false);
				}
			}
		});
		
		this.setTitle("Server");
		this.setVisible(true);

	}
	/*
	 * Expression received from client does not contain spaces to separate symbols from numbers
	 * Add spaces to unrefined expression 
	 */
	public static String parseExpression(String exp) {
		String newexp="";
		char ch = ' ';
		for (int i =0; i< exp.length(); i++) {
			ch= exp.charAt(i);
			if(ch == '+' || ch == '-' || ch == '*' || ch == '/')
			{
				// if symbol then add spaces around it
				newexp = newexp+" "+Character.toString(ch)+" ";
			}
			else
				// append number to string
				newexp = newexp+Character.toString(ch);
		}
		return newexp;
	}
	public static double calculateExpression(String exp) 
	{ 
		char[] tokens = exp.toCharArray(); 
		System.out.println(exp);
		// Stack for numbers: 'values' 
		Stack<Double> values = new Stack<Double>(); 

		// Stack for Operators: 'ops' 
		Stack<Character> ops = new Stack<Character>(); 

		for (int i = 0; i < tokens.length; i++) 
		{ 
			// Current token is a whitespace, skip it 
			if (tokens[i] == ' ') 
				continue; 

			// Current token is a number, push it to stack for numbers 
			if (tokens[i] >= '0' && tokens[i] <= '9') 
			{ 
				StringBuffer sbuf = new StringBuffer(); 
				// There may be more than one digits in number 
				while ((i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') || ( i < tokens.length && tokens[i] == '.')) 
					sbuf.append(tokens[i++]); 
				values.push(Double.parseDouble(sbuf.toString())); 
			} 

			

			// Current token is an operator. 
			else if (tokens[i] == '+' || tokens[i] == '-' || 
					tokens[i] == '*' || tokens[i] == '/') 
			{ 
				// While top of 'ops' has same or greater precedence to current 
				// token, which is an operator. Apply operator on top of 'ops' 
				// to top two elements in values stack 
				while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) 
				values.push(applyOp(ops.pop(), values.pop(), values.pop())); 

				// Push current token to 'ops'. 
				ops.push(tokens[i]); 
			} 
		} 

		// Entire expression has been parsed at this point, apply remaining 
		// ops to remaining values 
		while (!ops.empty()) 
			values.push(applyOp(ops.pop(), values.pop(), values.pop())); 

		// Top of 'values' contains result, return it 
		return values.pop(); 
	} 

	// Returns true if 'op2' has higher or same precedence as 'op1', 
	// otherwise returns false. 
	public static boolean hasPrecedence(char op1, char op2) 
	{ 
		if (op2 == '(' || op2 == ')') 
			return false; 
		if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) 
			return false; 
		else
			return true; 
	} 
	// A utility method to apply an operator 'op' on operands 'a' 
		// and 'b'. Return the result. 
		public static double applyOp(char op, double b, double a) 
		{ 
			switch (op) 
			{ 
			case '+': 
				return a + b; 
			case '-': 
				return a - b; 
			case '*': 
				return a * b; 
			case '/': 
				if (b == 0) 
					throw new
					UnsupportedOperationException("Cannot divide by zero"); 
				return a / b; 
			} 
			return 0; 
		} 
	public static void main(String[] args) {

		new Server(); //creating the GUI

		try {
			ServerSocket serverSocket = new ServerSocket(2228);
			System.out.println("Starting server at 2228");
			Socket socket;

			while (true) {

				// Accept all incoming connections
				socket = serverSocket.accept();
				System.out.println("New client request received : " + socket);

				// obtain input and output streams
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream());

				// Create a new handler object for handling this client's
				// requests.
				String clientName = "Client " + clientCount;
				ClientHandler clientHandler = new ClientHandler(clientName,ispolling, br,
						pw, socket,sharedvalue);

				// Create a new Thread with this object.
				Thread thread = new Thread(clientHandler);
				System.out.println("New client connection");

				// Display the client connection message on the server's text
				// area
				Server.textFromClient.append("\nNew connection detected: "
						+ clientName + " on port : " + socket.getPort() + "\n");

				// Add this client to active clients list
				clientList.add(clientHandler);

				// Start the thread.
				thread.start();

				clientCount++;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

/**
 * This class will handle requests coming for every client. Each client will
 * have its own handler thread.
 */
class ClientHandler implements Runnable {

	String name,usernamer,expression;
	Socket socket;
	BufferedReader br;
	PrintWriter pw;
	Boolean loggedIn;
    static Queue<String> q = new LinkedList<>(); 
    boolean running,polling,receivingFile;
    double evalvalue;//store expression app
	
    public ClientHandler(String name, boolean polling, BufferedReader br, PrintWriter pw,
			Socket s, double val ) {
		super();
		this.name = name;
		this.socket = s;
		this.br = br;
		this.pw = pw;
		this.loggedIn = true;
		evalvalue = val;
		running = true;// Flag to terminate thread
		polling =false;
		receivingFile = false;
	}
	static double compute( double a,char op, double b) 
    { 
        switch (op) 
        { 
        case '+': 
        	return a + b; 
        case '-': 
        	return a - b; 
        case '*': 
        	return a * b; 
        case '/': 
            if (b == 0) 
                throw new
                UnsupportedOperationException("Cannot divide by zero");
            return a / b; 
        }
        return 0.0; 
    } 
	
	void sendMessage(BufferedReader br, PrintWriter pw,Socket sc, boolean poll) {
		
		try {
			
			StringBuffer response = new StringBuffer();
			response.append("HTTP/1.1 200 OK\n");
			response.append("Date: " + new Date() + "\n");
			response.append("Content-Type: text/plain\n");
			response.append("Content-Length: " + name.length());
			response.append("\r\n");
			response.append("New Value\n"+evalvalue+"\n");
			response.append("Polling\n"+polling+"\n");
			pw.print(response.toString());			
			pw.print("\r\n");    					
			pw.flush();
			receivingFile = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	String receiveFile(BufferedReader br, PrintWriter pw,Socket sc,boolean poll) {
		String thisLine;
		String exp ="";
		// send message to client polling started and receive logged expressions
		sendMessage(br,pw ,sc, poll);
		// Now receive all expressions
		try {
			BufferedReader BR = new BufferedReader(new InputStreamReader(
					sc.getInputStream()));
			PrintWriter PW = new PrintWriter(sc.getOutputStream());
			 if ((thisLine = BR.readLine()) != null) {
				 String header = thisLine;
				 String hostname = BR.readLine();
				 String userAgent = BR.readLine();
				 String contentType = BR.readLine();
				 String contentLength = BR.readLine();
				 BR.readLine();//ignore carriage return
				 name = BR.readLine(); //set username
				 BR.readLine();//skip message line with "Expression"
				 //  Start Receiving Expressions	
				 while ((thisLine= BR.readLine()) != null) {
					if(thisLine.equalsIgnoreCase("end")) {
						System.out.println("Read File");
								break;
					}
					exp = exp.concat(thisLine);
					System.out.println(exp);
				 }
		     }
		}catch(IOException e) {
			e.printStackTrace();
		}
		return exp;
	}
		
	void terminate() {
		running = false;
	}
	
	@Override
	public void run() {
		long startTime,endTime, totalTime;
		String thisLine;
		while (running)
			try {
					
				if ((thisLine = br.readLine()) != null) {
					// Get request from the client and parse the attributes
					startTime=System.currentTimeMillis();
					String header = thisLine;
					String hostname = br.readLine();
					String userAgent = br.readLine();
					String contentType = br.readLine();
					String contentLength = br.readLine();
					br.readLine();
					name = br.readLine();
					
					// Appends request received to server's text area
//					Server.textFromClient.append("\nRequest received from :\t"
//							+ name);
//					Server.textFromClient.append("\nHTTP Header: \n" + header
//							+ "\n" + hostname + "\n" + userAgent + "\n"
//							+ contentType + "\n" + contentLength + "\n");				
					while ((thisLine= br.readLine()) != null) {
						if(thisLine.equalsIgnoreCase("end")) 
							break;						
					}
                    q.add(name);
					/*
					 * Building response according to HTTP 1.1 Response format
					 * 
					 * HTTP/1.1 200 OK Date: Fri, 31 Dec 1999 23:59:59 GMT
					 * Content-Type: text/plain Content-Length: 42
					 */

					
    				if(!q.isEmpty())
    				{
    					System.out.println("Sending response to"+name);
    					q.remove();
    					StringBuffer response = new StringBuffer();
    					response.append("HTTP/1.1 200 OK\n");
    					response.append("Date: " + new Date() + "\n");
    					response.append(contentType + "\n");
    					response.append("Content-Length: " + name.length());
    					response.append("\r\n");
    					response.append("New Value\n"+evalvalue+"\n");
    					response.append("Polling\n"+polling+"\n");    					
    					// Display the response being sent to the client
//    					Server.textFromClient.append("\nResponse sent to " + name + "\n");
//    					Server.textFromClient.append(response.toString());
    					
    					// Send response to the client
    					
    					PrintWriter out = new PrintWriter(socket.getOutputStream(),
    							false);
    					    					
    					out.print(response.toString());    					
    					out.flush();  					
    				}
                    					
				} else {
					try{
						
						
					}
					catch(Exception e) {
					}
					
					return;
				}
			} catch (IOException e) {
				Server.textFromClient.append(name + " disconnected!\n");
				terminate();//stop thread for this client
			} 
	}
}
