# jmx-dump

Dumps JMX metrics on the command line.

## Installation

Download binaries from [Bintray](https://bintray.com/r4um/generic/jmx-dump)

[![Dependencies Status](http://jarkeeper.com/r4um/jmx-dump/status.png)](http://jarkeeper.com/r4um/jmx-dump)
[![Download](https://api.bintray.com/packages/r4um/generic/jmx-dump/images/download.svg) ](https://bintray.com/r4um/generic/jmx-dump/_latestVersion)

## Usage

```shell
$ java -jar jmx-dump-0.3.2-standalone.jar --help
Dump JMX Metrics

Usage: jmx-dump [options]

  -h, --host HOST            localhost  JMX Host
  -p, --port PORT            3000       JMX Port
  -j, --jndi-path JNDI-PATH  jmxrmi     jndi-path to use
  -u, --url URL                         JMX URL
  -l, --local VMID                      Fetch from local VM
  -m, --mbeans                          List MBean names
  -a, --attrs MBEAN                     List attributes of mbean MBEAN
  -o, --operations MBEAN                List operations on mbean MBEAN
  -i, --invoke MBEAN OP                 Invoke operation OP on mbean MBEAN
  -d, --dump MBEAN                      Dump MBEAN mbean attributes and values in json
      --dump-all                        Dump all mbean attributes and values in json
      --help
```


## License

Copyright © 2015 Pranay Kanwar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
