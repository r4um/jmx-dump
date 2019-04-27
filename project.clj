(defproject jmx-dump "0.9.3"
  :description "Dump JMX Metrics"
  :url "https://github.com/r4um/jmx-dump"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/java.jmx "0.3.4"]
                 [org.apache.commons/commons-lang3 "3.8.1"]
                 [cheshire "5.8.1"]]
  :main ^:skip-aot jmx-dump.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
