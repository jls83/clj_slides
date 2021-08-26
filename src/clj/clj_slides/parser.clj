(ns clj-slides.parser
  (:require
   [commonmark-hiccup.core :refer [markdown->hiccup default-config]]))

(def slide-file-path "blah.md")

(defn paginate-hiccup [hiccup-seq]
  (remove #(= [:hr] (first %))
    (partition-by #(= [:hr] %) hiccup-seq)))

(defn parse-markdown-to-hiccup [raw-text]
  (markdown->hiccup default-config raw-text))

(def slides-paginated
  (paginate-hiccup
    (parse-markdown-to-hiccup (slurp slide-file-path))))

