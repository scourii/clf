(ns clf.core
  (:import [java.io File])
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]))

(defn list-files
  [directory]
  (->> (.listFiles (File. directory))
       (filter #(not (.isHidden %)))
       (sort)
       (map #(.getName %))))

(defn list-hidden
  [directory]
  (->> (.listFiles (File. directory))
       (sort)
       (map #(.getName %))
       (str/join \newline)
       (println)))

(defn- usage
  [options]
  (->> ["Usage: clf [options] directory"
        ""
        "Options:"
        options
        ""]
       (str/join \newline)
       (println)))

(def cli-options
  [["-a" "--all" "do not ignore entries starting with ."
    :default "."]
   [nil  "--test"]
   ["-h" "--help" "Print this help information"]])

(defn -main [& args]
  (let [{:keys [options arguments summary errors]} (parse-opts args cli-options)]
    (if (empty? (second arguments))
      (def directory ".")
      (def directory (second arguments)))

    (cond
      (:help options)
      (usage summary)

      (:all options)
      (list-hidden directory)

      (empty? options)
      (list-files directory)))) 
