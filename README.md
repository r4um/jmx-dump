# jmx-dump

Dumps JMX metrics.

## Installation

Download from Releases

## Usage

```shell
$ java -jar target/uberjar/jmx-dump-0.1.0-standalone.jar --help
Dump JMX Metrics

Usage: jmx-dump [options]

  -h, --host HOST            localhost  JMX Host
  -p, --port PORT            3000       JMX Port
  -j, --jndi-path JNDI-PATH  jmxrmi     jndi-path to use
  -u, --jmx-url URL                     JMX URL
  -m, --mbeans                          List MBean names
  -a, --attrs MBEAN                     List attributes of mbean MBEAN
  -d, --dump MBEAN                      Dump MBEAN mbean attributes and values in json
      --dump-all                        Dump all mbean attributes and values in json
      --help
```


## License

Copyright © 2015 Pranay Kanwar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
