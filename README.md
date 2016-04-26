# AML2RDF   
Tool to integrate Industry 4.0 standards using Linked Data


## Important Dependencies

AML2RDF needs Java 1.7, Maven 3.0. Download Java SE from  
* http://www.oracle.com/technetwork/java/javase/downloads/index.html


## What is AML2RDF?

AML2RDF is a java based tool , which converts AML files (based on XML) to RDF and performs integration utilizing SPARQL. AML to RDF conversion is based on R2RML mapping generated using KARMA - integration tool. The modeling file is created in KARMA utilizing AML ontology. AML2RDF utilizes this modeling file to convert AML to RDF which becomes input for integration process that utilizes SPARQL to expel hetrogenity between AML files.Once the hetrogenity is removed, AML2RDF converts RDF files back to AML and finishes the integration process.

You can find useful video tutorials at:   
* https://drive.google.com/open?id=0B7FScfr1FLRDLXVBcEY3UWNObGc


## Installation and Setup  

Look in the Wiki : [Installation](https://github.com/i40-Tools/AML2RDF/wiki/Installation%3A-Source-Code)

## Documentation  

Available at: [Documentation](documentation/)

Research Documentation available at:   
* https://docs.google.com/document/d/1YoOr08Gfg8TZjPP_RXflSJ0KElqmOp_YDDjGOsxgaLo/edit?usp=sharing

AML ontology available at:
* https://github.com/i40-Tools/vocabularies

## Usage  

* Select multiple AML Files.
* Click Integration.
* output is stored in selected AML files folder.


##License

Copyright (C) 2016 EIS University of Bonn
