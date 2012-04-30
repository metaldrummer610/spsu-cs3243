#!/bin/bash

# args: "data file" "num cpus" "scheduling type" "output file"
echo "Running for the 1st time"
java -cp bin/ edu.spsu.cs3243.Driver "docs/DataFile2.txt" 1 1 "stats/run_1_1.txt"
echo "Running for the 2nd time"
java -cp bin/ edu.spsu.cs3243.Driver "docs/DataFile2.txt" 1 2 "stats/run_1_2.txt"
echo "Running for the 3rd time"
java -cp bin/ edu.spsu.cs3243.Driver "docs/DataFile2.txt" 1 3 "stats/run_1_3.txt"

echo "Running for the 4th time"
java -cp bin/ edu.spsu.cs3243.Driver "docs/DataFile2.txt" 4 1 "stats/run_4_1.txt"
echo "Running for the 5th time"
java -cp bin/ edu.spsu.cs3243.Driver "docs/DataFile2.txt" 4 2 "stats/run_4_2.txt"
echo "Running for the 6th time"
java -cp bin/ edu.spsu.cs3243.Driver "docs/DataFile2.txt" 4 3 "stats/run_4_3.txt"