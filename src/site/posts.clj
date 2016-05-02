(ns site.posts
    (:require  [stasis.core :refer [slurp-directory]]
               [selmer.parser :refer [render-file]]
               [markdown.core :refer [md-to-html-string-with-meta]]
               [clj-time.format :refer [formatter parse]]))

(def date-format (formatter "yyyy-MM-dd HH:mm:SS Z"))
(defn- get-metadata [post] 
   (into {} (for [[k v] (:metadata post)] 
              [ k  
                (cond 
                  (and (= 1 (count v)) (= :date k)) (parse date-format (first v))
                  (and (= 1 (count v)) (not= :tags k)) (first v) 
                  :else v)])))

(defn get-posts []
  (let [posts (slurp-directory "./resources/posts/" #".*\.md")]
    (into {} (for [[p post] posts] 
                (let [c (md-to-html-string-with-meta post)
                      m (get-metadata c) h (:html c)]
                  [(str "/blog/" (:name m) "/") (render-file "templates/post.html" {:page m :content (:html c)})]))))) 
