(ns clf.core
  (:import [java.io File])
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]))

(defn list-files
  [directory]
  (->> (.listFiles (File. directory))
       (filter #(not (.isHidden %)))
       (sort)
       (map #(.getName %))
       (str/join \newline)))

(defn list-hidden
  [directory]
  (->> (.listFiles (File. directory))
       (sort)
       (map #(.getName %))
       (str/join \newline)))

(defn list-recursive
  [directory] 
  (let [dir? #(.isDirectory %)]
    (->> (tree-seq dir? #(.listFiles %) (File. directory))
         (filter (comp not dir?))
         (sort) 
         (map #(.getPath %)) 
         (str/join \newline))))

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
  [["-a" "--all" "do not ignore entries starting with ."]
   ["nil" "--test"] 
   ["-R" "--recursive" "list subdirectories recursively"]
   ["-h" "--help" "Print this help information"]])

(defn -main [& args]
  (let [{:keys [options arguments summary]} (parse-opts args cli-options)]
    (println arguments)
    (println options)
    (if (empty? (second arguments))
      (def directory ".")
      (def directory (second arguments)))

    (cond
      (:help options)
      (usage summary)

      (:all options)
      (println (list-hidden directory))

      (:recursive options)
      (println (list-recursive directory)))

      (empty? options)
      (println (list-files directory))))
