# Parallel Data Mining Utility

## Overview

The Wikipedia Data Mining project is a Java-based tool designed to process Wikipedia XML dumps and extract word frequency statistics from articles. It uses Java's concurrent utilities to attempt to maximize performance on multi-core systems by processing articles in parallel.

## Features

- **XML Parsing:** Utilizes StAX to parse Wikipedia XML files.
- **Parallel Processing:** Implements a producer-consumer pipeline with multiple consumer threads that compute term frequencies concurrently.
- **Thread Safety:** Employs BlockingQueue, ConcurrentHashMap, and AtomicInteger to ensure safe and efficient parallel updates.
- **Performance Measurement:** Provides tests comparing sequential, partially parallel, and fully parallel executions.
- **Simulated Data Storage:** Demonstrates an optimistic "database" insertion of processed page analyses.

## Project Structure

- **model**  
  Contains the core data model classes:
    - Page.java
    - Revision.java
    - PageAnalysis.java

- **Processing Components**
    - WikipediaXMLParser.java – Parses the XML dump.
    - PageConsumer.java – Worker class that computes term frequencies.
    - ParallelProcessingPipeline.java – Manages the pipeline execution and performance measurement.
    - DatabaseInserter.java – Simulates inserting analyzed data into a database.

- **Tests**
    - ParallelProcessingPipelineTest.java – Validates parsing and processing.
    - PerformanceComparisonTest.java – Compares performance across different threading configurations.
