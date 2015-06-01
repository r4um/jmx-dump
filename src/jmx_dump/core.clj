(ns jmx-dump.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.jmx :as jmx]
            [cheshire.core :as cc]
            [cheshire.generate :as cg]
            [clojure.walk :as w])
  (:import (java.util Date Map List Set SimpleTimeZone UUID)
           (java.sql Timestamp)
           (clojure.lang IPersistentCollection Keyword Ratio Symbol))
  (:gen-class))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

;; main cli options
(def cli-options
  [["-h" "--host HOST" "JMX Host" :default "localhost"]
   ["-p" "--port PORT" "JMX Port" :default 3000]
   ["-j" "--jndi-path JNDI-PATH" "jndi-path to use" :default "jmxrmi"]
   ["-u" "--jmx-url URL" "JMX URL" :default nil]
   ["-m" "--mbeans" "List MBean names"]
   ["-a" "--attrs MBEAN" "List attributes of mbean MBEAN"]
   ["-o" "--operations MBEAN" "List operations on mbean MBEAN"]
   ["-i" "--invoke MBEAN OP" "Invoke operation OP on mbean MBEAN"]
   ["-d" "--dump MBEAN" "Dump MBEAN mbean attributes and values in json"]
   [nil "--dump-all" "Dump all mbean attributes and values in json"]
   [nil "--help"]])

;; cli usage
(defn usage [options-summary]
  (->> ["Dump JMX Metrics"
        ""
        "Usage: jmx-dump [options]"
        ""
        options-summary
        ""]
       (string/join \newline)))

(defn println-seq [args]
  (doseq [a args]
    (println (str a))))

;; encode JMX data, sequences and weird class names :/
(defn encode-jmx-data [v]
  (cond
    (nil? v) v
    (instance? IPersistentCollection v) v
    (instance? Number v) v
    (instance? Boolean v) v
    (instance? String v) v
    (instance? Character v) v
    (instance? Keyword v) v
    (instance? Map v) v
    (instance? List v) v
    (instance? Set v) v
    (instance? UUID v) v
    (instance? Date v) v
    (instance? Timestamp v) v
    (instance? Symbol v) v
    ;; handle array types
    (re-find #"^\[" (.getName (class v)))
    (for [x (seq v)]
      (encode-jmx-data x))
    :else
    (.toString v)))

;; walk the map and encode data
(defn encode-jmx-map [m]
  (let [f (fn [[k v]] (if (map? v) [k v] [k (encode-jmx-data v)]))]
    (w/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn jmx-mbean-names []
  (map #(.getCanonicalName %1) (jmx/mbean-names "*:*")))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (parse-opts args cli-options)]

    ;; help and error conditions
    (cond
      (:help options)
      (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))

    (jmx/with-connection options
      ;; list a mbean's attributes
      (when-let [attrs-mbean (options :attrs)]
        (println-seq (jmx/attribute-names attrs-mbean)))
      ;; list all mbeans
      (when-let [mbeans? (options :mbeans)]
        (println-seq (jmx-mbean-names)))
      ;; list all mbean operations
      (when-let [mbean-ops (options :operations)]
        (println-seq (jmx/operation-names mbean-ops)))
      ;; invoke mbean operation
      (when-let [invk-mbean (options :invoke)]
        (let [op (first arguments)
              args (next arguments)]
          (apply jmx/invoke invk-mbean (keyword op) args)
          (println "OK")))
      ;; dump mbean in json
      (when-let [dump-mbean (options :dump)]
        (println (cc/generate-string (encode-jmx-map
                                      (jmx/mbean dump-mbean))
                                     {:pretty true})))
      ;; dump all mbeans
      (when-let [dump-all? (options :dump-all)]
        (let [m (jmx-mbean-names)]
          (println (cc/generate-string
                    (into {} (map
                              #(vec [%1 (encode-jmx-map (jmx/mbean %1))])
                              m)) {:pretty true})))))))
