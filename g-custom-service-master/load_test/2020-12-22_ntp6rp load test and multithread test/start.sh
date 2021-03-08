#!/bin/bash
ab -v 4 -c 4 -n 10 \
    -e log.csv -g pnuplot \
    -T 'application/json' \
    -H 'Authorization: Bearer 4a182199-9315-4896-8db9-ce330ca83adf' \
    -H 'Content-Type: application/json' \
    -p post.json \
    "http://localhost:7010/rp?st=126.977956,37.566316&dt=126.851365,35.159897&crs=WGS84&wps=127.384851,36.350439%3B129.311498,35.538861"

