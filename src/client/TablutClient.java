package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import enums.GameState;
import enums.Loader;
import enums.PlayerKind;
import utils.StreamUtils;
import domain.AiPlayer;
import domain.Move;
import domain.Pair;
import domain.Player;
import domain.State;
import domain.TablutBoard;
import domain.TablutState;
import ai.IterativeDeepeningSearch;

public class TablutClient extends Client implements Runnable {
//	private final int MY_PORT = 9595;
	public static int WHITEPORT = 5800;
	public static int BLACKPORT = 5801;
	PlayerKind role;
	String roleToString;
	int timeOut;
	InetAddress ip;
	State state;
	Player player;
	Gson gson;
	Socket playerSocket;
	DataOutputStream out;
	DataInputStream in;

	
	public TablutClient(PlayerKind role, int timeOut, InetAddress ip) {
		this.role = role;
		this.roleToString = role == PlayerKind.WHITE ? "white" : "black";
		this.timeOut = (timeOut - 1) * 1000;
		this.ip = ip;
		this.gson = new Gson();
		
		System.out.println("Timeout in millis: " + this.timeOut);
		
		try {
			System.out.println("Initializing connection with server... ");
			playerSocket = new Socket(ip, role == PlayerKind.WHITE ? WHITEPORT : BLACKPORT);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		try {
			System.out.println("Creating Output stream...");
			out = new DataOutputStream(playerSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			System.out.println("Creating Input stream...");
			in = new DataInputStream(playerSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void run() {	
		
		Function<Pair<Long, Long>, Boolean> f = pair -> {
			if((pair.getFirst() - pair.getSecond()) >= timeOut) 
				return true; 
			else return false;
		};		
		this.state = new TablutState(new TablutBoard());
		this.player = new AiPlayer("TheQuaranteam", role, null, new IterativeDeepeningSearch(3, 11, f));

		
		try {
			System.out.println("Sending to server our name: " + player.getId());
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
						
		while(true) {
			try {
				state.fromJson(readState(in)); 
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			if(state.getGameState()==GameState.PLAYING) {
				
				if (state.getTurnOf() == role)
				{
					System.out.println("Looking for a good move with state:");
					System.out.println(state.toString());
					Move nextMove = player.getNextMove(state);
					state.applyMove(nextMove);
					sendMove(nextMove, out);
				}
				else {
					System.out.println("Waiting for opponent...");
				}
			}
			else if(state.getGameState()==GameState.LOSE && state.getTurnOf() != role){ //VINTO
				System.out.println(state.toString());
				System.out.println("I Won!");
				System.exit(0);
			}
			else if(state.getGameState()==GameState.LOSE && state.getTurnOf() == role){ //PERSO
				System.out.println(state.toString());
				System.out.println("I Lost!");	
				System.exit(0);
			}
			else if(state.getGameState()==GameState.DRAW){
				System.out.println(state.toString());
				System.out.println("We Drawed!");
				System.exit(0);
			}
		}
	}
	
	private void declareName() throws IOException {
		StreamUtils.writeString(out, this.gson.toJson(player.getId()));
	}

	private void sendMove(Move moveToSend, DataOutputStream out) {
			
		JsonObject obj = moveToSend.toJsonObject();
		obj.addProperty("turn", this.roleToString.toUpperCase());
		
		try {
			StreamUtils.writeString(out, obj.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	public String readState(DataInputStream in) throws IOException {
		System.out.println(player.getId() + " reading incoming state...");
		String inputString = StreamUtils.readString(in);
//		System.out.println(roleToString + " state received: " + inputString);
		return inputString;
	}
}

