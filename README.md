# Parallel Data Mining Utility for Wikipedia

## Overview
The Wikipedia Data Mining project is a Java-based tool designed to process Wikipedia XML dumps and extract word frequency statistics from articles. It uses Java's concurrent utilities to maximize performance on multi-core systems by processing articles in parallel.

## Features
- **XML Parsing:** Utilizes StAX to parse Wikipedia XML files
- **Parallel Processing:** Implements a producer-consumer pipeline with multiple consumer threads that compute term frequencies concurrently
- **Thread Safety:** Employs BlockingQueue, ConcurrentHashMap, and AtomicInteger to ensure safe and efficient parallel updates
- **Performance Measurement:** Provides tests comparing sequential, partially parallel, and fully parallel executions
- **Simulated Data Storage:** Demonstrates an optimistic "database" insertion of processed page analyses

## Project Structure
- **model/**  
  Contains the core data model classes:
  - `Page.java`
  - `Revision.java`
  - `PageAnalysis.java`
- **Processing Components**
  - `WikipediaXMLParser.java` – Parses the XML dump
  - `PageConsumer.java` – Worker class that computes term frequencies
  - `ParallelProcessingPipeline.java` – Manages the pipeline execution and performance measurement
  - `DatabaseInserter.java` – Simulates inserting analyzed data into a database
- **Tests**
  - `ParallelProcessingPipelineTest.java` – Validates parsing and processing
  - `PerformanceComparisonTest.java` – Compares performance across different threading configurations

## Installation

### Prerequisites
- Java 11 or later
- Maven

### Setup
1. Clone the repository:
   ```
   git clone https://github.com/yourusername/wikipedia-data-mining.git
   cd wikipedia-data-mining
   ```

2. From root directory, download the test Wikipedia XML file (300MB, leave it compressed):
   ```
   wget https://dumps.wikimedia.org/simplewiki/latest/simplewiki-latest-pages-articles.xml.bz2
   bunzip2 src/test/resources/simplewiki-latest-pages-articles.xml.bz2
   ```
   
   Alternatively, you can download the file directly from:
   [https://dumps.wikimedia.org/simplewiki/latest/simplewiki-latest-pages-articles.xml.bz2](https://dumps.wikimedia.org/simplewiki/latest/simplewiki-latest-pages-articles.xml.bz2)

3. Build the project with Maven:
   ```
   mvn clean install
   ```

## Running Tests

Execute the performance comparison tests to test the pipeline against `src/test/resources/simplewiki-latest-pages-articles.xml.bz2`
https://dumps.wikimedia.org/simplewiki/latest/simplewiki-latest-pages-articles.xml.bz2

```
mvn test -Dtest=PerformanceComparisonTest
```

### Example Output

When you run the performance tests, you'll see output:

```
Sequential processing with 1 consumer thread(s) took 49250 ms
Parallel processing with 4 consumer thread(s) took 38885 ms
Parallel processing with 12 consumer thread(s) took 37110 ms
```

The test shows the processing time for different configurations:
- Single-threaded sequential processing
- Parallel processing with 4 threads
- Parallel processing with "max" threads (detects system cores)
