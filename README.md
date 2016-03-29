Smart Meter Data Generator
======================
This data generator is a framework that could generate the data for different analytic purpose. The following figure shows the architecture of the data generator. The kernel is the generator factory, which is responsible for creating the generator that satisfies a specific data model. This generator currently supports generating the following data sets:

![Data Generator Architecutre](https://photos-4.dropbox.com/t/2/AADnhb3sv7E6e33Z0fHfSTKJnBNZFhsutlWmzMROyB8DzQ/12/313886353/png/32x32/1/_/1/2/architecture.png/EN-c2p4EGOM-IAIoAg/QSiOW0EIdzlXU7uTe_uJh9sWGErDiAjaDj1bqmduR9U?size_mode=5 "Data generator")

* Simple linear regression data
* Multiple linear regression data
* Clustering data
* Smart meter data (satisfying 3-line model with seeded data)

Compile & Usage
=====================
**Compile:** mvn package

**Usage:** java org.iss4e.datagen.DataGenMain --help
