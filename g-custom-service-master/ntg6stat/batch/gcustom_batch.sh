#!/bin/bash

PSQL_HOME=/postgres/10
LOG_FILE=$PSQL_HOME/log/gcustom_batch.log
PSQL=$PSQL_HOME/bin/psql
PSQL_CMD="$PSQL -U gcustom -d gcustom"

# refresh materialized view

echo "`date '+%Y/%m/%d %H:%M:%s'` [REFRESH MATERIALIZED VIEW]" >> $LOG_FILE & \
PGPASSWORD=postgre2020 $PSQL_CMD -c "REFRESH MATERIALIZED VIEW mv_rp_time_stat" >> $LOG_FILE

# update stat
echo "`date '+%Y/%m/%d %H:%M:%s'` [UPDATE STAT TABLES       ]" >> $LOG_FILE & \
PGPASSWORD=postgre2020 $PSQL_CMD -c "select fnc_go_gcstm_schedule(1);" >> $LOG_FILE
