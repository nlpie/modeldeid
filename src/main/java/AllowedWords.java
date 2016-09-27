import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class for querying a de-id model on whether any given word is allowed
 * Built using Greg's strategy (see the AMIA 2016 abstract)
 *
 * Created by gpfinley on 9/26/16.
 */
public class AllowedWords {

    private static Logger LOGGER = Logger.getLogger(ModelScrubber.class.getName());

    private final Set<String> allowedWords;
    // if not using a source like SPECIALIST to filter, work on forbidden words instead
    private final Set<String> forbiddenWords;

    private static final String ALLOWED_WORDS_STRATEGY = "using_allowed_words_strategy";
    private static final String FORBIDDEN_WORDS_STRATEGY = "using_forbidden_words_strategy";

    /**
     * Constructor for various spins on generating a list of allowed words
     * @param allowed words to allow (leave null if any non-forbidden words are allowed)
     * @param forbidden words to not allow (leave null if all allowed words should be allowed)
     * @param alwaysKeep words to keep even if they are 'forbidden', e.g. very common words (leave null to not use)
     */
    public AllowedWords(@Nullable Set<String> allowed, @Nullable Set<String> forbidden, @Nullable Set<String> alwaysKeep) {
        forbiddenWords = forbidden == null ? null : new HashSet<>(forbidden);
        if (alwaysKeep != null && forbidden != null) {
            forbiddenWords.removeAll(alwaysKeep);
        }
        if (allowed == null) {
            allowedWords = null;
        } else {
            allowedWords = new HashSet<>(allowed);
            if (forbidden != null) {
                allowedWords.removeAll(forbiddenWords);
            }
        }
    }

    /**
     * Determine if a word is allowed under this de-id scheme
     * @param word any string
     * @return true if the word is not an identifier, false if it needs to be scrubbed
     */
    public boolean isAllowed(String word) {
        if (word.length() < 2) return true;
        word = word.toLowerCase();
        return allowedWords == null ? !forbiddenWords.contains(word) : allowedWords.contains(word);
    }

    /**
     * Save this object to a text file using a simple human-readable output
     * @param outPath
     * @throws IOException
     */
    public void save(String outPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
        Set<String> words;
        if (allowedWords == null) {
            words = forbiddenWords;
            writer.write(FORBIDDEN_WORDS_STRATEGY + "\n");
        } else {
            words = allowedWords;
            writer.write(ALLOWED_WORDS_STRATEGY + "\n");
        }
        for (String word : words) {
            writer.write(word);
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }

    /**
     * Load an instance of this object from a text file
     * @param inPath
     * @return
     * @throws IOException
     */
    public static AllowedWords load(String inPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inPath));
        String strategy = reader.readLine();
        String nextLine;
        Set<String> words = new HashSet<>();
        while((nextLine = reader.readLine()) != null) {
            words.add(nextLine);
        }
        if (strategy.equals(ALLOWED_WORDS_STRATEGY)) {
            return new AllowedWords(words, null, null);
        } else if(strategy.equals(FORBIDDEN_WORDS_STRATEGY)) {
            return new AllowedWords(null, words, null);
        } else {
            System.out.println("File format not correct");
            throw new IOException();
        }
    }

    public static Set<String> getAllowedWordsFromSpecialist(String specialistPath) throws IOException {
        LOGGER.info("Parsing SPECIALIST Lexicon for allowed words...");
        Set<String> words = new HashSet<>();
        Scanner scanner = new Scanner(new File(specialistPath)).useDelimiter("base=");
        while (scanner.hasNext()) {
            String entry = scanner.next();
            if (!entry.contains("\tproper\n")) {
                String headword = entry.split("\\n")[0].toLowerCase();
                words.addAll(Arrays.asList(headword.split("\\W")));
            }
        }
        return words;
    }

    public static Set<String> getForbiddenWordsFromNamesAddresses(String namesAddressesPath) throws IOException {
        LOGGER.info("Parsing names and addresses database...");
        Set<String> words = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(namesAddressesPath));
        String nextLine;
        while((nextLine = reader.readLine()) != null) {
            String[] theseWords = nextLine.toLowerCase().split("\\W+");
            for (String thisWord : theseWords) {
                if (thisWord.length() > 1) words.add(thisWord);
            }
        }

        return words;
    }

    public static Set<String> getMostFrequentWords(String vocabPath, int n) throws IOException {
        LOGGER.info("Parsing word2vec-style vocab file for most common words...");
        Set<String> words = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(vocabPath));
        for(int i=0; i<n; i++) {
            words.add(reader.readLine().split("\\s")[0]);
        }
        return words;
    }

    public static void main(String[] args) throws IOException {

//        System.out.println(getAllowedWordsFromSpecialist("/Volumes/gregdata/metathesaurus/2015AA/LEX/LEXICON").size());
//        System.out.println(getForbiddenWordsFromNamesAddresses("/Users/gpfinley/hermes/home/gpfinley/data/deid/name_address.tsv").size());
//        System.out.println(getMostFrequentWords("/Volumes/gregdata/word2vec_models/pmc-vocab.txt", 1000));

        AllowedWords allowedWords = new AllowedWords(
                getAllowedWordsFromSpecialist(args[0]),
                getForbiddenWordsFromNamesAddresses(args[1]),
                getMostFrequentWords(args[2], Integer.parseInt(args[3]))
        );

        allowedWords.save(args[4]);

    }

}
