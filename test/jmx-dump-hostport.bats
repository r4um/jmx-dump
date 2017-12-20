#!/usr/bin/env bats

setup() {
 # use the latest built jar
 export JMX_DUMP_UBERJAR=$(ls -rt target/uberjar/*standalone.jar | tail -1)
 export JMX_DUMP_TEST_PRG_JMX_PORT=$(shuf -i 3000-9999 -n 1)
 # launch sleep helper
 (cd test && javac sleep.java)
 java -cp test/ -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMX_DUMP_TEST_PRG_JMX_PORT\
      -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false\
      -Dcom.sun.management.jmxremote.ssl=false sleep 10000 &
 export JMX_DUMP_TEST_PRG_PID=$!
}

teardown() {
 kill $JMX_DUMP_TEST_PRG_PID
}

@test "should be able to dump all metrics given host/port" {
   run java -jar $JMX_DUMP_UBERJAR --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --dump-all
   [ "$status" -eq 0 ]
}

@test "should be able to list all mbeans given host/port" {
   run java -jar $JMX_DUMP_UBERJAR --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --mbeans
   [ "$status" -eq 0 ]
}

@test "should be able to list mbean attributes given mbean and host/port" {
   run java -jar $JMX_DUMP_UBERJAR --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --attrs 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to dump mbean given mbean and host/port" {
   run java -jar $JMX_DUMP_UBERJAR --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --dump 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to list mbean operations given mbean and host/port" {
   run java -jar $JMX_DUMP_UBERJAR --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --operations 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to invoke mbean operation given mbean, operation and host/port" {
   run java -jar $JMX_DUMP_UBERJAR --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --invoke 'java.lang:type=Memory' gc
   [ "$status" -eq 0 ]
}