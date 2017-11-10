# Model De-ID
De-identification for co-occurrence models

This is a simple Java implementation of the distributional model de-identification strategy proposed in the 2016 AMIA Annual Symposium podium abstract "Automated De-Identification of Distributional Semantic Models" by Finley, Pakhomov, and Melton.

Current capabilities include building an allowed- or forbidden-words list and applying that as a filter to a word2vec model and its vocabulary. See the shell script for details.

Future functionality will include applying the filter to the co-occurrence models used by BioMedICUS (e.g., the acronym sense model).
