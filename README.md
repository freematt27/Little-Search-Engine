# Little-Search-Engine

This little search engine takes input of text files, and noise words.
The noise words are hashed to create a dictionary of restricted words, noisewords.txt includes words such as 'a', 'the', 'an', etc.
The term frequency of each document is then independetly created and merged with the master keyWordsIndex.
When terms from different documents are being inserted into their respective lists where they are sorted. 

The program flow looks like this:

Little Search Engine Driver --> created LittleSearchEngine object "lse"
lse.makeIndex()
makeIndex --> loadKeyWords()
          --> mergeKeyWords()
loadKeyWords --> getKeyWord()
makeKeyWords --> insertLastOccurence()

top5Search is independent of the other functions. 
