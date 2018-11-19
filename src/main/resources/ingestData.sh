#!/usr/bin/env bash

INPUT="data.csv"
OUTPUT="output.csv"

START=1
STEP=1000
END=1000
STOP=20000
WAIT=0
> $OUTPUT
sleep $WAIT

while [ $END -le $STOP ]
do
START_TIME=$(date +%s.%N)
    for i in $(seq ${START} ${END});
    do
    sed -n  "${i}p" $INPUT >> $OUTPUT;
    done
    START=$(($END+1))
    END=$(($END+$STEP))
    sleep ${WAIT}
END_TIME=$(date +%s.%N)
DIFF=$(echo "$END_TIME - $START_TIME" | bc)
echo $DIFF
done

