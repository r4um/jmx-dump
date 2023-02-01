#!/usr/bin/env bats

setup() {
 # use the latest built binary
 export JMX_DUMP=./target/jmx-dump
 # launch sleep helper
 (cd test && javac sleep.java)
 java -cp test/ sleep 10000 &
 export JMX_DUMP_TEST_PRG_PID=$!
}

teardown() {
 kill $JMX_DUMP_TEST_PRG_PID
}

@test "should be able to dump all metrics given local id" {
   run java -jar $JMX_DUMP --local $JMX_DUMP_TEST_PRG_PID --dump-all
   [ "$status" -eq 0 ]
}

@test "should be able to list all mbeans given local id" {
   run java -jar $JMX_DUMP --local $JMX_DUMP_TEST_PRG_PID --mbeans
   [ "$status" -eq 0 ]
}

@test "should be able to list mbean attributes given mbean and local id" {
   run java -jar $JMX_DUMP --local $JMX_DUMP_TEST_PRG_PID --attrs 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to dump mbean given mbean and local id" {
   run java -jar $JMX_DUMP --local $JMX_DUMP_TEST_PRG_PID --dump 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to list mbean operations given mbean and local id" {
   run java -jar $JMX_DUMP --local $JMX_DUMP_TEST_PRG_PID --operations 'java.lang:type=Memory'
   [ "$status" -eq 0 ]
}

@test "should be able to invoke mbean operation given mbean, operation and local id" {
   run java -jar $JMX_DUMP --local $JMX_DUMP_TEST_PRG_PID --invoke 'java.lang:type=Memory' gc
   [ "$status" -eq 0 ]
}
