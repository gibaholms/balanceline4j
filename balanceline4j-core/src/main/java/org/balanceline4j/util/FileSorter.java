package org.balanceline4j.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FileSorter<K extends Comparable<? super K>> {
		
	public static interface KeyResolver<K extends Comparable<? super K>> {
		public K getKeyFromRecord(String line);
	}
	
	private File in;
	private KeyResolver<K> keyResolver;
	private long maxLinesPerChunk;
	
	public FileSorter(File in, KeyResolver<K> keyResolver, long maxLinesPerChunk) {
		if (in == null) {
			throw new IllegalArgumentException("File object is null");
		} else if (!in.exists()) {
			throw new IllegalArgumentException("Specified input file does not exists: " + in);
		} else if (!in.isFile()) {
			throw new IllegalArgumentException("Specified input file does not represent a file: " + in);
	    } else if (!in.canRead()) {
	    	throw new IllegalArgumentException("Specified input file cannot be read, please check the SO permissions: " + in);
	    } else if (keyResolver == null) {
			throw new IllegalArgumentException("KeyResolver object is null");
		} else if (maxLinesPerChunk <= 0) {
			throw new IllegalArgumentException("The maxLinesPerChunk argument must be a positive number, grater than one");
		}
		this.in = in;
		this.keyResolver = keyResolver;
		this.maxLinesPerChunk = maxLinesPerChunk;
	}

	public File sort() throws IOException {		
		File out = File.createTempFile("sorted_" + Thread.currentThread().getName() + "_" + new Date().getTime(), ".txt");
		sort(out, true);
		return out;
	}
	
	public void sort(File out, boolean overrideIfExists) throws IOException {		
		if (out == null) {
			throw new IllegalArgumentException("File object is null");
		} else if (out.exists()) {
			if (!out.isFile()) {
				throw new IllegalArgumentException("Specified output file does not represent a file: " + out);
		    } else if (!out.canWrite()) {
		    	throw new IllegalArgumentException("Specified output file cannot be written, please check the SO permissions: " + out);
		    } else if (!overrideIfExists){
			    throw new IllegalArgumentException("Specified output file already exists: " + out);
		    }
		} else {
			out.createNewFile();
	    	if (!out.canWrite()) {
		    	throw new IllegalArgumentException("Specified output file cannot be written, please check the SO permissions: " + out);
		    }
		}
		List<File> chunks = splitFileInOrderedChunks(in);
		do {
			chunks = mergeOrderedChunksList(chunks);
		} while(chunks.size() > 1);
		File mergedChunk = chunks.get(0);
		copyFile(mergedChunk, out, true);
		mergedChunk.delete();
	}
	
	private List<File> splitFileInOrderedChunks(File file) throws IOException {
		Map<K, String> recordsByKey = new TreeMap<K, String>();
		List<File> chunks = new ArrayList<File>();	
		BufferedReader reader = null;
		try {			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			int lineCount = 1;
			while((line=reader.readLine()) != null) {
				recordsByKey.put(keyResolver.getKeyFromRecord(line), line);
				if (lineCount % maxLinesPerChunk == 0) {
					chunks.add(createOrderedChunk(recordsByKey));
					recordsByKey.clear();
				}
				lineCount++;
			}
			if (!recordsByKey.isEmpty()) {
				chunks.add(createOrderedChunk(recordsByKey));
				recordsByKey.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { if (reader != null) reader.close(); } catch(Exception e) {}
		}
		return chunks;
	}
	
	private File createOrderedChunk(Map<K, String> recordsByKey) throws IOException {
		File chunk = File.createTempFile("chunk_" + Thread.currentThread().getName() + "_" + new Date().getTime(), ".txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(chunk));
			int count = 0;
			for (K key : recordsByKey.keySet()) {
				if (count++ > 0) {
					writer.newLine();
				}
				writer.write(recordsByKey.get(key));
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { if (writer != null) writer.close(); } catch(Exception e) {}
		}
		return chunk;
	}
	
	private List<File> mergeOrderedChunksList(List<File> chunks) throws IOException {
		int size = chunks.size();
		if (size == 0) {
			throw new RuntimeException("No chunks to merge");
		} else if (size == 1) {
			return chunks;
		} else {
			File chunk1 = chunks.get(0);
			File chunk2 = chunks.get(1);
			File mergedChunk = mergeOrderedChunks(chunk1, chunk2);
			chunk1.delete();
			chunk2.delete();
			chunks.remove(chunk1);
			chunks.remove(chunk2);
			chunks.add(mergedChunk);
		}
		return chunks;
	}
	
	private File mergeOrderedChunks(File chunk1, File chunk2) throws IOException {
		File chunkOut = File.createTempFile("chunk_" + Thread.currentThread().getName() + "_" + new Date().getTime(), ".txt");
		BufferedReader reader1 = null;
		BufferedReader reader2 = null;
		BufferedWriter writer = null;
		try {			
			reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(chunk1)));
			reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(chunk2)));
			writer = new BufferedWriter(new FileWriter(chunkOut));
			boolean isChunk1Active;
			boolean isChunk2Active;
			String line1;
			String line2;
			isChunk1Active = (line1=reader1.readLine()) != null;
			isChunk2Active = (line2=reader2.readLine()) != null;
			int count = 0;
			while(isChunk1Active || isChunk2Active) {
				if (count++ > 0) {
					writer.newLine();
				}
				
				if (!isChunk1Active) {
					writer.write(line2);
					isChunk2Active = (line2=reader2.readLine()) != null;
				} else if (!isChunk2Active) {
					writer.write(line1);
					isChunk1Active = (line1=reader1.readLine()) != null;
				} else { 
					K keyChunk1 = keyResolver.getKeyFromRecord(line1);
					K keyChunk2 = keyResolver.getKeyFromRecord(line2);
					int comparisonResult = keyChunk1.compareTo(keyChunk2);
					if (comparisonResult < 0) {
						writer.write(line1);
						isChunk1Active = (line1=reader1.readLine()) != null;
					} else if (comparisonResult > 0) {
						writer.write(line2);
						isChunk2Active = (line2=reader2.readLine()) != null;
					} else {
						throw new RuntimeException("Duplicated key in chunks");
					}
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { if (reader1 != null) reader1.close(); } catch(Exception e) {}
			try { if (reader2 != null) reader2.close(); } catch(Exception e) {}
			try { if (writer != null) writer.close(); } catch(Exception e) {}
		}
		return chunkOut;
	}

	private void copyFile(File in, File out, boolean override) throws IOException {          
		FileChannel sourceChannel = new FileInputStream(in).getChannel();
		FileChannel destinationChannel = new FileOutputStream(out).getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		sourceChannel.close();
		destinationChannel.close();
    }
	
}
