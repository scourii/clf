(ns clf.core
  (:import [java.io File])
  (:require [clojure.string :as str]
            [io.aviso.ansi :as ansi]
            [clojure.tools.cli :refer [parse-opts]]))

(defn colorize-output
  [path]
  (let [split (str/split path #"\n")]
    (doseq [item split]
      (println 
       (if (.isDirectory (File. item))
        (ansi/bold-cyan (.getName (File. item)))
        (.getName (File. item)))))))

(defn list-files
  [directory]
  (->> (.listFiles (File. directory))
       (filter #(not (.isHidden %)))
       (sort-by (fn [f] (.getName f)))
       (str/join \newline)))

(defn get-path
  [directory]
  (->> (.listFiles (File. directory))
       (filter #(not (.isHidden %)))
       (sort-by (fn [f] (.getAbsolutePath f)))
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
   ["-C" "--colorize" "use terminal colors for output"]
   ["-h" "--help" "Print this help information"]])

(defn -main 
  [& args]
  (let [{:keys [options arguments summary]} (parse-opts args cli-options)
        directory (if (str/blank? (second arguments))
                    "."
                    (second arguments))]
      (condp apply [options]
        :help (usage summary)
        :all (println (list-hidden directory))
        :recursive (println (list-recursive directory))
        :colorize (colorize-output (get-path directory))
        (println (list-files directory)))))
