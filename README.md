# Wikipedia Dump Processor

This project processes the raw Wikipedia XML dump from [enwiki-latest-pages-articles.xml.bz2](https://dumps.wikimedia.org/enwiki/latest/), which is approximately 22 GB of raw XML. The project is designed to handle large-scale XML files using a streaming approach, parallel processing, and batch SQL database insertion.

## 1. Streaming XML Parsing & Decompression

- **Streaming Parser:**  
  Use a streaming XML parser (e.g., StAX) to process the file sequentially without loading it entirely into memory. This is essential for handling huge files.

- **On-the-fly Decompression:**  
  Since BZip2 isn’t inherently splittable, decompress the file on the fly. Consider libraries that support parallel decompression or pre-decompress into manageable chunks if hardware permits.

## 2. Parallel Processing Pipeline

Implement a producer-consumer model that decouples parsing, analysis, and database insertion:

- **Producer (Parsing Stage):**  
  A dedicated thread (or a few) performs sequential XML parsing. Each `<page>` element is parsed into a Page object and added to a thread-safe queue.

- **Consumer (Analysis Stage):**  
  A pool of worker threads (using Java’s ForkJoinPool or ExecutorService) retrieves Page objects from the queue to perform:
    - **TF-IDF Calculation:** Compute term frequencies for each page in parallel. For the inverse document frequency (IDF), accumulate statistics using thread-safe structures (e.g., ConcurrentHashMap) or a reduce phase.
    - **Other Analyses:** Additional metadata extraction or text normalization can also be performed in parallel.

- **Database Insertion Stage:**  
  A separate thread pool handles writing to the SQL database using batch inserts for performance:
    - Collect processed pages (or analysis results) into batches.
    - Use a connection pool and prepared statements for efficient writes.
    - Manage transactions properly to avoid conflicts.

## 3. SQL Database Schema & Integration

- **Schema Design:**
    - **Pages Table:** Store page metadata (title, ns, id, redirect info, etc.).
    - **Revisions Table:** Optionally store revision details.
    - **Analysis Results Table:** Map words to their frequencies and computed metrics (e.g., TF-IDF scores) per page.

- **Indexing & Optimizations:**  
  Consider indexes on frequently queried fields (e.g., page id, title) to speed up analysis. Ensure the schema supports batch operations to reduce overhead.

## 4. Sample Workflow Diagram

1. **Decompression & Parsing:**
    - A streaming XML parser reads the compressed file.
    - Each `<page>` is parsed into a Page object and added to a blocking queue.

2. **Parallel Analysis:**
    - Worker threads retrieve Page objects from the queue.
    - They perform text analysis (e.g., word count, term frequency calculations) in parallel.
    - Global statistics for IDF computation are accumulated.

3. **Batch Database Insertion:**
    - Processed pages (or their analysis results) are batched and inserted into the SQL database using efficient bulk operations.

4. **Post-Processing:**
    - After processing all pages, perform any global analysis (e.g., final TF-IDF score calculation) using the aggregated data.
