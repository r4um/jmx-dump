(ns jmx-dump.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.jmx :as jmx]
            [cheshire.core :as cc]
            [clojure.walk :as w])
  (:import (java.util Date Map List Set UUID)
           (java.sql Timestamp)
           (clojure.lang IPersistentCollection Keyword Symbol)
           (javax.management.remote JMXConnector)
           (org.apache.commons.lang3 SystemUtils)
           (org.apache.commons.lang3 JavaVersion))
  (:gen-class))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

;; main cli options
(def cli-options
  [["-a" "--attrs MBEAN" "List attributes of mbean MBEAN"]
   ["-c" "--creds CREDS" "JMX Credentials, ROLE:PASS"]
   ["-d" "--dump MBEAN" "Dump MBEAN mbean attributes and values in json"]
   [nil  "--dump-all" "Dump all mbean attributes and values in json"]
   ["-h" "--host HOST" "JMX Host" :default "localhost"]
   ["-i" "--invoke MBEAN OP" "Invoke operation OP on mbean MBEAN"]
   ["-j" "--jndi-path JNDI-PATH" "jndi-path to use" :default "jmxrmi"]
   ["-l" "--local VMID" "Fetch from local VM"]
   ["-m" "--mbeans" "List MBean names"]
   ["-o" "--operations MBEAN" "List operations on mbean MBEAN"]
   ["-p" "--port PORT" "JMX Port" :default 3000]
   ["-u" "--url URL" "JMX URL"]
   ["-v" "--value MBEAN ATTR1..." "Dump values of specific MBEAN attributes"]
   [nil  "--help"]])

;; cli usage
(defn usage [options-summary]
  (string/join \newline
        ["Dump JMX Metrics"
        ""
        "Usage: jmx-dump [options]"
        ""
        options-summary
        ""]))

(defn println-seq [args]
  (doseq [a args]
    (println (str a))))

(defn is-atleast-java9 []
  (SystemUtils/isJavaVersionAtLeast JavaVersion/JAVA_9))

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
    (str v)))

;; walk the map and encode data
(defn encode-jmx-map [m]
  (let [f (fn [[k v]] (if (map? v) [k v] [k (encode-jmx-data v)]))]
    (w/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn jmx-mbean-names []
  (map #(.getCanonicalName %1) (jmx/mbean-names "*:*")))

;; pre java 9 we need to load the tools.jar
(defn vm-attach-pre-java9 [vmd]
  (let [tjp (format "jar:file:///%s/../lib/tools.jar!/" (System/getProperty "java.home"))
        tju (java.net.URL. tjp)
        cl (java.net.URLClassLoader. (into-array [tju]))
        vm (.loadClass cl "com.sun.tools.attach.VirtualMachine")
        vma (.getDeclaredMethod vm "attach"  (into-array [String]))]
    (.invoke vma vm (into-array[vmd]))))

;; post java 9 there is no tools.jar
(defn vm-attach-post-java9 [vmd]
  (let [cl (.getParent (.getClassLoader clojure.lang.RT))
        vm (.loadClass cl "com.sun.tools.attach.VirtualMachine")
        vma (.getDeclaredMethod vm "attach"  (into-array [String]))]
    (.invoke vma vm (into-array[vmd]))))

(defn vm-attach [vmd]
(if (is-atleast-java9)
  (vm-attach-post-java9 vmd)
  (vm-attach-pre-java9 vmd)))

(defn local-jmx-addr [vma]
  (.getProperty
   (.getAgentProperties vma) "com.sun.management.jmxremote.localConnectorAddress"))

;; pre java 9 management-agent.jar needs to be loaded
(defn load-agent-pre-java9 [vma]
  (let [java-home (.getProperty (.getSystemProperties vma) "java.home")
        agent-lib (string/join java.io.File/separator [java-home "lib" "management-agent.jar"])]
    (.loadAgent vma agent-lib)))

;; post java 9 there is no management-agent.jar
(defn load-agent-post-java9 [vma]
  (.startLocalManagementAgent vma))

(defn load-agent [vma]
(if (is-atleast-java9)
  (load-agent-post-java9 vma)
  (load-agent-pre-java9 vma)))

;; fetch local JMX url given a local VM id
(defn local-jmx-url [vmd]
  (let [vma (vm-attach vmd)
        lurl (local-jmx-addr vma)]
    (when (nil? lurl) (load-agent vma))
    (local-jmx-addr vma)))

(defn build-credentials-env [creds]
  (let [role_pass (string/split creds #":")]
    {JMXConnector/CREDENTIALS (into-array role_pass)}))

(defn process-opts [options]
  (let [local (options :local)
        creds (options :creds)]
    (cond-> options
      local (assoc :url (local-jmx-url local))
      creds (assoc :environment (build-credentials-env creds)))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (parse-opts args cli-options)]

    ;; help and error conditions
    (cond
      (:help options)
      (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))

    (jmx/with-connection (process-opts options)
      ;; list a mbean's attributes
      (when-let [attrs-mbean (options :attrs)]
        (println-seq (jmx/attribute-names attrs-mbean)))
      ;; list all mbeans
      (when-let [_mbeans? (options :mbeans)]
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
      ;; dump mbean attr
      (when-let [dump-mbean-attr (options :value)]
        (let [attrs (if (< (count arguments) 2) (keyword (first arguments)) (map keyword arguments))]
          (println (cc/generate-string (jmx/read dump-mbean-attr attrs)))))

      ;; dump all mbeans
      (when-let [_dump-all? (options :dump-all)]
        (let [m (jmx-mbean-names)]
          (println (cc/generate-string
                    (into {} (map
                              #(vec [%1 (encode-jmx-map (jmx/mbean %1))])
                              m)) {:pretty true})))))))
