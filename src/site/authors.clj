(ns site.authors
    (:require [clj-yaml.core :as yaml]))

(def authors (into {} (for [author (yaml/parse-string (slurp "resources/authors/team.yml"))] [(:name author) author])))
(defn get-author [name] (authors name)) 
