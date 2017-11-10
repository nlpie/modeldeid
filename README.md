# Model De-ID
De-identification for co-occurrence models

This is a simple Java implementation of the distributional model de-identification strategy proposed in the 2016 AMIA Annual Symposium podium abstract "Automated De-Identification of Distributional Semantic Models" by Finley, Pakhomov, and Melton. See [abstract](https://amia2016.zerista.com/event/member/286646) for a short description.

Current capabilities include building an allowed- or forbidden-words list and applying that as a filter to a word2vec model and its vocabulary. See the shell script for details on how to invoke model deidentification.

Given a word2vec model trained on clinical notes, the algorithm removes PHI words that are not part of the SPECIALIST Lexicon and any words that are part of the "patient info database" (names and addresses of patients associated with notes used to train the word2vec model). Our research indicates that model performance is minimally impacted by allowing exceptions for the 2,000 most common words in the patient database. Retaining these words in the model tends to account for homonyms like ‘white’. This top-n inclusion parameter is configurable.

Expressed as pseudocode:
```
def keep-word = ( top-n-corpus-word || ( specialist-lexicon-word & !phi-word ) ) ? true : false
```

## Javadoc

You can find the api documentation for this project [here](https://nlpie.github.io/modeldeid/site/index.html)

## Invoking the model scrubber
The bash script assumes the Java source code has been compiled and paths to compiled classes, the raw word2vec model, patient names and addresses file, and output model have been specified in the shell script.
```sh
./deidentify_model.sh
```

## Contact and Support
For issues or enhancement requests, feel free to submit to the Issues tab on GitHub.

## About Us
BioMedICUS is developed by the
[University of Minnesota Institute for Health Informatics NLP/IE Group](http://www.bmhi.umn.edu/ihi/research/nlpie/).

## Credits
Code in this repository was originally written by Greg Finley as part of his post-doctoral work at the University of Minnesota.
