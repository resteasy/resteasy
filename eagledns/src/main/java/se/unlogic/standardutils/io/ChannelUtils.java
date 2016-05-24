package se.unlogic.standardutils.io;

import java.io.IOException;
import java.nio.channels.Channel;


public class ChannelUtils {

	public void closeChannel(Channel channel){
		
		try{
			channel.close();
		}catch(IOException e){}
	}
}
