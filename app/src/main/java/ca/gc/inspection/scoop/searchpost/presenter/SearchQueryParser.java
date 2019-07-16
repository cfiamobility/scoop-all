package ca.gc.inspection.scoop.searchpost.presenter;

import android.util.Log;

public class SearchQueryParser {

    private static final String TAG = "SearchQueryParser";
    private String mParsedQuery = null;
    private String[] mWords = null;

    SearchQueryParser(String query) {
        if (query != null) {
            StringBuilder parsedQuery = new StringBuilder();
            mWords = query.split("(\\s|\\.|,)+");
            for (int i = 0; i < mWords.length; i++) {
                mWords[i] = mWords[i].replaceAll("[^A-Za-z0-9\'\\-]", "");
                String queryWord = mWords[i].replaceAll("['\\-]", "");
                if (!queryWord.isEmpty()) {
                    if (parsedQuery.length() > 0)
                        parsedQuery.append(" | ");
                    parsedQuery.append(queryWord);
                }
            }
            mParsedQuery = parsedQuery.toString();
        }
    }

    public String getParsedQuery() {
        return mParsedQuery;
    }

    public String[] getQueryWords() {
        return mWords;
    }

    public double getNumberOfMatchesWeightedBy(MatchedWordWeighting weighting, String text) {
        double matches = 0;
        if (text != null && text.length() > 0)
            for (String mWord : mWords) {
                if (mWord != null && mWord.length() > 0) {
                    String temp = text.toLowerCase().replace(mWord.toLowerCase(), "");
                    int wordMatches = (text.length() - temp.length()) / mWord.length();

                    if (weighting == MatchedWordWeighting.UNIQUE)
                        matches += Math.min(1, wordMatches);   // each matched word in user's query can only be counted once
                    else if (weighting == MatchedWordWeighting.LOGARITHMIC)
                        matches += Math.log(wordMatches + 1);   // log base e
                    else
                        matches += wordMatches;
                    Log.d(TAG, "wordMatches = " + wordMatches + " for " + mWord);
                }
            }
        Log.d(TAG, "getNumberOfMatchesWeightedBy = " + matches);
        return matches;
    }
}