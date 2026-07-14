(defproject jmx-dump "0.16.0"
  :description "Dump JMX Metrics"
  :url "https://github.com/r4um/jmx-dump"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.5"]
                 [org.clojure/tools.cli "1.4.256"]
                 [org.clojure/java.jmx "1.1.1"]
                 [org.apache.commons/commons-lang3 "3.20.0"]
                 [cheshire "6.2.0"]]
  :main jmx-dump.core
  :bin {:name          "jmx-dump"
        :jvm-opts ["$JVM_OPTS"]}
  :profiles {:uberjar {:aot :all}
             :dev     {:dependencies [[clj-kondo "2026.05.25"]]
                       :plugins      [[lein-binplus "0.6.8"]
                                      [lein-ancient "1.0.0"]]}}
  :aliases {"clj-kondo" ["run" "-m" "clj-kondo.main" "--lint" "src"]})
