elflock
=======

_Ever wondered where all that money went?_ Now you have __elflock__...

## Features

1. Load transaction history from CSV

2. Search transaction history

3. Plot weekly/monthly history

4. Group and plot transactions matching search terms

5. Save searches on close


## Requires

1. Java 8 JDK ea (sorry :p)

2. gradle 1.11

## Installation

Build and run with:
```
gradle build && java -jar build/libs/elflock.jar
```

This also produces native installers in build/distributions/bundles/
(.rpm, .deb) if you are so inclined.

## Limitations

1. Only works with NatWest account format; it shouldn't be too hard to
   extend this to work with other formats, or add a general matcher
   that allows users to map CSV fields to standard properties
   (e.g. account number, balance etc.)
