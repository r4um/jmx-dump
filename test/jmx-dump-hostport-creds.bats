#!/usr/bin/env bats

setup() {
 # use the latest built binary
 export JMX_DUMP=./target/jmx-dump
 export JMX_DUMP_TEST_PRG_JMX_PORT=$(shuf -i 3000-9999 -n 1)
 # launch sleep helper
 (cd test && javac sleep.java)
 java -cp test/ -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMX_DUMP_TEST_PRG_JMX_PORT\
      -Dcom.sun.management.jmxremote.local.only=false\
      -Dcom.sun.management.jmxremote.authenticate=true\
      -Dcom.sun.management.jmxremote.password.file=./test/creds.pass\
      -Dcom.sun.management.jmxremote.access.file=./test/creds.accs\
      -Dcom.sun.management.jmxremote.ssl=false sleep 10000 &
 export JMX_DUMP_TEST_PRG_PID=$!
}

teardown() {
 kill $JMX_DUMP_TEST_PRG_PID
}

@test "should be able to dump all metrics given host/port/creds" {
   run java -jar $JMX_DUMP --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --creds test:test --dump-all
   echo $output
   [ "$status" -eq 0 ]
}

@test "should be able to list all mbeans given host/port/creds" {
   run java -jar $JMX_DUMP --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --creds test:test --mbeans
   [ "$status" -eq 0 ]
}

@test "should be able to list mbean attributes given mbean and host/port/creds" {
   run java -jar $JMX_DUMP --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --creds test:test --attrs 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to dump mbean given mbean and host/port/creds" {
   run java -jar $JMX_DUMP --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --creds test:test --dump 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to list mbean operations given mbean and host/port/creds" {
   run java -jar $JMX_DUMP --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --creds test:test --operations 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to invoke mbean operation given mbean, operation and host/port/creds" {
   run java -jar $JMX_DUMP --host localhost --port $JMX_DUMP_TEST_PRG_JMX_PORT --creds test:test --invoke 'java.lang:type=Memory' gc
   [ "$status" -eq 0 ]
}
