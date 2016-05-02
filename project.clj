(defproject codurance-site "0.1.0-SNAPSHOT"
  :description "Codurance website"
  :url "http://codurance.com"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [selmer "1.0.4"]
                 [stasis "2.3.0"]
                 [ring "1.4.0"]
                 [optimus "0.18.5"]
                 [markdown-clj "0.9.89"]
                 [clj-time "0.11.0"] 
                 [circleci/clj-yaml "0.5.5"]
  ]
:jvm-opts  ["-Djava.awt.headless=true"] 
:ring  {:handler site.core/app :port 3456} 
:aliases  {"build-site"  ["run" "-m" "site.core/export"]}
:profiles  {:dev {:plugins [[lein-ring "0.9.7"]] }})
