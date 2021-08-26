(ns clj-slides.parser
  (:require
   [commonmark-hiccup.core :refer [markdown->hiccup default-config]]))

(defn paginate-hiccup [hiccup-seq]
  (remove #(= [:hr] (first %))
    (partition-by #(= [:hr] %) hiccup-seq)))

(defn get-paginated-slides [slides-path]
  (->>
    slides-path
    (slurp)
    (markdown->hiccup default-config)
    (paginate-hiccup)))
