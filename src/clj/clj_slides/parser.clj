(ns clj-slides.parser
  (:require
   [commonmark-hiccup.core :refer [markdown->hiccup default-config]]))

(defn paginate-hiccup [hiccup-seq]
  (remove #(= [:hr] (first %))
    (partition-by #(= [:hr] %) hiccup-seq)))

(defn parse-markdown-to-hiccup [raw-text]
  (markdown->hiccup default-config raw-text))

(defn get-paginated-slides [slides-path]
  (paginate-hiccup (parse-markdown-to-hiccup (slurp slides-path))))
