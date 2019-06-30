#!/usr/bin/env bash

for i in `seq 65 1 $((65 + 25))`
do
    sachgebiet=`printf "\x$(printf "%x" ${i})"`
    awk "/^$sachgebiet.*/{print}" Gesamt.dat >Gesamt_$sachgebiet.dat
done

exit 0
