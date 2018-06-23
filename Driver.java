package search;

import java.io.*;

public class Driver {
	
	public static void main(String[] args) throws IOException {
		System.out.println("Little Search Engine");
		LittleSearchEngine lse = new LittleSearchEngine();
		lse.makeIndex("docs.txt", "noisewords.txt");
		/*
		for(String key: lse.keywordsIndex.keySet()){
			System.out.println("Key: " + key);
			System.out.println("Value: " + lse.keywordsIndex.get(key));
		}
		*/
		System.out.println(lse.top5search("beginning","Alice"));
		//System.out.println(lse.top5search("beginning", "test"));
		//System.out.println(lse.top5search("OMGMY", "isakeyword"));
		System.out.println("The end");
	}

}
