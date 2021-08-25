(ns clj-slides.prod
  (:require [clj-slides.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
