(ns site.posts
    (:require  [stasis.core :refer [slurp-directory]]
               [selmer.parser :refer [render-file]]
               [markdown.core :refer [md-to-html-string-with-meta]]
               [clj-time.format :refer [formatter parse]]
               [site.authors :refer [get-author]]))

(def rendered-posts (atom {}))
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

(defn- process-metadata [metadata]
 (add-author (extract-from-lists metadata)))

(defn- to-html-with-metadata [md-content]
  (let [{:keys [metadata html]} (md-to-html-string-with-meta md-content)]
    [(add-author (extract-from-lists metadata)) html]))

(defn- to-html [ps]
  (into {} 
        (for [v (vals ps) ] 
          (let [ [metadata html] (to-html-with-metadata v)]
            [(str "/blog/" (:name metadata) "/") {:page metadata :content html}])   )))

(defn- render-page [post]
  (render-file "templates/post.html" post))

(defn- latest-posts [post-path posts]
  (map #(nth % 1) (take 7 (filter (fn [[k v]] (not= k post-path)) posts))))

(defn- add-site-details [posts]
  (into {} (for [[k v] posts] [k (assoc v :site {:posts (latest-posts k posts)  })])))

(defn render-posts []
  (let [posts (to-html (slurp-directory "./resources/posts/" #".*\.md")) 
        site-posts (add-site-details posts) ]
    (reset! rendered-posts 
            (into {} (for [[path content] site-posts] [path (render-page content)] ))))) 
