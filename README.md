# fintx-j
A personal Java project for reconciling financial transactions.


# Example Commands
The examples below show how to invoke the application via Gradle, based on files used by unit tests.

## Example: No Transaction Discrepancies Found
```console
./gradlew run --args="-file1=src/test/resources/ReconcilerTest/exactMatch/rakuten.csv -file1Format=rakuten -file2=src/test/resources/ReconcilerTest/exactMatch/generic.csv"

> Task :app:run
===================================
Summary
===================================
Date range: [-∞, ∞)

File 1: src/test/resources/ReconcilerTest/exactMatch/rakuten.csv
num transactions: 2
num transactions in date range: 2
num transactions not found in File 2: 0
num file errors: 0

File 2: src/test/resources/ReconcilerTest/exactMatch/generic.csv
num transactions: 2
num transactions in date range: 2
num transactions not found in File 1: 0
num file errors: 0

===================================
File 1 Details
===================================
transactions not found in File 2:
(none)

file errors:
(none)

===================================
File 2 Details
===================================
transactions not found in File 1:
(none)

file errors:
(none)

BUILD SUCCESSFUL in 1s
```

## Example: Transaction Discrepancies Found  
```console
 ./gradlew run --args="-file1=src/test/resources/ReconcilerTest/partialOverlap/rakuten.csv -file1Format=rakuten -file2=src/test/resources/ReconcilerTest/partialOverlap/generic.csv"

> Task :app:run
===================================
Summary
===================================
Date range: [-∞, ∞)

File 1: src/test/resources/ReconcilerTest/partialOverlap/rakuten.csv
num transactions: 2
num transactions in date range: 2
num transactions not found in File 2: 1
num file errors: 0

File 2: src/test/resources/ReconcilerTest/partialOverlap/generic.csv
num transactions: 2
num transactions in date range: 2
num transactions not found in File 1: 1
num file errors: 0

===================================
File 1 Details
===================================
transactions not found in File 2:
line    date            amount                  place or item       
2       2022-12-30      7580                    item 1

file errors:
(none)

===================================
File 2 Details
===================================
transactions not found in File 1:
line    date            amount                  place or item       
2       2023-02-03      1784                    eon

file errors:
(none)

BUILD SUCCESSFUL in 1s
```
