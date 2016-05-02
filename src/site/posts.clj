(ns site.posts
    (:require  [stasis.core :refer [slurp-directory]]
               [selmer.parser :refer [render-file]]
               [markdown.core :refer [md-to-html-string-with-meta]]
               [clj-time.format :refer [formatter parse]]
               [site.authors :refer [get-author]]))

(def date-format (formatter "yyyy-MM-dd HH:mm:SS Z"))

(defn- extract-metadata-value [k v] 
  (cond 
    (and (= 1 (count v)) (= :date k)) (parse date-format (first v))
    (and (= 1 (count v)) (not= :tags k)) (first v) 
    :else v))

(defn- extract-from-lists [m]
  (into {} (for [[k v] m] [ k (extract-metadata-value k v) ])))

(defn- add-author [m]
  (assoc m :author (get-author (:author m))))

(defn- get-metadata [post]
 (add-author (extract-from-lists (:metadata post))))

(defn- render-post [md-content]
  (let [c (md-to-html-string-with-meta md-content)
        m (get-metadata c) h (:html c)]
      
    [(str "/blog/" (:name m) "/") 
     (render-file "templates/post.html" {:page m :content (:html c)})]))

(defn get-posts []
  (let [posts (slurp-directory "./resources/posts/" #".*\.md")]
    (into {} (for [[_ md-content] posts] (render-post md-content))))) 
