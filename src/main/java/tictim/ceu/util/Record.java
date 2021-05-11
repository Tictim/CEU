package tictim.ceu.util;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import tictim.ceu.CeuMod;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.UUID;

/**
 * Simple shit for testing both GTEU and FE generation. Can be exported into csv.
 */
public class Record{
	private final Long2LongMap timeToValue = new Long2LongLinkedOpenHashMap();

	private long start;
	private long end;
	@Nullable private final UUID recordingPlayer;

	public Record(long start, long end, @Nullable UUID recordingPlayer){
		this.start = start;
		this.end = end;
		this.recordingPlayer = recordingPlayer;
	}

	public long getStart(){
		return start;
	}
	public void setStart(long start){
		this.start = start;
	}
	public long getEnd(){
		return end;
	}
	public void setEnd(long end){
		this.end = end;
	}

	@Nullable public UUID getRecordingPlayer(){
		return recordingPlayer;
	}

	public void put(long time, long value){
		timeToValue.put(time, value);
	}

	public void add(long time, long value){
		timeToValue.put(time, timeToValue.get(time)+value);
	}

	public long getValue(long time){
		return timeToValue.get(time);
	}

	public boolean save(String fileName){
		boolean succeed = true;
		File file = new File("ceu_records/"+fileName);
		try{
			file.getParentFile().mkdir();
			file.createNewFile();
			try(Writer w = new FileWriter(file)){
				writeToCsv(w);
			}
		}catch(IOException e){
			CeuMod.LOGGER.error("Failed to save record at {}", file, e);
			succeed = false;
		}
		if(succeed) CeuMod.LOGGER.info("Record saved at '{}'", file);
		return succeed;
	}

	private void writeToCsv(Writer writer){
		try(PrintWriter pw = new PrintWriter(writer)){
			pw.print("time,value");
			for(long t = start; t<=end; t++){
				pw.print('\n');
				pw.print(t);
				pw.print(',');
				pw.print(timeToValue.get(t));
			}
			pw.flush();
		}
	}
}
