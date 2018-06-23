package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		sc.close();
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	
	/*
	 * This will look very similar to my work from last semester.
	 * Fixed a bug in getKeyWord, had to read some regular expressions documentation to fix it.
	 * I have updated the insert last occurrence and finished the top5search.
	 * The first three methods did not need to be changed at all.
	 * Added some more comments as well
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		HashMap<String, Occurrence> keymap = new HashMap<String, Occurrence>();  
		Scanner scan = new Scanner(new File(docFile));
		while(scan.hasNext()){
			String key = getKeyWord(scan.next());		//gotta put words into format in getKeyWord
			if(key != null){							//not a noise word and actually gets trimmed properly
				if(keymap.containsKey(key)){
					keymap.get(key).frequency++;		//if the key exists already, freq ++
				}else{
					keymap.put(key, new Occurrence(docFile,1));   //if key DNE then make a new one with freq 1
				}
			}
			
		}
		scan.close();
		return keymap;
		//YOU IMPLEMENT
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		word = word.toLowerCase();								//lower case
		word = word.replaceAll("[.,?:;!']+$", "");				//remove delimiters
		if(noiseWords.containsKey(word))						//can't be a noise word
			return null;
		return word;											//return word
		//YOU IMPLEMENT
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		ArrayList<Occurrence> newList = new ArrayList<Occurrence>(); 	//newList for merging keywords
		for(String key : kws.keySet()){		//enhanced for loops b/c length is not known
			Occurrence one = kws.get(key);
			if(keywordsIndex.containsKey(key)){
				newList = keywordsIndex.get(key);
				newList.add(one);
				insertLastOccurrence(newList);
				keywordsIndex.put(key, newList);
			}else{
				ArrayList<Occurrence> secondList = new ArrayList<Occurrence>();	
				secondList.add(one);	//second list to add keys that do not currently exist in keyWords
				keywordsIndex.put(key, secondList);
			}
		}
		
		
		//YOU IMPLEMENT
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		int size = occs.size();
		if(size == 1)  //this is stated clearly above lol
			return null;
		
		//just implement a binary search, need a start and end length
		//if even number of entries, use last item from the first half as the midpoint
		//because it is an ArrayList we can just add at any index
		Occurrence key = occs.get(size - 1);//last element is to be inserted
		int end = occs.size() - 2; //need to offset because you are inserting the last one.
		int front = 0;
		ArrayList<Integer> midpts = new ArrayList<Integer>();
		while(front <= end){
			int midpt = (end + front)/2;//integer divide and make sure its the 'lower' midpoint
			midpts.add(midpt);
			if(key.frequency >= occs.get(midpt).frequency){
				end = midpt - 1; //because descending order, initially forgot this and messed up search
			}else{
				front = midpt+1;
			}
			
		}
		occs.add(front, key);
		//System.out.println(occs);
		return midpts;
		
		//YOU IMPLEMENT
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		 
		
		ArrayList<String> output = new ArrayList<String>(5);	//return must be an array list of size 5
		int size = output.size();								//int for the while loop
		String key1 = kw1.toLowerCase();						//user input might be in the wrong form
		String key2 = kw2.toLowerCase();
		ArrayList<Occurrence> k1list = keywordsIndex.get(getKeyWord(key1));	//get occurrences of kw1
		ArrayList<Occurrence> k2list = keywordsIndex.get(getKeyWord(key2));	//get occurrences of kw2
		
		if(k1list == null & k2list == null){ //if neither are keywords had to use BITWISE OMG
			return null;
		}
		if(!keywordsIndex.containsKey(key1)){				//dealing with keywords that don't exist
			for(int i = 0; i < 5; i++){
				if(i <k2list.size()){
					output.add(k2list.get(i).document);
				}else{
					break;
				}
			}
		}
		if(!keywordsIndex.containsKey(key2)){				//same as above
			for(int i = 0; i < 5; i++){
				if(i < k1list.size()){
				output.add(k1list.get(i).document);
				}else{
					break;
				}
			}
		}
		if(output.size() > 0)								//so it doesn't enter the next loop if it already exists
			return output;
		int i =0, j = 0;
		while(size < 5){
			
			if(j >= k2list.size() & i != k1list.size()){		//in case k2list is out of bounds
				output.add(k1list.get(i).document);
				i++;
				size++;
			}
			else if(i >= k1list.size() & j != k2list.size()){	//in case k1list is out of bounds
				output.add(k2list.get(j).document);
				j++;
				size++;
			}
			else if(i == k1list.size() & j == k2list.size()){	//break if both are at the end
				break;
			}
			else if(k1list.get(i).frequency > k2list.get(j).frequency){	//if k1 is larger
				output.add(k1list.get(i).document);
				i++;
				size++;
			}
			else if(k2list.get(j).frequency > k1list.get(i).frequency){ //if k2 is larger
				output.add(k2list.get(j).document);
				j++;
				size++;
			}
			
			else if(k1list.get(i).frequency == k2list.get(j).frequency){
				output.add(k1list.get(i).document);	//tie breaker
				i++;								//when testing realized I was adding over 5
				size++;
				if(size < 5){//was getting more than 5 results if there were too many duplicates, so need this check
					output.add(k2list.get(j).document);
					j++;
					size++;
				}else{
					break;
				}
			}
		}
		return output;
	}  
	
}
