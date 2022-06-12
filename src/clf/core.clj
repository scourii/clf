(ns clf.core
  (:import [java.io File])
  (:require [clojure.string :as str]))

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
       (map #(.getName %))))

(defn -main
  [& args]
  (->> (first args)
       list-hidden
       (str/join \newline)
       println))
