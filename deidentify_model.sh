#!/bin/bash

# INSTRUCTIONS:
# Fill in the top two sections with file paths, then run this script
#
# specialist            the SPECIALIST Lexicon
# names_addresses       a database of names and addresses
# vocab_file            word2vec-style vocabulary file of a similar corpus to determine frequent words
# keep_top              number of most frequent words to keep (probably somewhere between a few hundred, a few thousand)
#
# vectors_in            word2vec vectors to de-identify
# vectors_out           path for the de-identified vectors
# vocab_in              word2vec vocab to de-identify
# vocab_out             path for the de-identified vocab

specialist=
names_addresses=
vocab_file=
keep_top=2000

vectors_in=
vectors_out=
vocab_in=
vocab_out=

java -cp target/classes AllowedWords $specialist $names_addresses $vocab_file $keep_top allowed_words.txt
java -cp target/classes ModelScrubber allowed_words.txt $vectors_in $vectors_out $vocab_in $vocab_out
