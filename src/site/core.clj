(ns site.core
    (:require  [ring.middleware.content-type :refer  [wrap-content-type]]
               [stasis.core :as stasis]
               [optimus.assets :as assets]
               [optimus.prime :as optimus]
               [optimus.optimizations :as optimizations]
               [optimus.strategies :refer  [serve-live-assets]]
               [optimus.export]
               [clojure.string :as s]
               [site.posts :as p]
               [selmer.parser :refer [render-file add-tag!]]))

(add-tag! :navlink
  (fn [args context-map]
    (str "<li><a href='" (first args) "'>" (s/join " " (rest args)) "</a></li>")))

(add-tag! :navgroup
  (fn [args context-map]
    (str "<li class='dropdown'><a class='dropdown-toggle' data-toggle='dropdown'>"
        (clojure.string/join " " args) 
        "<i class='icon-angle-down'>&nbsp;</i></a><ul class='dropdown-menu'>")))

(add-tag! :endnavgroup
  (fn [args context-map]
    (str "</li></ul>")))

(defn get-assets  []
    (concat
         (assets/load-assets "public" [#"/assets/.*\.png"])
         (assets/load-assets "public" [#"/assets/.*\.[jpg|jpeg]"])
         (assets/load-assets "public" [#"/assets/.*\.css"])
         (assets/load-assets "public" [#"/assets/.*\.js"])))

(defn get-pages  []
  (let [f (clojure.java.io/file "resources/site")
        paths (map #(subs % 14) (map #(.getPath %) (filter #(.isFile %) (file-seq f))))]
    
    (into {} (for [p paths] [p (render-file (str "site" p) {})])))) 

(defn get-all-pages [] (concat (get-pages) (p/get-posts)))

(def optimize optimizations/all)

(def app  (->  (stasis/serve-pages get-all-pages)
               (optimus/wrap get-assets optimize 
                    serve-live-assets {:cache-live-assets 500000} )
                    wrap-content-type ))

(def export-directory "./target/build/")

(defn- load-export-dir  []
    (stasis/slurp-directory export-directory #"\.[^.]+$"))

(defn export  []
  (let [assets (optimize  (get-assets) {})
        old-files  (load-export-dir)]
          (stasis/empty-directory! export-directory)
          (optimus.export/save-assets assets export-directory)
          (stasis/export-pages  (get-all-pages) export-directory  {:optimus-assets assets})
          (println)
          (println "Export complete:")
          (stasis/report-differences old-files  (load-export-dir))
          (println)))
