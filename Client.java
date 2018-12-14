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
 * https://stackoverflow.com/questions/1500174/check-if-a-file-is-locked-in-java
 */

package uta.projects.ds;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Client connects to the server via socket and generates a random integer and uploads that integer to the server.
 */
public class Client extends JFrame implements ActionListener {
	private static int serverPort = 2228;
	static Socket socket;
	private static JTextArea textFromServer,clientmsg;
	JButton sendRandomNumber;
	JButton disconnectButton;
	JButton b0,b1,b2,b3,b4,b5,b6,b7,b8,b9,badd,bsub,bmul,bdiv,bdec,beq,bdel,bclr;
	JTextField txtInput,expressionText;
	static String expression, username,end;//store expression
	static boolean validexpression,logcreated,ispolling,connected,sendingFile;
	static double servervalue, clientvalue;
	Client() {
		
		// constructor that creates the GUI for Client
		this.setTitle("Client");
		this.setSize(435, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.getContentPane().setLayout(null);

		
		this.setVisible(false);
		validexpression = false;
		connected =false;
		sendingFile =false;
		clientvalue = 1.0;
		expression = "";
		username = "";
		end ="end";
		logcreated = false; //Flag for checking if log is created
		initComponents();
		showUsername();
	}
	/**
	 * Initializing all the components used in this JFrame
	 */
	private void initComponents() {
		textFromServer = new JTextArea();
		textFromServer.setEditable(false);
		textFromServer.setLineWrap(true);
		textFromServer.setBounds(410,180, 400, 400);
		add(textFromServer);
		
		clientmsg = new JTextArea();
		clientmsg.setEditable(true);
		clientmsg.setLineWrap(true);
		clientmsg.setBounds(410, 30, 400, 100);
		add(clientmsg);
		
		expressionText = new JTextField();
		expressionText.setText(Double.toString(clientvalue));
		expressionText.setBounds(10, 30, 400, 70);
		expressionText.addActionListener(this);
		add(expressionText);
        
		
		txtInput = new JTextField(20);
		txtInput.setBounds(10, 110, 400, 30);
		txtInput.setEditable(false);
		txtInput.setText("");
		add(txtInput);
		
	 	b1=new JButton("1");      
        b1.setBounds(10,290,90,50);
        b1.addActionListener(this);
        add(b1);
        
        b2=new JButton("2");
        b2.addActionListener(this);
        b2.setBounds(115,290,90,50);
        add(b2);
        
        b3=new JButton("3");
        b3.addActionListener(this);
        b3.setBounds(215,290,90,50);
        add(b3);
      
        b4=new JButton("4");
        b4.addActionListener(this);
        b4.setBounds(10,220,90,50);
        add(b4);
      
        b5=new JButton("5");
        b5.addActionListener(this);
        b5.setBounds(115,220,90,50);
        add(b5);
      
        b6=new JButton("6");
        b6.addActionListener(this);
        b6.setBounds(215,220,90,50);
        add(b6);
      
        b7=new JButton("7");
        b7.addActionListener(this);
        b7.setBounds(10,150,90,50);
        add(b7);
        
        b8=new JButton("8");
        b8.addActionListener(this);
        b8.setBounds(115,150,90,50);
        add(b8);
        
        b9=new JButton("9");
        b9.addActionListener(this);
        b9.setBounds(215,150,90,50);
        add(b9);
        
        b0=new JButton("0");
        b0.addActionListener(this);
        b0.setBounds(115,360,90,50);
        add(b0);
    
        bdiv=new JButton("/");
        bdiv.addActionListener(this);
        bdiv.setBounds(315,150,90,50);
        add(bdiv);
     
        bmul=new JButton("*");
        bmul.addActionListener(this);
        bmul.setBounds(315,220,90,50);
        add(bmul);
     
        bsub=new JButton("-");
        bsub.addActionListener(this);
        bsub.setBounds(315,290,90,50);
        add(bsub);
     
        badd=new JButton("+");
        badd.addActionListener(this);
        badd.setBounds(315,360,90,50);
        add(badd);        
        
        bdec=new JButton(".");
        bdec.addActionListener(this);
        bdec.setBounds(10,360,90,50);
        add(bdec);

        beq=new JButton("=");
        beq.addActionListener(this);
        beq.setBounds(215,360,90,50);
        add(beq);
        
        bdel=new JButton("Delete");
        bdel.addActionListener(this);
        bdel.setBounds(90,430,100,50);
        add(bdel);
        
        bclr=new JButton("Clear");
        bclr.addActionListener(this);  
        bclr.setBounds(230,430,100,50);
        add(bclr);        		
		        
		disconnectButton = new JButton();
		disconnectButton.setText("Disconnect");
		disconnectButton.setBounds(10, 570, 400, 50);
		disconnectButton.addActionListener(this);
		add(disconnectButton);
	}
	//	Set username
	private String getUsername() {
		String input="";
		JFrame frame = new JFrame("Username Input");

	    // prompt the user to enter their name
	    input = JOptionPane.showInputDialog(this, "What's your name?");
		return input;
	}
	
	private void showUsername() {
		try {
			while(username.equalsIgnoreCase("")) {
				username = this.getUsername();
			}
		}
		catch(Exception e) {
			// Terminate if user presses close or cancel button			
			System.exit(0);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(disconnectButton)) {
			// Action performed when the disconnect button is clicked
			System.out.println("Client Disconnected gracefully");
			System.exit(0); //kills the client process
		}
        else if(e.getSource().equals(b1)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 1"));
                expression = expression.concat("1");
            }
            else
              txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
       
        else if(e.getSource().equals(b2)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 2"));
                expression = expression.concat("2");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        
        else if(e.getSource().equals(b3)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 3"));
                expression = expression.concat("3");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        
        else if(e.getSource().equals(b4)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 4"));
                expression = expression.concat("4");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        
        else if(e.getSource().equals(b5)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 5"));
                expression = expression.concat("5");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        
        else if(e.getSource().equals(b6)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 6"));
                expression = expression.concat("6");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        
        else if(e.getSource().equals(b7)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 7"));
                expression = expression.concat("7");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        else if(e.getSource().equals(b8)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 8"));
                expression = expression.concat("8");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        else if(e.getSource().equals(b9)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 9"));
                expression = expressionText.getText();
                expression = expression.concat("9");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        
        else if(e.getSource().equals(b0)) {
            if (validexpression) {
                expressionText.setText(expressionText.getText().concat(" 0"));
                expression = expression.concat("0");
            }
            else
                txtInput.setText(txtInput.getText().concat(":\t Click an operand first"));
        }
        
        else if(e.getSource().equals(badd)) {
        	if (!validexpression) {
                validexpression = true;
                txtInput.setText(username);
            }
            expressionText.setText(expressionText.getText().concat(" +"));
            expression = expression.concat(" + ");
        }
        
        else if(e.getSource().equals(bsub)) {
        	if (!validexpression) {
                validexpression = true;
                txtInput.setText(username);
            }
            expressionText.setText(expressionText.getText().concat(" -"));
            expression = expression.concat(" - ");
        }
        
        else if(e.getSource().equals(bmul)) {
        	if (!validexpression) {
                validexpression = true;
                txtInput.setText(username);
            }
            expressionText.setText(expressionText.getText().concat(" *"));
            expression = expression.concat(" * ");
        }
        
        else if(e.getSource().equals(bdiv)) {
            if (!validexpression) {
                validexpression = true;
                txtInput.setText(username);
            }
            expressionText.setText(expressionText.getText().concat(" /"));
            expression = expression.concat(" / ");
        }
        
        else if(e.getSource().equals(bdel)) {
        	String newExpression;
        	newExpression = expressionText.getText();
            if(newExpression.length()> 1)
                newExpression = newExpression.substring(0, newExpression.length()-2);
            else 
                newExpression = "";
            expressionText.setText(newExpression);
            if(newExpression.length()> 1) {
            	if ( expression.charAt(expression.length()-1) == ' ') 
            		expression = expression.substring(0, expression.length()-3);
            	else
            		expression = expression.substring(0, expression.length()-1);
            }
            else 
                expression = "";
        }
        
        else if(e.getSource().equals(bclr)) {
            // Reset value to client value          
            expressionText.setText(Double.toString(clientvalue));
            expression = "";
        }
        // Compute expression here      
        else if(e.getSource().equals(beq)) {
        	double a = calculateExpression(Double.toString(clientvalue).concat(expression));        	
            expressionText.setText(Double.toString(a));
            updateLog(expression);
            expression = "";
            validexpression = false;
        }
	}
	
	void sendConnectionMessage(BufferedReader br, PrintWriter pw,
			Socket s) {
		System.out.println(username+" sending text");
		String end ="end";
		/*
		 * Start sending our reply using the HTTP 1.1 protocol Host is
		 * mandatory in HTTP 1.1 Body contains the random number
		 * 
		 * POST /path/script.cgi HTTP/1.0 From: frog@jmarshall.com
		 * User-Agent: HTTPTool/1.0 Content-Type:
		 * application/x-www-form-urlencoded Content-Length: 32
		 * 
		 * home=Cosby&favorite+flavor=flies
		 */
		try {
			pw.print("POST /path/script.cgi HTTP/1.1\n"); // Version &
															// status code
			pw.print("Host: " + socket.getInetAddress().getCanonicalHostName() + "\n");
			pw.print("User-Agent: HTTPTool/1.0\n");
			pw.print("Content-Type: text/plain\n"); // The type of data
			pw.print("Content-Length"+(username.length()+end.length())+"\n");
			pw.print("\r\n"); // End of headers
			// Body
			pw.print(username + "\n");
			pw.print(end+"\n");//End Body
			pw.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
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
	
	/*
	 * Following functions create, read, upadate the log file.
	 * Store expressions created by user while server is not polling.
	 * Log file is saved with username 
	 */
	public static void createLogDirectory() {
		File f = null;
	      boolean bool = false;	      
	      try {
	         // returns pathnames for files and directory
	         f = new File(System.getProperty("user.dir")+"/src/Log");	         
	         // create client directory
	         bool = f.mkdir();
	         
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	}
	public static void createLogFile() {
		createLogDirectory();
		try {
			File file = new File(System.getProperty("user.dir")+"/src/Log/"+username+".txt");
			
	        file.createNewFile();
		} catch(Exception e) {
			 e.printStackTrace();
		}
	}
	
	public static void updateLog(String data){
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter output = null;
		
		if(logcreated) {
			try {
				fw = new FileWriter(System.getProperty("user.dir")+"/src/Log/"+username+".txt", true);
			    bw = new BufferedWriter(fw);
			    output = new PrintWriter(bw);
			    output.println(expression);
			}
			catch (IOException e) {
			    
			}
			finally {
			    try {
			        if(output != null)
			            output.close();
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			    try {
			        if(bw != null)
			            bw.close();
			    } catch (IOException e) {
			    	e.printStackTrace();
			    }
			    try {
			        if(fw != null)
			            fw.close();
			    } catch (IOException e) {
			    	e.printStackTrace();
			    }
			}
		}
	}	
	
	void sendFile(BufferedReader br, PrintWriter pw, Socket s) {		
		String conexp="";
		System.out.println("Sending File...");
		try {
			File file = new File(System.getProperty("user.dir")+"/src/Log/"+username+".txt");
			Scanner sc = new Scanner(file); 		
			// Read file contents and concatenate all the expressions and send them to server
		    while (sc.hasNext()) {
		    	String line =sc.next();
		    	conexp= conexp.concat(line); 
		  	}
		    System.out.println(conexp);
			pw.print("POST /path/script.cgi HTTP/1.1\n"); // Version &
			// status code
			pw.print("Host: " + socket.getInetAddress().getCanonicalHostName() + "\n");
			pw.print("User-Agent: HTTPTool/1.0\n");
			pw.print("Content-Type: text/plain\n"); // The type of data
			pw.print("Content-Length"+username.length()+end.length()+file.length()+"\n");
			pw.print("\r\n"); // End of headers
			pw.print(username+"\n");
			pw.print("SendingFile\n");
		    pw.print(conexp+"\n");
		    pw.write(end+"\n");
		    pw.flush();
		    sc.close();
		    System.out.println("Done.");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getServerPort() {
		return serverPort;
	}
	
	public static void main(String args[]) {
		Client ob = new Client();
		InetAddress inetAddress;
		// Show calculator after username is received.	
		ob.setVisible(true);
		//Show username		
		ob.txtInput.setText(username);
		try {
			createLogFile();	
			logcreated = true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		try {
			inetAddress = InetAddress.getByName("localhost");

			// establish the connection
			socket = new Socket(inetAddress, getServerPort());

			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			
						
			PrintWriter out = new PrintWriter(socket.getOutputStream(),
					false);
			// Send connection request
			ob.sendConnectionMessage(br,out,socket);
			// read data sent from the server
			while (true) {
				String thisLine = br.readLine();
				
                //Read the messages sent from the server
				while (!thisLine.isEmpty()) {	
					if (thisLine.equalsIgnoreCase("Polling")) {
						thisLine = br.readLine();
						if(thisLine.equalsIgnoreCase("true")) {
							ispolling = true;	
							textFromServer.append("\nPolling started:    " + thisLine);
							ob.sendFile(br, out, socket);
						}
					}
					else if (thisLine.equalsIgnoreCase("New Value")) {						
						// Update with server value
						thisLine = br.readLine();
						clientvalue = Double.parseDouble(thisLine);	
						textFromServer.append("\nVlaue received from server:    " + thisLine);
					}
					else if(thisLine.equalsIgnoreCase("ReceivedFile")) {
						textFromServer.append("\nServer received expression.");
						break;
					}
					thisLine = br.readLine();
				}
				String msg = br.readLine();
				String isalive = br.readLine();
				textFromServer.append("\n" + msg);				
			}
		} catch (UnknownHostException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			textFromServer.append("\nServer Disconnected");
			System.out.println("Server Disconnected");
//			exception.printStackTrace();
		}
	}
}