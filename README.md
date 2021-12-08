# jmx-dump

Dumps JMX metrics as JSON on the command line.

[![Dependencies Status](https://versions.deps.co/r4um/jmx-dump/status.svg)](https://versions.deps.co/r4um/jmx-dump)
[![Build Status](https://app.travis-ci.com/r4um/jmx-dump.svg?branch=master)](https://app.travis-ci.com/r4um/jmx-dump)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/r4um/jmx-dump)](https://github.com/r4um/jmx-dump/releases/latest)

## Installation

Download standalone binary or jar from [latest release](https://github.com/r4um/jmx-dump/releases/latest).

Available also via homebrew tap

```shell
brew tap r4um/homebrew
brew install jmx-dump
```

Older versions at [Bintray](https://bintray.com/r4um/generic/jmx-dump).

## Usage

Use the standalone binary or jar from the releases.

```shell
$ jmx-dump --help
Dump JMX Metrics

Usage: jmx-dump [options]

  -a, --attrs MBEAN                      List attributes of mbean MBEAN
  -c, --creds CREDS                      JMX Credentials, ROLE:PASS
  -d, --dump MBEAN                       Dump MBEAN mbean attributes and values in json
      --dump-all                         Dump all mbean attributes and values in json
  -h, --host HOST             localhost  JMX Host
  -i, --invoke MBEAN OP                  Invoke operation OP on mbean MBEAN
  -j, --jndi-path JNDI-PATH   jmxrmi     jndi-path to use
  -l, --local VMID                       Fetch from local VM
  -m, --mbeans                           List MBean names
  -o, --operations MBEAN                 List operations on mbean MBEAN
  -p, --port PORT             3000       JMX Port
  -u, --url URL                          JMX URL
  -v, --value MBEAN ATTR1...             Dump values of specific MBEAN attributes
      --help
```


## License

Copyright © Pranay Kanwar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
