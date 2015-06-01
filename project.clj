(defproject jmx-dump "0.2.1"
  :description "Dump JMX Metrics"
  :url "https://github.com/r4um/jmx-dump"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/java.jmx "0.3.1"]
                 [cheshire "5.4.0"]]
  :main ^:skip-aot jmx-dump.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
