(defproject jmx-dump "0.10.3"
  :description "Dump JMX Metrics"
  :url "https://github.com/r4um/jmx-dump"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/java.jmx "1.0.0"]
                 [org.apache.commons/commons-lang3 "3.9"]
                 [cheshire "5.10.0"]]
  :main jmx-dump.core
  :target-path "target/%s"
  :bin {:name          "jmx-dump"
        :jvm-opts [ "$JVM_OPTS" ]}
  :profiles {:uberjar {:aot :all}
             :dev     {:dependencies [[clj-kondo "2020.03.20"]]
                       :plugins      [[lein-binplus "0.6.6"]]}}
  :aliases {"clj-kondo" ["run" "-m" "clj-kondo.main" "--lint" "src"]})
