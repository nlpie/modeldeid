# Model De-ID
De-identification for co-occurrence models

This is a simple Java implementation of the distributional model de-identification strategy proposed in the 2016 AMIA Annual Symposium podium abstract "Automated De-Identification of Distributional Semantic Models" by Finley, Pakhomov, and Melton.

Current capabilities include building an allowed- or forbidden-words list and applying that as a filter to a word2vec model and its vocabulary. See the shell script for details on how to invoke the model scrubber.

The model has removes all words that are not part of the SPECIALIST Lexicon and any words that are part of the "patient info database" (names and addresses of patients associated with notes used to train the word2vec model). The 2,000 most common words in the patient database are included in the model as an exception, to allow for homonyms like ‘white’.

Expressed as pseudocode:
def keep-word = ( top-n-corpus-word || ( specialist-lexicon-word & !phi-word ) ) ? true : false


Future functionality will include applying the filter to the co-occurrence models used by BioMedICUS (e.g., the acronym sense model).
