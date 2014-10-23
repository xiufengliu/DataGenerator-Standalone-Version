Smart Meter Data Generator
======================
This data generator is a framework that could generate the data for different analytic purpose. The following figure shows the architecture of the data generator. The kernel of the generator is the generator factory, which is responsible for creating the generator that satisfies a specific data model. This generator currently supports generating the following data sets:
![Data Generator Architecutre](https://dl.dropboxusercontent.com/u/8691433/benchmark/img/datagenerator.png "Data generator")

* Simple linear regression data
* Multiple linear regression data
* Clustering data
* Smart meter data (satisfying 3-line model with seeded data)

To generate smart meter data

