(ns site.authors
    (:require [clj-yaml.core :as yaml]))

(def authors (into {} (for [author (parse-string (slurp "authors/team.yml"))] [(:name author) author])))
(def get-author [name] (authors name)) 
